/*
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
 * An {@link brooklyn.entity.Entity} that represents a Ganglia monitoring daemon, {@code gmond}.
 */
@ImplementedBy(GangliaMonitorImpl.class)
public interface GangliaMonitor extends SoftwareProcess {

    @SetFromFlag("version")
    public static final ConfigKey<String> SUGGESTED_VERSION = new BasicConfigKey<String>(SoftwareProcess.SUGGESTED_VERSION, "1.1.6");

    @SetFromFlag("gangliaPort")
    public static final PortAttributeSensorAndConfigKey GANGLIA_PORT = new PortAttributeSensorAndConfigKey("ganglia.port", "Ganglia communications port", PortRanges.fromString("7000+"));

    @SetFromFlag("clusterName")
    public static final BasicAttributeSensor<String> CLUSTER_NAME = GangliaCluster.CLUSTER_NAME;

    public Integer getGangliaPort();

    public String getClusterName();

}
