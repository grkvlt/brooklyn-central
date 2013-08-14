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

import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;
=======
package brooklyn.entity.monitoring.zabbix;

import java.util.List;
>>>>>>> Add Zabbix entities to sandbox

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import brooklyn.entity.Entity;
import brooklyn.entity.basic.AbstractEntity;
import brooklyn.entity.basic.DynamicGroup;
import brooklyn.entity.group.AbstractMembershipTrackingPolicy;
<<<<<<< HEAD
import brooklyn.entity.proxying.EntitySpecs;
=======
import brooklyn.entity.proxying.EntitySpec;
>>>>>>> Add Zabbix entities to sandbox
import brooklyn.entity.trait.Startable;
import brooklyn.event.feed.http.HttpFeed;
import brooklyn.event.feed.http.HttpPollConfig;
import brooklyn.event.feed.http.HttpValueFunctions;
import brooklyn.location.Location;
import brooklyn.location.basic.SshMachineLocation;
<<<<<<< HEAD
import brooklyn.location.jclouds.JcloudsSshMachineLocation;
import brooklyn.util.collections.MutableMap;
import brooklyn.util.text.Strings;

import com.google.common.base.Function;
=======
import brooklyn.util.collections.MutableMap;

>>>>>>> Add Zabbix entities to sandbox
import com.google.common.base.Functions;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
<<<<<<< HEAD
import com.google.common.collect.ImmutableSet;
=======
>>>>>>> Add Zabbix entities to sandbox
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;

public class ZabbixServerImpl extends AbstractEntity implements ZabbixServer {

<<<<<<< HEAD
    /** serialVersionUID */
    private static final long serialVersionUID = -7585820580395149167L;

=======
>>>>>>> Add Zabbix entities to sandbox
    private static final Logger log = LoggerFactory.getLogger(ZabbixServerImpl.class);

    private Object[] mutex = new Object[0];
    private DynamicGroup monitoredEntities;
    private AbstractMembershipTrackingPolicy policy;
    private Multimap<Location, Entity> entityLocations = HashMultimap.create();

<<<<<<< HEAD
    private transient HttpFeed login = null;
=======

    private transient HttpFeed login;
>>>>>>> Add Zabbix entities to sandbox

    @Override
    public void init() {
        super.init();
        Predicate<? super Entity> filter = getConfig(ENTITY_FILTER);
<<<<<<< HEAD
        monitoredEntities = addChild(EntitySpecs.spec(DynamicGroup.class)
=======
        monitoredEntities = addChild(EntitySpec.create(DynamicGroup.class)
>>>>>>> Add Zabbix entities to sandbox
                .configure(DynamicGroup.ENTITY_FILTER, filter)
                .displayName("agents"));
    }

    @Override
    public void onManagementStarted() {
        final byte[] jsonData = ZabbixFeed.JSON_USER_LOGIN
                .replace("{{username}}", getConfig(ZABBIX_SERVER_USERNAME))
                .replace("{{password}}", getConfig(ZABBIX_SERVER_PASSWORD))
                .getBytes();
        login = HttpFeed.builder()
                .entity(this)
                .baseUri(getConfig(ZABBIX_SERVER_API_URL))
                .headers(ImmutableMap.of("Content-Type", "application/json"))
                .poll(new HttpPollConfig<String>(ZABBIX_TOKEN)
                        .method("POST")
                        .body(jsonData)
<<<<<<< HEAD
                        .onError(Functions.constant(""))
                        .onSuccess(Functions.compose(new Function<String, String>() {
                            @Override
                            public String apply(@Nullable String input) {
                                login.suspend();
                                return input;
                            }
                        }, HttpValueFunctions.jsonContents("result", String.class))))
                .build();

        Map<?, ?> flags = MutableMap.builder()
                .put("name", "Zabbix Agent Tracker")
                .put("sensorsToTrack", ImmutableSet.of(ZabbixMonitored.ZABBIX_AGENT_HOSTNAME, Startable.SERVICE_UP))
                .build();
        policy = new AbstractMembershipTrackingPolicy(flags) {
=======
                        .onFailure(Functions.constant(""))
                        .onSuccess(HttpValueFunctions.jsonContents("result", String.class)))
                .build();

        policy = new AbstractMembershipTrackingPolicy(MutableMap.of("name", "Zabbix Agent Tracker")) {
>>>>>>> Add Zabbix entities to sandbox
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
<<<<<<< HEAD
            if (location.isPresent() && member.getAttribute(Startable.SERVICE_UP)
                    && Strings.isNonBlank(member.getAttribute(ZabbixMonitored.ZABBIX_AGENT_HOSTNAME))) {
                SshMachineLocation machine = (SshMachineLocation) location.get();
                if (!entityLocations.containsKey(machine)) {
                    entityLocations.put(machine, member);

                    // Configure the Zabbix agent
                    List<String> commands = ImmutableList.<String>builder()
                            .add("sed -i.bk 's/\\$HOSTNAME/" + machine.getName() + "/' /etc/zabbix/zabbix_agentd.conf")
                            .add("zabbix_agentd")
                            .build();
                    int result = machine.execCommands("configuring Zabbix agent", commands);

                    if (result == 0) {
                        log.info("Zabbix agent configured on {} at {}", member, machine);
                    } else {
                        log.warn("failed to configure Zabbix agent on {} at {}", member, machine);
                    }
                }
            } else {
                log.warn("ZabbixServer#added({}) called but entity not ready", member);
=======
            if (location.isPresent() && member.getAttribute(Startable.SERVICE_UP)) {
                SshMachineLocation machine = (SshMachineLocation) location.get();
                if (!entityLocations.containsKey(machine)) {
                    entityLocations.put(machine, member);
                    // Configure the Zabbix agent
                    List<String> commands = ImmutableList.<String>builder()
                            .add("sed -i.bk 's/\\$HOSTNAME/" + machine.getDisplayName() + "/' /etc/zabbix/zabbix_agentd.conf")
                            .add("zabbix_agentd")
                            .build();
                    int result = machine.execCommands("configuring zabbix_agentd", commands);
                    if (result == 0) {
                        log.info("zabbix_agentd configured on {} at {}", member, machine);
                    } else {
                        log.warn("failed to configure zabbix_agentd on {}, status {}", machine, result);
                    }
                }
            } else {
                log.warn("zabbix added({}) called but no location or service not started", member);
>>>>>>> Add Zabbix entities to sandbox
            }
        }
    }

    public void removed(Entity member) {
        synchronized (mutex) {
            for (Location location : member.getLocations()) {
                entityLocations.remove(location, member);
            }
        }
    }

<<<<<<< HEAD
    public String getHostName(Entity entity) {
        // Find the first JcloudsSshMachineLocation if available
        Optional<Location> location = Iterables.tryFind(entity.getLocations(), Predicates.instanceOf(JcloudsSshMachineLocation.class));
        if (!location.isPresent()) {
            return null; // Do nothing until location is present
        }
        JcloudsSshMachineLocation jclouds = (JcloudsSshMachineLocation) location.get();
        String name = jclouds.getNode().getName(); // TODO will this exceed 64 characters?
        return name;
    }

=======
>>>>>>> Add Zabbix entities to sandbox
}
