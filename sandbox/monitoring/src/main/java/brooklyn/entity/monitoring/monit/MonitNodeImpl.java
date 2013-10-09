package brooklyn.entity.monitoring.monit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import brooklyn.entity.basic.SoftwareProcessImpl;
import brooklyn.event.feed.ssh.SshFeed;
import brooklyn.event.feed.ssh.SshPollConfig;
import brooklyn.event.feed.ssh.SshPollValue;
import brooklyn.location.Location;
import brooklyn.location.basic.SshMachineLocation;
import brooklyn.util.text.Strings;
import brooklyn.util.time.Duration;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;

public class MonitNodeImpl extends SoftwareProcessImpl implements MonitNode {
    
    private static final Logger LOG = LoggerFactory.getLogger(MonitNodeImpl.class);
    
    private SshFeed feed;
    
    @Override
    public Class<?> getDriverInterface() {
        return MonitDriver.class;
    }
    
    @Override
    public MonitDriver getDriver() {
        return (MonitDriver) super.getDriver();
    }

    @Override
    protected void connectSensors() {
        super.connectSensors();
        Location machine = Iterables.get(getLocations(), 0, null);
        
        if (machine instanceof SshMachineLocation) {
            String cmd = getDriver().getStatusCmd();
            feed = SshFeed.builder()
                .entity(this)
                .period(Duration.FIVE_SECONDS)
                .machine((SshMachineLocation) machine)
                .poll(new SshPollConfig<Boolean>(SERVICE_UP)
                    .command(cmd)
                    .setOnSuccess(true)
                    .setOnFailureOrException(false))
                .poll(new SshPollConfig<String>(MONIT_TARGET_PROCESS_NAME)
                    .command(cmd)
                    .onSuccess(new Function<SshPollValue, String>() {
                        @Override
                        public String apply(SshPollValue input) {
                            String process = Strings.getFirstWordAfter(input.getStdout(), "Process");
                            return process;
                        }
                    })
                    .setOnFailureOrException(null))
                .poll(new SshPollConfig<String>(MONIT_TARGET_PROCESS_STATUS)
                    .command(cmd)
                    .onSuccess(new Function<SshPollValue, String>() {
                        @Override
                        public String apply(SshPollValue input) {
                            String process = Strings.getFirstWordAfter(input.getStdout(), "status");
                            return process;
                        }
                    })
                    .setOnFailureOrException(null))
                .build();
        } else {
            LOG.warn("Location(s) %s not an ssh-machine location, so not polling for status; setting serviceUp immediately", getLocations());
            setAttribute(SERVICE_UP, true);
        }
    }
    
    @Override
    protected void disconnectSensors() {
        if (feed != null) feed.stop();
    }

    @Override
    public String getShortName() {
        return "Monit";
    }
}
