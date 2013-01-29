package brooklyn.entity.monitoring.ganglia;

import static brooklyn.test.TestUtils.*
import static java.util.concurrent.TimeUnit.*
import static org.testng.Assert.*

import java.util.concurrent.TimeUnit

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.testng.annotations.AfterMethod
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test

import com.google.common.base.Joiner;

import brooklyn.config.BrooklynProperties;
import brooklyn.entity.Application
import brooklyn.entity.basic.Entities
import brooklyn.entity.trait.Startable
import brooklyn.location.Location
import brooklyn.location.basic.BasicLocationRegistry
import brooklyn.test.entity.TestApplication
import brooklyn.util.internal.TimeExtras

/**
 * Test the operation of the {@link GangliaManager} class.
 */
public class GangliaIntegrationTest {
    private static final Logger log = LoggerFactory.getLogger(GangliaIntegrationTest.class)

    static { TimeExtras.init() }

    private Application app
    private Location testLocation
    private GangliaCluster ganglia

    @BeforeMethod(alwaysRun = true)
    public void setup() {
        app = new TestApplication();
        ganglia = new GangliaCluster(parent:app);
        Entities.startManagement(app);
//        testLocation = new LocalhostMachineProvisioningLocation()
        testLocation = new BasicLocationRegistry(app.getManagementContext()).resolve("ganglia")
    }

    @AfterMethod(alwaysRun = true)
    public void shutdown() {
        if (ganglia != null && ganglia.getAttribute(Startable.SERVICE_UP)) {
            ganglia.stop();
        }
        Entities.destroy(app)
    }

    /**
     * Test that the server starts up and sets SERVICE_UP correctly.
     */
    @Test(groups = "Integration")
    public void canStartupAndShutdown() {
        app.start([ testLocation ])
        executeUntilSucceedsWithShutdown(ganglia, timeout:600*TimeUnit.SECONDS) {
            assertTrue ganglia.getAttribute(Startable.SERVICE_UP)
            assertTrue ganglia.manager.getAttribute(Startable.SERVICE_UP)
            Entities.dumpInfo(app)
            assertEquals ganglia.monitoredEntities.currentSize, 1
            assertEquals ganglia.monitors.currentSize, 1
        }
        assertFalse ganglia.getAttribute(Startable.SERVICE_UP)
    }
}
