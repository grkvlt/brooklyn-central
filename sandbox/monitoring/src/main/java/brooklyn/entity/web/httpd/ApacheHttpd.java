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
package brooklyn.entity.web.httpd;

import brooklyn.entity.basic.Attributes;
import brooklyn.entity.basic.SoftwareProcess;
import brooklyn.entity.proxying.ImplementedBy;
import brooklyn.event.basic.BasicConfigKey;
import brooklyn.event.basic.PortAttributeSensorAndConfigKey;
import brooklyn.util.flags.SetFromFlag;

/**
 * An {@link brooklyn.entity.Entity} that represents a single Apache {@code httpd} instance.
 */
@ImplementedBy(ApacheHttpdImpl.class)
public interface ApacheHttpd extends SoftwareProcess {

    @SetFromFlag("version")
    BasicConfigKey<String> SUGGESTED_VERSION =
            new BasicConfigKey<String>(SoftwareProcess.SUGGESTED_VERSION, "7.0.34");

    PortAttributeSensorAndConfigKey HTTP_PORT = Attributes.HTTP_PORT;

    Integer getHttpPort();

}
