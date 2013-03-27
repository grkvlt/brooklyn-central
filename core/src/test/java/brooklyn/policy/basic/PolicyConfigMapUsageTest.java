package brooklyn.policy.basic;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import brooklyn.config.ConfigKey;
import brooklyn.entity.basic.ApplicationBuilder;
import brooklyn.entity.basic.ConfigKeys;
import brooklyn.entity.basic.Entities;
import brooklyn.event.basic.DependentConfiguration;
import brooklyn.test.entity.TestApplication;
import brooklyn.test.entity.TestEntity;
import brooklyn.util.MutableMap;
import brooklyn.util.exceptions.Exceptions;
import brooklyn.util.flags.SetFromFlag;

import com.google.common.util.concurrent.Callables;

/**
 * Test that configuration properties are usable and inherited correctly.
 */
public class PolicyConfigMapUsageTest {
    private static final int EARLY_RETURN_GRACE = 10;

    public static class MyPolicy extends AbstractPolicy {
        @SetFromFlag("intKey")
        public static final ConfigKey<Integer> INT_KEY = ConfigKeys.newConfigKey("bkey", "b key");

        @SetFromFlag("strKey")
        public static final ConfigKey<String> STR_KEY = ConfigKeys.newConfigKey("akey", "a key");
        public static final ConfigKey<Integer> INT_KEY_WITH_DEFAULT = ConfigKeys.newConfigKey("ckey", "c key", 1);
        public static final ConfigKey<String> STR_KEY_WITH_DEFAULT = ConfigKeys.newConfigKey("strKey", "str key", "str key default");

        MyPolicy(Map flags) {
            super(flags);
        }

        MyPolicy() {
            super();
        }
    }

    private ConfigKey<String> differentKey = ConfigKeys.newConfigKey("differentkey", "diffval");

    private TestApplication app;

    @BeforeMethod(alwaysRun=true)
    public void setUp() {
        app = ApplicationBuilder.newManagedApp(TestApplication.class);
    }

    @AfterMethod(alwaysRun=true)
    public void tearDown() throws Exception {
        if (app != null) Entities.destroyAll(app);
    }

    @Test
    public void testConfigFlagsPassedInAtConstructionIsAvailable() throws Exception {
        MyPolicy policy = new MyPolicy(MutableMap.builder()
                .put("strKey", "aval")
                .put("intKey", 2)
                .build());
        app.addPolicy(policy);
        
        assertEquals(policy.getConfig(MyPolicy.STR_KEY), "aval");
        assertEquals(policy.getConfig(MyPolicy.INT_KEY), (Integer)2);
        // this is set, because key name matches annotation on STR_KEY
        assertEquals(policy.getConfig(MyPolicy.STR_KEY_WITH_DEFAULT), "aval");
    }
    
    @Test
    public void testUnknownConfigPassedInAtConstructionIsWarnedAndIgnored() throws Exception {
        // TODO Also assert it's warned
        MyPolicy policy = new MyPolicy(MutableMap.builder()
                .put(differentKey, "aval")
                .build());
        app.addPolicy(policy);
        
        assertEquals(policy.getConfig(differentKey), null);
        assertEquals(policy.getPolicyType().getConfigKey(differentKey.getName()), null);
    }
    
    @Test
    public void testConfigPassedInAtConstructionIsAvailable() throws Exception {
        MyPolicy policy = new MyPolicy(MutableMap.builder()
                .put(MyPolicy.STR_KEY, "aval")
                .put(MyPolicy.INT_KEY, 2)
                .build());
        app.addPolicy(policy);
        
        assertEquals(policy.getConfig(MyPolicy.STR_KEY), "aval");
        assertEquals(policy.getConfig(MyPolicy.INT_KEY), (Integer)2);
        // this is not set (contrast with above)
        assertEquals(policy.getConfig(MyPolicy.STR_KEY_WITH_DEFAULT), MyPolicy.STR_KEY_WITH_DEFAULT.getDefaultValue());
    }
    
