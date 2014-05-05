/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package brooklyn.entity.basic;

import javax.annotation.Nullable;

import brooklyn.config.ConfigKey.HasConfigKey;
import brooklyn.entity.Entity;
import brooklyn.entity.Group;
import brooklyn.event.AttributeSensor;
import brooklyn.location.Location;

import com.google.common.base.Objects;
import com.google.common.base.Predicate;

public class EntityPredicates {

    public static <T> Predicate<Entity> idEqualTo(final T val) {
        return new Predicate<Entity>() {
            @Override
            public boolean apply(@Nullable Entity input) {
                return Objects.equal(input.getId(), val);
            }
        };
    }
    
    public static <T> Predicate<Entity> displayNameEqualTo(final T val) {
        return new Predicate<Entity>() {
            @Override
            public boolean apply(@Nullable Entity input) {
                return Objects.equal(input.getDisplayName(), val);
            }
        };
    }
    
    public static Predicate<Entity> applicationIdEqualTo(final String val) {
        return new Predicate<Entity>() {
            @Override
            public boolean apply(Entity input) {
                return val.equals(input.getApplicationId());
            }
        };
    }

    public static <T> Predicate<Entity> attributeEqualTo(final AttributeSensor<T> attribute, final T val) {
        return new Predicate<Entity>() {
            @Override
            public boolean apply(@Nullable Entity input) {
                return Objects.equal(input.getAttribute(attribute), val);
            }
        };
    }
    

    public static <T> Predicate<Entity> configEqualTo(final HasConfigKey<T> configKey, final T val) {
        return new Predicate<Entity>() {
            @Override
            public boolean apply(Entity input) {
                return Objects.equal(input.getConfig(configKey), val);
            }
        };
    }

    public static <T> Predicate<Entity> isChildOf(final Entity parent) {
        return new Predicate<Entity>() {
            @Override
            public boolean apply(@Nullable Entity input) {
                return Objects.equal(input.getParent(), parent);
            }
        };
    }

    public static <T> Predicate<Entity> isMemberOf(final Group group) {
        return new Predicate<Entity>() {
            @Override
            public boolean apply(@Nullable Entity input) {
                return group.hasMember(input);
            }
        };
    }

    /**
     * Create a predicate that matches any entity who has an exact match for the given location
     * (i.e. {@code entity.getLocations().contains(location)}).
     */
    public static <T> Predicate<Entity> withLocation(final Location location) {
        return new Predicate<Entity>() {
            @Override
            public boolean apply(@Nullable Entity input) {
                return (input != null) && input.getLocations().contains(location);
            }
        };
    }
    
    public static <T> Predicate<Entity> managed() {
        return new Predicate<Entity>() {
            @Override
            public boolean apply(@Nullable Entity input) {
                return input != null && Entities.isManaged(input);
            }
        };
    }

}
