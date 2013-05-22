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

import brooklyn.config.ConfigKey;
import brooklyn.entity.basic.SoftwareProcess;
import brooklyn.entity.proxying.ImplementedBy;
import brooklyn.entity.webapp.WebAppServiceConstants;
import brooklyn.event.AttributeSensor;
import brooklyn.event.basic.BasicAttributeSensorAndConfigKey;
import brooklyn.event.basic.BasicConfigKey;
import brooklyn.event.basic.PortAttributeSensorAndConfigKey;
import brooklyn.util.flags.SetFromFlag;

import com.google.common.collect.ImmutableList;

/**
 * An {@link brooklyn.entity.Entity} that represents a single Apache {@code httpd} instance.
 */
@ImplementedBy(ApacheHttpdImpl.class)
public interface ApacheHttpd extends SoftwareProcess {

    @SetFromFlag("version")
    ConfigKey<String> SUGGESTED_VERSION = new BasicConfigKey<String>(SoftwareProcess.SUGGESTED_VERSION, "2.4.4");

    @SetFromFlag("downloadUrl")
    BasicAttributeSensorAndConfigKey<String> DOWNLOAD_URL = new BasicAttributeSensorAndConfigKey<String>(
            SoftwareProcess.DOWNLOAD_URL, "${driver.mirrorUrl}/httpd-${version}.tar.gz");

    /** download mirror, if desired */
    @SetFromFlag("mirrorUrl")
    ConfigKey<String> MIRROR_URL = new BasicConfigKey<String>(String.class, "httpd.install.mirror.url", "URL of mirror", "http://www.mirrorservice.org/sites/ftp.apache.org/httpd");

    @SetFromFlag("tgzUrl")
    ConfigKey<String> TGZ_URL = new BasicConfigKey<String>(String.class, "httpd.install.tgzUrl", "URL of TGZ download file");

    @SetFromFlag("siteContentTgz")
    ConfigKey<String> SITE_CONTENT_TGZ_URL = new BasicConfigKey<String>(String.class, "httpd.site.contentUrl.tgz", "URL of site content archive file (as .tar.gz)");

    @SetFromFlag("siteContentZip")
    ConfigKey<String> SITE_CONTENT_ZIP_URL = new BasicConfigKey<String>(String.class, "httpd.site.contentUrl.zip", "URL of site content archive file (as .zip)");

    @SetFromFlag("httpPort")
    PortAttributeSensorAndConfigKey HTTP_PORT = new PortAttributeSensorAndConfigKey("http.port", "HTTP port", ImmutableList.of(80,"8080+"));

    AttributeSensor<String> ROOT_URL = WebAppServiceConstants.ROOT_URL;

    AttributeSensor<Integer> REQUEST_COUNT = WebAppServiceConstants.REQUEST_COUNT;
    AttributeSensor<Integer> ERROR_COUNT = WebAppServiceConstants.ERROR_COUNT;

    Integer getHttpPort();

}
