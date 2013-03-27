package brooklyn.extras.whirr.core;

import brooklyn.config.ConfigKey;
import brooklyn.entity.Entity;
import brooklyn.entity.basic.ConfigKeys;
import brooklyn.entity.proxying.ImplementedBy;
import brooklyn.util.flags.SetFromFlag;

@ImplementedBy(WhirrRoleImpl.class)
public interface WhirrRole extends Entity {

    @SetFromFlag("role")
    ConfigKey<String> ROLE = ConfigKeys.newConfigKey("whirr.instance.role", "Apache Whirr instance role");

    String getRole();
}
