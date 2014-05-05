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
package brooklyn.catalog;

import com.google.common.base.Predicate;

public interface BrooklynCatalog {

    /** finds the item with the given ID, or null if not found */
    CatalogItem<?> getCatalogItem(String id);
    
    /** variant of {@link #getCatalogItem(String)} which checks (and casts) type for convenience
     * (returns null if type does not match) */
    <T> CatalogItem<T> getCatalogItem(Class<T> type, String id);
    
    /** returns all items in the catalog */
    <T> Iterable<CatalogItem<T>> getCatalogItems();
    /** convenience for filtering items in the catalog; see CatalogPredicates for useful filters */
    <T> Iterable<CatalogItem<T>> getCatalogItems(Predicate<? super CatalogItem<T>> filter);

    /** returns the classloader which should be used to load classes and entities;
     * this includes all the catalog's classloaders in the right order */
    public ClassLoader getRootClassLoader();
    
    /** throws exceptions if any problems */
    <T> Class<? extends T> loadClass(CatalogItem<T> item);
    <T> Class<? extends T> loadClassByType(String typeName, Class<T> typeClass);
    
    /** adds an item to the 'manual' catalog;
     * this does not update the classpath or have a record to the java Class,
     * so callers of this method will typically also need to call 
     * {@link #addToClasspath(String)} or {@link #addToClasspath(ClassLoader)} */
    void addItem(CatalogItem<?> item);
    
    /** creates a catalog item and adds it to the 'manual' catalog,
     * with the corresponding Class definition (loaded by a classloader)
     * registered and available in the classloader.
     * <p> 
     * note that the class will be available for this session only,
     * although the record of the item will appear in the catalog DTO if exported,
     * so it is recommended to edit the 'manual' catalog DTO if using it to
     * generate a catalog, either adding the appropriate classpath URL or removing this entry. */
    CatalogItem<?> addItem(Class<?> clazz);

    /** adds a classpath entry which will be used by the 'manual' catalog */
    void addToClasspath(ClassLoader loader);
    /** adds a classpath entry which will be used by the 'manual' catalog */
    void addToClasspath(String url);
    
}
