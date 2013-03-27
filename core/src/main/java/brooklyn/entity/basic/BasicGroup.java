package brooklyn.entity.basic;

import brooklyn.config.ConfigKey;
import brooklyn.entity.proxying.ImplementedBy;
import brooklyn.util.flags.SetFromFlag;

@ImplementedBy(BasicGroupImpl.class)
public interface BasicGroup extends AbstractGroup {

    @SetFromFlag("childrenAsMembers")
    ConfigKey<Boolean> CHILDREN_AS_MEMBERS = ConfigKeys.newConfigKey(
            "brooklyn.BasicGroup.childrenAsMembers", "Whether children are automatically added as group members", false);

}
