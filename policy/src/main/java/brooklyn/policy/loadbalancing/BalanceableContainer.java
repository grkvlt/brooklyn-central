package brooklyn.policy.loadbalancing;

import java.util.Set;

import brooklyn.entity.Entity;
import brooklyn.event.Sensor;
import brooklyn.event.basic.BasicNotificationSensor;

/**
 * Contains worker items that can be moved between this container and others.
 * <p>
 * Used to effect load balancing. Membership of a balanceable container does not
 * imply a parent-child relationship in the Brooklyn management sense.
 *
 * @see {@link Movable}
 */
public interface BalanceableContainer<M extends Movable> extends Entity {

    Sensor<Entity> ITEM_ADDED = new BasicNotificationSensor<Entity>(
            Entity.class, "balanceablecontainer.item.added", "Movable item added to balanceable container");

    Sensor<Entity> ITEM_REMOVED = new BasicNotificationSensor<Entity>(
            Entity.class, "balanceablecontainer.item.removed", "Movable item removed from balanceable container");

    Set<M> getBalanceableItems();

}
