package brooklyn.entity.nosql.redis;

import brooklyn.config.ConfigKey;
import brooklyn.entity.proxying.ImplementedBy;
import brooklyn.event.basic.BasicConfigKey;
import brooklyn.util.flags.SetFromFlag;

/**
 * A {@link RedisStore} configured as a slave.
 */
@ImplementedBy(RedisSlaveImpl.class)
public interface RedisSlave extends RedisStore {

    @SetFromFlag("master")
    ConfigKey<RedisStore> MASTER = new BasicConfigKey<RedisStore>("redis.master", "Redis master");

    @SetFromFlag("redisConfigTemplateUrl")
    ConfigKey<String> REDIS_CONFIG_TEMPLATE_URL = new BasicConfigKey<String>(
            "redis.config.templateUrl", "Template file (in freemarker format) for the redis.conf config file", 
            "classpath://brooklyn/entity/nosql/redis/slave.conf");

    RedisStore getMaster();

}
