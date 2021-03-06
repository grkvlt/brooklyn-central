package brooklyn.launcher;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import brooklyn.config.BrooklynProperties;
import brooklyn.config.BrooklynServerConfig;
import brooklyn.entity.rebind.persister.BrooklynMementoPersisterToObjectStore;
import brooklyn.entity.rebind.persister.jclouds.BlobStoreTest;
import brooklyn.entity.rebind.persister.jclouds.JcloudsBlobStoreBasedObjectStore;
import brooklyn.management.ManagementContext;
import brooklyn.test.entity.LocalManagementContextForTests;
import brooklyn.util.javalang.JavaClassNames;
import brooklyn.util.text.Identifiers;

@Test(groups="Integration")
public class BrooklynLauncherRebindToCloudObjectStoreTest extends BrooklynLauncherRebindTestFixture {

    { persistenceLocationSpec = BlobStoreTest.PERSIST_TO_OBJECT_STORE_FOR_TEST_SPEC; }

    @BeforeMethod
    public void setUp() throws Exception {
        persistenceDir = newTempPersistenceContainerName();
    }

    @Override
    protected BrooklynLauncher newLauncherBase() {
        return super.newLauncherBase().persistenceLocation(persistenceLocationSpec);
    }
    
    protected LocalManagementContextForTests newManagementContextForTests(BrooklynProperties props) {
        BrooklynProperties p2 = BrooklynProperties.Factory.newDefault();
        if (props!=null) p2.putAll(props);
        return new LocalManagementContextForTests(p2);
    }

    @Override
    protected String newTempPersistenceContainerName() {
        return "test-"+JavaClassNames.callerStackElement(0).getClassName()+"-"+Identifiers.makeRandomId(4);
    }
    
    protected String badContainerName() {
        return "container-does-not-exist-"+Identifiers.makeRandomId(4);
    }
    
    protected void checkPersistenceContainerNameIs(String expected) {
        assertEquals(getPersistenceContainerName(lastMgmt()), expected);
    }

    static String getPersistenceContainerName(ManagementContext managementContext) {
        BrooklynMementoPersisterToObjectStore persister = (BrooklynMementoPersisterToObjectStore)managementContext.getRebindManager().getPersister();
        JcloudsBlobStoreBasedObjectStore store = (JcloudsBlobStoreBasedObjectStore)persister.getObjectStore();
        return store.getContainerName();
    }

    protected void checkPersistenceContainerNameIsDefault() {
        checkPersistenceContainerNameIs(BrooklynServerConfig.DEFAULT_PERSISTENCE_CONTAINER_NAME);
    }

    @Override @Test(groups="Integration")
    public void testRebindsToExistingApp() throws Exception {
        super.testRebindsToExistingApp();
    }

    @Override @Test(groups="Integration")
    public void testRebindCanAddNewApps() throws Exception {
        super.testRebindCanAddNewApps();
    }

    @Override @Test(groups="Integration")
    public void testAutoRebindsToExistingApp() throws Exception {
        super.testAutoRebindsToExistingApp();
    }

    @Override @Test(groups="Integration")
    public void testCleanDoesNotRebindToExistingApp() throws Exception {
        super.testCleanDoesNotRebindToExistingApp();
    }

    @Override @Test(groups="Integration")
    public void testAutoRebindCreatesNewIfEmptyDir() throws Exception {
        super.testAutoRebindCreatesNewIfEmptyDir();
    }

    @Override @Test(groups="Integration")
    public void testRebindRespectsPersistenceDirSetInProperties() throws Exception {
        super.testRebindRespectsPersistenceDirSetInProperties();
    }

    @Override @Test(groups="Integration")
    public void testRebindRespectsDefaultPersistenceDir() throws Exception {
        super.testRebindRespectsDefaultPersistenceDir();
    }

    @Override @Test(groups="Integration")
    public void testPersistenceFailsIfNoDir() throws Exception {
        super.testPersistenceFailsIfNoDir();
    }

    @Override @Test(groups="Integration")
    public void testExplicitRebindFailsIfEmpty() throws Exception {
        super.testExplicitRebindFailsIfEmpty();
    }

}
