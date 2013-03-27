package brooklyn.entity.group;

import brooklyn.config.ConfigKey;
import brooklyn.entity.Group;
import brooklyn.entity.basic.ConfigKeys;
import brooklyn.entity.trait.Resizable;
import brooklyn.entity.trait.Startable;
import brooklyn.util.flags.SetFromFlag;

/**
 * Intended to represent a group of homogeneous entities in a single location.
 */
public interface Cluster extends Group, Startable, Resizable {

    @SetFromFlag("initialSize")
    ConfigKey<Integer> INITIAL_SIZE = ConfigKeys.newConfigKey("cluster.initial.size", "Initial cluster size", 1);

}
