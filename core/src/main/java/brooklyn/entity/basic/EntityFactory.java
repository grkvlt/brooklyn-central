package brooklyn.entity.basic;

import java.util.Map;

import brooklyn.entity.Entity;

/**
 * A Factory for creating entities.
 *
 * @param <T>
 */
public interface EntityFactory<T extends Entity> {
    T newEntity(Map flags, Entity parent);
}
