package brooklyn.event.basic;

import java.util.Map;

import brooklyn.config.ConfigKey;
import brooklyn.management.ExecutionContext;

@SuppressWarnings("rawtypes")
public class SubElementConfigKey<T> extends BasicConfigKey<T> {

    private static final long serialVersionUID = -1587240876351450665L;

    public final ConfigKey parent;

    public SubElementConfigKey(ConfigKey parent, String name) {
        this(parent, name, name, null);
    }
    public SubElementConfigKey(ConfigKey parent, String name, String description) {
        this(parent, name, description, null);
    }
    public SubElementConfigKey(ConfigKey parent, String name, String description, T defaultValue) {
        super(name, description, defaultValue);
        this.parent = parent;
    }

    @Override
    public T extractValue(Map vals, ExecutionContext exec) {
        return super.extractValue(vals, exec);
    }

    @Override
    public boolean isSet(Map<?,?> vals) {
        return super.isSet(vals);
    }
}
