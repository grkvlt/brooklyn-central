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
package example.infinispan;

import org.infinispan.Cache
import org.infinispan.config.Configuration
import org.infinispan.config.GlobalConfiguration
import org.infinispan.manager.DefaultCacheManager
import org.infinispan.manager.EmbeddedCacheManager

public class OneB {
    
    public static void main(String[] args) throws Exception {
        println "starting OneB cache"
        
        GlobalConfiguration gc = GlobalConfiguration.getClusteredDefault();
        Configuration c = new Configuration();
        c.setCacheMode(Configuration.CacheMode.DIST_SYNC);
        c.setL1CacheEnabled(false);
        c.setNumOwners(1)
        
        EmbeddedCacheManager cm = new DefaultCacheManager(gc, c);
        println "B   "+cm.getStatus()+ " ... "+cm.getCacheNames()

        Cache<?, ?> cache = cm.getCache("x");
        println "started OneB cache"
        
        assert cache.size() <= 1;
        println "B cache size: "+cache.size()

        int count = 10;
        while (count>0) {
            println "B-9 "+cm.getStatus()+ " ... "+cm.getCacheNames()
            println "B   "+cache.getStatus()+" ... " + cache.keySet()
            println "B key: "+cache.get("key")
            println "B something serializable, it's: "+cache.get("key2")?.name
            if (cache.get("key")) count--;

//            println "B something not serializable, it's: "+cache.get("key3")?.name
//            println "B something definitely not serializable, it's: "+cache.get("key4")?.name
//            println "B something definitely not serializable, it's: "+cache.get("key4")?.t
            Thread.sleep(300);
            cache = cm.getCache("x");
        }
        cm.stop()
     }
    
    
}
