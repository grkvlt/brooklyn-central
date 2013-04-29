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

import brooklyn.config.ConfigKey;
import brooklyn.config.ConfigUtils;
import brooklyn.event.basic.BasicConfigKey;
import brooklyn.util.internal.ssh.SshTool;

/**
 * Dictionary of {@link ConfigKey} entries.
 */
public class ConfigKeys {

    public static final ConfigKey<String> BROOKLYN_DATA_DIR = new BasicConfigKey<String>(
            "brooklyn.datadir", "Directory for writing all brooklyn data", "/tmp/brooklyn-" + System.getProperty("user.name"));

    // FIXME Rename to VERSION, instead of SUGGESTED_VERSION? And declare as BasicAttributeSensorAndConfigKey?
    public static final ConfigKey<String> SUGGESTED_VERSION = new BasicConfigKey<String>("install.version", "Suggested version");
    public static final ConfigKey<String> SUGGESTED_INSTALL_DIR = new BasicConfigKey<String>("install.dir", "Suggested installation directory");
    public static final ConfigKey<String> SUGGESTED_RUN_DIR = new BasicConfigKey<String>("run.dir", "Suggested working directory for the running app");

    /**
     * Intention is to use this with {@link brooklyn.event.basic.DependentConfiguration#attributeWhenReady(brooklyn.entity.Entity, brooklyn.event.AttributeSensor) DependentConfiguration#attributeWhenReady}
     * to allow an entity's start to block until dependents are ready. This is particularly useful when we want to block until a dependent
     * component is up, but do not care about the its actual config values.
     */
    public static final ConfigKey<Boolean> START_LATCH = new BasicConfigKey<Boolean>("start.latch", "Latch for blocking start until ready");
    public static final ConfigKey<Boolean> INSTALL_LATCH = new BasicConfigKey<Boolean>("install.latch", "Latch for blocking install until ready");
    public static final ConfigKey<Boolean> CUSTOMIZE_LATCH = new BasicConfigKey<Boolean>("customize.latch", "Latch for blocking customize until ready");
    public static final ConfigKey<Boolean> LAUNCH_LATCH = new BasicConfigKey<Boolean>("launch.latch", "Latch for blocking launch until ready");

    public static final ConfigKey<Integer> START_TIMEOUT = new BasicConfigKey<Integer>("start.timeout", "Time to wait for SERVICE_UP to be set before failing (in seconds, default 60)", 60);

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

}
