package brooklyn.entity.dns;

import java.util.Map;

import brooklyn.config.ConfigKey;
import brooklyn.entity.Entity;
import brooklyn.entity.basic.Attributes;
import brooklyn.entity.basic.ConfigKeys;
import brooklyn.entity.basic.Lifecycle;
import brooklyn.entity.trait.Startable;
import brooklyn.event.AttributeSensor;
import brooklyn.location.geo.HostGeoInfo;
import brooklyn.util.flags.SetFromFlag;

public interface AbstractGeoDnsService extends Entity {

    @SetFromFlag("pollPeriod")
    ConfigKey<Long> POLL_PERIOD = ConfigKeys.newConfigKey("geodns.pollperiod", "Poll period (in milliseconds) for refreshing target hosts", 5000L);

    AttributeSensor<Lifecycle> SERVICE_STATE = Attributes.SERVICE_STATE;

    AttributeSensor<Boolean> SERVICE_UP = Startable.SERVICE_UP;

    AttributeSensor<String> HOSTNAME = Attributes.HOSTNAME;

    AttributeSensor<Map<String, String>> TARGETS = Attributes.newAttributeSensor(
            "geodns.targets", "Map of targets currently being managed (entity ID to URL)");

    void setServiceState(Lifecycle state);

    /** if target is a group, its members are searched; otherwise its children are searched */
    void setTargetEntityProvider(final Entity entityProvider);

    /** should return the hostname which this DNS service is configuring */
    String getHostname();

    Map<Entity, HostGeoInfo> getTargetHosts();
}
