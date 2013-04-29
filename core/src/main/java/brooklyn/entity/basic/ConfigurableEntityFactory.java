package brooklyn.entity.basic;

import java.util.Map;

import brooklyn.config.ConfigKey;
import brooklyn.entity.Entity;

public interface ConfigurableEntityFactory<T extends Entity> extends EntityFactory<T> {
   ConfigurableEntityFactory<T> configure(Map flags);
   ConfigurableEntityFactory<T> configure(ConfigKey key, Object value);
   ConfigurableEntityFactory<T> configure(ConfigKey.HasConfigKey key, Object value);
   
   ConfigurableEntityFactory<T> setConfig(ConfigKey key, Object value);
   ConfigurableEntityFactory<T> setConfig(ConfigKey.HasConfigKey key, Object value);
}