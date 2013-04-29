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

import brooklyn.config.ConfigKey;
import brooklyn.entity.Entity;
import brooklyn.event.Sensor;

import com.google.common.reflect.TypeToken;

/**
 * A {@link Sensor} describing an attribute that can be configured with a default value.
 * <p>
 * The {@link ConfigKey} has the same type, name and description as the sensor,
 * and is typically used to populate the sensor's value at runtime.
 */
public class BasicAttributeSensorAndConfigKey<T> extends AttributeSensorAndConfigKey<T, T> {
    /** serialVersionUID */
    private static final long serialVersionUID = 2743840637293430398L;

    public BasicAttributeSensorAndConfigKey(Class<T> type, String name) {
        this(type, name, name, null);
    }

    public BasicAttributeSensorAndConfigKey(Class<T> type, String name, String description) {
        this(type, name, description, null);
    }

    public BasicAttributeSensorAndConfigKey(Class<T> type, String name, String description, T defaultValue) {
        super(type, name, description, defaultValue);
    }

    @SuppressWarnings("unchecked")
    public BasicAttributeSensorAndConfigKey(TypeToken<T> type, String name) {
        this((Class<T>) type.getRawType(), name, name, null);
    }

    @SuppressWarnings("unchecked")
    public BasicAttributeSensorAndConfigKey(TypeToken<T> type, String name, String description) {
        this((Class<T>) type.getRawType(), name, description, null);
    }

    @SuppressWarnings("unchecked")
    public BasicAttributeSensorAndConfigKey(TypeToken<T> type, String name, String description, T defaultValue) {
        this((Class<T>) type.getRawType(), name, description, defaultValue);
    }

    @SuppressWarnings("serial")
    public BasicAttributeSensorAndConfigKey(String name) {
        this(new TypeToken<T>(BasicAttributeSensorAndConfigKey.class) { }, name, name, null);
    }

    @SuppressWarnings("serial")
    public BasicAttributeSensorAndConfigKey(String name, String description) {
        this(new TypeToken<T>(BasicAttributeSensorAndConfigKey.class) { }, name, description, null);
    }

    @SuppressWarnings("serial")
    public BasicAttributeSensorAndConfigKey(String name, String description, T defaultValue) {
        this(new TypeToken<T>(BasicAttributeSensorAndConfigKey.class) { }, name, description, defaultValue);
    }

    public BasicAttributeSensorAndConfigKey(BasicAttributeSensorAndConfigKey<T> orig, T defaultValue) {
        super(orig, defaultValue);
    }

    protected T convertConfigToSensor(T value, Entity entity) { return value; }

}
