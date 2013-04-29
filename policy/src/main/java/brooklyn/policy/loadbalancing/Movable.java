package brooklyn.policy.loadbalancing;

import brooklyn.config.ConfigKey;
import brooklyn.entity.Effector;
import brooklyn.entity.Entity;
import brooklyn.entity.basic.MethodEffector;
import brooklyn.entity.basic.NamedParameter;
import brooklyn.event.AttributeSensor;
import brooklyn.event.basic.BasicAttributeSensor;
import brooklyn.event.basic.BasicConfigKey;
import brooklyn.util.flags.SetFromFlag;

/**
 * Represents an item that can be migrated between balanceable containers.
 */
public interface Movable extends Entity {

    @SetFromFlag("immovable")
    ConfigKey<Boolean> IMMOVABLE = new BasicConfigKey<Boolean>(
            "movable.item.immovable", "Indicates whether this item instance is immovable, so cannot be moved by policies", false);

    AttributeSensor<BalanceableContainer<?>> CONTAINER = new BasicAttributeSensor<BalanceableContainer<?>>(
            "movable.item.container", "The container that this item is on");

    Effector<Void> MOVE = new MethodEffector<Void>(Movable.class, "move");

    String getContainerId();

    void move(@NamedParameter("destination") Entity destination);

}
