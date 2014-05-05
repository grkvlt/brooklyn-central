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

import java.util.List;

public class CatalogDto {
    
    String id;
    String url;
    String name;
    String description;
    CatalogClasspathDto classpath;
    List<CatalogItemDtoAbstract<?>> entries = null;
    
    // for thread-safety, any dynamic additions to this should be handled by a method 
    // in this class which does copy-on-write
    List<CatalogDto> catalogs = null;

    public static CatalogDto newNamedInstance(String name, String description) {
        CatalogDto result = new CatalogDto();
        result.name = name;
        result.description = description;
        return result;
    }

    public static CatalogDto newLinkedInstance(String url) {
        CatalogDto result = new CatalogDto();
        result.url = url;
        return result;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName()+"["+
                (url!=null ? url+"; " : "")+
                (name!=null ? name+":" : "")+
                (id!=null ? id : "")+
                "]";
    }

    /**
     * @throws NullPointerException If source is null (and !skipNulls)
     */
    void copyFrom(CatalogDto source, boolean skipNulls) throws IllegalAccessException {
        if (source==null) {
            if (skipNulls) return;
            throw new NullPointerException("source DTO is null, when copying to "+this);
        }
        
        if (!skipNulls || source.id != null) id = source.id;
        if (!skipNulls || source.url != null) url = source.url;
        if (!skipNulls || source.name != null) name = source.name;
        if (!skipNulls || source.description != null) description = source.description;
        if (!skipNulls || source.classpath != null) classpath = source.classpath;
        if (!skipNulls || source.entries != null) entries = source.entries;
    }

}
