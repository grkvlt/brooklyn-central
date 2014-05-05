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
package brooklyn.entity.rebind;

import brooklyn.entity.Entity;
import brooklyn.location.Location;
import brooklyn.policy.Policy;

/**
 * Gives access to things that are being currently rebinding. This is used during a
 * rebind to wire everything back together again, e.g. to find the necessary entity 
 * instances even before they are available through 
 * {@code managementContext.getEntityManager().getEnties()}.
 * 
 * Users are not expected to implement this class. It is for use by {@link Rebindable} 
 * instances, and will generally be created by the {@link RebindManager}.
 */
public interface RebindContext {

    public Entity getEntity(String id);

    public Location getLocation(String id);

    public Policy getPolicy(String id);

    public Class<?> loadClass(String typeName) throws ClassNotFoundException;
}
