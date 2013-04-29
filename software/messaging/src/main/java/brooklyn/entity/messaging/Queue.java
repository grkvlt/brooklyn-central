package brooklyn.entity.messaging;

import brooklyn.event.AttributeSensor;
import brooklyn.event.basic.BasicAttributeSensor;
import brooklyn.event.basic.BasicAttributeSensorAndConfigKey;

/**
 * An interface that describes a messaging queue.
 */
public interface Queue {

    BasicAttributeSensorAndConfigKey<String> QUEUE_NAME = new BasicAttributeSensorAndConfigKey<String>("queue.name", "Queue name");

    AttributeSensor<Integer> QUEUE_DEPTH_BYTES = new BasicAttributeSensor<Integer>("queue.depth.bytes", "Queue depth in bytes");
    AttributeSensor<Integer> QUEUE_DEPTH_MESSAGES = new BasicAttributeSensor<Integer>("queue.depth.messages", "Queue depth in messages");

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
