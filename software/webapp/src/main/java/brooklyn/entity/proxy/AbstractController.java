package brooklyn.entity.proxy;

import java.util.Map;
import java.util.Set;

import brooklyn.config.ConfigKey;
import brooklyn.entity.basic.Attributes;
import brooklyn.entity.basic.ConfigKeys;
import brooklyn.entity.basic.Description;
import brooklyn.entity.basic.MethodEffector;
import brooklyn.entity.basic.SoftwareProcess;
import brooklyn.entity.group.Cluster;
import brooklyn.entity.proxying.ImplementedBy;
import brooklyn.entity.webapp.WebAppService;
import brooklyn.event.AttributeSensor;
import brooklyn.event.basic.BasicAttributeSensorAndConfigKey;
import brooklyn.event.basic.PortAttributeSensorAndConfigKey;
import brooklyn.util.flags.SetFromFlag;

import com.google.common.collect.ImmutableList;

/**
 * Represents a controller mechanism for a {@link Cluster}.
 */
@ImplementedBy(AbstractControllerImpl.class)
public interface AbstractController extends SoftwareProcess, LoadBalancer {

    /** sensor for port to forward to on target entities */
    @SetFromFlag("portNumberSensor")
    BasicAttributeSensorAndConfigKey<AttributeSensor<Integer>> PORT_NUMBER_SENSOR = ConfigKeys.newAttributeSensorAndConfigKey(
            "member.sensor.portNumber", "Port number sensor on members (defaults to http.port)", Attributes.HTTP_PORT.asAttributeSensor());

    @SetFromFlag("port")
    /** port where this controller should live */
    PortAttributeSensorAndConfigKey PROXY_HTTP_PORT = ConfigKeys.newPortAttributeSensorAndConfigKey(
            "proxy.http.port", "Main HTTP port where this proxy listens", ImmutableList.of(8000, "8001+"));

    @SetFromFlag("protocol")
    BasicAttributeSensorAndConfigKey<String> PROTOCOL = ConfigKeys.newAttributeSensorAndConfigKey(
            "proxy.protocol", "Main URL protocol this proxy answers (typically http or https)", null);

    @SetFromFlag("domain")
    BasicAttributeSensorAndConfigKey<String> DOMAIN_NAME = ConfigKeys.newAttributeSensorAndConfigKey(
            "proxy.domainName", "Domain name that this controller responds to, or null if it responds to all domains", null);

    @SetFromFlag("ssl")
    ConfigKey<ProxySslConfig> SSL_CONFIG = ConfigKeys.newConfigKey(
            "proxy.ssl.config", "configuration (e.g. certificates) for SSL; will use SSL if set, not use SSL if not set");

    AttributeSensor<String> ROOT_URL = WebAppService.ROOT_URL;

    AttributeSensor<Set<String>> SERVER_POOL_TARGETS = Attributes.newAttributeSensor(
            "proxy.serverpool.targets", "The downstream targets in the server pool");

    /**
     * @deprecated Use SERVER_POOL_TARGETS
     */
    AttributeSensor<Set<String>> TARGETS = SERVER_POOL_TARGETS;

    MethodEffector<Void> RELOAD = new MethodEffector<Void>(AbstractController.class, "reload");
    MethodEffector<Void> UPDATE = new MethodEffector<Void>(AbstractController.class, "update");

    /**
     * Opportunity to do late-binding of the cluster that is being controlled. Must be called before start().
     * Can pass in the 'cluster'.
     */
    void bind(Map<?, ?> flags);

    boolean isActive();

    String getProtocol();

    /** returns primary domain this controller responds to, or null if it responds to all domains */
    String getDomain();

    Integer getPort();

    /** primary URL this controller serves, if one can / has been inferred */
    String getUrl();

    AttributeSensor<Integer> getPortNumberSensor();

    @Description("Forces reload of the configuration")
    void reload();

    @Description("Updates the entities configuration, and then forces reload of that configuration")
    void update();

}
