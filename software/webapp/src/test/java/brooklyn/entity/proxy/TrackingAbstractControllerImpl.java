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
package brooklyn.entity.proxy;

import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import brooklyn.entity.driver.MockSshDriver;

import com.google.common.collect.Lists;

public class TrackingAbstractControllerImpl extends AbstractControllerImpl implements TrackingAbstractController {
    
    private static final Logger log = LoggerFactory.getLogger(TrackingAbstractControllerImpl.class);

    private final List<Collection<String>> updates = Lists.newCopyOnWriteArrayList();
    
    @Override
    public List<Collection<String>> getUpdates() {
        return updates;
    }
    
    @Override
    public void connectSensors() {
        super.connectSensors();
        setAttribute(SERVICE_UP, true);
    }
    
    @Override
    protected void reconfigureService() {
        log.info("test controller reconfigure, addresses "+serverPoolAddresses);
        if ((!serverPoolAddresses.isEmpty() && updates.isEmpty()) || (!updates.isEmpty() && serverPoolAddresses!=updates.get(updates.size()-1))) {
            updates.add(serverPoolAddresses);
        }
    }

    @Override
    public Class getDriverInterface() {
        return MockSshDriver.class;
    }
    public void reload() {
        // no-op
    }
}
