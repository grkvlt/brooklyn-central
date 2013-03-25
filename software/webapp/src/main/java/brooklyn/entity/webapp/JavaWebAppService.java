package brooklyn.entity.webapp;

import java.util.List;
import java.util.Map;

import brooklyn.config.ConfigKey;
import brooklyn.entity.java.UsesJava;
import brooklyn.event.basic.BasicConfigKey;
import brooklyn.util.flags.SetFromFlag;

public interface JavaWebAppService extends WebAppService, UsesJava {

	@SetFromFlag("war")
    ConfigKey<String> ROOT_WAR = ConfigKeys.newConfigKey("wars.root",
            "WAR file to deploy as the ROOT, as URL (supporting file: and classpath: prefixes)");

    // TODO replace with ListConfigKey<String>
    @SetFromFlag("wars")
    ConfigKey<List<String>> NAMED_WARS = ConfigKeys.newConfigKey("wars.named",
            "Archive files to deploy, as URL strings (supporting file: and classpath: prefixes); context (path in user-facing URL) will be inferred by name");
    
    // TODO replace with MapConfigKey<String>
    @SetFromFlag("warsByContext")
    ConfigKey<Map<String,String>> WARS_BY_CONTEXT = ConfigKeys.newConfigKey("wars.by.context",
            "Map of context keys (path in user-facing URL, typically without slashes) to archives (e.g. WARs by URL) to deploy, supporting file: and classpath: prefixes)");

}
