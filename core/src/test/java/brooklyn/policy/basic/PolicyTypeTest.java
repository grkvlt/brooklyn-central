package brooklyn.policy.basic;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import brooklyn.config.ConfigKey;
import brooklyn.entity.basic.ConfigKeys;
import brooklyn.policy.PolicyType;

import com.google.common.collect.ImmutableSet;

public class PolicyTypeTest {
    private MyPolicy policy;
    
    @BeforeMethod(alwaysRun=true)
    public void setUpTestEntity() throws Exception{
        policy = new MyPolicy();
    }

    @AfterMethod(alwaysRun=true)
    public void tearDown() throws Exception {
    }
    
    @Test
    public void testGetConfig() throws Exception {
        PolicyType policyType = policy.getPolicyType();
        assertEquals(policyType.getConfigKeys(), ImmutableSet.of(MyPolicy.CONF1, MyPolicy.CONF2));
        assertEquals(policyType.getName(), MyPolicy.class.getCanonicalName());
        assertEquals(policyType.getConfigKey("test.conf1"), MyPolicy.CONF1);
        assertEquals(policyType.getConfigKey("test.conf2"), MyPolicy.CONF2);
    }
    
    public static class MyPolicy extends AbstractPolicy {
        public static final ConfigKey<String> CONF1 = ConfigKeys.newConfigKey("test.conf1", "my descr, conf1", "defaultval1");
        public static final ConfigKey<Integer> CONF2 = ConfigKeys.newConfigKey("test.conf2", "my descr, conf2", 2);
    }
}
