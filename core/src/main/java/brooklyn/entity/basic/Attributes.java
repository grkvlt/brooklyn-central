/*
 * Copyright 2011-2013 by Cloudsoft Corp.
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
package brooklyn.entity.basic;

import java.util.List;
import java.util.Map;

import brooklyn.event.AttributeSensor;
import brooklyn.event.Sensor;
import brooklyn.event.basic.BasicAttributeSensor;
import brooklyn.event.basic.BasicAttributeSensorAndConfigKey;
import brooklyn.event.basic.BasicNotificationSensor;
import brooklyn.event.basic.PortAttributeSensorAndConfigKey;
import brooklyn.location.basic.PortRanges;

import com.google.common.collect.ImmutableList;

/**
 * Dictionary of {@link Sensor} entries.
 */
public class Attributes {

    public static final Sensor<Void> LOCATION_CHANGED = new BasicNotificationSensor<Void>(
            "entity.locationChanged", "Indicates that an entity's location has been changed");

    /**
     * Application information sensors.
     * 
     * @deprecated since 0.5; see {@link ConfigKeys#SUGGESTED_VERSION}
     */
    @Deprecated
    public static final AttributeSensor<String> VERSION = new BasicAttributeSensor<String>("version", "Version information");

    public static final BasicAttributeSensorAndConfigKey<String> DOWNLOAD_URL = new BasicAttributeSensorAndConfigKey<String>(
            "download.url", "URL pattern for downloading the installer (will substitute things like ${version} automatically)");

    public static final BasicAttributeSensorAndConfigKey<Map<String, String>> DOWNLOAD_ADDON_URLS = new BasicAttributeSensorAndConfigKey<Map<String, String>>(
            "download.addon.urls", "URL patterns for downloading named add-ons (will substitute things like ${version} automatically)");

    /*
     * JMX attributes.
     */

    /** 1099 is standard, sometimes 9999. */
    public static final PortAttributeSensorAndConfigKey JMX_PORT = new PortAttributeSensorAndConfigKey(
            "jmx.port", "JMX port (RMI registry port)", PortRanges.fromString("1099, 31099+"));

    /** Usually chosen by java; setting this will often not have any effect. */
    public static final PortAttributeSensorAndConfigKey RMI_SERVER_PORT = new PortAttributeSensorAndConfigKey(
            "rmi.server.port", "RMI server port", PortRanges.fromString("9001, 39001+"));

    /** @deprecated since 0.4, use {@link #RMI_REGISTRY_PORT} instead */
    @Deprecated
    public static final PortAttributeSensorAndConfigKey RMI_PORT = RMI_SERVER_PORT;

    public static final BasicAttributeSensorAndConfigKey<String> JMX_USER = new BasicAttributeSensorAndConfigKey<String>("jmx.user", "JMX username");

    public static final BasicAttributeSensorAndConfigKey<String> JMX_PASSWORD = new BasicAttributeSensorAndConfigKey<String>("jmx.password", "JMX password");

    public static final BasicAttributeSensorAndConfigKey<String> JMX_CONTEXT = new BasicAttributeSensorAndConfigKey<String>("jmx.context", "JMX context path", "jmxrmi");

    public static final BasicAttributeSensorAndConfigKey<String> JMX_SERVICE_URL = new BasicAttributeSensorAndConfigKey<String>("jmx.serviceurl", "The URL for connecting to the MBean Server");

    /*
     * Port number attributes.
     */

    public static final AttributeSensor<List<Integer>> PORT_NUMBERS = new BasicAttributeSensor<List<Integer>>("port.list", "List of port numbers");

    public static final AttributeSensor<List<Sensor<Integer>>> PORT_SENSORS = new BasicAttributeSensor<List<Sensor<Integer>>>("port.list.sensors", "List of port number attributes");

    public static final PortAttributeSensorAndConfigKey HTTP_PORT = new PortAttributeSensorAndConfigKey(
            "http.port", "HTTP port", ImmutableList.of(8080, "18080+"));

    public static final PortAttributeSensorAndConfigKey HTTPS_PORT = new PortAttributeSensorAndConfigKey(
            "https.port", "HTTP port (with SSL/TLS)", ImmutableList.of(8443, "18443+"));

    public static final PortAttributeSensorAndConfigKey SSH_PORT = new PortAttributeSensorAndConfigKey("ssh.port", "SSH port", 22);
    public static final PortAttributeSensorAndConfigKey SMTP_PORT = new PortAttributeSensorAndConfigKey("smtp.port", "SMTP port", 25);
    public static final PortAttributeSensorAndConfigKey DNS_PORT = new PortAttributeSensorAndConfigKey("dns.port", "DNS port", 53);
    public static final PortAttributeSensorAndConfigKey AMQP_PORT = new PortAttributeSensorAndConfigKey("amqp.port", "AMQP port", "5672+");

    /*
     * Location/connection attributes.
     */

    public static final AttributeSensor<String> HOSTNAME = new BasicAttributeSensor<String>("host.name", "Host name");
    public static final AttributeSensor<String> ADDRESS = new BasicAttributeSensor<String>("host.address", "Host IP address");
	
    /*
     * Lifecycle attributes
     */

    public static final AttributeSensor<Lifecycle> SERVICE_STATE = new BasicAttributeSensor<Lifecycle>("service.state", "Service lifecycle state");

    public static final AttributeSensor<String> LOG_FILE_LOCATION = new BasicAttributeSensor<String>("log.location", "Log file location (optional)");

}
