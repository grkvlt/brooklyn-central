package brooklyn.test.entity;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.testng.collections.Lists;

import brooklyn.config.ConfigKey;
import brooklyn.entity.Effector;
import brooklyn.entity.Entity;
import brooklyn.entity.basic.Attributes;
import brooklyn.entity.basic.ConfigKeys;
import brooklyn.entity.basic.Description;
import brooklyn.entity.basic.EntityInternal;
import brooklyn.entity.basic.EntityLocal;
import brooklyn.entity.basic.Lifecycle;
import brooklyn.entity.basic.MethodEffector;
import brooklyn.entity.basic.NamedParameter;
import brooklyn.entity.proxying.BasicEntitySpec;
import brooklyn.entity.proxying.EntitySpec;
import brooklyn.entity.proxying.ImplementedBy;
import brooklyn.entity.trait.Startable;
import brooklyn.event.AttributeSensor;
import brooklyn.event.Sensor;
import brooklyn.event.basic.ListConfigKey;
import brooklyn.event.basic.MapConfigKey;
import brooklyn.util.MutableMap;
import brooklyn.util.flags.SetFromFlag;

/**
 * Mock entity for testing.
 */
//FIXME Don't want to extend EntityLocal, but tests call things like entity.subscribe(); how to deal with that elegantly?
@ImplementedBy(TestEntityImpl.class)
public interface TestEntity extends Entity, Startable, EntityLocal, EntityInternal {

    public static class Spec<T extends TestEntity, S extends Spec<T,S>> extends BasicEntitySpec<T,S> {

        private static class ConcreteSpec extends Spec<TestEntity, ConcreteSpec> {
            ConcreteSpec() {
                super(TestEntity.class);
            }
        }

        public static Spec<TestEntity, ?> newInstance() {
            return new ConcreteSpec();
        }

        protected Spec(Class<T> type) {
            super(type);
        }
    }

    @SetFromFlag("confName")
    public static final ConfigKey<String> CONF_NAME = ConfigKeys.newConfigKey("test.confName", "Configuration key, my name", "defaultval");
    public static final ConfigKey<Map<?, ?>> CONF_MAP_PLAIN = ConfigKeys.newConfigKey("test.confMapPlain", "Configuration key that's a plain map", MutableMap.of());
    public static final ConfigKey<List<?>> CONF_LIST_PLAIN = ConfigKeys.newConfigKey("test.confListPlain", "Configuration key that's a plain list", Lists.newArrayList());
    public static final MapConfigKey<String> CONF_MAP_THING = ConfigKeys.newMapConfigKey("test.confMapThing", "Configuration key that's a map thing");
    public static final ListConfigKey<String> CONF_LIST_THING = ConfigKeys.newListConfigKey("test.confListThing", "Configuration key that's a list thing");
    
    public static final AttributeSensor<Integer> SEQUENCE = Attributes.newAttributeSensor("test.sequence", "Test Sequence");
    public static final AttributeSensor<String> NAME = Attributes.newAttributeSensor("test.name", "Test name");
    public static final Sensor<Integer> MY_NOTIF = Attributes.newNotificationSensor("test.myNotif", "Test notification");
    
    public static final AttributeSensor<Lifecycle> SERVICE_STATE = Attributes.SERVICE_STATE;
    
    public static final Effector<Void> MY_EFFECTOR = new MethodEffector<Void>(TestEntity.class, "myEffector");
    public static final Effector<Object> IDENTITY_EFFECTOR = new MethodEffector<Object>(TestEntity.class, "identityEffector");
    
    public boolean isLegacyConstruction();
    
    @Description("an example of a no-arg effector")
    public void myEffector();
    
    @Description("returns the arg passed in")
    public Object identityEffector(@NamedParameter("arg") @Description("val to return") Object arg);
    
    public AtomicInteger getCounter();
    
    public int getCount();
    
    public Map getConstructorProperties();

    public int getSequenceValue();

    public void setSequenceValue(int value);
    
    public <T extends Entity> T createChild(EntitySpec<T> spec);

    public <T extends Entity> T createAndManageChild(EntitySpec<T> spec);
}
