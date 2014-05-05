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
package brooklyn.entity.dns.geoscaling

import static org.testng.AssertJUnit.*

import java.util.LinkedHashSet
import java.util.Set

import org.testng.annotations.Test

import brooklyn.location.geo.HostGeoInfo
import brooklyn.util.ResourceUtils


/**
 * {@link GeoscalingScriptGenerator} unit tests.
 */
class GeoscalingScriptGeneratorTest {
    
    private final static Set<HostGeoInfo> HOSTS = new LinkedHashSet<HostGeoInfo>();
    static {
        HOSTS.add(new HostGeoInfo("1.2.3.100", "Server 1", 40.0, -80.0));
        HOSTS.add(new HostGeoInfo("1.2.3.101", "Server 2", 30.0, 20.0));
    }
    
    
    @Test
    public void testScriptGeneration() {
        Date generationTime = new Date(0);
        String generatedScript = GeoscalingScriptGenerator.generateScriptString(generationTime, HOSTS);
        assertTrue(generatedScript.contains("1.2.3"));
        String expectedScript = ResourceUtils.create(this).getResourceAsString("brooklyn/entity/dns/geoscaling/expectedScript.php");
        assertEquals(expectedScript, generatedScript);
        //also make sure leading slash is allowed
        String expectedScript2 = ResourceUtils.create(this).getResourceAsString("/brooklyn/entity/dns/geoscaling/expectedScript.php");
        assertEquals(expectedScript, generatedScript);
    }
    
}
