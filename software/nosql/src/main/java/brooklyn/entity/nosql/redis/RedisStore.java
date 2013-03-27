package brooklyn.entity.nosql.redis;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import brooklyn.catalog.Catalog;
import brooklyn.config.ConfigKey;
import brooklyn.entity.Entity;
import brooklyn.entity.basic.Attributes;
import brooklyn.entity.basic.ConfigKeys;
import brooklyn.entity.basic.SoftwareProcess;
import brooklyn.entity.nosql.DataStore;
import brooklyn.entity.proxying.ImplementedBy;
import brooklyn.event.AttributeSensor;
import brooklyn.event.basic.BasicAttributeSensorAndConfigKey;
import brooklyn.event.basic.BasicConfigKey;
import brooklyn.event.basic.PortAttributeSensorAndConfigKey;
import brooklyn.util.flags.SetFromFlag;

/**
 * An entity that represents a Redis key-value store service.
 */
@Catalog(name="Redis Server", description="Redis is an open-source, networked, in-memory, key-value data store with optional durability", iconUrl="classpath:///redis-logo.jpeg")
@ImplementedBy(RedisStoreImpl.class)
public interface RedisStore extends SoftwareProcess, DataStore {

    @SetFromFlag("version")
    ConfigKey<String> SUGGESTED_VERSION = ConfigKeys.newConfigKey(SoftwareProcess.SUGGESTED_VERSION, "2.6.7");

    @SetFromFlag("downloadUrl")
    BasicAttributeSensorAndConfigKey<String> DOWNLOAD_URL = ConfigKeys.newAttributeSensorAndConfigKey(
            SoftwareProcess.DOWNLOAD_URL, "http://redis.googlecode.com/files/redis-${version}.tar.gz");

    @SetFromFlag("redisPort")
    PortAttributeSensorAndConfigKey REDIS_PORT = ConfigKeys.newPortAttributeSensorAndConfigKey("redis.port", "Redis port number", 6379);

    @SetFromFlag("redisConfigTemplateUrl")
    ConfigKey<String> REDIS_CONFIG_TEMPLATE_URL = ConfigKeys.newConfigKey(
            "redis.config.templateUrl", "Template file (in freemarker format) for the redis.conf config file", 
            "classpath://brooklyn/entity/nosql/redis/redis.conf");

    AttributeSensor<Integer> UPTIME = Attributes.newAttributeSensor("redis.uptime", "Redis uptime in seconds");

    // See http://redis.io/commands/info for details of all information available
    AttributeSensor<Integer> TOTAL_CONNECTIONS_RECEIVED = Attributes.newAttributeSensor("redis.connections.received.total", "Total number of connections accepted by the server");
    AttributeSensor<Integer> TOTAL_COMMANDS_PROCESSED = Attributes.newAttributeSensor("redis.commands.processed.total", "Total number of commands processed by the server");
    AttributeSensor<Integer> EXPIRED_KEYS = Attributes.newAttributeSensor("redis.keys.expired", "Total number of key expiration events");
    AttributeSensor<Integer> EVICTED_KEYS = Attributes.newAttributeSensor("redis.keys.evicted", "Number of evicted keys due to maxmemory limit");
    AttributeSensor<Integer> KEYSPACE_HITS = Attributes.newAttributeSensor("redis.keyspace.hits", "Number of successful lookup of keys in the main dictionary");
    AttributeSensor<Integer> KEYSPACE_MISSES = Attributes.newAttributeSensor("redis.keyspace.misses", "Number of failed lookup of keys in the main dictionary");

    String getAddress();

    Integer getRedisPort();

}
