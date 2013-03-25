package brooklyn.entity.messaging;

import brooklyn.entity.Entity;
import brooklyn.entity.basic.Attributes;
import brooklyn.event.AttributeSensor;

/**
 * Marker interface identifying message brokers.
 */
public interface MessageBroker extends Entity {

    AttributeSensor<String> BROKER_URL = Attributes.newAttributeSensor("broker.url", "Broker Connection URL");

    /** Setup the URL for external connections to the broker. */
    void setBrokerUrl();

}
