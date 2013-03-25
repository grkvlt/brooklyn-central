package brooklyn.entity.messaging.activemq;

import brooklyn.catalog.Catalog;
import brooklyn.config.ConfigKey;
import brooklyn.entity.basic.Attributes;
import brooklyn.entity.basic.ConfigKeys;
import brooklyn.entity.basic.SoftwareProcess;
import brooklyn.entity.java.UsesJmx;
import brooklyn.entity.messaging.MessageBroker;
import brooklyn.entity.proxying.ImplementedBy;
import brooklyn.event.basic.BasicAttributeSensorAndConfigKey;
import brooklyn.event.basic.PortAttributeSensorAndConfigKey;
import brooklyn.util.flags.SetFromFlag;
/**
 * An {@link brooklyn.entity.Entity} that represents a single ActiveMQ broker instance.
 */
@Catalog(name="ActiveMQ Broker", description="ActiveMQ is an open source message broker which fully implements the Java Message Service 1.1 (JMS)", iconUrl="classpath:///activemq-logo.png")
@ImplementedBy(ActiveMQBrokerImpl.class)
public interface ActiveMQBroker extends SoftwareProcess, MessageBroker, UsesJmx {

    @SetFromFlag("version")
    ConfigKey<String> SUGGESTED_VERSION = ConfigKeys.newConfigKey(SoftwareProcess.SUGGESTED_VERSION, "5.7.0");

    @SetFromFlag("downloadUrl")
    BasicAttributeSensorAndConfigKey<String> DOWNLOAD_URL = ConfigKeys.newAttributeSensorAndConfigKey(
            Attributes.DOWNLOAD_URL, "${driver.mirrorUrl}/${version}/apache-activemq-${version}-bin.tar.gz");

    /** Download mirror, if desired */
    @SetFromFlag("mirrorUrl")
    ConfigKey<String> MIRROR_URL = ConfigKeys.newConfigKey("activemq.install.mirror.url", "URL of mirror",
        "http://www.mirrorservice.org/sites/ftp.apache.org/activemq/apache-activemq");

    @SetFromFlag("openWirePort")
    PortAttributeSensorAndConfigKey OPEN_WIRE_PORT = ConfigKeys.newPortAttributeSensorAndConfigKey("openwire.port", "OpenWire port", "61616+");

    @SetFromFlag("jmxUser")
    BasicAttributeSensorAndConfigKey<String> JMX_USER = ConfigKeys.newAttributeSensorAndConfigKey(Attributes.JMX_USER, "admin");

    @SetFromFlag("jmxPassword")
    BasicAttributeSensorAndConfigKey<String> JMX_PASSWORD = ConfigKeys.newAttributeSensorAndConfigKey(Attributes.JMX_PASSWORD, "admin");

    @SetFromFlag("templateConfigurationUrl")
    BasicAttributeSensorAndConfigKey<String> TEMPLATE_CONFIGURATION_URL = ConfigKeys.newAttributeSensorAndConfigKey(
            "activemq.templateConfigurationUrl", "Template file (in freemarker format) for the conf/activemq.xml file", 
            "classpath://brooklyn/entity/messaging/activemq/activemq.xml");
}
