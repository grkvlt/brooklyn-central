package brooklyn.rest;

import brooklyn.config.ConfigKey;
import brooklyn.entity.basic.ConfigKeys;
import brooklyn.rest.security.provider.ExplicitUsersSecurityProvider;

public class BrooklynWebConfig {

    public final static String BASE_NAME = "brooklyn.webconsole";

    /** e.g. brooklyn.webconsole.security.provider=brooklyn.rest.security.provider.AnyoneSecurityProvider will allow anyone to log in;
     * default is explicitly named users, using SECURITY_PROVIDER_EXPLICIT__USERS  */
    public final static ConfigKey<String> SECURITY_PROVIDER_CLASSNAME = ConfigKeys.newConfigKey(
            BASE_NAME+".security.provider", "class name of a Brooklyn SecurityProvider",
            ExplicitUsersSecurityProvider.class.getCanonicalName());
    
    /** explicitly set the users/passwords, e.g. in brooklyn.properties:
     * brooklyn.webconsole.security.explicit.users=admin,bob
     * brooklyn.webconsole.security.explicit.user.admin=password
     * brooklyn.webconsole.security.explicit.user.bob=bobspass
     */
    public final static ConfigKey<String> SECURITY_PROVIDER_EXPLICIT__USERS = ConfigKeys.newConfigKey(
            BASE_NAME+".security.explicit.users");

    public final static ConfigKey<String> LDAP_URL = ConfigKeys.newConfigKey(BASE_NAME+".security.ldap.url");

    public final static ConfigKey<String> LDAP_REALM = ConfigKeys.newConfigKey(BASE_NAME+".security.ldap.realm");

    public final static ConfigKey<String> SECURITY_PROVIDER_EXPLICIT__PASSWORD(String user) {
        return ConfigKeys.newConfigKey(BASE_NAME+".security.explicit.user."+user);
    }

}
