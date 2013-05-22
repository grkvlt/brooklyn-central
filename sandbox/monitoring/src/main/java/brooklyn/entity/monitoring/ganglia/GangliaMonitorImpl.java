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

import java.util.Map;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import brooklyn.entity.Entity;
import brooklyn.entity.basic.EntityLocal;
import brooklyn.entity.basic.SoftwareProcessImpl;
import brooklyn.event.feed.ConfigToAttributes;
import brooklyn.event.feed.function.FunctionFeed;
import brooklyn.event.feed.function.FunctionPollConfig;

import com.google.common.base.Functions;
import com.google.common.collect.Maps;

/**
 * An implementation of {@link GangliaMonitor}.
 */
public class GangliaMonitorImpl extends SoftwareProcessImpl implements GangliaMonitor {

    /** serialVersionUID */
    private static final long serialVersionUID = -9113861914926145090L;

    private static final Logger log = LoggerFactory.getLogger(GangliaMonitorImpl.class);

    public GangliaMonitorImpl() {
        this(Maps.newHashMap(), null);
    }

    public GangliaMonitorImpl(Map<?, ?> flags) {
        this(flags, null);
    }

    public GangliaMonitorImpl(Entity owner) {
        this(Maps.newHashMap(), owner);
    }

    public GangliaMonitorImpl(Map<?, ?> flags, Entity owner) {
        super(flags, owner);
    }

    @Override
    public void init() {
        ConfigToAttributes.apply((EntityLocal) this);
//        setAttribute(CLUSTER_NAME, getConfig(GangliaCluster.CLUSTER_NAME));
    }

    @Override
    public Integer getGangliaPort() {
        return getAttribute(GANGLIA_PORT);
    }

    @Override
    public String getClusterName() {
        return getAttribute(CLUSTER_NAME);
    }

    @Override
    public GangliaManager getGangliaManager() {
        return getConfig(GANGLIA_MANAGER);
    }

    @Override
    public Entity getMonitoredEntity() {
        return getConfig(MONITORED_ENTITY);
    }

    @Override
    public Class getDriverInterface() {
        return GangliaMonitorDriver.class;
    }

    transient FunctionFeed serviceUp;

    @Override
    protected void connectSensors() {
        super.connectSensors();

        serviceUp = FunctionFeed.builder()
                .entity(this)
                .poll(new FunctionPollConfig<Object, Boolean>(SERVICE_UP)
                        .period(500)
                        .callable(new Callable<Boolean>() {
                            public Boolean call() {
                                return getDriver().isRunning();
                            }
                        })
                        .onError(Functions.constant(false)))
                .build();
    }
}