/*
 * Copyright 2012-2013 by Cloudsoft Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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

import brooklyn.entity.basic.ApplicationBuilder
import brooklyn.entity.basic.Entities
import brooklyn.entity.proxying.BasicEntitySpec
import brooklyn.entity.trait.Startable
import brooklyn.location.Location
import brooklyn.location.basic.LocalhostMachineProvisioningLocation
import brooklyn.test.entity.TestApplication
import brooklyn.util.internal.TimeExtras

/**
 * Test the operation of the {@link GangliaCluster} class.
 */
public class GangliaClusterIntegrationTest {
    private static final Logger log = LoggerFactory.getLogger(GangliaClusterIntegrationTest.class)

    static { TimeExtras.init() }

    private TestApplication app
    private Location testLocation
    private GangliaCluster ganglia

    @BeforeMethod(alwaysRun = true)
    public void setup() {
        app = ApplicationBuilder.builder(TestApplication.class).manage();
        ganglia = app.createAndManageChild(BasicEntitySpec.newInstance(GangliaCluster.class));
        testLocation = new LocalhostMachineProvisioningLocation()
    }

    @AfterMethod(alwaysRun = true)
    public void shutdown() {
        Entities.destroyAll(app)
    }

    /**
     * Test that the server starts up and sets SERVICE_UP correctly.
     */
    @Test(groups = "Integration")
    public void canStartupAndShutdown() {
        app.start([ testLocation ])
        executeUntilSucceedsWithShutdown(ganglia, timeout:2*TimeUnit.MINUTES) {
            assertTrue ganglia.getAttribute(Startable.SERVICE_UP)
            assertTrue ganglia.manager.getAttribute(Startable.SERVICE_UP)
            Entities.dumpInfo(app)
            assertEquals ganglia.monitoredEntities.currentSize, 1
            assertEquals ganglia.monitors.currentSize, 1
        }
        assertFalse ganglia.getAttribute(Startable.SERVICE_UP)
    }
}
