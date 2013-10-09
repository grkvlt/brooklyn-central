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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import brooklyn.entity.Entity;
import brooklyn.entity.basic.AbstractEntity;
import brooklyn.entity.basic.DynamicGroup;
import brooklyn.entity.group.AbstractMembershipTrackingPolicy;
import brooklyn.entity.proxying.EntitySpec;
import brooklyn.entity.trait.Startable;
import brooklyn.location.Location;
import brooklyn.location.basic.SshMachineLocation;
import brooklyn.util.ResourceUtils;
import brooklyn.util.collections.MutableMap;
import brooklyn.util.exceptions.Exceptions;
import brooklyn.util.ssh.BashCommands;
import brooklyn.util.text.Strings;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;

import freemarker.cache.StringTemplateLoader;
import freemarker.ext.beans.BeansWrapper;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateHashModel;

public class DynaTraceServerImpl extends AbstractEntity implements DynaTraceServer {

    private static final Logger log = LoggerFactory.getLogger(DynaTraceServerImpl.class);

    private Object[] mutex = new Object[0];
    private DynamicGroup monitoredEntities;
    private AbstractMembershipTrackingPolicy policy;
    private Multimap<Location, Entity> entityLocations = HashMultimap.create();

    @Override
    public void init() {
        super.init();
        Predicate<? super Entity> filter = getConfig(ENTITY_FILTER);
        monitoredEntities = addChild(EntitySpec.create(DynamicGroup.class)
                .configure(DynamicGroup.ENTITY_FILTER, filter)
                .displayName("agents"));
    }

    @Override
    public String getServerName() {
        return getConfig(DYNATRACE_SERVER_NAME);
    }

    @Override
    public Integer getDynaTracePort() {
        return getConfig(DYNATRACE_SERVER_PORT);
    }

    @Override
    public Integer getRestApiPort() {
        return getConfig(DYNATRACE_REST_API_PORT);
    }

    @Override
    public void onManagementStarted() {
        Map<?, ?> flags = MutableMap.builder()
                .put("name", "DynaTrace Agent Tracker")
                .put("sensorsToTrack", ImmutableSet.of(DynaTraceMonitored.DYNATRACE_AGENT_NAME, Startable.SERVICE_UP))
                .build();
        policy = new AbstractMembershipTrackingPolicy(flags) {
            @Override
            protected void onEntityChange(Entity member) { added(member); }
            @Override
            protected void onEntityAdded(Entity member) { } // Ignore
            @Override
            protected void onEntityRemoved(Entity member) { removed(member); }
        };
        addPolicy(policy);
        policy.setGroup(monitoredEntities);

        for (Entity each : monitoredEntities.getMembers()) {
            added(each);
        }

        setAttribute(Startable.SERVICE_UP, true);
    }

    public void added(Entity member) {
        synchronized (mutex) {
            Optional<Location> location = Iterables.tryFind(member.getLocations(), Predicates.instanceOf(SshMachineLocation.class));
            if (location.isPresent() && member.getAttribute(Startable.SERVICE_UP)
                    && Strings.isNonBlank(member.getAttribute(DynaTraceMonitored.DYNATRACE_AGENT_NAME))) {
                SshMachineLocation machine = (SshMachineLocation) location.get();
                if (!entityLocations.containsKey(machine)) {
                    entityLocations.put(machine, member);

                    // Lookup agent configuration template locations
                    String agentConfigTemplate = member.getConfig(DynaTraceMonitored.DYNATRACE_AGENT_CONFIG_TEMPLATE_URL);
                    String agentConfigFile = member.getConfig(DynaTraceMonitored.DYNATRACE_AGENT_CONFIG_FILE);
                    String hostConfigTemplate = member.getConfig(DynaTraceMonitored.DYNATRACE_HOST_CONFIG_TEMPLATE_URL);
                    String hostConfigFile = member.getConfig(DynaTraceMonitored.DYNATRACE_HOST_CONFIG_FILE);

                    // Parse and copy templates
                    int copyAgentConfig = machine.copyTo(processTemplate(agentConfigTemplate, member), agentConfigFile);
                    int copyHostConfig = machine.copyTo(processTemplate(hostConfigTemplate, member), hostConfigFile);

                    // Restart web server
                    int restartHttpd = machine.execCommands("configuring DynaTrace agent", ImmutableList.of(BashCommands.sudo("service httpd restart")));

                    // Check results
                    if (copyAgentConfig == 0 && copyHostConfig == 0 && restartHttpd == 0) {
                        log.info("DynaTrace agent configured on {} at {}", member, machine);
                    } else {
                        log.warn("failed to configure DynaTrace agent on {} at {}", member, machine);
                    }
                }
            } else {
                log.warn("DynaTrace#added({}) called but entity not ready", member);
            }
        }
    }

    public InputStream processTemplate(String templateConfigUrl, Entity agent) {
        try {
            BeansWrapper wrapper = BeansWrapper.getDefaultInstance();
            TemplateHashModel staticModels = wrapper.getStaticModels();
            TemplateHashModel dynaTraceMonitored = (TemplateHashModel) staticModels.get(DynaTraceMonitored.class.getName());
            TemplateHashModel dynaTraceServer = (TemplateHashModel) staticModels.get(DynaTraceServer.class.getName());

            Map<String, Object> substitutions = ImmutableMap.<String, Object>builder()
                    .put("DynaTraceMonitored", dynaTraceMonitored)
                    .put("DynaTraceServer", dynaTraceServer)
                    .put("agent", agent)
                    .put("server", this)
                    .build();

            String templateFile = new ResourceUtils(this).getResourceAsString(templateConfigUrl);

            Configuration cfg = new Configuration();
            StringTemplateLoader templateLoader = new StringTemplateLoader();
            templateLoader.putTemplate("config", templateFile);
            cfg.setTemplateLoader(templateLoader);
            Template template = cfg.getTemplate("config");

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Writer out = new OutputStreamWriter(baos);
            template.process(substitutions, out);
            out.flush();

            return new ByteArrayInputStream(baos.toByteArray());
        } catch (Exception e) {
            log.warn("Error creating configuration file for "+agent, e);
            throw Exceptions.propagate(e);
        }
    }

    public void removed(Entity member) {
        synchronized (mutex) {
            for (Location location : member.getLocations()) {
                entityLocations.remove(location, member);
            }
        }
    }

}
