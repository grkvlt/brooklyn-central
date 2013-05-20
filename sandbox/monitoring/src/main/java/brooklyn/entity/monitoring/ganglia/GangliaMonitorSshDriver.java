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

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import brooklyn.entity.basic.AbstractSoftwareProcessSshDriver;
import brooklyn.location.Location;
import brooklyn.location.basic.SshMachineLocation;
import brooklyn.util.NetworkUtils;
import brooklyn.util.collections.MutableMap;
import brooklyn.util.ssh.CommonCommands;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

/**
 * Start a {@link GangliaMonitor} in a {@link Location} accessible over ssh.
 */
public class GangliaMonitorSshDriver extends AbstractSoftwareProcessSshDriver implements GangliaMonitorDriver {

    private static final Logger log = LoggerFactory.getLogger(GangliaMonitorSshDriver.class);
    
    public GangliaMonitorSshDriver(GangliaMonitorImpl entity, SshMachineLocation machine) {
        super(entity, machine);
    }

    protected String getLogFileLocation() { return String.format("%s/ganglia.log", getRunDir()); }

    @Override
    public Integer getGangliaPort() { return entity.getAttribute(GangliaManager.GANGLIA_PORT); }

    @Override
    public String getClusterName() { return entity.getAttribute(GangliaManager.CLUSTER_NAME); }
    
    @Override
    public void install() {
        List<String> commands = ImmutableList.<String>builder()
                .add(CommonCommands.installPackage(ImmutableMap.of("apt", "ganglia-monitor", "port", "ganglia"), "ganglia-gmond"))
                .build();

        newScript(INSTALLING)
                .failOnNonZeroResultCode()
                .body.append(commands)
                .execute();
    }

    @Override
    public void customize() {
        log.info("Customizing {}", entity);
        NetworkUtils.checkPortsValid(ImmutableMap.<String, Integer>builder()
                .put("gangliaPort", getGangliaPort())
                .build());

        List<String> commands = ImmutableList.<String>builder()
                .add(CommonCommands.sudo("service ganglia-monitor start"))
                .build();

        newScript(CUSTOMIZING)
                .body.append(commands)
                .execute();
    }

    @Override
    public void launch() {
        log.info("Launching: {}", entity);
        newScript(MutableMap.of("usePidFile", Boolean.FALSE), LAUNCHING)
                .body.append(CommonCommands.sudo("service ganglia-monitor restart"))
                .execute();
    }

    @Override
    public boolean isRunning() {
        log.info("Check Running: {}", entity);
        return newScript(MutableMap.of("usePidFile", Boolean.FALSE), CHECK_RUNNING)
                .body.append(CommonCommands.sudo("service ganglia-monitor start"))
                .execute() == 0;
    }

    @Override
    public void stop() {
        log.info("Stopping: {}", entity);
        newScript(MutableMap.of("usePidFile", Boolean.FALSE), STOPPING)
                .body.append(CommonCommands.sudo("service ganglia-monitor stop"))
                .execute();
    }
}
