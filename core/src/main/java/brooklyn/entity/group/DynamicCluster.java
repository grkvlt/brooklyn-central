package brooklyn.entity.group;

import groovy.lang.Closure;

import java.util.Collection;
import java.util.NoSuchElementException;

import brooklyn.config.ConfigKey;
import brooklyn.entity.Effector;
import brooklyn.entity.Entity;
import brooklyn.entity.Group;
import brooklyn.entity.basic.AbstractGroup;
import brooklyn.entity.basic.Attributes;
import brooklyn.entity.basic.Description;
import brooklyn.entity.basic.EntityFactory;
import brooklyn.entity.basic.Lifecycle;
import brooklyn.entity.basic.MethodEffector;
import brooklyn.entity.basic.NamedParameter;
import brooklyn.entity.proxying.EntitySpec;
import brooklyn.entity.proxying.ImplementedBy;
import brooklyn.event.AttributeSensor;
import brooklyn.event.Sensor;
import brooklyn.event.basic.BasicAttributeSensor;
import brooklyn.event.basic.BasicConfigKey;
import brooklyn.event.basic.BasicNotificationSensor;
import brooklyn.util.flags.SetFromFlag;

import com.google.common.base.Function;

/**
 * A cluster of entities that can dynamically increase or decrease the number of entities.
 */
@ImplementedBy(DynamicClusterImpl.class)
@SuppressWarnings("serial")
public interface DynamicCluster extends AbstractGroup, Cluster {

    Effector<String> REPLACE_MEMBER = new MethodEffector<String>(DynamicCluster.class, "replaceMember");

    @SetFromFlag("quarantineFailedEntities")
    ConfigKey<Boolean> QUARANTINE_FAILED_ENTITIES = new BasicConfigKey<Boolean>("dynamiccluster.quarantineFailedEntities", "Whether to guarantine entities that fail to start, or to try to clean them up", false);

    AttributeSensor<Lifecycle> SERVICE_STATE = Attributes.SERVICE_STATE;

    Sensor<Entity> ENTITY_QUARANTINED = new BasicNotificationSensor<Entity>("dynamiccluster.entityQuarantined", "Entity failed to start, and has been quarantined");

    AttributeSensor<Group> QUARANTINE_GROUP = new BasicAttributeSensor<Group>("dynamiccluster.quarantineGroup", "Group of quarantined entities that failed to start");

    @SetFromFlag("memberSpec")
    ConfigKey<EntitySpec<?>> MEMBER_SPEC = new BasicConfigKey<EntitySpec<?>>("dynamiccluster.memberspec", "entity spec for creating new cluster members", null);

    @SetFromFlag("factory")
    ConfigKey<EntityFactory<?>> FACTORY = new BasicConfigKey<EntityFactory<?>>("dynamiccluster.factory", "factory for creating new cluster members", null);

    @SetFromFlag("removalStrategy")
    ConfigKey<Function<Collection<? extends Entity>, Entity>> REMOVAL_STRATEGY = new BasicConfigKey<Function<Collection<? extends Entity>, Entity>>("dynamiccluster.removalstrategy", "strategy for deciding what to remove when down-sizing", null);

    /**
     * Replaces the entity with the given ID.
     *
     * @param memberId
     * @throws NoSuchElementException If entity cannot be resolved, or it is not a member 
     */
    @Description("Replaces the entity with the given ID, if it is a member; first adds a new member, then removes this one. "+
            "Returns id of the new entity; or throws exception if couldn't be replaced.")
    String replaceMember(@NamedParameter("memberId") @Description("The entity id of a member to be replaced") String memberId);

    void setRemovalStrategy(Function<Collection<? extends Entity>, Entity> val);

    void setRemovalStrategy(Closure val);

    void setMemberSpec(EntitySpec<?> memberSpec);

    void setFactory(EntityFactory<?> factory);
}
