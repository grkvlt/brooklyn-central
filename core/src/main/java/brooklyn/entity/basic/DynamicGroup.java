package brooklyn.entity.basic;

import groovy.lang.Closure;
import brooklyn.config.ConfigKey;
import brooklyn.entity.Entity;
import brooklyn.entity.proxying.ImplementedBy;
import brooklyn.event.AttributeSensor;
import brooklyn.event.Sensor;
import brooklyn.event.SensorEvent;
import brooklyn.util.flags.SetFromFlag;

import com.google.common.base.Predicate;

@ImplementedBy(DynamicGroupImpl.class)
public interface DynamicGroup extends AbstractGroup {

    @SetFromFlag("entityFilter")
    ConfigKey<Predicate<? super Entity>> ENTITY_FILTER = ConfigKeys.newConfigKey("dynamicgroup.entityfilter", "Filter for which entities will automatically be in group", null);

    AttributeSensor<Boolean> RUNNING = Attributes.newAttributeSensor(
            "dynamicgroup.running", "Whether the entity is running, so will automatically update group membership");

    /**
     * Stops this group (but does not stop any of its members). De-activates the filter and unsubscribes to
     * entity-updates, so the membership of the group will not change.
     */
    void stop();

    /** Rescans <em>all</em> entities to determine whether they match the filter. */
    void rescanEntities();

    /** Sets {@link #ENTITY_FILTER}, overriding (and rescanning all) if already set. */
    void setEntityFilter(Predicate<? super Entity> filter);

    /** @see {@link #setEntityFilter(Predicate)} */
    void setEntityFilter(Closure<Boolean> filter);

    /**
     * Monitor an entity and/or sensor but with an additional filter.
     *
     * @see {@link #addSubscription(Entity, Sensor)}
     */
    <T> void addSubscription(Entity producer, Sensor<T> sensor, final Predicate<? super SensorEvent<? super T>> filter);

    /**
     * Indicates an entity and/or sensor this group should monitor.
     * <p>
     * If either is null it means "all". Note that subscriptions do not <em>restrict</em> what can be added,
     * they merely ensure prompt membership checking (via {@link #ENTITY_FILTER}) for those entities so
     * subscribed.
     */
    <T> void addSubscription(Entity producer, Sensor<T> sensor);

}
