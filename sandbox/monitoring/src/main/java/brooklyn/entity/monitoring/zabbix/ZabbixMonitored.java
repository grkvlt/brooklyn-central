<<<<<<< HEAD
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
package brooklyn.entity.monitoring.zabbix;

import brooklyn.config.ConfigKey;
import brooklyn.entity.Entity;
=======
package brooklyn.entity.monitoring.zabbix;

import brooklyn.config.ConfigKey;
>>>>>>> Add Zabbix entities to sandbox
import brooklyn.event.AttributeSensor;
import brooklyn.event.basic.BasicAttributeSensor;
import brooklyn.event.basic.BasicConfigKey;
import brooklyn.event.basic.PortAttributeSensorAndConfigKey;
import brooklyn.util.flags.SetFromFlag;

<<<<<<< HEAD
import com.google.common.base.Function;
import com.google.common.base.Functions;

=======
>>>>>>> Add Zabbix entities to sandbox
public interface ZabbixMonitored {

    /** The entity representing the Zabbix server monitoring an entity. */
    @SetFromFlag("zabbixServer")
    ConfigKey<ZabbixServer> ZABBIX_SERVER = new BasicConfigKey<ZabbixServer>(ZabbixServer.class, "zabbix.server.entity", "Zabbix server for this entity");

    PortAttributeSensorAndConfigKey ZABBIX_AGENT_PORT = new PortAttributeSensorAndConfigKey("zabbix.agent.port", "The port the Zabbix agent is listening on", "10050+");

<<<<<<< HEAD
    AttributeSensor<String> ZABBIX_AGENT_HOSTID = new BasicAttributeSensor<String>(String.class, "zabbix.agent.hostid", "The host ID for a Zabbix monitored agent");

    AttributeSensor<String> ZABBIX_AGENT_HOSTNAME = new BasicAttributeSensor<String>(String.class, "zabbix.agent.hostname", "The host name for a Zabbix monitored agent");

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @SetFromFlag("agentNameFunction")
    ConfigKey<Function<Entity, String>> ZABBIX_AGENT_NAME_FUNCTION = new BasicConfigKey(Function.class,
            "zabbix.server.function.agentName", "Function to generate the name of the Zabbix agent for an entity", Functions.toStringFunction());
=======
    AttributeSensor<String> ZABBIX_AGENT_HOSTID = new BasicAttributeSensor<String>(String.class, "zabbix.agent.hostid", "The hostId for a Zabbix monitored agent");
>>>>>>> Add Zabbix entities to sandbox

}