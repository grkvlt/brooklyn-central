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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import brooklyn.catalog.internal.CatalogClasspathDo.CatalogScanningModes;
import brooklyn.entity.basic.Entities;
import brooklyn.management.internal.LocalManagementContext;

public class CatalogDtoTest {

    private static final Logger log = LoggerFactory.getLogger(CatalogDtoTest.class);

    private LocalManagementContext managementContext;
    
    @BeforeMethod(alwaysRun = true)
    public void setUp() throws Exception {
        managementContext = new LocalManagementContext();
    }
    
    @AfterMethod(alwaysRun = true)
    public void tearDown() throws Exception {
        if (managementContext != null) Entities.destroyAll(managementContext);
    }

    @Test
    public void testCatalogLookup() {
        CatalogDto root = buildHadoopsExample();
        checkHadoopsExample(root);
    }
    
    @Test
    public void testCatalogSerializeAndLookup() {
        CatalogDto root = buildHadoopsExample();
        CatalogXmlSerializer seriailizer = new CatalogXmlSerializer();
        
        String xml = seriailizer.toString(root);
        log.info("Hadoops example catalog serialized as:\n"+xml);
        
        CatalogDto root2 = (CatalogDto) seriailizer.fromString(xml);
        checkHadoopsExample(root2);
    }

    protected void checkHadoopsExample(CatalogDto root) {
        Assert.assertEquals(root.catalogs.size(), 4);
        CatalogDo loader = new CatalogDo(root).load(managementContext, null);
        
        CatalogItemDo<?> worker = loader.getCache().get("io.brooklyn.mapr.m3.WorkerNode");
        Assert.assertNotNull(worker);
        Assert.assertEquals(worker.getName(), "M3 Worker Node");
    }

    public static CatalogDto buildHadoopsExample() {
        CatalogDo root = new CatalogDo(CatalogDto.newNamedInstance("My Local Catalog", 
                "My favourite local settings, including remote catalogs -- " +
        		"intended partly as a teaching example for what can be expressed, and how"));
        root.setClasspathScanForEntities(CatalogScanningModes.NONE);
        
        CatalogDo m3Catalog = new CatalogDo(CatalogDto.newNamedInstance("MapR M3", null));
        m3Catalog.addToClasspath("file://~/.m2/repository/io/cloudsoft/brooklyn-mapr/1.0.0-SNAPSHOT/brooklyn-mapr.jar");
        m3Catalog.addEntry(CatalogItemDtoAbstract.newTemplate(
                "io.brooklyn.mapr.M3App", "M3 Application"));
        m3Catalog.addEntry(CatalogItemDtoAbstract.newEntity(
                "io.brooklyn.mapr.m3.ZookeperWorkerNode", "M3 Zookeeper+Worker Node"));
        m3Catalog.addEntry(CatalogItemDtoAbstract.newEntity(
                "io.brooklyn.mapr.m3.WorkerNode", "M3 Worker Node"));
        root.addCatalog(m3Catalog.dto);
        
        CatalogDo cdhCatalog = new CatalogDo(CatalogDto.newNamedInstance("Cloudera", 
                "CDH catalog, pointing to JARs as I have them installed on my machine already, "+
                "tweaked for my preferences (overriding templates scanned from these JARs)"));
        cdhCatalog.setClasspathScanForEntities(CatalogScanningModes.ANNOTATIONS);
        cdhCatalog.addToClasspath(
                "file://~/.m2/repository/io/cloudsoft/brooklyn-cdh/1.0.0-SNAPSHOT/brooklyn-cdh.jar",
                "file://~/.m2/repository/io/cloudsoft/brooklyn-cdh/1.0.0-SNAPSHOT/whirr-cm.jar");
        cdhCatalog.addEntry(CatalogItemDtoAbstract.newTemplate(
                "io.brooklyn.cloudera.ClouderaForHadoopWithManager",
                "RECOMMENDED: CDH Hadoop Application with Cloudera Manager"));
        root.addCatalog(cdhCatalog.dto);

        root.addCatalog(CatalogDto.newLinkedInstance("http://cloudsoftcorp.com/amp-brooklyn-catalog.xml"));
        root.addCatalog(CatalogDto.newLinkedInstance("http://microsoot.com/oofice-catalog.xml"));
        return root.dto;
    }

}

