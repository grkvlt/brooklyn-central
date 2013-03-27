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
import brooklyn.entity.basic.EntityLocal;
import brooklyn.event.AttributeSensor;
import brooklyn.event.Sensor;
import brooklyn.event.feed.ConfigToAttributes;
import brooklyn.util.flags.TypeCoercions;

/**
 * A {@link Sensor} describing an attribute that can be configured with inputs that are used to derive the final value.
 * <p>
 * The {@link ConfigKey} will have the same name and description as the sensor but not necessarily the same type.
 * Conversion to set the sensor value from the config key must be supplied in a subclass.
 */
public abstract class AttributeSensorAndConfigKey<ConfigType, SensorType> extends BasicAttributeSensor<SensorType> 
        implements ConfigKey.HasConfigKey<ConfigType> {
    private static final long serialVersionUID = -3103809215973264600L;

    private ConfigKey<ConfigType> configKey;

    public AttributeSensorAndConfigKey(Class<ConfigType> configType, Class<SensorType> sensorType, String name) {
        this(configType, sensorType, name, name, null);
    }

    public AttributeSensorAndConfigKey(Class<ConfigType> configType, Class<SensorType> sensorType, String name, String description) {
        this(configType, sensorType, name, description, null);
    }

    public AttributeSensorAndConfigKey(Class<ConfigType> configType, Class<SensorType> sensorType, String name, String description, Object defaultValue) {
        super(sensorType, name, description);
        configKey = new BasicConfigKey<ConfigType>(configType, name, description, TypeCoercions.coerce(defaultValue, configType));
    }

    public AttributeSensorAndConfigKey(AttributeSensorAndConfigKey<ConfigType,SensorType> orig, ConfigType defaultValue) {
        super(orig.getType(), orig.getName(), orig.getDescription());
        configKey = new BasicConfigKey<ConfigType>(orig.configKey.getType(), orig.getName(), orig.getDescription(), 
            TypeCoercions.coerce(defaultValue, orig.configKey.getType()));
    }

    public ConfigKey<ConfigType> getConfigKey() { return configKey; }

    public AttributeSensor<SensorType> asAttributeSensor() { return this; }

    /**
     * Returns the sensor value for this attribute on the given entity, if present,
     * otherwise works out what the sensor value should be based on the config key's value.
     * <p>
     * Calls to this may allocate resources (e.g. ports) so should be called only once and 
     * then (if non-null) assigned as the sensor's value.
     * <p>
     * <em>For this reason this method should generally not be invoked by callers except in tests and by the framework,
     * and similarly should not be overridden; implement convertConfigToSensor instead for single-execution calls.
     * the framework calls this from {@link EntityLocal#setAttribute(AttributeSensorAndConfigKey)} 
     * typically via {@link ConfigToAttributes#apply(EntityLocal)} e.g. from SoftwareProcess.preStart.</em> 
     */
    public SensorType getAsSensorValue(Entity e) {
        SensorType sensorValue = e.getAttribute(this);
        if (sensorValue!=null) return sensorValue;

        ConfigType v = ((EntityLocal)e).getConfig(this);
        if (v==null) v = configKey.getDefaultValue();
        try {
            return convertConfigToSensor(v, e);
        } catch (Throwable t) {
            throw new IllegalArgumentException("Cannot convert config value "+v+" for sensor "+this+": "+t, t);
        }
    }

    /**
     * Converts the given ConfigType value to the corresponding SensorType value, 
     * with respect to the given entity.
     * <p>
     * This is invoked after checks whether the entity already has a value for the sensor,
     * and the entity-specific config value is passed for convenience if set, 
     * otherwise the config key default value is passed for convenience.
     * <p>
     * This message should be allowed to return null if the conversion cannot be completed at this time.
     */
    protected abstract SensorType convertConfigToSensor(ConfigType value, Entity entity);

}
