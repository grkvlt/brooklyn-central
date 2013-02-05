/*
<<<<<<< HEAD
 * Copyright 2012-2013 by Cloudsoft Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
=======
 * Copyright 2012-2013 by Cloiudsoft Corp.
>>>>>>> Refactor Ganglia entities to use interface and implementation pattern
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
<<<<<<< HEAD
import brooklyn.entity.proxying.EntitySpecs;
import brooklyn.entity.trait.Startable;
import brooklyn.location.Location;
import brooklyn.location.basic.SshMachineLocation;
import brooklyn.util.collections.MutableMap;
import brooklyn.util.collections.MutableSet;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
=======
import brooklyn.entity.proxying.BasicEntitySpec;
import brooklyn.entity.trait.Startable;
import brooklyn.location.Location;
import brooklyn.location.MachineLocation;
import brooklyn.util.MutableMap;
import brooklyn.util.MutableSet;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.HashMultimap;
>>>>>>> Refactor Ganglia entities to use interface and implementation pattern
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;

/**
 * An implementation of {@link GangliaCluster}.
 */
public class GangliaClusterImpl extends AbstractEntity implements GangliaCluster {

    /** serialVersionUID */
    private static final long serialVersionUID = -8719174966402620778L;

    private static final Logger log = LoggerFactory.getLogger(GangliaClusterImpl.class);

    private Object[] mutex = new Object[0];
    private AbstractMembershipTrackingPolicy policy;

    private GangliaManager manager;
    private DynamicGroup monitoredEntities;
    private Group monitors;

    private Multimap<Location, Entity> entityLocations = HashMultimap.create();
    private Map<Location, GangliaMonitor> monitoredLocations = Maps.newHashMap();

<<<<<<< HEAD
    @Override
    public void init() {
        manager = addChild(EntitySpecs.spec(GangliaManager.class));

        Predicate<? super Entity> filter = Predicates.and(Predicates.not(Predicates.instanceOf(GangliaMonitor.class)), getConfig(ENTITY_FILTER));
        monitoredEntities = addChild(EntitySpecs.spec(DynamicGroup.class)
                .configure(DynamicGroup.ENTITY_FILTER, filter)
                .displayName("Monitored Entities"));

        monitors = addChild(EntitySpecs.spec(BasicGroup.class)
                .configure(BasicGroup.CHILDREN_AS_MEMBERS, true)
                .displayName("Ganglia Monitors"));
=======
    public GangliaClusterImpl() {
        this(Maps.newHashMap(), null);
    }

    public GangliaClusterImpl(Map<?, ?> flags) {
        this(flags, null);
    }

    public GangliaClusterImpl(Entity owner) {
        this(Maps.newHashMap(), owner);
    }

    public GangliaClusterImpl(Map<?, ?> flags, Entity owner) {
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
>>>>>>> Refactor Ganglia entities to use interface and implementation pattern
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

<<<<<<< HEAD
        Map<?, ?> flags = MutableMap.builder()
                .put("name", "Ganglia Monitor Tracker")
                .put("sensorsToTrack", ImmutableSet.of(Startable.SERVICE_UP))
                .build();
        policy = new AbstractMembershipTrackingPolicy(flags) {
            @Override
            protected void onEntityChange(Entity member) {
=======
        policy = new AbstractMembershipTrackingPolicy(MutableMap.of("name", "Ganglia Cluster Tracker")) {
            @Override
            protected void onEntityChange(Entity member) { } // Don't care
            @Override
            protected void onEntityAdded(Entity member) {
>>>>>>> Refactor Ganglia entities to use interface and implementation pattern
                if (log.isDebugEnabled()) log.debug("Added {} to monitored cluster {}", member, getClusterName());
                added(member);
            }
            @Override
<<<<<<< HEAD
            protected void onEntityAdded(Entity member) { } // Don't care
            @Override
=======
>>>>>>> Refactor Ganglia entities to use interface and implementation pattern
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
<<<<<<< HEAD
            Optional<Location> location = Iterables.tryFind(member.getLocations(), Predicates.instanceOf(SshMachineLocation.class));
            if (location.isPresent() && member.getAttribute(Startable.SERVICE_UP)) {
                SshMachineLocation machine = (SshMachineLocation) location.get();
                if (!entityLocations.containsKey(machine)) {
                    // install gmond
                    GangliaMonitor gmond = addChild(EntitySpecs.spec(GangliaMonitor.class)
                            .configure(GangliaMonitor.GANGLIA_MANAGER, manager)
                            .configure(GangliaMonitor.MONITORED_ENTITY, member));
                    monitoredLocations.put(machine, gmond);
                    monitors.addMember(gmond);
                    Entities.manage(gmond);
                    gmond.start(MutableSet.<Location>of(machine));
                }
                entityLocations.put(machine, member);
=======
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
>>>>>>> Refactor Ganglia entities to use interface and implementation pattern
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
<<<<<<< HEAD
                    monitors.removeMember(gmond);
=======
>>>>>>> Refactor Ganglia entities to use interface and implementation pattern
                    gmond.stop(); // May fail?
                }
            }
        }
    }

}