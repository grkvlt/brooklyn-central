package brooklyn.entity.messaging;

import brooklyn.entity.Entity;
import brooklyn.event.AttributeSensor;
import brooklyn.event.basic.BasicAttributeSensor;

/**
 * Marker interface identifying message brokers.
 */
public interface MessageBroker extends Entity {

    AttributeSensor<String> BROKER_URL = new BasicAttributeSensor<String>("broker.url", "Broker Connection URL");

    /** Setup the URL for external connections to the broker. */
    void setBrokerUrl();

}
