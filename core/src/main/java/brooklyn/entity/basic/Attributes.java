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
import brooklyn.event.basic.BasicSensor;
import brooklyn.event.basic.PortAttributeSensorAndConfigKey;
import brooklyn.location.basic.PortRanges;

import com.google.common.collect.ImmutableList;
import com.google.common.reflect.TypeToken;

/**
 * Dictionary of {@link Sensor} entries.
 */
@SuppressWarnings("serial")
public class Attributes {

    public static final BasicSensor<Void> LOCATION_CHANGED = newNotificationSensor(
            Void.class, "entity.locationChanged", "Indicates that an entity's location has been changed");

    /**
     * Application information sensors.
     * 
     * @deprecated since 0.5; see {@link ConfigKeys#SUGGESTED_VERSION}
     */
    @Deprecated
    public static final AttributeSensor<String> VERSION = newAttributeSensor(String.class, "version", "Version information");

    public static final BasicAttributeSensorAndConfigKey<String> DOWNLOAD_URL = ConfigKeys.newAttributeSensorAndConfigKey(
            String.class, "download.url", "URL pattern for downloading the installer (will substitute things like ${version} automatically)");

    public static final BasicAttributeSensorAndConfigKey<Map<String, String>> DOWNLOAD_ADDON_URLS = ConfigKeys.newAttributeSensorAndConfigKey(
            new TypeToken<Map<String, String>>() { }, "download.addon.urls", "URL patterns for downloading named add-ons (will substitute things like ${version} automatically)");

    /*
     * JMX attributes.
     */

    /** 1099 is standard, sometimes 9999. */
    public static final PortAttributeSensorAndConfigKey JMX_PORT = ConfigKeys.newPortAttributeSensorAndConfigKey(
            "jmx.port", "JMX port (RMI registry port)", PortRanges.fromString("1099, 31099+"));

    /** Usually chosen by java; setting this will often not have any effect. */
    public static final PortAttributeSensorAndConfigKey RMI_SERVER_PORT = ConfigKeys.newPortAttributeSensorAndConfigKey(
            "rmi.server.port", "RMI server port", PortRanges.fromString("9001, 39001+"));

    /** @deprecated since 0.4, use {@link #RMI_REGISTRY_PORT} instead */
    @Deprecated
    public static final PortAttributeSensorAndConfigKey RMI_PORT = RMI_SERVER_PORT;

    public static final BasicAttributeSensorAndConfigKey<String> JMX_USER = ConfigKeys.newAttributeSensorAndConfigKey(
            String.class, "jmx.user", "JMX username");

    public static final BasicAttributeSensorAndConfigKey<String> JMX_PASSWORD = ConfigKeys.newAttributeSensorAndConfigKey(
            String.class, "jmx.password", "JMX password");

    public static final BasicAttributeSensorAndConfigKey<String> JMX_CONTEXT = ConfigKeys.newAttributeSensorAndConfigKey(
            String.class, "jmx.context", "JMX context path", "jmxrmi");

    public static final BasicAttributeSensorAndConfigKey<String> JMX_SERVICE_URL = ConfigKeys.newAttributeSensorAndConfigKey(
            String.class, "jmx.serviceurl", "The URL for connecting to the MBean Server");

    /*
     * Port number attributes.
     */

    public static final AttributeSensor<List<Integer>> PORT_NUMBERS = newAttributeSensor(
            new TypeToken<List<Integer>>() { }, "port.list", "List of port numbers");

    public static final AttributeSensor<List<Sensor<Integer>>> PORT_SENSORS = newAttributeSensor(
            new TypeToken<List<Sensor<Integer>>>() { }, "port.list.sensors", "List of port number attributes");

    public static final PortAttributeSensorAndConfigKey HTTP_PORT = new PortAttributeSensorAndConfigKey(
            "http.port", "HTTP port", ImmutableList.of(8080,"18080+"));

    public static final PortAttributeSensorAndConfigKey HTTPS_PORT = new PortAttributeSensorAndConfigKey(
            "https.port", "HTTP port (with SSL/TLS)", ImmutableList.of(8443,"18443+"));

