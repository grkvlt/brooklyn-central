package brooklyn.extras.whirr.core;

import brooklyn.config.ConfigKey;
import brooklyn.entity.Entity;
import brooklyn.entity.proxying.ImplementedBy;
import brooklyn.event.basic.BasicConfigKey;
import brooklyn.util.flags.SetFromFlag;

@ImplementedBy(WhirrRoleImpl.class)
public interface WhirrRole extends Entity {

    @SetFromFlag("role")
    ConfigKey<String> ROLE = new BasicConfigKey<String>("whirr.instance.role", "Apache Whirr instance role");

    String getRole();
}
