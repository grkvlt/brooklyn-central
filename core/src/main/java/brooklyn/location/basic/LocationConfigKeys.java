package brooklyn.location.basic;

import brooklyn.config.ConfigKey;
import brooklyn.entity.basic.ConfigKeys;

public class LocationConfigKeys {

    public static final ConfigKey<String> LOCATION_ID = ConfigKeys.newConfigKey("id");
    public static final ConfigKey<String> DISPLAY_NAME = ConfigKeys.newConfigKey("displayName");

    public static final ConfigKey<String> ACCESS_IDENTITY = ConfigKeys.newConfigKey("identity"); 
    public static final ConfigKey<String> ACCESS_CREDENTIAL = ConfigKeys.newConfigKey("credential"); 

    public static final ConfigKey<Double> LATITUDE = ConfigKeys.newConfigKey("latitude"); 
    public static final ConfigKey<Double> LONGITUDE = ConfigKeys.newConfigKey("longitude"); 

    public static final ConfigKey<String> CLOUD_PROVIDER = ConfigKeys.newConfigKey("provider");
    public static final ConfigKey<String> CLOUD_ENDPOINT = ConfigKeys.newConfigKey("endpoint");
    public static final ConfigKey<String> CLOUD_REGION_ID = ConfigKeys.newConfigKey("region");

    public static final ConfigKey<String> USER = ConfigKeys.newConfigKey("user", 
            "user account for normal access to the remote machine, defaulting to local user", System.getProperty("user.name"));

    public static final ConfigKey<String> PASSWORD = ConfigKeys.newConfigKey("password");
    public static final ConfigKey<String> PUBLIC_KEY_FILE = ConfigKeys.newConfigKey("publicKeyFile");
    public static final ConfigKey<String> PUBLIC_KEY_DATA = ConfigKeys.newConfigKey("publicKeyData");
    public static final ConfigKey<String> PRIVATE_KEY_FILE = ConfigKeys.newConfigKey("privateKeyFile");
    public static final ConfigKey<String> PRIVATE_KEY_DATA = ConfigKeys.newConfigKey("privateKeyData");
    public static final ConfigKey<String> PRIVATE_KEY_PASSPHRASE = ConfigKeys.newConfigKey("privateKeyPassphrase");

    public static final ConfigKey<Object> CALLER_CONTEXT = ConfigKeys.newConfigKey("callerContext",
            "An object whose toString is used for logging, to indicate wherefore a VM is being created");

}
