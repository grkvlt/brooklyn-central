package brooklyn.entity.nosql.mongodb;

import org.bson.BasicBSONObject;

import brooklyn.catalog.Catalog;
import brooklyn.config.ConfigKey;
import brooklyn.entity.basic.Attributes;
import brooklyn.entity.basic.ConfigKeys;
import brooklyn.entity.basic.SoftwareProcess;
import brooklyn.entity.proxying.ImplementedBy;
import brooklyn.event.AttributeSensor;
import brooklyn.event.basic.BasicAttributeSensorAndConfigKey;
import brooklyn.event.basic.PortAttributeSensorAndConfigKey;
import brooklyn.util.flags.SetFromFlag;

@Catalog(name="MongoDB Server", description="MongoDB (from \"humongous\") is a scalable, high-performance, open source NoSQL database", iconUrl="classpath:///mongodb-logo.png")
@ImplementedBy(MongoDbServerImpl.class)
public interface MongoDbServer extends SoftwareProcess {

    @SetFromFlag("version")
    ConfigKey<String> SUGGESTED_VERSION = ConfigKeys.newConfigKey(SoftwareProcess.SUGGESTED_VERSION, "2.2.3");

    // e.g. http://fastdl.mongodb.org/linux/mongodb-linux-x86_64-2.2.2.tgz,
    // http://fastdl.mongodb.org/osx/mongodb-osx-x86_64-2.2.2.tgz
    // http://downloads.mongodb.org/win32/mongodb-win32-x86_64-1.8.5.zip
    // Note Windows download is a zip.
    @SetFromFlag("downloadUrl")
    BasicAttributeSensorAndConfigKey<String> DOWNLOAD_URL = ConfigKeys.newAttributeSensorAndConfigKey(
            SoftwareProcess.DOWNLOAD_URL, "http://fastdl.mongodb.org/${driver.osDir}/${driver.osTag}-${version}.tgz");

    @SetFromFlag("port")
    PortAttributeSensorAndConfigKey PORT =
            ConfigKeys.newPortAttributeSensorAndConfigKey("mongodb.server.port", "Server port", "27017+");

    @SetFromFlag("dataDirectory")
    ConfigKey<String> DATA_DIRECTORY = ConfigKeys.newConfigKey("mongodb.data.directory", "Data directory to store MongoDB journals");

    @SetFromFlag("mongodbConfTemplateUrl")
    ConfigKey<String> MONGODB_CONF_TEMPLATE_URL = ConfigKeys.newConfigKey(
            "mongodb.config.url", "Template file (in freemarker format) for a a Mongo configuration file",
            "classpath://brooklyn/entity/nosql/mongodb/default-mongodb.conf");

    // Can also treat this as a Map
    AttributeSensor<BasicBSONObject> STATUS = Attributes.newAttributeSensor("mongodb.server.status", "Server status");

    AttributeSensor<Double> UPTIME_SECONDS = Attributes.newAttributeSensor("mongodb.server.uptime", "Server uptime in seconds");

    AttributeSensor<Long> OPCOUNTERS_INSERTS = Attributes.newAttributeSensor("mongodb.server.opcounters.insert", "Server inserts");

    AttributeSensor<Long> OPCOUNTERS_QUERIES = Attributes.newAttributeSensor("mongodb.server.opcounters.query", "Server queries");

    AttributeSensor<Long> OPCOUNTERS_UPDATES = Attributes.newAttributeSensor("mongodb.server.opcounters.update", "Server updates");

    AttributeSensor<Long> OPCOUNTERS_DELETES = Attributes.newAttributeSensor("mongodb.server.opcounters.delete", "Server deletes");

    AttributeSensor<Long> OPCOUNTERS_GETMORE = Attributes.newAttributeSensor("mongodb.server.opcounters.getmore", "Server getmores");

    AttributeSensor<Long> OPCOUNTERS_COMMAND = Attributes.newAttributeSensor("mongodb.server.opcounters.command", "Server commands");

    AttributeSensor<Long> NETWORK_BYTES_IN = Attributes.newAttributeSensor("mongodb.server.network.bytesIn", "Server incoming network traffic (in bytes)");

    AttributeSensor<Long> NETWORK_BYTES_OUT = Attributes.newAttributeSensor("mongodb.server.network.bytesOut", "Server outgoing network traffic (in bytes)");

    AttributeSensor<Long> NETWORK_NUM_REQUESTS = Attributes.newAttributeSensor("mongodb.server.network.numRequests", "Server network requests");

    Integer getServerPort();

}
