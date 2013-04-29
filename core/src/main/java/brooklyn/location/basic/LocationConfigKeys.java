package brooklyn.location.basic;

import brooklyn.config.ConfigKey;
import brooklyn.event.basic.BasicConfigKey;

public class LocationConfigKeys {

    public static final ConfigKey<String> LOCATION_ID = new BasicConfigKey<String>("id");
    public static final ConfigKey<String> DISPLAY_NAME = new BasicConfigKey<String>("displayName");

    public static final ConfigKey<String> ACCESS_IDENTITY = new BasicConfigKey<String>("identity"); 
    public static final ConfigKey<String> ACCESS_CREDENTIAL = new BasicConfigKey<String>("credential"); 

    public static final ConfigKey<Double> LATITUDE = new BasicConfigKey<Double>("latitude"); 
    public static final ConfigKey<Double> LONGITUDE = new BasicConfigKey<Double>("longitude"); 

    public static final ConfigKey<String> CLOUD_PROVIDER = new BasicConfigKey<String>("provider");
    public static final ConfigKey<String> CLOUD_ENDPOINT = new BasicConfigKey<String>("endpoint");
    public static final ConfigKey<String> CLOUD_REGION_ID = new BasicConfigKey<String>("region");

    public static final ConfigKey<String> USER = new BasicConfigKey<String>("user", 
            "user account for normal access to the remote machine, defaulting to local user", System.getProperty("user.name"));

    public static final ConfigKey<String> PASSWORD = new BasicConfigKey<String>("password");
    public static final ConfigKey<String> PUBLIC_KEY_FILE = new BasicConfigKey<String>("publicKeyFile");
    public static final ConfigKey<String> PUBLIC_KEY_DATA = new BasicConfigKey<String>("publicKeyData");
    public static final ConfigKey<String> PRIVATE_KEY_FILE = new BasicConfigKey<String>("privateKeyFile");
    public static final ConfigKey<String> PRIVATE_KEY_DATA = new BasicConfigKey<String>("privateKeyData");
    public static final ConfigKey<String> PRIVATE_KEY_PASSPHRASE = new BasicConfigKey<String>("privateKeyPassphrase");

    public static final ConfigKey<Object> CALLER_CONTEXT = new BasicConfigKey<Object>("callerContext",
            "An object whose toString is used for logging, to indicate wherefore a VM is being created");

}
