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

public class OneAB {

    public static void main(String[] args) {
        new OneAB().test();
    }
    
    Throwable error = null;
    public void test() {
        def t1 = new Thread({ try { OneA.main() } catch (Throwable t) { error = t; error.printStackTrace(); }});
        def t2 = new Thread({ try { OneB.main() } catch (Throwable t) { error = t; error.printStackTrace(); }});
        t1.start();
        Thread.sleep(5000+(int)(3000*Math.random()));
        t2.start();
        
        t1.join();
        t2.join();
        
        if (error) {
            error.printStackTrace()
            throw error
        }
    }
}
