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
import brooklyn.event.AttributeSensor;
import brooklyn.event.basic.BasicAttributeSensor;
import brooklyn.event.basic.BasicConfigKey;
import brooklyn.util.flags.SetFromFlag;

import com.google.common.base.Function;
import com.google.common.base.Functions;

public interface DynaTraceMonitored {

    /** The entity representing the DynaTrace server monitoring an entity. */
    @SetFromFlag("dynaTraceServer")
    ConfigKey<DynaTraceServer> DYNATRACE_SERVER = new BasicConfigKey<DynaTraceServer>(DynaTraceServer.class, "dynatrace.server.entity", "DynaTrace server for this entity");

    /** The agent configuration template for the entity being monitored. */
    @SetFromFlag("agentConfigTemplateUrl")
    ConfigKey<String> DYNATRACE_AGENT_CONFIG_TEMPLATE_URL = new BasicConfigKey<String>(String.class,
            "dynatrace.agent.agentConfig.template", "DynaTrace agent configuration file name",
            "classpath://io/cloudsoft/hcc/monitoring/dynatrace/dtwsagent.ini");

    /** The agent configuration file on the entity being monitored. */
    @SetFromFlag("agentConfigFile")
    ConfigKey<String> DYNATRACE_AGENT_CONFIG_FILE = new BasicConfigKey<String>(String.class,
            "dynatrace.agent.agentConfig.file", "DynaTrace agent configuration template URL",
            "/opt/dynatrace-5.5.0/agent/conf/dtwsagent.ini");

    /** The host configuration template for the entity being monitored. */
    @SetFromFlag("hostConfigTemplateUrl")
    ConfigKey<String> DYNATRACE_HOST_CONFIG_TEMPLATE_URL = new BasicConfigKey<String>(String.class,
            "dynatrace.agent.hostConfig.template", "DynaTrace host configuration file name",
            "classpath://io/cloudsoft/hcc/monitoring/dynatrace/dthostagent.ini");

    /** The host configuration file on the entity being monitored. */
    @SetFromFlag("hostConfigFile")
    ConfigKey<String> DYNATRACE_HOST_CONFIG_FILE = new BasicConfigKey<String>(String.class,
            "dynatrace.agent.hostConfig.file", "DynaTrace host configuration template URL",
            "/opt/dynatrace-5.5.0/agent/conf/dthostagent.ini");

    @SetFromFlag("reportName")
    ConfigKey<String> DYNATRACE_XML_REPORT_NAME = new BasicConfigKey<String>(String.class, "dynatrace.server.reportName", "DynaTrace XML measurement report name");

    AttributeSensor<String> DYNATRACE_AGENT_NAME = new BasicAttributeSensor<String>(String.class, "dynatrace.agent.hostname", "The host name for a DynaTrace monitored agent");

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @SetFromFlag("agentNameFunction")
    ConfigKey<Function<Entity, String>> DYNATRACE_AGENT_NAME_FUNCTION = new BasicConfigKey(Function.class,
            "dynatrace.server.function.agentName", "Function to generate the name of the Dynatrace agent for an entity", Functions.toStringFunction());

}