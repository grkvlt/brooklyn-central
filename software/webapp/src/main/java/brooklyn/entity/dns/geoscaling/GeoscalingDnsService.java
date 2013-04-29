package brooklyn.entity.dns.geoscaling;

import brooklyn.config.ConfigKey;
import brooklyn.entity.dns.AbstractGeoDnsService;
import brooklyn.entity.proxying.ImplementedBy;
import brooklyn.event.AttributeSensor;
import brooklyn.event.basic.BasicAttributeSensor;
import brooklyn.event.basic.BasicConfigKey;
import brooklyn.util.flags.SetFromFlag;

@ImplementedBy(GeoscalingDnsServiceImpl.class)
public interface GeoscalingDnsService extends AbstractGeoDnsService {

    @SetFromFlag("randomizeSubdomainName")
    ConfigKey<Boolean> RANDOMIZE_SUBDOMAIN_NAME = new BasicConfigKey<Boolean>("randomize.subdomain.name");

    @SetFromFlag("username")
    ConfigKey<String> GEOSCALING_USERNAME = new BasicConfigKey<String>("geoscaling.username");

    @SetFromFlag("password")
    ConfigKey<String> GEOSCALING_PASSWORD = new BasicConfigKey<String>("geoscaling.password");

    @SetFromFlag("primaryDomainName")
    ConfigKey<String> GEOSCALING_PRIMARY_DOMAIN_NAME = new BasicConfigKey<String>("geoscaling.primary.domain.name");

    @SetFromFlag("smartSubdomainName")
    ConfigKey<String> GEOSCALING_SMART_SUBDOMAIN_NAME = new BasicConfigKey<String>("geoscaling.smart.subdomain.name");

    AttributeSensor<String> GEOSCALING_ACCOUNT = new BasicAttributeSensor<String>("geoscaling.account", "Active user account for the GeoScaling.com service");
    AttributeSensor<String> MANAGED_DOMAIN = new BasicAttributeSensor<String>("geoscaling.managed.domain", "Fully qualified domain name that will be geo-redirected");

    void applyConfig();

    /** Minimum/default TTL here is 300s = 5m */
    long getTimeToLiveSeconds();

}
