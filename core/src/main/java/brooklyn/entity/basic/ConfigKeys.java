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
package brooklyn.entity.basic;

import java.util.List;
import java.util.Map;

import brooklyn.config.ConfigKey;
import brooklyn.config.ConfigUtils;
import brooklyn.event.basic.BasicAttributeSensorAndConfigKey;
import brooklyn.event.basic.BasicConfigKey;
import brooklyn.event.basic.ListConfigKey;
import brooklyn.event.basic.MapConfigKey;
import brooklyn.event.basic.PortAttributeSensorAndConfigKey;
import brooklyn.util.internal.ssh.SshTool;

import com.google.common.reflect.TypeToken;

/**
 * Dictionary of {@link ConfigKey} entries.
 */
public class ConfigKeys {

    public static final ConfigKey<String> BROOKLYN_DATA_DIR = newConfigKey(
            "brooklyn.datadir", "Directory for writing all brooklyn data", "/tmp/brooklyn-" + System.getProperty("user.name"));

    // FIXME Rename to VERSION, instead of SUGGESTED_VERSION? And declare as BasicAttributeSensorAndConfigKey?
    public static final ConfigKey<String> SUGGESTED_VERSION = newConfigKey("install.version", "Suggested version");
    public static final ConfigKey<String> SUGGESTED_INSTALL_DIR = newConfigKey("install.dir", "Suggested installation directory");
    public static final ConfigKey<String> SUGGESTED_RUN_DIR = newConfigKey("run.dir", "Suggested working directory for the running app");

    /**
     * Intention is to use this with {@link brooklyn.event.basic.DependentConfiguration#attributeWhenReady(brooklyn.entity.Entity, brooklyn.event.AttributeSensor) DependentConfiguration#attributeWhenReady}
     * to allow an entity's start to block until dependents are ready. This is particularly useful when we want to block until a dependent
     * component is up, but do not care about the its actual config values.
     */
    public static final ConfigKey<Boolean> START_LATCH = newConfigKey("start.latch", "Latch for blocking start until ready");
    public static final ConfigKey<Boolean> INSTALL_LATCH = newConfigKey("install.latch", "Latch for blocking install until ready");
    public static final ConfigKey<Boolean> CUSTOMIZE_LATCH = newConfigKey("customize.latch", "Latch for blocking customize until ready");
    public static final ConfigKey<Boolean> LAUNCH_LATCH = newConfigKey("launch.latch", "Latch for blocking launch until ready");

    public static final ConfigKey<Integer> START_TIMEOUT = newConfigKey("start.timeout", "Time to wait for SERVICE_UP to be set before failing (in seconds, default 60)", 60);

    /*
     * Selected properties from SshTool for external public access (e.g. putting on entities) 
     */

    public static final ConfigKey<String> SSH_TOOL_CLASS = ConfigUtils.prefixedKey(SshTool.BROOKLYN_CONFIG_KEY_PREFIX, SshTool.PROP_TOOL_CLASS);
    public static final ConfigKey<String> SSH_CONFIG_HOST = ConfigUtils.prefixedKey(SshTool.BROOKLYN_CONFIG_KEY_PREFIX, SshTool.PROP_HOST);
    public static final ConfigKey<Integer> SSH_CONFIG_PORT = ConfigUtils.prefixedKey(SshTool.BROOKLYN_CONFIG_KEY_PREFIX, SshTool.PROP_PORT);
    public static final ConfigKey<String> SSH_CONFIG_USER = ConfigUtils.prefixedKey(SshTool.BROOKLYN_CONFIG_KEY_PREFIX, SshTool.PROP_USER);
    public static final ConfigKey<String> SSH_CONFIG_PASSWORD = ConfigUtils.prefixedKey(SshTool.BROOKLYN_CONFIG_KEY_PREFIX, SshTool.PROP_PASSWORD);
    public static final ConfigKey<String> SSH_CONFIG_SCRIPT_DIR = ConfigUtils.prefixedKey(SshTool.BROOKLYN_CONFIG_KEY_PREFIX, SshTool.PROP_SCRIPT_DIR);
    public static final ConfigKey<String> SSH_CONFIG_SCRIPT_HEADER = ConfigUtils.prefixedKey(SshTool.BROOKLYN_CONFIG_KEY_PREFIX, SshTool.PROP_SCRIPT_HEADER);
    public static final ConfigKey<String> SSH_CONFIG_DIRECT_HEADER = ConfigUtils.prefixedKey(SshTool.BROOKLYN_CONFIG_KEY_PREFIX, SshTool.PROP_DIRECT_HEADER);

    /*
     * Static methods to build new AttributeSensorAndConfigKey instances.
     *
     * TODO Use an interface instead of the concrete implementation
     */

    public static <T> BasicAttributeSensorAndConfigKey<T> newAttributeSensorAndConfigKey(Class<T> type, String name) {
        return new BasicAttributeSensorAndConfigKey<T>(type, name);
    }

    public static <T> BasicAttributeSensorAndConfigKey<T> newAttributeSensorAndConfigKey(Class<T> type, String name, String description) {
        return new BasicAttributeSensorAndConfigKey<T>(type, name, description);
    }

    public static <T> BasicAttributeSensorAndConfigKey<T> newAttributeSensorAndConfigKey(Class<T> type, String name, String description, T defaultValue) {
        return new BasicAttributeSensorAndConfigKey<T>(type, name, description, defaultValue);
    }

    public static <T> BasicAttributeSensorAndConfigKey<T> newAttributeSensorAndConfigKey(TypeToken<T> type, String name) {
        return new BasicAttributeSensorAndConfigKey<T>(type, name);
    }

