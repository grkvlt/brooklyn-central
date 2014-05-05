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
package brooklyn.util.xstream;

import java.util.Iterator;
import java.util.Map;

import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.ExtendedHierarchicalStreamWriterHelper;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.Mapper;

/** equivalent to super, but cleaner methods */
public class MapConverter extends com.thoughtworks.xstream.converters.collections.MapConverter {

    public MapConverter(Mapper mapper) {
        super(mapper);
    }

    public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
        Map map = (Map) source;
        for (Iterator iterator = map.entrySet().iterator(); iterator.hasNext();) {
            Map.Entry entry = (Map.Entry) iterator.next();
            marshalEntry(writer, context, entry);
        }
    }

    protected String getEntryNodeName() { return mapper().serializedClass(Map.Entry.class); }
    
    protected void marshalEntry(HierarchicalStreamWriter writer, MarshallingContext context, Map.Entry entry) {
        ExtendedHierarchicalStreamWriterHelper.startNode(writer, getEntryNodeName(), Map.Entry.class);

        writeItem(entry.getKey(), context, writer);
        writeItem(entry.getValue(), context, writer);

        writer.endNode();
    }

    protected void populateMap(HierarchicalStreamReader reader, UnmarshallingContext context, Map map) {
        while (reader.hasMoreChildren()) {
            reader.moveDown();
            unmarshalEntry(reader, context, map);
            reader.moveUp();
        }
    }

    protected void unmarshalEntry(HierarchicalStreamReader reader, UnmarshallingContext context, Map map) {
        reader.moveDown();
        Object key = readItem(reader, context, map);
        reader.moveUp();

        reader.moveDown();
        Object value = readItem(reader, context, map);
        reader.moveUp();

        map.put(key, value);
    }

}
