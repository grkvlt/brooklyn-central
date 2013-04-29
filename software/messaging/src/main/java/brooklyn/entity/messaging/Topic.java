package brooklyn.entity.messaging;

import brooklyn.event.basic.BasicAttributeSensorAndConfigKey;

/**
 * An interface that describes a messaging topic.
 */
public interface Topic {

    BasicAttributeSensorAndConfigKey<String> TOPIC_NAME = new BasicAttributeSensorAndConfigKey<String>("topic.name", "Topic name");

    /**
     * Create the topic.
     * 
     * TODO make this an effector
     */
    void create();

    /**
     * Delete the topic.
     * 
     * TODO make this an effector
     */
    void delete();

    String getTopicName();

}
