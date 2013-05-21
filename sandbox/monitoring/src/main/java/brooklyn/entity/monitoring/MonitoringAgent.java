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
package brooklyn.entity.monitoring;

import brooklyn.config.ConfigKey;
import brooklyn.entity.Entity;
import brooklyn.entity.basic.SoftwareProcess;
import brooklyn.event.basic.BasicConfigKey;

/**
 * An {@link brooklyn.entity.Entity} that represents a monitoring agent.
 */
public interface MonitoringAgent extends SoftwareProcess {

    ConfigKey<MonitoringServer> MONITORING_SERVER = MonitoredEntity.MONITORING_SERVER;

    ConfigKey<Entity> MONITORED_ENTITY = new BasicConfigKey<Entity>(Entity.class, "monitoring.entity.monitored", "The monitored entity");

    MonitoringServer getMonitoringServer();

    Entity getMonitoredEntity();

}
