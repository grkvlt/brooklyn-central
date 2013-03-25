package brooklyn.entity.proxy.nginx;

import java.util.Collection;

import brooklyn.config.ConfigKey;
import brooklyn.entity.Effector;
import brooklyn.entity.Entity;
import brooklyn.entity.basic.AbstractGroup;
import brooklyn.entity.basic.Attributes;
import brooklyn.entity.basic.ConfigKeys;
import brooklyn.entity.basic.Description;
import brooklyn.entity.basic.MethodEffector;
import brooklyn.entity.proxy.AbstractController;
import brooklyn.entity.proxy.ProxySslConfig;
import brooklyn.entity.proxying.ImplementedBy;
import brooklyn.entity.webapp.WebAppService;
import brooklyn.event.AttributeSensor;
import brooklyn.util.flags.SetFromFlag;

/**
 * This is a group whose members will be made available to a load-balancer / URL forwarding service (such as nginx).
 * Configuration requires a <b>domain</b> and some mechanism for finding members.
 * The easiest way to find members is using a <b>target</b> whose children will be tracked,
 * but alternative membership policies can also be used.
 */
@ImplementedBy(UrlMappingImpl.class)
public interface UrlMapping extends AbstractGroup {

    Effector<Void> DISCARD = new MethodEffector<Void>(UrlMapping.class, "discard");

    @SetFromFlag("label")
    ConfigKey<String> LABEL = ConfigKeys.newConfigKey("urlmapping.label", "optional human-readable label to identify a server");

    @SetFromFlag("domain")
    ConfigKey<String> DOMAIN = ConfigKeys.newConfigKey("urlmapping.domain", "domain (hostname, e.g. www.foo.com) to present for this URL map rule; required.");

    @SetFromFlag("path")
    ConfigKey<String> PATH = ConfigKeys.newConfigKey("urlmapping.path",
                "URL path (pattern) for this URL map rule. Currently only supporting regex matches " +
                "(if not supplied, will match all paths at the indicated domain)");

    @SetFromFlag("ssl")
    ConfigKey<ProxySslConfig> SSL_CONFIG = AbstractController.SSL_CONFIG;

    @SetFromFlag("rewrites")
    ConfigKey<Collection<UrlRewriteRule>> REWRITES = ConfigKeys.newConfigKey("urlmapping.rewrites", "Set of URL rewrite rules to apply");

    @SetFromFlag("target")
    ConfigKey<Entity> TARGET_PARENT = ConfigKeys.newConfigKey("urlmapping.target.parent", "optional target entity whose children will be pointed at by this mapper");

    AttributeSensor<Collection<String>> TARGET_ADDRESSES = Attributes.newAttributeSensor("urlmapping.target.addresses", "set of addresses which should be forwarded to by this URL mapping");

    AttributeSensor<String> ROOT_URL = WebAppService.ROOT_URL;

    String getUniqueLabel();

    /**
     * Adds a rewrite rule, must be called at config time.
     *
     * @see {@link UrlRewriteRule} for more info.
     */
    UrlMapping addRewrite(String from, String to);

    /**
     * Adds a rewrite rule, must be called at config time.
     *
     * @see {@link UrlRewriteRule} for more info.
     */
    UrlMapping addRewrite(UrlRewriteRule rule);

    String getDomain();

    String getPath();

    Entity getTarget();

    void setTarget(Entity target);

    void recompute();

    @Description("Unmanages the url-mapping, so it is discarded and no longer applies")
    void discard();
}
