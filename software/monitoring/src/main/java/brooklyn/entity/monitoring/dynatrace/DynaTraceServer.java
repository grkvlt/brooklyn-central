/*
 * Copyright 2013 by Cloudsoft Corp.
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
package brooklyn.entity.monitoring.dynatrace;

import brooklyn.config.ConfigKey;
import brooklyn.entity.Entity;
import brooklyn.entity.proxying.ImplementedBy;
import brooklyn.event.basic.BasicConfigKey;
import brooklyn.util.flags.SetFromFlag;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

@ImplementedBy(DynaTraceServerImpl.class)
public interface DynaTraceServer extends Entity {

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @SetFromFlag("filter")
    ConfigKey<Predicate<? super Entity>> ENTITY_FILTER = new BasicConfigKey(Predicate.class,
            "dynatrace.server.filter", "Filter for entities which will automatically be monitored", Predicates.instanceOf(DynaTraceMonitored.class));

    @SetFromFlag("serverName")
    ConfigKey<String> DYNATRACE_SERVER_NAME = new BasicConfigKey<String>(String.class, "dynatrace.server.name", "DynaTrace server FQDN");

    @SetFromFlag("restApiPort")
    ConfigKey<Integer> DYNATRACE_REST_API_PORT = new BasicConfigKey<Integer>(Integer.class, "dynatrace.server.port.api", "The port the DynaTrace REST API is listening on", 8020);

    @SetFromFlag("dynaTracePort")
    ConfigKey<Integer> DYNATRACE_SERVER_PORT = new BasicConfigKey<Integer>(Integer.class, "dynatrace.server.port.server", "The port the DynaTrace server is listening on", 9998);

    @SetFromFlag("username")
    ConfigKey<String> DYNATRACE_SERVER_USERNAME = new BasicConfigKey<String>(String.class, "dynatrace.server.username", "DynaTrace server API login user");

    @SetFromFlag("password")
    ConfigKey<String> DYNATRACE_SERVER_PASSWORD = new BasicConfigKey<String>(String.class, "dynatrace.server.password", "DynaTrace server API login password");

    String getServerName();

    Integer getDynaTracePort();

    Integer getRestApiPort();

}
