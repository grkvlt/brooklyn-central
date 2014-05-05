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
package brooklyn.entity.proxying;

import brooklyn.entity.basic.EntityLocal;

public interface EntityInitializer {
    
    /** Applies initialization logic to a just-built entity.
     * Invoked immediately after the "init" call on the AbstractEntity constructed.
     * 
     * @param entity guaranteed to be the actual implementation instance, 
     * thus guaranteed to be castable to EntityInternal which is often desired,
     * or to the type at hand (it is not even a proxy)
     */
    public void apply(EntityLocal entity);
    
}
