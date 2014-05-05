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
package org.infinispan.examples.tutorial.clustered;

import org.infinispan.config.Configuration;
import org.infinispan.config.GlobalConfiguration;
import org.infinispan.examples.tutorial.clustered.util.ClusterValidation;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;

public abstract class AbstractNode {
   
   public static final int CLUSTER_SIZE = 2;

   private final EmbeddedCacheManager cacheManager;
   private final int nodeId;
   
   public AbstractNode(int nodeId) {
      this.nodeId = nodeId;
      // Create the configuration, and set to replication
      GlobalConfiguration gc = GlobalConfiguration.getClusteredDefault();
      Configuration c = new Configuration();
      c.setCacheMode(Configuration.CacheMode.REPL_SYNC);

      // Create the cache manager and get a handle to the cache we will use
      this.cacheManager = new DefaultCacheManager(gc, c);
   }
   
   protected EmbeddedCacheManager getCacheManager() {
      return cacheManager;
   }
   
   protected void waitForClusterToForm() {
      // Wait for the cluster to form, erroring if it doesn't form after the
      // timeout
      if (!ClusterValidation.waitForClusterToForm(getCacheManager(), getNodeId(), CLUSTER_SIZE)) {
	 throw new IllegalStateException("Error forming cluster, check the log");
      }
   }
   
   protected int getNodeId()
   {
      return nodeId;
   }

}
