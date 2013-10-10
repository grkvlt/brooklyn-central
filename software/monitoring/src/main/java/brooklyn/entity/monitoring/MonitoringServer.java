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
import brooklyn.entity.Group;
import brooklyn.entity.basic.DynamicGroup;
import brooklyn.entity.basic.SoftwareProcess;
import brooklyn.event.basic.BasicConfigKey;
import brooklyn.util.flags.SetFromFlag;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

/**
 * An {@link brooklyn.entity.Entity} that represents the monitoring management service.
 */
public interface MonitoringServer extends SoftwareProcess {

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @SetFromFlag("filter")
    ConfigKey<Predicate<? super Entity>> ENTITY_FILTER = new BasicConfigKey(Predicate.class, "monitoring.entity.filter", "Filter for entities which will automatically be monitored", Predicates.instanceOf(MonitoredEntity.class));

    @SetFromFlag("monitoringUsername")
    ConfigKey<String> MONITORING_USERNAME = new BasicConfigKey<String>(String.class, "monitoring.server.username", "Username for connecting to monitoring server");

    @SetFromFlag("monitoringPassword")
    ConfigKey<String> MONITORING_PASSWORD = new BasicConfigKey<String>(String.class, "monitoring.server.password", "Password for connecting to monitoring server");

    DynamicGroup getMonitoredEntities();

    Group getMonitoringAgents();

}
