/*
 * Copyright 2012-2013 by Cloiudsoft Corp.
 */
package brooklyn.entity.monitoring.ganglia;

import java.util.Collection;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import brooklyn.entity.Entity;
import brooklyn.entity.Group;
import brooklyn.entity.basic.AbstractEntity;
import brooklyn.entity.basic.BasicGroup;
import brooklyn.entity.basic.DynamicGroup;
import brooklyn.entity.basic.Entities;
import brooklyn.entity.group.AbstractMembershipTrackingPolicy;
import brooklyn.entity.proxying.BasicEntitySpec;
import brooklyn.entity.trait.Startable;
import brooklyn.location.Location;
import brooklyn.location.MachineLocation;
import brooklyn.util.MutableMap;
import brooklyn.util.MutableSet;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;

/**
 * An implementation of {@link GangliaCluster}.
 */
public class GangliaConsoleImpl extends AbstractEntity implements GangliaCluster {

    /** serialVersionUID */
    private static final long serialVersionUID = -8719174966402620778L;

    private static final Logger log = LoggerFactory.getLogger(GangliaConsoleImpl.class);

    private Object[] mutex = new Object[0];
    private AbstractMembershipTrackingPolicy policy;

    private GangliaManager manager;
    private DynamicGroup monitoredEntities;
    private Group monitors;

    private Multimap<Location, Entity> entityLocations = HashMultimap.create();
    private Map<Location, GangliaMonitor> monitoredLocations = Maps.newHashMap();

    public GangliaConsoleImpl() {
        this(Maps.newHashMap(), null);
    }

    public GangliaConsoleImpl(Map<?, ?> flags) {
        this(flags, null);
    }

    public GangliaConsoleImpl(Entity owner) {
        this(Maps.newHashMap(), owner);
    }

    public GangliaConsoleImpl(Map<?, ?> flags, Entity owner) {
        super(flags, owner);
    }

    public void postConstruct() {
        manager = getEntityManager().createEntity(BasicEntitySpec.newInstance(GangliaManager.class).parent(this));
        Entities.manage(manager);

        Predicate<? super Entity> filter = Predicates.and(Predicates.not(Predicates.instanceOf(GangliaMonitor.class)),
                getConfig(ENTITY_FILTER));
        monitoredEntities = getEntityManager().createEntity(BasicEntitySpec.newInstance(DynamicGroup.class)
                .parent(this)
                .configure(DynamicGroup.ENTITY_FILTER, filter));
        Entities.manage(monitoredEntities);

        monitors = getEntityManager().createEntity(BasicEntitySpec.newInstance(BasicGroup.class)
                .parent(this)
                .configure(BasicGroup.CHILDREN_AS_MEMBERS, true));
        Entities.manage(monitors);
    }

    public GangliaManager getManager() {
        return manager;
    }

    public DynamicGroup getMonitoredEntities() {
        return monitoredEntities;
    }

    public Group getMonitors() {
        return monitors;
    }

    public String getClusterName() {
        return getAttribute(CLUSTER_NAME);
    }

    @Override
    public void start(Collection<? extends Location> locations) {
        manager.start(locations);

        policy = new AbstractMembershipTrackingPolicy(MutableMap.of("name", "Ganglia Cluster Tracker")) {
            @Override
            protected void onEntityChange(Entity member) { } // Don't care
            @Override
            protected void onEntityAdded(Entity member) {
                if (log.isDebugEnabled()) log.debug("Added {} to monitored cluster {}", member, getClusterName());
                added(member);
            }
            @Override
            protected void onEntityRemoved(Entity member) {
                if (log.isDebugEnabled()) log.debug("Removed {} from monitored cluster {}", member, getClusterName());
                removed(member);
            }
        };
        addPolicy(policy);
        policy.setGroup(monitoredEntities);

        for (Entity each : monitoredEntities.getMembers()) {
            added(each);
        }

        setAttribute(Startable.SERVICE_UP, true);
    }

    @Override
    public void stop() {
        manager.stop();
        for (Entity monitor : monitors.getMembers()) {
            GangliaMonitor gmond = (GangliaMonitor) monitor;
            gmond.stop();
        }
        setAttribute(Startable.SERVICE_UP, false);
    }

    @Override
    public void restart() {
        throw new UnsupportedOperationException();
    }

    public void added(Entity member) {
        synchronized (mutex) {
            for (Location location : member.getLocations()) {
                if (location instanceof MachineLocation) {
                    if (!entityLocations.containsKey(location)) {
                        // install gmond
                        GangliaMonitor gmond = getEntityManager().createEntity(
                                BasicEntitySpec.newInstance(GangliaMonitor.class).parent(this));
                        monitoredLocations.put(location, gmond);
                        Entities.manage(gmond);
                        gmond.start(MutableSet.<Location> of(location));
                    }
                    entityLocations.put(location, member);
                }
            }
        }
    }

    public void removed(Entity member) {
        synchronized (mutex) {
            for (Location location : member.getLocations()) {
                entityLocations.remove(location, member);
                if (!entityLocations.containsKey(location)) {
                    // remove gmond
                    GangliaMonitor gmond = monitoredLocations.remove(location);
                    gmond.stop(); // May fail?
                }
            }
        }
    }

}