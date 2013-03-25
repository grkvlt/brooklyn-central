package brooklyn.entity.dns.geoscaling;

import brooklyn.config.ConfigKey;
import brooklyn.entity.basic.Attributes;
import brooklyn.entity.basic.ConfigKeys;
import brooklyn.entity.dns.AbstractGeoDnsService;
import brooklyn.entity.proxying.ImplementedBy;
import brooklyn.event.AttributeSensor;
import brooklyn.util.flags.SetFromFlag;

@ImplementedBy(GeoscalingDnsServiceImpl.class)
public interface GeoscalingDnsService extends AbstractGeoDnsService {

    @SetFromFlag("randomizeSubdomainName")
    ConfigKey<Boolean> RANDOMIZE_SUBDOMAIN_NAME = ConfigKeys.newConfigKey("randomize.subdomain.name");

    @SetFromFlag("username")
    ConfigKey<String> GEOSCALING_USERNAME = ConfigKeys.newConfigKey("geoscaling.username");

    @SetFromFlag("password")
    ConfigKey<String> GEOSCALING_PASSWORD = ConfigKeys.newConfigKey("geoscaling.password");

    @SetFromFlag("primaryDomainName")
    ConfigKey<String> GEOSCALING_PRIMARY_DOMAIN_NAME = ConfigKeys.newConfigKey("geoscaling.primary.domain.name");

    @SetFromFlag("smartSubdomainName")
    ConfigKey<String> GEOSCALING_SMART_SUBDOMAIN_NAME = ConfigKeys.newConfigKey("geoscaling.smart.subdomain.name");

    AttributeSensor<String> GEOSCALING_ACCOUNT = Attributes.newAttributeSensor("geoscaling.account", "Active user account for the GeoScaling.com service");
    AttributeSensor<String> MANAGED_DOMAIN = Attributes.newAttributeSensor("geoscaling.managed.domain", "Fully qualified domain name that will be geo-redirected");

    void applyConfig();

    /** Minimum/default TTL here is 300s = 5m */
    long getTimeToLiveSeconds();

}
