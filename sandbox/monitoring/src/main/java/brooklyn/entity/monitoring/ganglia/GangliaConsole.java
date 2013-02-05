/*
 * Copyright 2012-2013 by Cloudsoft Corp.
 */
package brooklyn.entity.monitoring.ganglia;

import brooklyn.config.ConfigKey;
import brooklyn.entity.Entity;
import brooklyn.entity.Group;
import brooklyn.entity.basic.DynamicGroup;
import brooklyn.entity.basic.SoftwareProcess;
import brooklyn.entity.group.DynamicCluster;
import brooklyn.entity.proxying.ImplementedBy;
import brooklyn.entity.trait.Startable;
import brooklyn.event.basic.BasicAttributeSensorAndConfigKey;
import brooklyn.event.basic.BasicConfigKey;
import brooklyn.util.flags.SetFromFlag;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

/**
 * A cluster of {@link GangliaManager}s based on {@link DynamicCluster} which can be resized by a policy if required.
 *
 * TODO add sensors with aggregated Ganglia statistics from cluster
 */
@ImplementedBy(GangliaClusterImpl.class)
public interface GangliaConsole extends Entity, Startable {

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @SetFromFlag("filter")
    ConfigKey<Predicate<? super Entity>> ENTITY_FILTER = new BasicConfigKey(Predicate.class, "ganglia.cluster.filter", "Filter for entities which will automatically be monitored", Predicates.instanceOf(SoftwareProcess.class));

    @SetFromFlag("clusterName")
    BasicAttributeSensorAndConfigKey<String> CLUSTER_NAME = new BasicAttributeSensorAndConfigKey<String>(String.class, "ganglia.cluster.name", "Name of the Ganglia cluster", "Brooklyn Ganglia Cluster");

    public GangliaManager getManager();

    public DynamicGroup getMonitoredEntities();

    public Group getMonitors();

    public String getClusterName();

}
