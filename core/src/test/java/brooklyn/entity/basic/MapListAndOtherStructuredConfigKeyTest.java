package brooklyn.entity.basic;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

import java.util.List;
import java.util.Map;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import brooklyn.config.ConfigKey;
import brooklyn.entity.proxying.EntitySpecs;
import brooklyn.event.basic.DependentConfiguration;
import brooklyn.event.basic.ListConfigKey;
import brooklyn.event.basic.MapConfigKey;
import brooklyn.event.basic.ListConfigKey.ListModifications;
import brooklyn.event.basic.MapConfigKey.MapModifications;
import brooklyn.location.basic.SimulatedLocation;
import brooklyn.test.entity.TestApplication;
import brooklyn.test.entity.TestEntity;
import brooklyn.util.exceptions.Exceptions;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.util.concurrent.Callables;

/**
 * Tests for {@link MapConfigKey} and {@link ListConfigKey}.
 * <p>
 * <strong>NOTE</strong> several tests do not work as pure Java code, as type safety means they will not compile.
 * This also means some features of these configuration key classes are not available.
 */
public class MapListAndOtherStructuredConfigKeyTest {

    private List<SimulatedLocation> locs;
    private TestApplication app;
    private TestEntity entity;

    @BeforeMethod(alwaysRun=true)
    public void setUp() {
        locs = ImmutableList.of(new SimulatedLocation());
        app = ApplicationBuilder.newManagedApp(TestApplication.class);
        entity = app.createAndManageChild(EntitySpecs.spec(TestEntity.class));
    }

    @AfterMethod(alwaysRun=true)
    public void tearDown() throws Exception {
        if (app != null) Entities.destroy(app);
    }

    @Test
    public void testMapConfigKeyCanStoreAndRetrieveVals() throws Exception {
        entity.setConfig(TestEntity.CONF_MAP_THING.subKey("akey"), "aval");
        entity.setConfig(TestEntity.CONF_MAP_THING.subKey("bkey"), "bval");
        app.start(locs);
        assertEquals(entity.getConfig(TestEntity.CONF_MAP_THING), ImmutableMap.of("akey", "aval", "bkey", "bval"));
    }

    @Test
    public void testMapConfigKeyCanStoreAndRetrieveFutureVals() throws Exception {
        entity.setConfig(TestEntity.CONF_MAP_THING.subKey("akey"), DependentConfiguration.whenDone(Callables.returning("aval")));
        entity.setConfig(TestEntity.CONF_MAP_THING.subKey("bkey"), DependentConfiguration.whenDone(Callables.returning("bval")));
        app.start(locs);

        assertEquals(entity.getConfig(TestEntity.CONF_MAP_THING), ImmutableMap.of("akey", "aval", "bkey", "bval"));
    }

//    @Test
//    public void testConfigKeyStringWontStoreAndRetrieveMaps() throws Exception {
//        Map<String, ?> v1 = ImmutableMap.of("a", 1, "b", "bb");
//        //it only allows strings
//        try {
//            entity.setConfig(TestEntity.CONF_MAP_THING.subKey("akey"), v1); // XXX
//            fail();
//        } catch (Exception e) {
//            ClassCastException cce = Exceptions.getFirstThrowableOfType(e, ClassCastException.class);
//            if (cce == null) throw e;
//            if (!cce.getMessage().contains("Cannot coerce type")) throw e;
//        }
//    }

    @Test
    public void testConfigKeyCanStoreAndRetrieveMaps() throws Exception {
        Map<String, ?> v1 = ImmutableMap.of("a", 1, "b", "bb");
        entity.setConfig(TestEntity.CONF_MAP_PLAIN, v1);
        app.start(locs);
        assertEquals(entity.getConfig(TestEntity.CONF_MAP_PLAIN), v1);
    }

    @Test
    public void testListConfigKeyCanStoreAndRetrieveVals() throws Exception {
        entity.setConfig(TestEntity.CONF_LIST_THING.subKey(), "aval");
        entity.setConfig(TestEntity.CONF_LIST_THING.subKey(), "bval");
        app.start(locs);

        assertEquals(entity.getConfig(TestEntity.CONF_LIST_THING), ImmutableMap.of("akey", "aval", "bkey", "bval"));
    }

