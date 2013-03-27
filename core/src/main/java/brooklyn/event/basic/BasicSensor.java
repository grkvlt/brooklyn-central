/*
 * Copyright 2011-2013 by Cloudsoft Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package brooklyn.event.basic;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import brooklyn.entity.Entity;
import brooklyn.event.Sensor;
import brooklyn.event.SensorEvent;

import com.google.common.base.Objects;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.reflect.TypeToken;

/**
 * Parent for all {@link Sensor}s.
 */
public class BasicSensor<T> implements Sensor<T> {
    private static final long serialVersionUID = -3762018534086101323L;

    private static final Splitter dots = Splitter.on('.');

    private Class<T> type;
    private String name;
    private String description;
    private transient List<String> nameParts;

    // FIXME In groovy, fields were `public final` with a default constructor; do we need the gson?
    public BasicSensor() { /* for gson */ }

    /** name is typically a dot-separated identifier; description is optional */
    public BasicSensor(Class<T> type, String name) {
        this(type, name, name);
    }

    public BasicSensor(Class<T> type, String name, String description) {
        this.type = checkNotNull(type, "type");
        this.name = checkNotNull(name, "name");
        this.description = description;
    }

    @SuppressWarnings("unchecked")
    public BasicSensor(TypeToken<T> type, String name) {
        this((Class<T>) type.getRawType(), name, name);
    }

    @SuppressWarnings("unchecked")
    public BasicSensor(TypeToken<T> type, String name, String description) {
        this((Class<T>) type.getRawType(), name, description);
    }

    @SuppressWarnings("serial")
    public BasicSensor(String name) {
        this(new TypeToken<T>(BasicSensor.class) { }, name, name);
    }

    @SuppressWarnings("serial")
    public BasicSensor(String name, String description) {
        this(new TypeToken<T>(BasicSensor.class) { }, name, description);
    }

    /** @see Sensor#getType() */
    public Class<T> getType() { return type; }

    /** @see Sensor#getTypeName() */
    public String getTypeName() { 
        return type.getName();
    }

    /** @see Sensor#getName() */
    public String getName() { return name; }

    /** @see Sensor#getNameParts() */
    public synchronized List<String> getNameParts() {
        if (nameParts==null) nameParts = ImmutableList.copyOf(dots.split(name));
        return nameParts; 
    }
 
    /** @see Sensor#getDescription() */
    public String getDescription() { return description; }

    /** @see Sensor#newEvent(Entity, Object) */
    public SensorEvent<T> newEvent(Entity producer, T value) {
        return new BasicSensorEvent<T>(this, producer, value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getTypeName(), name, description);
    }

    @Override
    public boolean equals(Object other) {
        if (this==other) return true;
        if (!(other instanceof BasicSensor)) return false;
        BasicSensor<?> o = (BasicSensor<?>) other;

        return Objects.equal(getTypeName(), o.getTypeName()) && Objects.equal(name, o.name) && Objects.equal(description, o.description);
    }

    @Override
    public String toString() {
        return String.format("Sensor: %s (%s)", name, getTypeName());
    }
}
