/*
 * Copyright 2012-2013 by Cloudsoft Corp.
 */
package brooklyn.entity.monitoring.ganglia;

import brooklyn.config.ConfigKey;
import brooklyn.entity.basic.SoftwareProcess;
import brooklyn.entity.proxying.ImplementedBy;
import brooklyn.event.basic.BasicAttributeSensor;
import brooklyn.event.basic.BasicConfigKey;
import brooklyn.event.basic.PortAttributeSensorAndConfigKey;
import brooklyn.location.basic.PortRanges;
import brooklyn.util.flags.SetFromFlag;


/**
 * An {@link brooklyn.entity.Entity} that represents the Ganglia management service, {@code gmetad}.
 */
@ImplementedBy(GangliaManagerImpl.class)
public interface GangliaManager extends SoftwareProcess {

    @SetFromFlag("version")
    public static final ConfigKey<String> SUGGESTED_VERSION = new BasicConfigKey<String>(SoftwareProcess.SUGGESTED_VERSION, "1.1.6");

    @SetFromFlag("gangliaPort")
    public static final PortAttributeSensorAndConfigKey GANGLIA_PORT = new PortAttributeSensorAndConfigKey("ganglia.port", "Ganglia communications port", PortRanges.fromString("8649+"));

    @SetFromFlag("clusterName")
    public static final BasicAttributeSensor<String> CLUSTER_NAME = GangliaCluster.CLUSTER_NAME;

    public Integer getGangliaPort();

    public String getClusterName();

}