    public static final PortAttributeSensorAndConfigKey SSH_PORT = new PortAttributeSensorAndConfigKey("ssh.port", "SSH port", 22);
    public static final PortAttributeSensorAndConfigKey SMTP_PORT = new PortAttributeSensorAndConfigKey("smtp.port", "SMTP port", 25);
    public static final PortAttributeSensorAndConfigKey DNS_PORT = new PortAttributeSensorAndConfigKey("dns.port", "DNS port", 53);
    public static final PortAttributeSensorAndConfigKey AMQP_PORT = new PortAttributeSensorAndConfigKey("amqp.port", "AMQP port", "5672+");

    /*
     * Location/connection attributes.
     */

    public static final AttributeSensor<String> HOSTNAME = newAttributeSensor(String.class, "host.name", "Host name");
    public static final AttributeSensor<String> ADDRESS = newAttributeSensor(String.class, "host.address", "Host IP address");
	
    /*
     * Lifecycle attributes
     */

    public static final AttributeSensor<Lifecycle> SERVICE_STATE = newAttributeSensor(
            Lifecycle.class, "service.state", "Service lifecycle state");

    public static final AttributeSensor<String> LOG_FILE_LOCATION = newAttributeSensor(
            String.class, "log.location", "Log file location (optional)");

    /*
     * Static methods to build new AttributeSensor instances.
     */

    public static <T> AttributeSensor<T> newAttributeSensor(Class<T> type, String name) {
        return new BasicAttributeSensor<T>(type, name);
    }

    public static <T> AttributeSensor<T> newAttributeSensor(Class<T> type, String name, String description) {
        return new BasicAttributeSensor<T>(type, name, description);
    }

    public static <T> AttributeSensor<T> newAttributeSensor(TypeToken<T> type, String name) {
        return new BasicAttributeSensor<T>(type, name);
    }

    public static <T> AttributeSensor<T> newAttributeSensor(TypeToken<T> type, String name, String description) {
        return new BasicAttributeSensor<T>(type, name, description);
    }

    public static <T> AttributeSensor<T> newAttributeSensor(String name) {
        return new BasicAttributeSensor<T>(name);
    }

    public static <T> AttributeSensor<T> newAttributeSensor(String name, String description) {
        return new BasicAttributeSensor<T>(name, description);
    }

    /*
     * Static methods to build new Sensor instances.
     */

    public static <T> Sensor<T> newNotificationSensor(Class<T> type, String name) {
        return new BasicNotificationSensor<T>(type, name);
    }

    public static <T> Sensor<T> newNotificationSensor(Class<T> type, String name, String description) {
        return new BasicNotificationSensor<T>(type, name, description);
    }

    public static <T> Sensor<T> newNotificationSensor(TypeToken<T> type, String name) {
        return new BasicNotificationSensor<T>(type, name);
    }

    public static <T> Sensor<T> newNotificationSensor(TypeToken<T> type, String name, String description) {
        return new BasicNotificationSensor<T>(type, name, description);
    }

    public static <T> Sensor<T> newNotificationSensor(String name) {
        return new BasicNotificationSensor<T>(name);
    }

    public static <T> Sensor<T> newNotificationSensor(String name, String description) {
        return new BasicNotificationSensor<T>(name, description);
    }

    public static <T> Sensor<T> newSensor(Class<T> type, String name) {
        return new BasicSensor<T>(type, name);
    }

    public static <T> Sensor<T> newSensor(Class<T> type, String name, String description) {
        return new BasicSensor<T>(type, name, description);
    }

    public static <T> Sensor<T> newSensor(TypeToken<T> type, String name) {
        return new BasicSensor<T>(type, name);
    }

    public static <T> Sensor<T> newSensor(TypeToken<T> type, String name, String description) {
        return new BasicSensor<T>(type, name, description);
    }

    public static <T> Sensor<T> newSensor(String name) {
        return new BasicSensor<T>(name);
    }

    public static <T> Sensor<T> newSensor(String name, String description) {
        return new BasicSensor<T>(name, description);
    }

}
