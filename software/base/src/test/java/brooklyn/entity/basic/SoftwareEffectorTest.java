package brooklyn.entity.basic;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import brooklyn.entity.Effector;
import brooklyn.location.LocationSpec;
import brooklyn.location.basic.LocalhostMachineProvisioningLocation;
import brooklyn.location.basic.SshMachineLocation;
import brooklyn.management.ManagementContext;
import brooklyn.management.Task;
import brooklyn.test.entity.TestApplication;
import brooklyn.util.config.ConfigBag;

public class SoftwareEffectorTest {

    private static final Logger log = LoggerFactory.getLogger(SoftwareEffectorTest.class);
                
    TestApplication app;
    ManagementContext mgmt;
    
    @BeforeMethod(alwaysRun=true)
    public void setup() throws Exception {
        app = ApplicationBuilder.newManagedApp(TestApplication.class);
        mgmt = app.getManagementContext();
        
        LocalhostMachineProvisioningLocation lhc = mgmt.getLocationManager().createLocation(LocationSpec.spec(LocalhostMachineProvisioningLocation.class));
        SshMachineLocation lh = lhc.obtain();
        app.start(Arrays.asList(lh));
    }
    
    @AfterMethod(alwaysRun=true)
    public void tearDown() throws Exception {
        if (mgmt != null) Entities.destroyAll(mgmt);
        mgmt = null;
    }

    public static final Effector<String> GET_REMOTE_DATE = Effectors.effector(String.class, "getRemoteDate")
            .description("retrieves the date from the remote machine")
            .impl(new SshEffectorBody<String>() {
                public String main(ConfigBag parameters) {
                    queue( ssh("date").requiringZeroAndReturningStdout() );
                    return last(String.class);
                }
            })
            .build();
    
    @Test(groups="Integration")
    public void testSshDateEffector() {
        Task<String> call = Entities.invokeEffector(app, app, GET_REMOTE_DATE);
        log.info("ssh date gives: "+call.getUnchecked());
        Assert.assertTrue(call.getUnchecked().indexOf("20") >= 0);
    }

    public static final String COMMAND_THAT_DOES_NOT_EXIST = "blah_blah_blah_command_DOES_NOT_EXIST";
    
    @Test(groups="Integration", expectedExceptions=Exception.class)
    public void testBadExitCodeCaught() {
        Task<Void> call = Entities.invokeEffector(app, app, Effectors.effector(Void.class, "badExitCode")
                .impl(new SshEffectorBody<Void>() {
                    public Void main(ConfigBag parameters) {
                        queue( ssh(COMMAND_THAT_DOES_NOT_EXIST).requiringZeroAndReturningStdout() );
                        return null;
                    }
                }).build() );
        call.getUnchecked();
        log.error("ERROR: should have failed earlier in this test, instead got successful task result "+call.getUnchecked()+" from "+call);
    }
        
    @Test(groups="Integration")
    public void testBadExitCodeCaughtAndStdErrAvailable() {
        final SshTask[] sshTasks = new SshTask[1];
        
        Task<Void> call = Entities.invokeEffector(app, app, Effectors.effector(Void.class, "badExitCode")
                .impl(new SshEffectorBody<Void>() {
                    public Void main(ConfigBag parameters) {
                        sshTasks[0] = queue( ssh(COMMAND_THAT_DOES_NOT_EXIST).requiringExitCodeZero() );
                        return null;
                    }
                }).build() );
        call.blockUntilEnded();
        Assert.assertTrue(call.isError());
        log.info("stderr gives: "+new String(sshTasks[0].getStderr()));
        Assert.assertTrue(new String(sshTasks[0].getStderr()).indexOf(COMMAND_THAT_DOES_NOT_EXIST) >= 0);
    }
        
}
