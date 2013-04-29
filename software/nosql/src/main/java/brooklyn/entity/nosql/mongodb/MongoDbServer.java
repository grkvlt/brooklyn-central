package brooklyn.entity.nosql.mongodb;

import org.bson.BasicBSONObject;

import brooklyn.catalog.Catalog;
import brooklyn.config.ConfigKey;
import brooklyn.entity.basic.SoftwareProcess;
import brooklyn.entity.proxying.ImplementedBy;
import brooklyn.event.AttributeSensor;
import brooklyn.event.basic.BasicAttributeSensor;
import brooklyn.event.basic.BasicAttributeSensorAndConfigKey;
import brooklyn.event.basic.BasicConfigKey;
import brooklyn.event.basic.PortAttributeSensorAndConfigKey;
import brooklyn.util.flags.SetFromFlag;

@Catalog(name="MongoDB Server", description="MongoDB (from \"humongous\") is a scalable, high-performance, open source NoSQL database", iconUrl="classpath:///mongodb-logo.png")
@ImplementedBy(MongoDbServerImpl.class)
public interface MongoDbServer extends SoftwareProcess {

    @SetFromFlag("version")
    ConfigKey<String> SUGGESTED_VERSION = new BasicConfigKey<String>(SoftwareProcess.SUGGESTED_VERSION, "2.2.3");

    // e.g. http://fastdl.mongodb.org/linux/mongodb-linux-x86_64-2.2.2.tgz,
    // http://fastdl.mongodb.org/osx/mongodb-osx-x86_64-2.2.2.tgz
    // http://downloads.mongodb.org/win32/mongodb-win32-x86_64-1.8.5.zip
    // Note Windows download is a zip.
    @SetFromFlag("downloadUrl")
    BasicAttributeSensorAndConfigKey<String> DOWNLOAD_URL = new BasicAttributeSensorAndConfigKey<String>(
            SoftwareProcess.DOWNLOAD_URL, "http://fastdl.mongodb.org/${driver.osDir}/${driver.osTag}-${version}.tgz");

    @SetFromFlag("port")
    PortAttributeSensorAndConfigKey PORT =
            new PortAttributeSensorAndConfigKey("mongodb.server.port", "Server port", "27017+");

    @SetFromFlag("dataDirectory")
    ConfigKey<String> DATA_DIRECTORY = new BasicConfigKey<String>("mongodb.data.directory", "Data directory to store MongoDB journals");

    @SetFromFlag("mongodbConfTemplateUrl")
    ConfigKey<String> MONGODB_CONF_TEMPLATE_URL = new BasicConfigKey<String>(
            "mongodb.config.url", "Template file (in freemarker format) for a a Mongo configuration file",
            "classpath://brooklyn/entity/nosql/mongodb/default-mongodb.conf");

    // Can also treat this as a Map
    AttributeSensor<BasicBSONObject> STATUS = new BasicAttributeSensor<BasicBSONObject>("mongodb.server.status", "Server status");

    AttributeSensor<Double> UPTIME_SECONDS = new BasicAttributeSensor<Double>("mongodb.server.uptime", "Server uptime in seconds");

    AttributeSensor<Long> OPCOUNTERS_INSERTS = new BasicAttributeSensor<Long>("mongodb.server.opcounters.insert", "Server inserts");

    AttributeSensor<Long> OPCOUNTERS_QUERIES = new BasicAttributeSensor<Long>("mongodb.server.opcounters.query", "Server queries");

    AttributeSensor<Long> OPCOUNTERS_UPDATES = new BasicAttributeSensor<Long>("mongodb.server.opcounters.update", "Server updates");

    AttributeSensor<Long> OPCOUNTERS_DELETES = new BasicAttributeSensor<Long>("mongodb.server.opcounters.delete", "Server deletes");

    AttributeSensor<Long> OPCOUNTERS_GETMORE = new BasicAttributeSensor<Long>("mongodb.server.opcounters.getmore", "Server getmores");

    AttributeSensor<Long> OPCOUNTERS_COMMAND = new BasicAttributeSensor<Long>("mongodb.server.opcounters.command", "Server commands");

    AttributeSensor<Long> NETWORK_BYTES_IN = new BasicAttributeSensor<Long>("mongodb.server.network.bytesIn", "Server incoming network traffic (in bytes)");

    AttributeSensor<Long> NETWORK_BYTES_OUT = new BasicAttributeSensor<Long>("mongodb.server.network.bytesOut", "Server outgoing network traffic (in bytes)");

    AttributeSensor<Long> NETWORK_NUM_REQUESTS = new BasicAttributeSensor<Long>("mongodb.server.network.numRequests", "Server network requests");

    Integer getServerPort();

}
