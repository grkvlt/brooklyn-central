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
package brooklyn.catalog.internal;

import brooklyn.catalog.CatalogItem;

public abstract class CatalogItemDtoAbstract<T> implements CatalogItem<T> {
    
    String id;
    String type;
    String name;
    String description;
    String iconUrl;
    
    public String getId() {
        if (id!=null) return id;
        return type;
    }
    
    public String getJavaType() {
        return type;
    }
    
    public String getName() {
        return name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public String getIconUrl() {
        return iconUrl;
    }
    
    public static CatalogTemplateItemDto newTemplate(String type, String name) {
        return newTemplate(null, type, name, null);
    }
    public static CatalogTemplateItemDto newTemplate(String id, String type, String name, String description){
        return set(new CatalogTemplateItemDto(), id, type, name, description);
    }

    public static CatalogEntityItemDto newEntity(String type, String name) {
        return newEntity(null, type, name, null);
    }
    public static CatalogEntityItemDto newEntity(String id, String type, String name, String description){
        return set(new CatalogEntityItemDto(), id, type, name, description);
    }

    public static CatalogPolicyItemDto newPolicy(String type, String name) {
        return newPolicy(null, type, name, null);
    }
    public static CatalogPolicyItemDto newPolicy(String id, String type, String name, String description){
        return set(new CatalogPolicyItemDto(), id, type, name, description);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private static <T extends CatalogItemDtoAbstract> T set(T target, String id, String type, String name, String description) {
        target.id = id;
        target.type = type;
        target.name = name;
        target.description = description;
        return target;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName()+"["+getId()+"/"+getName()+"]";
    }

    transient CatalogXmlSerializer serializer;
    
    public String toXmlString() {
        if (serializer==null) loadSerializer();
        return serializer.toString(this);
    }
    
    private synchronized void loadSerializer() {
        if (serializer==null) 
            serializer = new CatalogXmlSerializer();
    }
    
}
