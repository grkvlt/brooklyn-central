package brooklyn.entity.messaging;

import brooklyn.entity.basic.Attributes;
import brooklyn.entity.basic.ConfigKeys;
import brooklyn.event.AttributeSensor;
import brooklyn.event.basic.BasicAttributeSensorAndConfigKey;

/**
 * An interface that describes a messaging queue.
 */
public interface Queue {

    BasicAttributeSensorAndConfigKey<String> QUEUE_NAME = ConfigKeys.newAttributeSensorAndConfigKey("queue.name", "Queue name");

    AttributeSensor<Integer> QUEUE_DEPTH_BYTES = Attributes.newAttributeSensor("queue.depth.bytes", "Queue depth in bytes");
    AttributeSensor<Integer> QUEUE_DEPTH_MESSAGES = Attributes.newAttributeSensor("queue.depth.messages", "Queue depth in messages");

    /**
     * Create the queue.
     *
     * TODO make this an effector
     */
    void create();

    /**
     * Delete the queue.
     *
     * TODO make this an effector
     */
    void delete();

    String getQueueName();

}
