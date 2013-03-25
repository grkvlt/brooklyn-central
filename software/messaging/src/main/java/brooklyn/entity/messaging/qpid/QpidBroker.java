package brooklyn.entity.messaging.qpid;

import java.util.Map;

import brooklyn.catalog.Catalog;
import brooklyn.config.ConfigKey;
import brooklyn.entity.basic.Attributes;
import brooklyn.entity.basic.ConfigKeys;
import brooklyn.entity.basic.SoftwareProcess;
import brooklyn.entity.java.UsesJmx;
import brooklyn.entity.messaging.MessageBroker;
import brooklyn.entity.messaging.amqp.AmqpServer;
import brooklyn.entity.proxying.ImplementedBy;
import brooklyn.event.basic.BasicAttributeSensorAndConfigKey;
import brooklyn.event.basic.PortAttributeSensorAndConfigKey;
import brooklyn.util.flags.SetFromFlag;

/**
 * An {@link brooklyn.entity.Entity} that represents a single Qpid broker instance, using AMQP 0-10.
 */
@Catalog(name="Qpid Broker", description="Apache Qpid is an open-source messaging system, implementing the Advanced Message Queuing Protocol (AMQP)", iconUrl="classpath:///qpid-logo.jpeg")
@ImplementedBy(QpidBrokerImpl.class)
public interface QpidBroker extends SoftwareProcess, MessageBroker, UsesJmx, AmqpServer {

    /* Qpid runtime file locations for convenience. */

    String CONFIG_XML = "etc/config.xml";
    String VIRTUALHOSTS_XML = "etc/virtualhosts.xml";
    String PASSWD = "etc/passwd";

    @SetFromFlag("version")
    ConfigKey<String> SUGGESTED_VERSION = ConfigKeys.newConfigKey(SoftwareProcess.SUGGESTED_VERSION, "0.20");

    @SetFromFlag("downloadUrl")
    BasicAttributeSensorAndConfigKey<String> DOWNLOAD_URL = ConfigKeys.newAttributeSensorAndConfigKey(
            Attributes.DOWNLOAD_URL, "http://download.nextag.com/apache/qpid/${version}/qpid-java-broker-${version}.tar.gz");

    @SetFromFlag("amqpPort")
    PortAttributeSensorAndConfigKey AMQP_PORT = AmqpServer.AMQP_PORT;

    @SetFromFlag("virtualHost")
    BasicAttributeSensorAndConfigKey<String> VIRTUAL_HOST_NAME = AmqpServer.VIRTUAL_HOST_NAME;

    @SetFromFlag("amqpVersion")
    BasicAttributeSensorAndConfigKey<String> AMQP_VERSION = ConfigKeys.newAttributeSensorAndConfigKey(AmqpServer.AMQP_VERSION, AmqpServer.AMQP_0_10);

    @SetFromFlag("httpManagementPort")
    PortAttributeSensorAndConfigKey HTTP_MANAGEMENT_PORT = ConfigKeys.newPortAttributeSensorAndConfigKey("qpid.http-management.port", "Qpid HTTP management plugin port");

    /** Files to be copied to the server, map of "subpath/file.name": "classpath://foo/file.txt" (or other url) */
    @SetFromFlag("runtimeFiles")
    ConfigKey<Map<String, String>> RUNTIME_FILES = ConfigKeys.newConfigKey("qpid.files.runtime", "Map of files to be copied, keyed by destination name relative to runDir");

    /** Templates to be filled in and then copied to the server. See {@link #RUNTIME_FILES}. */
    @SetFromFlag("runtimeTemplates")
    ConfigKey<Map<String, String>> RUNTIME_TEMPLATES = ConfigKeys.newConfigKey("qpid.templates.runtime", "Map of templates to be filled in and copied, keyed by destination name relative to runDir");

    @SetFromFlag("jmxUser")
    BasicAttributeSensorAndConfigKey<String> JMX_USER = ConfigKeys.newAttributeSensorAndConfigKey(Attributes.JMX_USER, "admin");

    @SetFromFlag("jmxPassword")
    BasicAttributeSensorAndConfigKey<String> JMX_PASSWORD = ConfigKeys.newAttributeSensorAndConfigKey(Attributes.JMX_PASSWORD, "admin");
}
