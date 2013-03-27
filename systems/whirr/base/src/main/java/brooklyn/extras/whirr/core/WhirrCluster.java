package brooklyn.extras.whirr.core;

import java.util.Collection;

import org.apache.whirr.Cluster;
import org.apache.whirr.ClusterController;
import org.apache.whirr.ClusterSpec;

import brooklyn.config.ConfigKey;
import brooklyn.entity.Entity;
import brooklyn.entity.basic.Attributes;
import brooklyn.entity.basic.ConfigKeys;
import brooklyn.entity.proxying.ImplementedBy;
import brooklyn.entity.trait.Startable;
import brooklyn.event.Sensor;
import brooklyn.location.Location;
import brooklyn.util.flags.SetFromFlag;

import com.google.common.annotations.Beta;
import com.google.common.annotations.VisibleForTesting;

/**
 * Generic entity that can be used to deploy clusters that are
 * managed by Apache Whirr.
 *
 */
@ImplementedBy(WhirrClusterImpl.class)
public interface WhirrCluster extends Entity, Startable {

    @SetFromFlag("recipe")
    ConfigKey<String> RECIPE = ConfigKeys.newConfigKey("whirr.recipe", "Apache Whirr cluster recipe");

    Sensor<String> CLUSTER_NAME = Attributes.newAttributeSensor("whirr.cluster.name", "Name of the Whirr cluster");

    /**
     * Apache Whirr can only start and manage a cluster in a single location
     *
     * @param locations
     */
    @Override
    void start(Collection<? extends Location> locations);

    @Beta
    ClusterSpec getClusterSpec();

    @Beta
    Cluster getCluster();

    @Beta
    @VisibleForTesting
    ClusterController getController();
}
