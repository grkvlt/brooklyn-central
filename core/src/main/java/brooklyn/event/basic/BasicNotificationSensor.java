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

import brooklyn.event.Sensor;

import com.google.common.reflect.TypeToken;

/**
 * A {@link Sensor} used to notify subscribers about events.
 */
public class BasicNotificationSensor<T> extends BasicSensor<T> {
    private static final long serialVersionUID = -7670909215973264600L;

    public BasicNotificationSensor(Class<T> type, String name) {
        this(type, name, name);
    }

    public BasicNotificationSensor(Class<T> type, String name, String description) {
        super(type, name, description);
    }

    @SuppressWarnings("unchecked")
    public BasicNotificationSensor(TypeToken<T> type, String name) {
        this((Class<T>) type.getRawType(), name, name);
    }

    @SuppressWarnings("unchecked")
    public BasicNotificationSensor(TypeToken<T> type, String name, String description) {
        this((Class<T>) type.getRawType(), name, description);
    }

    @SuppressWarnings("serial")
    public BasicNotificationSensor(String name) {
        this(new TypeToken<T>(BasicNotificationSensor.class) { }, name, name);
    }

    @SuppressWarnings("serial")
    public BasicNotificationSensor(String name, String description) {
        this(new TypeToken<T>(BasicNotificationSensor.class) { }, name, description);
    }

}
