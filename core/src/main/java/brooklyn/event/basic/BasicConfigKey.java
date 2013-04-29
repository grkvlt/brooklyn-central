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
package brooklyn.event.basic;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import brooklyn.config.ConfigKey;
import brooklyn.entity.basic.ConfigKeys;
import brooklyn.management.ExecutionContext;
import brooklyn.util.internal.ConfigKeySelfExtracting;
import brooklyn.util.task.Tasks;

import com.google.common.base.Objects;
import com.google.common.base.Splitter;
import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import com.google.common.reflect.TypeToken;

public class BasicConfigKey<T> implements ConfigKeySelfExtracting<T>, Serializable {
    private static final long serialVersionUID = -1762014059150215376L;

    private static final Splitter dots = Splitter.on('.');

    public static <T> Builder<T> builder() {
        return new Builder<T>();
    }

    public static class Builder<T> {
        private String name;
        private String description;
        private T defaultValue;
        private boolean reconfigurable;

        public Builder<T> name(String val) {
            this.name = val; return this;
        }
        public Builder<T> description(String val) {
            this.description = val; return this;
        }
        public Builder<T> defaultValue(T val) {
            this.defaultValue = val; return this;
        }
        public Builder<T> reconfigurable(boolean val) {
            this.reconfigurable = val; return this;
        }
        public BasicConfigKey<T> build() {
            return new BasicConfigKey<T>(this);
        }
    }

    private TypeToken<T> token = new TypeToken<T>(getClass()) { };
    private String name;
    private String description;
    private T defaultValue;
    private boolean reconfigurable;

    // FIXME In groovy, fields were `public final` with a default constructor; do we need the gson?
    public BasicConfigKey() { /* for gson */ }

    public BasicConfigKey(String name) {
        this(name, name);
    }

    public BasicConfigKey(String name, String description) {
        this(name, description, null);
    }

    public BasicConfigKey(String name, String description, T defaultValue) {
        this.description = description;
        this.name = checkNotNull(name, "name");
        this.defaultValue = defaultValue;
        this.reconfigurable = false;
    }

    public BasicConfigKey(ConfigKey<T> key, T defaultValue) {
        this.description = key.getDescription();
        this.name = checkNotNull(key.getName(), "name");
        this.defaultValue = defaultValue;
        this.reconfigurable = false;
    }

    protected BasicConfigKey(Builder<T> builder) {
        this.name = checkNotNull(builder.name, "name");
        this.description = builder.description;
        this.defaultValue = builder.defaultValue;
        this.reconfigurable = builder.reconfigurable;
    }

    /** @see ConfigKey#getName() */
    public String getName() { return name; }

    /** @see ConfigKey#getTypeName() */
    public String getTypeName() { return getType().getName(); }

    /** @see ConfigKey#getType() */
    public Class<? super T> getType() {
        return getTypeToken().getRawType();
    }

    /** @see ConfigKey#getTypeToken() */
    public TypeToken<T> getTypeToken() { return token; }

    /** @see ConfigKey#getDescription() */
    public String getDescription() { return description; }

    /** @see ConfigKey#getDefaultValue() */
    public T getDefaultValue() { return defaultValue; }

    /** @see ConfigKey#hasDefaultValue() */
    public boolean hasDefaultValue() {
        return defaultValue != null;
    }

    @Override
    public boolean isReconfigurable() {
        return reconfigurable;
    }
    
    /** @see ConfigKey#getNameParts() */
    public Collection<String> getNameParts() {
        return Lists.newArrayList(dots.split(name));
    }
 
    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof BasicConfigKey)) return false;
        BasicConfigKey<?> o = (BasicConfigKey<?>) obj;
        
        return Objects.equal(name,  o.name);
    }
    
    @Override
    public int hashCode() {
        return Objects.hashCode(name);
    }
    
    @Override
    public String toString() {
        return String.format("%s[ConfigKey:%s]", name, getTypeName());
    }

    /**
     * Retrieves the value corresponding to this config key from the given map.
     * Could be overridden by more sophisticated config keys, such as MapConfigKey etc.
     */
    @SuppressWarnings("unchecked")
    @Override
    public T extractValue(Map<?,?> vals, ExecutionContext exec) {
        Object v = vals.get(this);
        try {
            return (T) resolveValue(v, exec);
        } catch (ExecutionException e) {
            throw Throwables.propagate(e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw Throwables.propagate(e);
        }
    }

    @Override
    public boolean isSet(Map<?,?> vals) {
        return vals.containsKey(this);
    }

    protected Object resolveValue(Object v, ExecutionContext exec) throws ExecutionException, InterruptedException {
        return Tasks.resolveValue(v, getType(), exec, "config "+name);
    }

    /**
     * Attempt to resolve the given value as the given type, waiting on futures,
     * and coercing as allowed by TypeCoercions.
     *
     * @deprecated in 0.4.0, use {@link Tasks#resolveValue(Object, Class, ExecutionContext)}
     */
    @Deprecated
    public static <T> T resolveValue(Object v, Class<T> type, ExecutionContext exec) throws ExecutionException, InterruptedException {
        return Tasks.resolveValue(v, type, exec);
    }

    /** @deprecated in 0.5.0, use {@link ConfigKeys#newConfigKey(String, String, String)}
     */
    @Deprecated
    public static class StringConfigKey extends BasicConfigKey<String> {
        private static final long serialVersionUID = 8207099275514012088L;

        public StringConfigKey(String name) {
            super(name);
        }
        public StringConfigKey(String name, String description, String defaultValue) {
            super(name, description, defaultValue);
        }
    }
    
}
