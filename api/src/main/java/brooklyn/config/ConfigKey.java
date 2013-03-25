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
package brooklyn.config;

import java.util.Collection;

/**
 * Represents the name of a piece of typed configuration data for an entity.
 * <p>
 * Two {@link ConfigKey keys} should be considered equal if they have the same {@link #getName() FQN}.
 */
public interface ConfigKey<T> {
    /**
     * Returns the description of the configuration parameter, for display.
     */
    String getDescription();

    /**
     * Returns the name of the configuration parameter, in a dot-separated namespace (FQN).
     */
    String getName();

    /**
     * Returns the constituent parts of the configuration parameter name as a {@link Collection}.
     */
    Collection<String> getNameParts();

    /**
     * Returns the type of the configuration parameter data.
     */
    Class<T> getType();

    /**
     * Returns the name of of the configuration parameter data type, as a {@link String}.
     */
    String getTypeName();

    /**
     * Returns the default value of the configuration parameter.
     */
    T getDefaultValue();

    /**
     * Returns true if a default configuration value has been set.
     */
    boolean hasDefaultValue();

    /**
     * Returns true if the configuration can be changed at runtime.
     */
    boolean isReconfigurable();

    /**
     * Interface for elements which want to be treated as a config key without actually being one.
     * 
     * @see AttributeSensorAndConfigKey
     */
    public interface HasConfigKey<T> {
        public ConfigKey<T> getConfigKey();
    }
}
