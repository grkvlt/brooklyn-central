/*
 * Copyright 2012 by Andrew Kennedy
 */
package brooklyn.entity.monitoring.ganglia;

import brooklyn.entity.basic.SoftwareProcessDriver;

public interface GangliaMonitorDriver extends SoftwareProcessDriver {

    Integer getGangliaPort();

    String getClusterName();

}
