package brooklyn.entity.messaging.amqp;

import brooklyn.event.basic.BasicAttributeSensorAndConfigKey;

/**
 * An interface that describes an AMQP exchange.
 */
public interface AmqpExchange {

    /* AMQP standard exchange names. */
    
    String DIRECT = "amq.direct";
    String TOPIC = "amq.topic";

    /** The AMQP exchange name {@link brooklyn.event.Sensor sensor}. */
    BasicAttributeSensorAndConfigKey<String> EXCHANGE_NAME = new BasicAttributeSensorAndConfigKey<String>("amqp.exchange.name", "AMQP exchange name");

    /**
     * Return the AMQP exchange name.
     */
    public String getExchangeName();
}
