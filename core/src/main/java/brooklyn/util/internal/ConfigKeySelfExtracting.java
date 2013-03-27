package brooklyn.util.internal;

import java.util.Map;

import brooklyn.management.ExecutionContext;

/**
 * Interface for resolving key values. Typically implemented by the config key,
 * but discouraged for external usage.
 *
 * TODO replace by brooklyn.config.ConfigKey, when we removed the deprecated one
 */
public interface ConfigKeySelfExtracting<T> extends brooklyn.config.ConfigKey<T> {

    /**
     * Extracts the value for this config key from the given map.
     */
    T extractValue(Map<?,?> configMap, ExecutionContext exec);

    /**
     * @return True if there is an entry in the configMap that could be extracted
     */
    boolean isSet(Map<?,?> configMap);

}
