package brooklyn.entity.hello;

import static org.testng.Assert.*
import brooklyn.config.ConfigKey
import brooklyn.entity.Effector
import brooklyn.entity.Entity
import brooklyn.entity.basic.AbstractGroupImpl
import brooklyn.entity.basic.Attributes;
import brooklyn.entity.basic.ConfigKeys;
import brooklyn.entity.basic.Description;
import brooklyn.entity.basic.MethodEffector
import brooklyn.entity.basic.NamedParameter
import brooklyn.event.Sensor
import brooklyn.event.basic.BasicAttributeSensor
import brooklyn.event.basic.BasicConfigKey
import brooklyn.event.basic.BasicSensor


public class HelloEntity extends AbstractGroupImpl {
    public HelloEntity() { super() }
    public HelloEntity(Entity owner) { super(owner) }
    public HelloEntity(Map flags) { super(flags) }
    public HelloEntity(Map flags, Entity owner) { super(flags, owner) }

    /** records name of the person represented by this entity */
    public static ConfigKey<String> MY_NAME = ConfigKeys.newConfigKey("my.name");
    
    /** this "person"'s favourite name */
    public static Sensor<String> FAVOURITE_NAME = Attributes.newAttributeSensor("my.favourite.name");
    
    /** records age (in years) of the person represented by this entity */
    public static Sensor<Integer> AGE = Attributes.newAttributeSensor("my.age");
    
    /** emits a "birthday" event whenever age is changed (tests non-attribute events) */    
    public static Sensor<Void> ITS_MY_BIRTHDAY = Attributes.newSensor("my.birthday");
    
    /**  */
    public static Effector<Void> SET_AGE = new MethodEffector<String>(HelloEntity.class, "setAge");
    
    @Description("allows setting the age")
    public void setAge(@NamedParameter("age") Integer age) {
        setAttribute(AGE, age);
        emit(ITS_MY_BIRTHDAY, null);
    }
}
