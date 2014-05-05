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

import com.google.common.base.Preconditions;

import brooklyn.catalog.CatalogItem;
import brooklyn.util.exceptions.Exceptions;

public class CatalogItemDo<T> implements CatalogItem<T> {

    protected final CatalogDo catalog;
    protected final CatalogItem<T> itemDto;

    protected volatile Class<T> javaClass; 
    
    public CatalogItemDo(CatalogDo catalog, CatalogItem<T> itemDto) {
        this.catalog = Preconditions.checkNotNull(catalog, "catalog");
        this.itemDto = Preconditions.checkNotNull(itemDto, "itemDto");
    }

    public CatalogItem<?> getDto() {
        return itemDto;
    }

    @Override
    public brooklyn.catalog.CatalogItem.CatalogItemType getCatalogItemType() {
        return itemDto.getCatalogItemType();
    }

    @Override
    public Class<T> getCatalogItemJavaType() {
        return itemDto.getCatalogItemJavaType();
    }

    @Override
    public String getId() {
        return itemDto.getId();
    }

    @Override
    public String getJavaType() {
        return itemDto.getJavaType();
    }

    @Override
    public String getName() {
        return itemDto.getName();
    }

    @Override
    public String getDescription() {
        return itemDto.getDescription();
    }

    @Override
    public String getIconUrl() {
        return itemDto.getIconUrl();
    }
    
    public Class<T> getJavaClass() {
        if (javaClass==null) loadJavaClass();
        return javaClass;
    }
    
    @SuppressWarnings("unchecked")
    protected Class<? extends T> loadJavaClass() {
        try {
            if (javaClass!=null) return javaClass;
            javaClass = (Class<T>) catalog.getRootClassLoader().loadClass(getJavaType());
            return javaClass;
        } catch (ClassNotFoundException e) {
            throw Exceptions.propagate(e);
        }
    }

    @Override
    public String toString() {
        return getClass().getCanonicalName()+"["+itemDto+"]";
    }

    public String toXmlString() {
        return itemDto.toXmlString();
    }
    
}