    @Test
    public void testListConfigKeyCanStoreAndRetrieveFutureVals() throws Exception {
        entity.setConfig(TestEntity.CONF_LIST_THING.subKey(), DependentConfiguration.whenDone(Callables.returning("aval")));
        entity.setConfig(TestEntity.CONF_LIST_THING.subKey(), DependentConfiguration.whenDone(Callables.returning("bval")));
        app.start(locs);

        assertEquals(entity.getConfig(TestEntity.CONF_LIST_THING), ImmutableMap.of("akey", "aval", "bkey", "bval"));
    }

//    @Test
//    public void testListConfigKeyAddDirect() throws Exception {
//        entity.setConfig(TestEntity.CONF_LIST_THING.subKey(), "aval");
//        entity.setConfig(TestEntity.CONF_LIST_THING, "bval"); // XXX
//        assertEquals(entity.getConfig(TestEntity.CONF_LIST_THING), ImmutableList.of("aval", "bval"));
//    }

//    @Test
//    public void testListConfigKeyClear() throws Exception {
//        entity.setConfig(TestEntity.CONF_LIST_THING.subKey(), "aval");
//        entity.setConfig(TestEntity.CONF_LIST_THING, ListModifications.clearing()); // XXX
//        // for now defaults to null, but empty list might be better? or whatever the default is?
//        assertEquals(entity.getConfig(TestEntity.CONF_LIST_THING), null);
//    }

    @Test
    public void testListConfigKeyAddMod() throws Exception {
        entity.setConfig(TestEntity.CONF_LIST_THING.subKey(), "aval");
        entity.setConfig(TestEntity.CONF_LIST_THING, ListModifications.add("bval", "cval"));
        assertEquals(entity.getConfig(TestEntity.CONF_LIST_THING), ImmutableList.of("aval", "bval", "cval"));
    }

    @Test
    public void testListConfigKeyAddAllMod() throws Exception {
        entity.setConfig(TestEntity.CONF_LIST_THING.subKey(), "aval");
        entity.setConfig(TestEntity.CONF_LIST_THING, ListModifications.addAll(ImmutableList.of("bval", "cval")));
        assertEquals(entity.getConfig(TestEntity.CONF_LIST_THING), ImmutableList.of("aval", "bval", "cval"));
    }

//    @Test
//    public void testListConfigKeyAddItemMod() throws Exception {
//        entity.setConfig(TestEntity.CONF_LIST_THING.subKey(), "aval");
//        entity.setConfig(TestEntity.CONF_LIST_THING, ListModifications.addItem(ImmutableList.of("bval", "cval"))); // XXX
//        assertEquals(entity.getConfig(TestEntity.CONF_LIST_THING), ImmutableList.of("aval", ImmutableList.of("bval", "cval")));
//    }

    @Test
    public void testListConfigKeySetMod() throws Exception {
        entity.setConfig(TestEntity.CONF_LIST_THING.subKey(), "aval");
        entity.setConfig(TestEntity.CONF_LIST_THING, ListModifications.set(ImmutableList.of("bval", "cval")));
        assertEquals(entity.getConfig(TestEntity.CONF_LIST_THING), ImmutableList.of("bval", "cval"));
    }

    @Test
    public void testMapConfigPutDirect() throws Exception {
        entity.setConfig(TestEntity.CONF_MAP_THING.subKey("akey"), "aval");
        entity.setConfig(TestEntity.CONF_MAP_THING, ImmutableMap.of("bkey", "bval"));
        assertEquals(entity.getConfig(TestEntity.CONF_MAP_THING), ImmutableMap.of("akey", "aval", "bkey", "bval"));
    }

    @Test
    public void testMapConfigPutAllMod() throws Exception {
        entity.setConfig(TestEntity.CONF_MAP_THING.subKey("akey"), "aval");
        entity.setConfig(TestEntity.CONF_MAP_THING, MapModifications.put(ImmutableMap.of("bkey", "bval")));
        assertEquals(entity.getConfig(TestEntity.CONF_MAP_THING), ImmutableMap.of("akey", "aval", "bkey", "bval"));
    }

//    @Test
//    public void testMapConfigClearMod() throws Exception {
//        entity.setConfig(TestEntity.CONF_MAP_THING.subKey("akey"), "aval");
//        entity.setConfig(TestEntity.CONF_MAP_THING, MapModifications.clearing()); // XXX
//        // for now defaults to null, but empty map might be better? or whatever the default is?
//        assertEquals(entity.getConfig(TestEntity.CONF_MAP_THING), null);
//    }

    @Test
    public void testMapConfigSetMode() throws Exception {
        entity.setConfig(TestEntity.CONF_MAP_THING.subKey("akey"), "aval");
        entity.setConfig(TestEntity.CONF_MAP_THING, MapModifications.set(ImmutableMap.of("bkey", "bval")));
        assertEquals(entity.getConfig(TestEntity.CONF_MAP_THING), ImmutableMap.of("bkey", "bval"));
    }

}