    @Test
    public void testConfigSetToGroovyTruthFalseIsAvailable() throws Exception {
        MyPolicy policy = new MyPolicy(MutableMap.builder()
                .put(MyPolicy.INT_KEY_WITH_DEFAULT, 0)
                .build());
        app.addPolicy(policy);
        
        assertEquals(policy.getConfig(MyPolicy.INT_KEY_WITH_DEFAULT), (Integer)0);
    }
    
    @Test
    public void testConfigSetToNullIsAvailable() throws Exception {
        MyPolicy policy = new MyPolicy(MutableMap.builder()
                .put(MyPolicy.STR_KEY_WITH_DEFAULT, null)
                .build());
        app.addPolicy(policy);
        
        assertEquals(policy.getConfig(MyPolicy.STR_KEY_WITH_DEFAULT), null);
    }
    
    @Test
    public void testConfigCanBeSetOnPolicy() throws Exception {
        MyPolicy policy = new MyPolicy();
        policy.setConfig(MyPolicy.STR_KEY, "aval");
        policy.setConfig(MyPolicy.INT_KEY, 2);
        app.addPolicy(policy);
        
        assertEquals(policy.getConfig(MyPolicy.STR_KEY), "aval");
        assertEquals(policy.getConfig(MyPolicy.INT_KEY), (Integer)2);
    }
    
    @Test
    public void testConfigSetterOverridesConstructorValue() throws Exception {
        MyPolicy policy = new MyPolicy(MutableMap.builder()
                .put(MyPolicy.STR_KEY, "aval")
                .build());
        policy.setConfig(MyPolicy.STR_KEY, "diffval");
        app.addPolicy(policy);
        
        assertEquals(policy.getConfig(MyPolicy.STR_KEY), "diffval");
    }

    @Test
    public void testConfigCannotBeSetAfterApplicationIsStarted() throws Exception {
        MyPolicy policy = new MyPolicy(MutableMap.builder()
                .put(MyPolicy.STR_KEY, "origval")
                .build());
        app.addPolicy(policy);
        
        try {
            policy.setConfig(MyPolicy.STR_KEY,"newval");
            fail();
        } catch (UnsupportedOperationException e) {
            // success
        }
        
        assertEquals(policy.getConfig(MyPolicy.STR_KEY), "origval");
    }
    
    @Test
    public void testConfigReturnsDefaultValueIfNotSet() throws Exception {
        MyPolicy policy = new MyPolicy();
        app.addPolicy(policy);
        
        assertEquals(policy.getConfig(MyPolicy.STR_KEY_WITH_DEFAULT), "str key default");
    }
    
    // FIXME Should we support this now?
    @Test(enabled=false)
    public void testGetFutureConfigWhenReady() throws Exception {
        MyPolicy policy = new MyPolicy(MutableMap.builder()
                .put(TestEntity.CONF_NAME, DependentConfiguration.whenDone(Callables.returning("aval")))
                .build());
        app.addPolicy(policy);
        
        assertEquals(policy.getConfig(TestEntity.CONF_NAME), "aval");
    }
    
    // FIXME Should we support this now?
    @Test(enabled=false)
    public void testGetFutureConfigBlocksUntilReady() throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        MyPolicy policy = new MyPolicy(MutableMap.builder()
                .put(TestEntity.CONF_NAME, DependentConfiguration.whenDone(new Callable<String>() {
                        public String call() {
                            try {
                                latch.await(); return "aval";
                            } catch (InterruptedException e) {
                                throw Exceptions.propagate(e);
                            }
                        }}))
                .build());
        app.addPolicy(policy);
        
        Thread t = new Thread(new Runnable() {
                public void run() {
                    try {
                        Thread.sleep(10+EARLY_RETURN_GRACE); latch.countDown();
                    } catch (InterruptedException e) {
                        throw Exceptions.propagate(e);
                    }
                }});
        try {
            long starttime = System.currentTimeMillis();
            t.start();
            assertEquals(policy.getConfig(TestEntity.CONF_NAME), "aval");
            long endtime = System.currentTimeMillis();
            
            assertTrue((endtime - starttime) >= 10, "starttime="+starttime+"; endtime="+endtime);
            
        } finally {
            t.interrupt();
        }
    }
}
