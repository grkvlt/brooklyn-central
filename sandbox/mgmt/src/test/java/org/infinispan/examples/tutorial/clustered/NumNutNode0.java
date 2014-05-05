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


public class NumNutNode0 extends AbstractNode {

   public static void main(String[] args) throws Exception {
      new NumNutNode0().run();
   }
   
   public NumNutNode0() {
      super(0);
   }   
   public void run() throws InterruptedException {

      // Add a listener so that we can see the put from Node1
//      cache.addListener(new LoggingListener());

      waitForClusterToForm();
      Cache<String, String> cache = getCacheManager().getCache("Demo");
      
      while (true) {
          Thread.sleep(5000);
          String result="";
          for (int i=0; i<10; i++) result += "K"+i+"="+cache.get("K"+i)+" ";
          System.out.println("Node0: "+result);
      }
   }
   
   @Override
   protected int getNodeId() {
      return 0;
   }

}
