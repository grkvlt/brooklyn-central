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
package brooklyn.management.internal;

import brooklyn.config.ConfigKey;
import brooklyn.entity.Effector;
import brooklyn.event.AttributeSensor;

public interface EntityChangeListener {

    public static final EntityChangeListener NOOP = new EntityChangeListener() {
        @Override public void onChanged() {}
        @Override public void onAttributeChanged(AttributeSensor<?> attribute) {}
        @Override public void onConfigChanged(ConfigKey<?> key) {}
        @Override public void onLocationsChanged() {}
        @Override public void onMembersChanged() {}
        @Override public void onChildrenChanged() {}
        @Override public void onPoliciesChanged() {}
        @Override public void onEffectorStarting(Effector<?> effector) {}
        @Override public void onEffectorCompleted(Effector<?> effector) {}
    };
    
    void onChanged();

    void onAttributeChanged(AttributeSensor<?> attribute);

    void onConfigChanged(ConfigKey<?> key);

    void onLocationsChanged();

    void onMembersChanged();

    void onChildrenChanged();

    void onPoliciesChanged();

    void onEffectorStarting(Effector<?> effector);
    
    void onEffectorCompleted(Effector<?> effector);
}
