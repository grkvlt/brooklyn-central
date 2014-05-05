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
package brooklyn.entity.rebind.persister;

import static org.testng.Assert.assertEquals;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import brooklyn.entity.Entity;
import brooklyn.entity.basic.ApplicationBuilder;
import brooklyn.entity.basic.Entities;
import brooklyn.location.Location;
import brooklyn.location.LocationSpec;
import brooklyn.location.basic.SimulatedLocation;
import brooklyn.management.ManagementContext;
import brooklyn.mementos.BrooklynMementoPersister.LookupContext;
import brooklyn.test.entity.TestApplication;
import brooklyn.util.collections.MutableList;
import brooklyn.util.collections.MutableMap;
import brooklyn.util.collections.MutableSet;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

public class XmlMementoSerializerTest {

    private static final Logger LOG = LoggerFactory.getLogger(XmlMementoSerializerTest.class);

    private XmlMementoSerializer<Object> serializer;

    @BeforeMethod(alwaysRun=true)
    public void setUp() throws Exception {
        serializer = new XmlMementoSerializer<Object>(XmlMementoSerializerTest.class.getClassLoader());
    }
    
    @Test
    public void testMutableSet() throws Exception {
        Set<?> obj = MutableSet.of("123");
        assertSerializeAndDeserialize(obj);
    }
    
    @Test
    public void testLinkedHashSet() throws Exception {
        Set<String> obj = new LinkedHashSet<String>();
        obj.add("123");
        assertSerializeAndDeserialize(obj);
    }
    
    @Test
    public void testImmutableSet() throws Exception {
        Set<String> obj = ImmutableSet.of("123");
        assertSerializeAndDeserialize(obj);
    }
    
    @Test
    public void testMutableList() throws Exception {
        List<?> obj = MutableList.of("123");
        assertSerializeAndDeserialize(obj);
    }
    
    @Test
    public void testLinkedList() throws Exception {
        List<String> obj = new LinkedList<String>();
        obj.add("123");
        assertSerializeAndDeserialize(obj);
    }
    
    @Test
    public void testImmutableList() throws Exception {
        List<String> obj = ImmutableList.of("123");
        assertSerializeAndDeserialize(obj);
    }
    
    @Test
    public void testMutableMap() throws Exception {
        Map<?,?> obj = MutableMap.of("mykey", "myval");
        assertSerializeAndDeserialize(obj);
    }
    
    @Test
    public void testLinkedHashMap() throws Exception {
        Map<String,String> obj = new LinkedHashMap<String,String>();
        obj.put("mykey", "myval");
        assertSerializeAndDeserialize(obj);
    }
    
    @Test
    public void testImmutableMap() throws Exception {
        Map<?,?> obj = ImmutableMap.of("mykey", "myval");
        assertSerializeAndDeserialize(obj);
    }
    
    @Test
    public void testEntity() throws Exception {
        final TestApplication app = ApplicationBuilder.newManagedApp(TestApplication.class);
        ManagementContext managementContext = app.getManagementContext();
        try {
            serializer.setLookupContext(new LookupContextImpl(ImmutableMap.of(app.getId(), app), ImmutableMap.<String,Location>of()));
            assertSerializeAndDeserialize(app);
        } finally {
            Entities.destroyAll(managementContext);
        }
    }
    
    @Test
    public void testLocation() throws Exception {
        TestApplication app = ApplicationBuilder.newManagedApp(TestApplication.class);
        ManagementContext managementContext = app.getManagementContext();
        try {
            final Location loc = managementContext.getLocationManager().createLocation(LocationSpec.create(SimulatedLocation.class));
            serializer.setLookupContext(new LookupContextImpl(ImmutableMap.<String,Entity>of(), ImmutableMap.of(loc.getId(), loc)));
            assertSerializeAndDeserialize(loc);
        } finally {
            Entities.destroyAll(managementContext);
        }
    }
    
    @Test
    public void testFieldReffingEntity() throws Exception {
        final TestApplication app = ApplicationBuilder.newManagedApp(TestApplication.class);
        ReffingEntity reffer = new ReffingEntity(app);
        ManagementContext managementContext = app.getManagementContext();
        try {
            serializer.setLookupContext(new LookupContextImpl(ImmutableMap.of(app.getId(), app), ImmutableMap.<String,Location>of()));
            ReffingEntity reffer2 = assertSerializeAndDeserialize(reffer);
            assertEquals(reffer2.entity, app);
        } finally {
            Entities.destroyAll(managementContext);
        }
    }
    
    @Test
    public void testUntypedFieldReffingEntity() throws Exception {
        final TestApplication app = ApplicationBuilder.newManagedApp(TestApplication.class);
        ReffingEntity reffer = new ReffingEntity((Object)app);
        ManagementContext managementContext = app.getManagementContext();
        try {
            serializer.setLookupContext(new LookupContextImpl(ImmutableMap.of(app.getId(), app), ImmutableMap.<String,Location>of()));
            ReffingEntity reffer2 = assertSerializeAndDeserialize(reffer);
            assertEquals(reffer2.obj, app);
        } finally {
            Entities.destroyAll(managementContext);
        }
    }
    
    public static class ReffingEntity {
        public Entity entity;
        public Object obj;
        public ReffingEntity(Entity entity) {
            this.entity = entity;
        }
        public ReffingEntity(Object obj) {
            this.obj = obj;
        }
        @Override
        public boolean equals(Object o) {
            return (o instanceof ReffingEntity) && Objects.equal(entity, ((ReffingEntity)o).entity) && Objects.equal(obj, ((ReffingEntity)o).obj);
        }
        @Override
        public int hashCode() {
            return Objects.hashCode(entity, obj);
        }
    }
    
    private <T> T assertSerializeAndDeserialize(T obj) throws Exception {
        String serializedForm = serializer.toString(obj);
        System.out.println("serializedForm="+serializedForm);
        Object deserialized = serializer.fromString(serializedForm);
        assertEquals(deserialized, obj, "serializedForm="+serializedForm);
        return (T) deserialized;
    }
    
    static class LookupContextImpl implements LookupContext {
        private final Map<String, ? extends Entity> entities;
        private final Map<String, ? extends Location> locations;
        
        LookupContextImpl(Map<String,? extends Entity> entities, Map<String,? extends Location> locations) {
            this.entities = entities;
            this.locations = locations;
        }
        @Override public Entity lookupEntity(Class<?> type, String id) {
            if (entities.containsKey(id)) {
                Entity result = entities.get(id);
                if (type != null && !type.isInstance(result)) {
                    throw new IllegalStateException("Entity with id "+id+" does not match type "+type+"; got "+result);
                }
                return result;
            }
            throw new NoSuchElementException("no entity with id "+id+"; contenders are "+locations.keySet()); 
        }
        @Override public Location lookupLocation(Class<?> type, String id) {
            if (locations.containsKey(id)) {
                Location result = locations.get(id);
                if (type != null && !type.isInstance(result)) {
                    throw new IllegalStateException("Location with id "+id+" does not match type "+type+"; got "+result);
                }
                return result;
            }
            throw new NoSuchElementException("no location with id "+id+"; contenders are "+locations.keySet()); 
        }
    };
}
