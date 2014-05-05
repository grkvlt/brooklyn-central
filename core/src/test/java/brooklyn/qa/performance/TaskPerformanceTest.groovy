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

import brooklyn.management.internal.LocalManagementContext
import org.testng.annotations.AfterMethod

import static brooklyn.test.TestUtils.*
import static org.testng.Assert.*

import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test

import brooklyn.util.task.BasicExecutionManager
import brooklyn.util.task.SingleThreadedScheduler

public class TaskPerformanceTest extends AbstractPerformanceTest {

    private static final Logger LOG = LoggerFactory.getLogger(TaskPerformanceTest.class)
    
    private static final long LONG_TIMEOUT_MS = 30*1000
    
    BasicExecutionManager executionManager
    
    @BeforeMethod(alwaysRun=true)
    public void setUp() {
        super.setUp()
        
        app.start([loc])
        
        executionManager = app.managementContext.executionManager
    }

    public static final int numIterations = 200000;

    @Test(groups=["Integration", "Acceptance"])
    public void testExecuteSimplestRunnable() {
        double minRatePerSec = 1000 * PERFORMANCE_EXPECTATION;
        
        final AtomicInteger counter = new AtomicInteger();
        final CountDownLatch completionLatch = new CountDownLatch(1)
        
        Runnable work = new Runnable() { public void run() {
                int val = counter.incrementAndGet()
                if (val >= numIterations) completionLatch.countDown()
            }}

        measureAndAssert("executeSimplestRunnable", numIterations, minRatePerSec,
                { executionManager.submit(work) },
                { completionLatch.await(LONG_TIMEOUT_MS, TimeUnit.MILLISECONDS); assertTrue(completionLatch.getCount() <= 0) })
    }
    
    @Test(groups=["Integration", "Acceptance"])
    public void testExecuteRunnableWithTags() {
        double minRatePerSec = 1000 * PERFORMANCE_EXPECTATION;
        
        final AtomicInteger counter = new AtomicInteger();
        final CountDownLatch completionLatch = new CountDownLatch(1)

        Runnable work = new Runnable() { public void run() {
                int val = counter.incrementAndGet()
                if (val >= numIterations) completionLatch.countDown()
            }}

        Map flags = [tags:["a","b"]]
        
        measureAndAssert("testExecuteRunnableWithTags", numIterations, minRatePerSec,
                { executionManager.submit(flags, work) },
                { completionLatch.await(LONG_TIMEOUT_MS, TimeUnit.MILLISECONDS); assertTrue(completionLatch.getCount() <= 0) })
    }
    
    @Test(groups=["Integration", "Acceptance"])
    public void testExecuteWithSingleThreadedScheduler() {
        double minRatePerSec = 1000 * PERFORMANCE_EXPECTATION;

        executionManager.setTaskSchedulerForTag("singlethreaded", SingleThreadedScheduler.class);
        
        final AtomicInteger concurrentCallCount = new AtomicInteger();
        final AtomicInteger submitCount = new AtomicInteger();
        final AtomicInteger counter = new AtomicInteger();
        final CountDownLatch completionLatch = new CountDownLatch(1)
        final List<Exception> exceptions = new CopyOnWriteArrayList()
        
        Runnable work = new Runnable() { public void run() {
                int numConcurrentCalls = concurrentCallCount.incrementAndGet()
                try {
                    if (numConcurrentCalls > 1) throw new IllegalStateException("numConcurrentCalls=$numConcurrentCalls")
                    int val = counter.incrementAndGet()
                    if (val >= numIterations) completionLatch.countDown()
                } catch (Exception e) {
                    exceptions.add(e)
                    LOG.warn("Exception in runnable of testExecuteWithSingleThreadedScheduler", e)
                    throw e
                } finally {
                    concurrentCallCount.decrementAndGet()
                }
            }}

        measureAndAssert("testExecuteWithSingleThreadedScheduler", numIterations, minRatePerSec,
                { 
                    while (submitCount.get() > counter.get() + 5000) {
                        LOG.info("delaying because ${submitCount.get()} submitted and only ${counter.get()} run")
                        Thread.sleep(500);
                    }
                    executionManager.submit([tags:["singlethreaded"]], work); submitCount.incrementAndGet(); },
                { completionLatch.await(LONG_TIMEOUT_MS, TimeUnit.MILLISECONDS); assertTrue(completionLatch.getCount() <= 0) })
        
        if (exceptions.size() > 0) throw exceptions.get(0)
    }
    
    public static void main(String[] args) {
        def t = new TaskPerformanceTest();
        t.setUp();
        t.testExecuteWithSingleThreadedScheduler();
    }
}
