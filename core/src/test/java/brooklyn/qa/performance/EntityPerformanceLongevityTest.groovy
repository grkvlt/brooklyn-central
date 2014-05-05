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
package brooklyn.qa.performance

import static brooklyn.test.TestUtils.*
import static org.testng.Assert.*

import org.slf4j.Logger
import org.slf4j.LoggerFactory

import brooklyn.test.entity.TestEntity


public class EntityPerformanceLongevityTest extends EntityPerformanceTest {

    private static final Logger LOG = LoggerFactory.getLogger(EntityPerformanceLongevityTest.class)

    // TODO enable this to some big number to see what happens when things run for a long time.
    // e.g. will we eventually get OOME when storing all tasks relating to effector calls?
    
//    protected int numIterations() {
//        return 1000000
//    }
    
}