    public static <T> BasicAttributeSensorAndConfigKey<T> newAttributeSensorAndConfigKey(TypeToken<T> type, String name, String description) {
        return new BasicAttributeSensorAndConfigKey<T>(type, name, description);
    }

    public static <T> BasicAttributeSensorAndConfigKey<T> newAttributeSensorAndConfigKey(TypeToken<T> type, String name, String description, T defaultValue) {
        return new BasicAttributeSensorAndConfigKey<T>(type, name, description, defaultValue);
    }

    public static <T> BasicAttributeSensorAndConfigKey<T> newAttributeSensorAndConfigKey(String name) {
        return new BasicAttributeSensorAndConfigKey<T>(name);
    }

    public static <T> BasicAttributeSensorAndConfigKey<T> newAttributeSensorAndConfigKey(String name, String description) {
        return new BasicAttributeSensorAndConfigKey<T>(name, description);
    }

    public static <T> BasicAttributeSensorAndConfigKey<T> newAttributeSensorAndConfigKey(String name, String description, T defaultValue) {
        return new BasicAttributeSensorAndConfigKey<T>(name, description, defaultValue);
    }

    public static <T> BasicAttributeSensorAndConfigKey<T> newAttributeSensorAndConfigKey(BasicAttributeSensorAndConfigKey<T> orig, T defaultValue) {
        return new BasicAttributeSensorAndConfigKey<T>(orig, defaultValue);
    }

    /*
     * Static methods to build new ConfigKey instances.
     */
        
    public static <T> ConfigKey<T> newConfigKey(TypeToken<T> type, String name) {
        return new BasicConfigKey<T>(type, name);
    }

    public static <T> ConfigKey<T> newConfigKey(TypeToken<T> type, String name, String description) {
        return new BasicConfigKey<T>(type, name, description);
    }

    public static <T, V extends T> ConfigKey<T> newConfigKey(TypeToken<T> type, String name, String description, V defaultValue) {
        return new BasicConfigKey<T>(type, name, description, defaultValue);
    }

    public static <T> ConfigKey<T> newConfigKey(Class<T> type, String name) {
        return new BasicConfigKey<T>(type, name);
    }

    public static <T> ConfigKey<T> newConfigKey(Class<T> type, String name, String description) {
        return new BasicConfigKey<T>(type, name, description);
    }

    public static <T, V extends T> ConfigKey<T> newConfigKey(Class<T> type, String name, String description, V defaultValue) {
        return new BasicConfigKey<T>(type, name, description, defaultValue);
    }

    public static <T> ConfigKey<T> newConfigKey(ConfigKey<T> key, T defaultValue) {
        return new BasicConfigKey<T>(key, defaultValue);
    }

    public static <T> ConfigKey<T> newConfigKey(String name) {
        return new BasicConfigKey<T>(name);
    }

    public static <T> ConfigKey<T> newConfigKey(String name, String description) {
        return new BasicConfigKey<T>(name, description);
    }

    public static <T, V extends T> ConfigKey<T> newConfigKey(String name, String description, V defaultValue) {
        return new BasicConfigKey<T>(name, description, defaultValue);
    }

    /*
     * Static methods to build new PortAttributeSensorAndConfigKey instances.
     */

    public static PortAttributeSensorAndConfigKey newPortAttributeSensorAndConfigKey(String name) {
        return new PortAttributeSensorAndConfigKey(name);
    }

    public static PortAttributeSensorAndConfigKey newPortAttributeSensorAndConfigKey(String name, String description) {
        return new PortAttributeSensorAndConfigKey(name, description);
    }

    public static PortAttributeSensorAndConfigKey newPortAttributeSensorAndConfigKey(String name, String description, Object defaultValue) {
        return new PortAttributeSensorAndConfigKey(name, description, defaultValue);
    }

    public static PortAttributeSensorAndConfigKey newPortAttributeSensorAndConfigKey(PortAttributeSensorAndConfigKey orig, Object defaultValue) {
        return new PortAttributeSensorAndConfigKey(orig, defaultValue);
    }

    /*
     * Static methods to build new MapConfigKey instances.
     */

    public static <V> MapConfigKey<V> newMapConfigKey(String name) {
        return new MapConfigKey<V>(name);
    }

    public static <V> MapConfigKey<V> newMapConfigKey(String name, String description) {
        return new MapConfigKey<V>(name, description);
    }

    public static <V> MapConfigKey<V> newMapConfigKey(String name, String description, Map<String, ? extends V> defaultValue) {
        return new MapConfigKey<V>(name, description, defaultValue);
    }

    public static <V> MapConfigKey<V> newMapConfigKey(MapConfigKey<V> orig, Map<String, ? extends V> defaultValue) {
        return new MapConfigKey<V>(orig, defaultValue);
    }

    /*
     * Static methods to build new ListConfigKey instances.
     */

    public static <V> ListConfigKey<V> newListConfigKey(String name) {
        return new ListConfigKey<V>(name);
    }

    public static <V> ListConfigKey<V> newListConfigKey(String name, String description) {
        return new ListConfigKey<V>(name, description);
    }

    public static <V> ListConfigKey<V> newListConfigKey(String name, String description, List<? extends V> defaultValue) {
        return new ListConfigKey<V>(name, description, defaultValue);
    }

    public static <V> ListConfigKey<V> newListConfigKey(ListConfigKey<V> orig, List<? extends V> defaultValue) {
        return new ListConfigKey<V>(orig, defaultValue);
    }

}
