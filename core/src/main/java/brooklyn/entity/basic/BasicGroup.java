package brooklyn.entity.basic;

import brooklyn.config.ConfigKey;
import brooklyn.entity.proxying.ImplementedBy;
import brooklyn.event.basic.BasicConfigKey;
import brooklyn.util.flags.SetFromFlag;

@ImplementedBy(BasicGroupImpl.class)
public interface BasicGroup extends AbstractGroup {

    @SetFromFlag("childrenAsMembers")
    ConfigKey<Boolean> CHILDREN_AS_MEMBERS = new BasicConfigKey<Boolean>(
            "brooklyn.BasicGroup.childrenAsMembers", "Whether children are automatically added as group members", false);

}
