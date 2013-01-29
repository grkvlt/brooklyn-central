/*
 * Copyright 2012 by Andrew Kennedy
 */
package brooklyn.entity.monitoring.ganglia;

import java.util.Collection;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import brooklyn.config.ConfigKey;
import brooklyn.entity.Entity;
import brooklyn.entity.Group;
import brooklyn.entity.basic.AbstractEntity;
import brooklyn.entity.basic.BasicGroup;
import brooklyn.entity.basic.DynamicGroup;
import brooklyn.entity.basic.Entities;
import brooklyn.entity.basic.SoftwareProcessEntity;
import brooklyn.entity.group.AbstractMembershipTrackingPolicy;
import brooklyn.entity.group.DynamicCluster;
import brooklyn.entity.trait.Startable;
import brooklyn.event.basic.BasicAttributeSensorAndConfigKey;
import brooklyn.event.basic.BasicConfigKey;
import brooklyn.location.Location;
import brooklyn.location.MachineLocation;
import brooklyn.util.MutableMap;
import brooklyn.util.MutableSet;
import brooklyn.util.flags.SetFromFlag;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;

/**
 * A cluster of {@link GangliaManager}s based on {@link DynamicCluster} which can be resized by a policy if required.
 *
 * TODO add sensors with aggregated Ganglia statistics from cluster
 */
public class GangliaCluster extends AbstractEntity implements Startable {
    /** serialVersionUID */
    private static final long serialVersionUID = -8719174966402620778L;

    private static final Logger log = LoggerFactory.getLogger(GangliaCluster.class);

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @SetFromFlag("filter")
    public static final ConfigKey<Predicate<? super Entity>> ENTITY_FILTER = new BasicConfigKey(Predicate.class, "ganglia.cluster.filter", "Filter for entities which will automatically be monitored", Predicates.instanceOf(SoftwareProcessEntity.class));
    
    @SetFromFlag("clusterName")
    public static final BasicAttributeSensorAndConfigKey<String> CLUSTER_NAME = new BasicAttributeSensorAndConfigKey<String>(String.class, "ganglia.cluster.name", "Name of the Ganglia cluster", "Brooklyn Ganglia Cluster");

    private AbstractMembershipTrackingPolicy policy;

    private GangliaManager manager;
    private DynamicGroup monitoredEntities;
    private Group monitors;

    private Multimap<Location, Entity> entityLocations = HashMultimap.create();
    private Map<Location, GangliaMonitor> monitoredLocations = Maps.newHashMap();

    public GangliaCluster(Map<?, ?> flags) {
        this(flags, null);
    }

    public GangliaCluster(Entity owner){
        this(Maps.newHashMap(), owner);
    }

    public GangliaCluster(Map<?, ?> flags, Entity owner) {
        super(flags, owner);

        manager = new GangliaManager(this);
        
        Predicate<? super Entity> filter = Predicates.and(Predicates.not(Predicates.instanceOf(GangliaMonitor.class)), getConfig(ENTITY_FILTER));
        monitoredEntities = new DynamicGroup(this, filter);
        monitors = new BasicGroup(MutableMap.of("childrenAsMembers", true), this);
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

    @Override
    public void start(Collection<? extends Location> locations) {
        manager.start(locations);

        policy = new AbstractMembershipTrackingPolicy(MutableMap.of("name", "Ganglia Cluster Tracker")) {
            @Override
            protected void onEntityChange(Entity member) { } // Don't care
            @Override
            protected void onEntityAdded(Entity member) { added(member); }
            @Override
            protected void onEntityRemoved(Entity member) { removed(member); }
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
    
    public synchronized void added(Entity member) {
        for (Location location : member.getLocations()) {
            if (location instanceof MachineLocation) {
                if (!entityLocations.containsKey(location)) {
                    // install gmond
                    GangliaMonitor gmond = new GangliaMonitor(monitors);
                    monitoredLocations.put(location, gmond);
                    Entities.manage(gmond);
                    gmond.start(MutableSet.<Location>of(location));
                }
                entityLocations.put(location, member);
            }
        }
    }
    
    public synchronized void removed(Entity member) {
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
