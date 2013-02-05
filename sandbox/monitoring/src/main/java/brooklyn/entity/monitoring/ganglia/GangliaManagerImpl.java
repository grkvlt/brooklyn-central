/*
<<<<<<< HEAD
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import brooklyn.entity.basic.SoftwareProcessImpl;
import brooklyn.event.feed.ConfigToAttributes;
=======
 * Copyright 2012 by Andrew Kennedy
 */
package brooklyn.entity.monitoring.ganglia;

import java.util.Map;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import brooklyn.entity.Entity;
import brooklyn.entity.basic.SoftwareProcessImpl;
import brooklyn.event.feed.function.FunctionFeed;
import brooklyn.event.feed.function.FunctionPollConfig;

import com.google.common.base.Functions;
import com.google.common.collect.Maps;
>>>>>>> Refactor Ganglia entities to use interface and implementation pattern

/**
 * An implementation of {@link GangliaManager}.
 */
public class GangliaManagerImpl extends SoftwareProcessImpl implements GangliaManager {

    /** serialVersionUID */
    private static final long serialVersionUID = 4682848188913389323L;

    private static final Logger log = LoggerFactory.getLogger(GangliaManagerImpl.class);

<<<<<<< HEAD
    @Override
    public void init() {
        ConfigToAttributes.apply(this);
=======
    public GangliaManagerImpl() {
        this(Maps.newHashMap(), null);
    }

    public GangliaManagerImpl(Map<?, ?> flags) {
        this(flags, null);
    }

    public GangliaManagerImpl(Entity owner) {
        this(Maps.newHashMap(), owner);
    }

    public GangliaManagerImpl(Map<?, ?> flags, Entity owner) {
        super(flags, owner);
>>>>>>> Refactor Ganglia entities to use interface and implementation pattern
    }

    public Integer getGangliaPort() {
        return getAttribute(GANGLIA_PORT);
    }

    public String getClusterName() {
        return getAttribute(CLUSTER_NAME);
    }

    @Override
<<<<<<< HEAD
    public Class<GangliaManagerDriver> getDriverInterface() {
        return GangliaManagerDriver.class;
    }

    @Override
    protected void connectSensors() {
        super.connectSensors();
        connectServiceUpIsRunning();
    }

    @Override
    protected void disconnectSensors() {
        super.disconnectSensors();
        disconnectServiceUpIsRunning();
    }

=======
    public Class getDriverInterface() {
        return GangliaManagerDriver.class;
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
>>>>>>> Refactor Ganglia entities to use interface and implementation pattern
}
