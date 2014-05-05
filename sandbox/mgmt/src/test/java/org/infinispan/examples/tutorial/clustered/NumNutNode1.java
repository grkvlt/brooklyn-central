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

import org.infinispan.Cache;

public class NumNutNode1 extends AbstractNode {

   public static void main(String[] args) throws Exception {
      new NumNutNode1().run();
   }
   
   public NumNutNode1() {
      super(1);
   }

   public void run() throws InterruptedException {

      waitForClusterToForm();
      Cache<String, String> cache = getCacheManager().getCache("Demo");

      // Put some information in the cache that we can display on the other node
      cache.put("key", "value");
      
      while (true) {
          Thread.sleep(1000);
          String k = "K"+((int)(10*Math.random()));
          String v = "V"+((int)(10*Math.random()));
          cache.put(k, v);
          System.out.println("Node1: "+k+"="+v);
      }
   }

}
