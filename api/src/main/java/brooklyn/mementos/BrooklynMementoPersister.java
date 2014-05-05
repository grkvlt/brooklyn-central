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
package brooklyn.mementos;

import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import brooklyn.entity.Entity;
import brooklyn.entity.rebind.RebindManager;
import brooklyn.location.Location;

import com.google.common.annotations.VisibleForTesting;

/**
 * Controls the persisting and reading back of mementos. Used by {@link RebindManager} 
 * to support brooklyn restart.
 */
public interface BrooklynMementoPersister {

    public static interface LookupContext {
        Entity lookupEntity(Class<?> type, String id);
        Location lookupLocation(Class<?> type, String id);
    }
    
    /**
     * Note that this method is *not* thread safe.
     */
    BrooklynMemento loadMemento(LookupContext lookupContext) throws IOException;
    
    void checkpoint(BrooklynMemento memento);
    
    void delta(Delta delta);

    void stop();

    @VisibleForTesting
    void waitForWritesCompleted(long timeout, TimeUnit unit) throws InterruptedException, TimeoutException;

    public interface Delta {
        Collection<LocationMemento> locations();
        Collection<EntityMemento> entities();
        Collection<PolicyMemento> policies();
        Collection<String> removedLocationIds();
        Collection<String> removedEntityIds();
        Collection<String> removedPolicyIds();
    }
}
