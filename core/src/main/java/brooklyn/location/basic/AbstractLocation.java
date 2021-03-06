package brooklyn.location.basic;

import static brooklyn.util.GroovyJavaMethods.elvis;
import static brooklyn.util.JavaGroovyEquivalents.groovyTruth;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Closeable;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import brooklyn.config.ConfigKey;
import brooklyn.config.ConfigKey.HasConfigKey;
import brooklyn.entity.basic.EntityDynamicType;
import brooklyn.entity.proxying.InternalLocationFactory;
import brooklyn.entity.rebind.BasicLocationRebindSupport;
import brooklyn.entity.rebind.RebindManagerImpl;
import brooklyn.entity.rebind.RebindSupport;
import brooklyn.entity.trait.Configurable;
import brooklyn.event.basic.BasicConfigKey;
import brooklyn.internal.storage.BrooklynStorage;
import brooklyn.internal.storage.Reference;
import brooklyn.internal.storage.impl.BasicReference;
import brooklyn.location.Location;
import brooklyn.location.LocationSpec;
import brooklyn.location.geo.HasHostGeoInfo;
import brooklyn.location.geo.HostGeoInfo;
import brooklyn.management.ManagementContext;
import brooklyn.management.internal.LocalLocationManager;
import brooklyn.management.internal.ManagementContextInternal;
import brooklyn.mementos.LocationMemento;
import brooklyn.util.collections.SetFromLiveMap;
import brooklyn.util.config.ConfigBag;
import brooklyn.util.flags.FlagUtils;
import brooklyn.util.flags.SetFromFlag;
import brooklyn.util.flags.TypeCoercions;
import brooklyn.util.stream.Streams;
import brooklyn.util.text.Identifiers;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * A basic implementation of the {@link Location} interface.
 *
 * This provides an implementation which works according to the requirements of
 * the interface documentation, and is ready to be extended to make more specialized locations.
 * 
 * Override {@link #configure(Map)} to add special initialization logic.
 */
public abstract class AbstractLocation implements LocationInternal, HasHostGeoInfo, Configurable {
    
    /** @deprecated since 0.7.0 shouldn't be public */
    @Deprecated
    public static final Logger LOG = LoggerFactory.getLogger(AbstractLocation.class);

    public static final ConfigKey<Location> PARENT_LOCATION = new BasicConfigKey<Location>(Location.class, "parentLocation");
    
    private final AtomicBoolean configured = new AtomicBoolean(false);
    
    @SetFromFlag(value="id")
    private String id = Identifiers.makeRandomId(8);

    private Reference<Long> creationTimeUtc = new BasicReference<Long>(System.currentTimeMillis());
    
    // _not_ set from flag; configured explicitly in configure, because we also need to update the parent's list of children
    private Reference<Location> parent = new BasicReference<Location>();
    
    // NB: all accesses should be synchronized
    private Set<Location> children = Sets.newLinkedHashSet();

    private Reference<String> name = new BasicReference<String>();
    private boolean displayNameAutoGenerated = true;

    private Reference<HostGeoInfo> hostGeoInfo = new BasicReference<HostGeoInfo>();

    private ConfigBag configBag = new ConfigBag();

    private volatile ManagementContext managementContext;
    private volatile boolean managed;

    private boolean _legacyConstruction;

    private boolean inConstruction;

    private final Map<Class<?>, Object> extensions = Maps.newConcurrentMap();
    
    private final EntityDynamicType entityType;
    
    /**
     * Construct a new instance of an AbstractLocation.
     */
    public AbstractLocation() {
        this(Maps.newLinkedHashMap());
    }
    
    /**
     * Construct a new instance of an AbstractLocation.
     *
     * The properties map recognizes the following keys:
     * <ul>
     * <li>name - a name for the location
     * <li>parentLocation - the parent {@link Location}
     * </ul>
     * 
     * Other common properties (retrieved via get/findLocationProperty) include:
     * <ul>
     * <li>latitude
     * <li>longitude
     * <li>displayName
     * <li>iso3166 - list of iso3166-2 code strings
     * <li>timeZone
     * <li>abbreviatedName
     * </ul>
     */
    public AbstractLocation(Map properties) {
        inConstruction = true;
        _legacyConstruction = !InternalLocationFactory.FactoryConstructionTracker.isConstructing();
        if (!_legacyConstruction && properties!=null && !properties.isEmpty()) {
            LOG.warn("Forcing use of deprecated old-style location construction for "+getClass().getName()+" because properties were specified ("+properties+")");
            _legacyConstruction = true;
        }
        
        // When one calls getConfig(key), we want to use the default value specified on *this* location
        // if it overrides the default config. The easiest way to look up all our config keys is to 
        // reuse the code for Entity (and this will become identical when locations become first-class
        // entities). See {@link #getConfig(ConfigKey)}
        entityType = new EntityDynamicType((Class)getClass());
        
        if (_legacyConstruction) {
            LOG.warn("Deprecated use of old-style location construction for "+getClass().getName()+"; instead use LocationManager().createLocation(spec)");
            if (LOG.isDebugEnabled())
                LOG.debug("Source of use of old-style location construction", new Throwable("Source of use of old-style location construction"));
            
            configure(properties);
            
            boolean deferConstructionChecks = (properties.containsKey("deferConstructionChecks") && TypeCoercions.coerce(properties.get("deferConstructionChecks"), Boolean.class));
            if (!deferConstructionChecks) {
                FlagUtils.checkRequiredFields(this);
            }
        }
        
        inConstruction = false;
    }

    protected void assertNotYetManaged() {
        if (!inConstruction && (managementContext != null && managementContext.getLocationManager().isManaged(this))) {
            LOG.warn("Configuration being made to {} after deployment; may not be supported in future versions", this);
        }
        //throw new IllegalStateException("Cannot set configuration "+key+" on active location "+this)
    }

    public void setManagementContext(ManagementContextInternal managementContext) {
        this.managementContext = managementContext;
        if (displayNameAutoGenerated && id != null) name.set(getClass().getSimpleName()+":"+id.substring(0, Math.min(id.length(),4)));

        Location oldParent = parent.get();
        Set<Location> oldChildren = children;
        Map<String, Object> oldConfig = configBag.getAllConfig();
        Long oldCreationTimeUtc = creationTimeUtc.get();
        String oldDisplayName = name.get();
        HostGeoInfo oldHostGeoInfo = hostGeoInfo.get();
        
        parent = managementContext.getStorage().getReference(id+"-parent");
        children = SetFromLiveMap.create(managementContext.getStorage().<Location,Boolean>getMap(id+"-children"));
        creationTimeUtc = managementContext.getStorage().getReference(id+"-creationTime");
        hostGeoInfo = managementContext.getStorage().getReference(id+"-hostGeoInfo");
        name = managementContext.getStorage().getReference(id+"-displayName");
        
        // Only override stored defaults if we have actual values. We might be in setManagementContext
        // because we are reconstituting an existing entity in a new brooklyn management-node (in which
        // case believe what is already in the storage), or we might be in the middle of creating a new 
        // entity. Normally for a new entity (using EntitySpec creation approach), this will get called
        // before setting the parent etc. However, for backwards compatibility we still support some
        // things calling the entity's constructor directly.
        if (oldParent != null) parent.set(oldParent);
        if (oldChildren.size() > 0) children.addAll(oldChildren);
        if (creationTimeUtc.isNull()) creationTimeUtc.set(oldCreationTimeUtc);
        if (hostGeoInfo.isNull()) hostGeoInfo.set(oldHostGeoInfo);
        if (name.isNull()) {
            name.set(oldDisplayName);
        } else {
            displayNameAutoGenerated = false;
        }
        
        configBag = ConfigBag.newLiveInstance(managementContext.getStorage().<String,Object>getMap(id+"-config"));
        if (oldConfig.size() > 0) {
            configBag.putAll(oldConfig);
        }
    }

    @Override
    public ManagementContext getManagementContext() {
        return managementContext;
    }
    
    /**
     * Will set fields from flags. The unused configuration can be found via the 
     * {@linkplain ConfigBag#getUnusedConfig()}.
     * This can be overridden for custom initialization but note the following. 
     * <p>
     * For new-style locations (i.e. not calling constructor directly, this will
     * be invoked automatically by brooklyn-core post-construction).
     * <p>
     * For legacy location use, this will be invoked by the constructor in this class.
     * Therefore if over-riding you must *not* rely on field initializers because they 
     * may not run until *after* this method (this method is invoked by the constructor 
     * in this class, so initializers in subclasses will not have run when this overridden 
     * method is invoked.) If you require fields to be initialized you must do that in 
     * this method with a guard (as in FixedListMachineProvisioningLocation).
     */ 
    public void configure(Map properties) {
        assertNotYetManaged();
        
        boolean firstTime = !configured.getAndSet(true);
            
        configBag.putAll(properties);
        
        if (properties.containsKey(PARENT_LOCATION.getName())) {
            // need to ensure parent's list of children is also updated
            setParent(configBag.get(PARENT_LOCATION));
            
            // don't include parentLocation in configBag, as breaks rebind
            configBag.remove(PARENT_LOCATION);
        }

        // NB: flag-setting done here must also be done in BasicLocationRebindSupport 
        FlagUtils.setFieldsFromFlagsWithBag(this, properties, configBag, firstTime);
        FlagUtils.setAllConfigKeys(this, configBag, false);

        if (properties.containsKey("displayName")) {
            name.set((String) removeIfPossible(properties, "displayName"));
            displayNameAutoGenerated = false;
        } else if (properties.containsKey("name")) {
            name.set((String) removeIfPossible(properties, "name"));
            displayNameAutoGenerated = false;
        } else if (isLegacyConstruction()) {
            name.set(getClass().getSimpleName()+":"+id.substring(0, Math.min(id.length(),4)));
            displayNameAutoGenerated = true;
        }

        // TODO Explicitly dealing with iso3166 here because want custom splitter rule comma-separated string.
        // Is there a better way to do it (e.g. more similar to latitude, where configKey+TypeCoercion is enough)?
        if (groovyTruth(properties.get("iso3166"))) {
            Object rawCodes = removeIfPossible(properties, "iso3166");
            Set<String> codes;
            if (rawCodes instanceof CharSequence) {
                codes = ImmutableSet.copyOf(Splitter.on(",").trimResults().split((CharSequence)rawCodes));
            } else {
                codes = TypeCoercions.coerce(rawCodes, Set.class);
            }
            configBag.put(LocationConfigKeys.ISO_3166, codes);
        }
    }

    // TODO ensure no callers rely on 'remove' semantics, and don't remove;
    // or perhaps better use a config bag so we know what is used v unused
    private static Object removeIfPossible(Map map, Object key) {
        try {
            return map.remove(key);
        } catch (Exception e) {
            return map.get(key);
        }
    }
    
    /**
     * Called by framework (in new-style locations) after configuring, setting parent, etc,
     * but before a reference to this location is shared with other locations.
     * 
     * To preserve backwards compatibility for if the location is constructed directly, one
     * can call the code below, but that means it will be called after references to this 
     * location have been shared with other entities.
     * <pre>
     * {@code
     * if (isLegacyConstruction()) {
     *     init();
     * }
     * }
     * </pre>
     */
    public void init() {
        // no-op
    }

    /**
     * Called by framework (in new-style locations) on rebind, after configuring, setting parent, etc.
     * Note that a future change to Brooklyn is that {@link #init()} will not be called when rebinding.
     */
    public void rebind() {
        // no-op
    }

    protected boolean isRebinding() {
        return RebindManagerImpl.RebindTracker.isRebinding();
    }
    
    public boolean isManaged() {
        return managementContext != null && managed;
    }

    public void onManagementStarted() {
        if (displayNameAutoGenerated) name.set(getClass().getSimpleName()+":"+id.substring(0, Math.min(id.length(),4)));
        this.managed = true;
    }
    
    public void onManagementStopped() {
        this.managed = false;
        if (managementContext.isRunning()) {
            BrooklynStorage storage = ((ManagementContextInternal)managementContext).getStorage();
            storage.remove(id+"-parent");
            storage.remove(id+"-children");
            storage.remove(id+"-creationTime");
            storage.remove(id+"-hostGeoInfo");
            storage.remove(id+"-displayName");
            storage.remove(id+"-config");
        }
    }
    
    protected boolean isLegacyConstruction() {
        return _legacyConstruction;
    }
    
    @Override
    public String getId() {
        return id;
    }
    
    @Override
    public String getDisplayName() {
        return name.get();
    }
    
    protected boolean isDisplayNameAutoGenerated() {
        return displayNameAutoGenerated;
    }
    
    @Override
    public Location getParent() {
        return parent.get();
    }
    
    @Override
    public Collection<Location> getChildren() {
        synchronized (children) {
            return ImmutableList.copyOf(children);
        }
    }

    @Override
    public void setParent(Location newParent) {
        if (newParent == this) {
            throw new IllegalArgumentException("Location cannot be its own parent: "+this);
        }
        if (newParent == parent.get()) {
            return; // no-op; already have desired parent
        }
        
        // TODO Should we support a location changing parent? The resulting unmanage/manage might cause problems.
        if (parent.get() != null) {
            Location oldParent = parent.get();
            parent.set(null);
            ((AbstractLocation)oldParent).removeChild(this); // FIXME Nasty cast
        }
        if (newParent != null) {
            parent.set(newParent);
            ((AbstractLocation)parent.get()).addChild(this); // FIXME Nasty cast
        }
    }

    @Override
    public <T> T getConfig(HasConfigKey<T> key) {
        return getConfig(key.getConfigKey());
    }

    @Override
    public <T> T getConfig(ConfigKey<T> key) {
        if (hasConfig(key, false)) return getLocalConfigBag().get(key);
        if (getParent()!=null) return getParent().getConfig(key);
        
        // In case this entity class has overridden the given key (e.g. to set default), then retrieve this entity's key
        // TODO when locations become entities, the duplication of this compared to EntityConfigMap.getConfig will disappear.
        ConfigKey<T> ownKey = (ConfigKey<T>) elvis(entityType.getConfigKey(key.getName()), key);

        return ownKey.getDefaultValue();
    }

    @Override
    public boolean hasConfig(ConfigKey<?> key, boolean includeInherited) {
        boolean locally = getLocalConfigBag().containsKey(key);
        if (locally) return true;
        if (!includeInherited) return false;
        if (getParent()!=null) return getParent().hasConfig(key, true);
        return false;
    }

    @Override
    public Map<String,Object> getAllConfig(boolean includeInherited) {
        ConfigBag bag = (includeInherited ? getAllConfigBag() : getLocalConfigBag());
        return bag.getAllConfig();
    }
    
    @Override
    public ConfigBag getAllConfigBag() {
        ConfigBag result = ConfigBag.newInstanceExtending(configBag, ImmutableMap.of());
        Location p = getParent();
        if (p!=null) result.putIfAbsent(((LocationInternal)p).getAllConfigBag().getAllConfig());
        return result;
    }
    
    @Override
    public ConfigBag getLocalConfigBag() {
        return configBag;
    }

    /** 
     * @deprecated since 0.7; use {@link #getLocalConfigBag()}
     * @since 0.6
     */
    public ConfigBag getRawLocalConfigBag() {
        return getLocalConfigBag();
    }
    
    @Override
    public <T> T setConfig(ConfigKey<T> key, T value) {
        return configBag.put(key, value);
    }

    /** @since 0.6.0 (?) - use getDisplayName */
    public void setName(String newName) {
        setDisplayName(newName);
        displayNameAutoGenerated = false;
    }

    public void setDisplayName(String newName) {
        name.set(newName);
        displayNameAutoGenerated = false;
    }

    @Override
    public boolean equals(Object o) {
        if (! (o instanceof Location)) {
            return false;
        }

        Location l = (Location) o;
		return getId().equals(l.getId());
    }

    @Override
    public int hashCode() {
        return getId().hashCode();
    }

    @Override
    public boolean containsLocation(Location potentialDescendent) {
        Location loc = potentialDescendent;
        while (loc != null) {
            if (this == loc) return true;
            loc = loc.getParent();
        }
        return false;
    }

    protected <T extends Location> T addChild(LocationSpec<T> spec) {
        T child = managementContext.getLocationManager().createLocation(spec);
        addChild(child);
        return child;
    }
    
    public void addChild(Location child) {
    	// Previously, setParent delegated to addChildLocation and we sometimes ended up with
    	// duplicate entries here. Instead this now uses a similar scheme to 
    	// AbstractLocation.setParent/addChild (with any weaknesses for distribution that such a 
    	// scheme might have...).
    	// 
    	// We continue to use a list to allow identical-looking locations, but they must be different 
    	// instances.
    	
        synchronized (children) {
            for (Location contender : children) {
                if (contender == child) {
                    // don't re-add; no-op
                    return;
                }
            }

            children.add(child);
        }
        
        if (isManaged()) {
            Locations.manage(child, managementContext);
        } else if (managementContext != null) {
            if (((LocalLocationManager)managementContext.getLocationManager()).getLocationEvenIfPreManaged(child.getId()) == null) {
                ((ManagementContextInternal)managementContext).prePreManage(child);
            }
        }

        children.add(child);
        child.setParent(this);
    }
    
    protected boolean removeChild(Location child) {
        boolean removed;
        synchronized (children) {
            removed = children.remove(child);
        }
        if (removed) {
            if (child instanceof Closeable) {
                Streams.closeQuietly((Closeable)child);
            }
            child.setParent(null);
            
            if (isManaged()) {
                managementContext.getLocationManager().unmanage(child);
            }
        }
        return removed;
    }

    /** Default String representation is simplified name of class, together with selected fields. */
    @Override
    public String toString() {
        return string().toString();
    }
    
    @Override
    public String toVerboseString() {
        return toString();
    }

    /** override this, adding to the returned value, to supply additional fields to include in the toString */
    protected ToStringHelper string() {
        return Objects.toStringHelper(getClass()).add("id", id).add("name", name);
    }
    
    @Override
    public HostGeoInfo getHostGeoInfo() { return hostGeoInfo.get(); }
    
    public void setHostGeoInfo(HostGeoInfo hostGeoInfo) {
        if (hostGeoInfo!=null) { 
            this.hostGeoInfo.set(hostGeoInfo);
            setConfig(LocationConfigKeys.LATITUDE, hostGeoInfo.latitude); 
            setConfig(LocationConfigKeys.LONGITUDE, hostGeoInfo.longitude); 
        } 
    }

    @Override
    public RebindSupport<LocationMemento> getRebindSupport() {
        return new BasicLocationRebindSupport(this);
    }
    
    @Override
    public boolean hasExtension(Class<?> extensionType) {
        return extensions.containsKey(checkNotNull(extensionType, "extensionType"));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getExtension(Class<T> extensionType) {
        Object extension = extensions.get(checkNotNull(extensionType, "extensionType"));
        if (extension == null) {
            throw new IllegalArgumentException("No extension of type "+extensionType+" registered for location "+this);
        }
        return (T) extension;
    }
    
    @Override
    public <T> void addExtension(Class<T> extensionType, T extension) {
        checkNotNull(extensionType, "extensionType");
        checkNotNull(extension, "extension");
        checkArgument(extensionType.isInstance(extension), "extension %s does not implement %s", extension, extensionType);
        extensions.put(extensionType, extension);
    }
    @Override
    public Map<String, String> toMetadataRecord() {
        ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();
        builder.put("id", getId());
        if (getDisplayName() != null) builder.put("displayName", getDisplayName());
        return builder.build();
    }
}
