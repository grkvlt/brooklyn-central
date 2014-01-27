package brooklyn.entity.nosql.cassandra;

import static org.testng.Assert.assertNotNull;

import java.io.File;
import java.math.BigInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import brooklyn.entity.basic.ApplicationBuilder;
import brooklyn.entity.basic.Entities;
import brooklyn.entity.proxy.nginx.NginxController;
import brooklyn.entity.proxying.EntitySpec;
import brooklyn.entity.rebind.RebindTestUtils;
import brooklyn.entity.trait.Startable;
import brooklyn.location.basic.LocalhostMachineProvisioningLocation;
import brooklyn.management.internal.LocalManagementContext;
import brooklyn.test.EntityTestUtils;
import brooklyn.test.entity.TestApplication;
import brooklyn.util.time.Duration;
import brooklyn.util.time.Time;

import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.io.Files;

/**
 * Test the operation of the {@link NginxController} class.
 */
public class CassandraClusterRebindIntegrationTest {
    private static final Logger LOG = LoggerFactory.getLogger(CassandraClusterRebindIntegrationTest.class);

    private LocalhostMachineProvisioningLocation localhostProvisioningLocation;
    private ClassLoader classLoader = getClass().getClassLoader();
    private LocalManagementContext origManagementContext;
    private File mementoDir;
    private TestApplication origApp;
    private TestApplication newApp;
    
    @BeforeMethod(alwaysRun=true)
    public void setUp() {
        mementoDir = Files.createTempDir();
        origManagementContext = RebindTestUtils.newPersistingManagementContext(mementoDir, classLoader);
        origApp = ApplicationBuilder.newManagedApp(TestApplication.class, origManagementContext);

        localhostProvisioningLocation = origApp.newLocalhostProvisioningLocation();
    }

    @AfterMethod(alwaysRun=true)
    public void tearDown() throws Exception {
        if (newApp != null) Entities.destroyAll(newApp.getManagementContext());
        if (origApp != null && origManagementContext.isRunning()) Entities.destroyAll(origManagementContext);
        if (mementoDir != null) RebindTestUtils.deleteMementoDir(mementoDir);
    }

    private TestApplication rebind() throws Exception {
        RebindTestUtils.waitForPersisted(origApp);
        RebindTestUtils.checkCurrentMementoSerializable(origApp);
        
        // Stop the old management context, so original entities won't interfere
        origManagementContext.terminate();
        
        return (TestApplication) RebindTestUtils.rebind(mementoDir, getClass().getClassLoader());
    }

    /**
     * Test that Brooklyn can rebind to a single node cluster.
     */
    @Test(groups = "Integration")
    public void testRebindClusterOfSizeOne() throws Exception {
        CassandraCluster origCluster = origApp.createAndManageChild(EntitySpec.create(CassandraCluster.class)
                .configure("initialSize", 1));

        origApp.start(ImmutableList.of(localhostProvisioningLocation));
        CassandraNode origNode = (CassandraNode) Iterables.get(origCluster.getMembers(), 0);

        EntityTestUtils.assertAttributeEqualsEventually(origCluster, CassandraCluster.GROUP_SIZE, 1);
        EntityTestUtils.assertAttributeEqualsEventually(origNode, Startable.SERVICE_UP, true);
        assertConsistentVersionAndPeersEventually(origNode);
        EntityTestUtils.assertAttributeEquals(origNode, CassandraNode.PEERS, 1);
        CassandraClusterLiveTest.checkConnectionRepeatedly(2, 5, origNode, origNode);
        BigInteger origToken = origNode.getAttribute(CassandraNode.TOKEN);
        assertNotNull(origToken);
        
        newApp = rebind();
        final CassandraCluster newCluster = (CassandraCluster) Iterables.find(newApp.getChildren(), Predicates.instanceOf(CassandraCluster.class));
        final CassandraNode newNode = (CassandraNode) Iterables.find(newCluster.getMembers(), Predicates.instanceOf(CassandraNode.class));
        
        EntityTestUtils.assertAttributeEqualsEventually(newCluster, CassandraCluster.GROUP_SIZE, 1);
        EntityTestUtils.assertAttributeEqualsEventually(newNode, Startable.SERVICE_UP, true);
        EntityTestUtils.assertAttributeEqualsEventually(newNode, CassandraNode.TOKEN, origToken);
        assertConsistentVersionAndPeersEventually(newNode);
        EntityTestUtils.assertAttributeEquals(newNode, CassandraNode.PEERS, 1);
        CassandraClusterLiveTest.checkConnectionRepeatedly(2, 5, newNode, newNode);
    }
    
    protected void assertConsistentVersionAndPeersEventually(CassandraNode node) {
        // may take some time to be consistent (with new thrift_latency checks on the node,
        // contactability should not be an issue, but consistency still might be)
        for (int i=0; ; i++) {
            boolean open = CassandraClusterLiveTest.isSocketOpen(node);
            Boolean consistant = open ? CassandraClusterLiveTest.areVersionsConsistent(node) : null;
            Integer numPeers = node.getAttribute(CassandraNode.PEERS);
            String msg = "consistency:  "
                    + (!open ? "unreachable" : consistant==null ? "error" : consistant)+"; "
                    + "peer group sizes: "+numPeers;
            LOG.info(msg);
            if (open && Boolean.TRUE.equals(consistant) && numPeers==1)
                break;
            if (i == 0) LOG.warn("NOT yet consistent, waiting");
            if (i >= 120) Assert.fail("Did not become consistent in time: "+msg);
            Time.sleep(Duration.ONE_SECOND);
        }
    }
}
