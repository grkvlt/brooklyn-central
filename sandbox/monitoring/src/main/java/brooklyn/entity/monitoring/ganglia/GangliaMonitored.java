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

import brooklyn.config.ConfigKey;
import brooklyn.event.basic.BasicConfigKey;
import brooklyn.util.flags.SetFromFlag;

/**
 * Interface to mark entities that should have a {@link GangliaMonitor} installed.
 */
public interface GangliaMonitored {

    @SetFromFlag("gangliaManager")
    ConfigKey<GangliaManager> GANGLIA_MANAGER = new BasicConfigKey<GangliaManager>(GangliaManager.class, "ganglia.entity.manager", "The Ganglia manager entity");

}