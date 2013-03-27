package brooklyn.extras.whirr.core;

import org.apache.whirr.Cluster;

import brooklyn.config.ConfigKey;
import brooklyn.entity.basic.AbstractGroup;
import brooklyn.entity.basic.Attributes;
import brooklyn.entity.basic.ConfigKeys;
import brooklyn.entity.proxying.ImplementedBy;
import brooklyn.event.AttributeSensor;
import brooklyn.util.flags.SetFromFlag;

@ImplementedBy(WhirrInstanceImpl.class)
public interface WhirrInstance extends AbstractGroup {

    @SetFromFlag("role")
    ConfigKey<String> ROLE = ConfigKeys.newConfigKey("whirr.instance.role", "Apache Whirr instance role");

    @SetFromFlag("instance")
    ConfigKey<Cluster.Instance> INSTANCE = ConfigKeys.newConfigKey("whirr.instance.instance", "Apache Whirr instance Cluster.Instance");

    AttributeSensor<String> HOSTNAME = Attributes.HOSTNAME;

    String getRole();

}
