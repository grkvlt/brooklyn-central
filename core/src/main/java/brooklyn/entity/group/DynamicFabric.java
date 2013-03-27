package brooklyn.entity.group;

import java.util.Map;

import brooklyn.config.ConfigKey;
import brooklyn.entity.basic.AbstractGroup;
import brooklyn.entity.basic.Attributes;
import brooklyn.entity.basic.ConfigKeys;
import brooklyn.entity.basic.EntityFactory;
import brooklyn.entity.proxying.EntitySpec;
import brooklyn.entity.proxying.ImplementedBy;
import brooklyn.entity.trait.Startable;
import brooklyn.event.AttributeSensor;
import brooklyn.util.flags.SetFromFlag;

import com.google.common.collect.ImmutableMap;

/**
 * When a dynamic fabric is started, it starts an entity in each of its locations. 
 * This entity will be the parent of each of the started entities. 
 */
@ImplementedBy(DynamicFabricImpl.class)
public interface DynamicFabric extends AbstractGroup, Startable, Fabric {

    AttributeSensor<Integer> FABRIC_SIZE = Attributes.newAttributeSensor("fabric.size", "Fabric size");

    @SetFromFlag("memberSpec")
    ConfigKey<EntitySpec<?>> MEMBER_SPEC = ConfigKeys.newConfigKey("dynamiccfabric.memberspec", "entity spec for creating new cluster members", null);

    @SetFromFlag("factory")
    ConfigKey<EntityFactory<?>> FACTORY = ConfigKeys.newConfigKey("dynamicfabric.factory", "factory for creating new cluster members", null);

    @SetFromFlag("displayNamePrefix")
    ConfigKey<String> DISPLAY_NAME_PREFIX = ConfigKeys.newConfigKey("dynamicfabric.displayNamePrefix", "Display name prefix, for created children", null);

    @SetFromFlag("displayNameSuffix")
    ConfigKey<String> DISPLAY_NAME_SUFFIX = ConfigKeys.newConfigKey("dynamicfabric.displayNameSuffix", "Display name suffix, for created children", null);

    @SetFromFlag("customChildFlags")
    ConfigKey<? extends Map<?, ?>> CUSTOM_CHILD_FLAGS = ConfigKeys.newConfigKey("dynamicfabric.customChildFlags", "Additional flags to be passed to children when they are being created", ImmutableMap.of());

    void setMemberSpec(EntitySpec<?> memberSpec);

    void setFactory(EntityFactory<?> factory);

    Integer getFabricSize();

}
