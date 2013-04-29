package brooklyn.extras.whirr.core;

import org.apache.whirr.Cluster;

import brooklyn.config.ConfigKey;
import brooklyn.entity.basic.AbstractGroup;
import brooklyn.entity.basic.Attributes;
import brooklyn.entity.proxying.ImplementedBy;
import brooklyn.event.AttributeSensor;
import brooklyn.event.basic.BasicConfigKey;
import brooklyn.util.flags.SetFromFlag;

@ImplementedBy(WhirrInstanceImpl.class)
public interface WhirrInstance extends AbstractGroup {

    @SetFromFlag("role")
    ConfigKey<String> ROLE = new BasicConfigKey<String>("whirr.instance.role", "Apache Whirr instance role");

    @SetFromFlag("instance")
    ConfigKey<Cluster.Instance> INSTANCE = new BasicConfigKey<Cluster.Instance>("whirr.instance.instance", "Apache Whirr instance Cluster.Instance");

    AttributeSensor<String> HOSTNAME = Attributes.HOSTNAME;

    String getRole();

}
