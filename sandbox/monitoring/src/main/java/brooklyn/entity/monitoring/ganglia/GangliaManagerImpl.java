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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import brooklyn.entity.basic.SoftwareProcessImpl;
import brooklyn.event.feed.ConfigToAttributes;

/**
 * An implementation of {@link GangliaManager}.
 */
public class GangliaManagerImpl extends SoftwareProcessImpl implements GangliaManager {

    /** serialVersionUID */
    private static final long serialVersionUID = 4682848188913389323L;

    private static final Logger log = LoggerFactory.getLogger(GangliaManagerImpl.class);

    @Override
    public void init() {
        ConfigToAttributes.apply(this);
    }

    public Integer getGangliaPort() {
        return getAttribute(GANGLIA_PORT);
    }

    public String getClusterName() {
        return getAttribute(CLUSTER_NAME);
    }

    @Override
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
}
