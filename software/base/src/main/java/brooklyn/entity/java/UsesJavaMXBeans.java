package brooklyn.entity.java;

import java.util.Map;

import brooklyn.config.ConfigKey;
import brooklyn.entity.basic.Attributes;
import brooklyn.entity.basic.ConfigKeys;
import brooklyn.event.AttributeSensor;
import brooklyn.event.basic.BasicAttributeSensor;
import brooklyn.util.flags.SetFromFlag;

public interface UsesJavaMXBeans {

    @SetFromFlag("mxbeanStatsEnabled")
    ConfigKey<Boolean> MXBEAN_STATS_ENABLED =
            ConfigKeys.newConfigKey("java.metrics.mxbeanStatsEnabled", "Enables collection of JVM stats from the MXBeans, such as memory and thread usage (default is true)", true);

    AttributeSensor<Long> USED_HEAP_MEMORY =
            Attributes.newAttributeSensor("java.metrics.heap.used", "current heap size in bytes");
    AttributeSensor<Long> INIT_HEAP_MEMORY =
            Attributes.newAttributeSensor("java.metrics.heap.init", "initial heap size in bytes");
    AttributeSensor<Long> COMMITTED_HEAP_MEMORY =
            Attributes.newAttributeSensor("java.metrics.heap.committed", "commited heap size in bytes");
    AttributeSensor<Long> MAX_HEAP_MEMORY =
            Attributes.newAttributeSensor("java.metrics.heap.max", "max heap size in bytes");
    AttributeSensor<Long> NON_HEAP_MEMORY_USAGE =
            Attributes.newAttributeSensor("java.metrics.nonheap.used", "current non-heap size in bytes");
    AttributeSensor<Integer> CURRENT_THREAD_COUNT =
            Attributes.newAttributeSensor("java.metrics.threads.current", "current number of threads");
    AttributeSensor<Integer> PEAK_THREAD_COUNT =
            Attributes.newAttributeSensor("java.metrics.threads.max", "peak number of threads");

    // runtime system attributes
    AttributeSensor<Long> START_TIME =
            Attributes.newAttributeSensor("java.metrics.starttime", "start time");
    AttributeSensor<Long> UP_TIME =
            Attributes.newAttributeSensor("java.metrics.uptime", "the uptime");

    //operating system attributes
    AttributeSensor<Long> PROCESS_CPU_TIME =
            Attributes.newAttributeSensor("java.metrics.processCpuTime", "the process cpu time (in 100 nanosecond blocks; use the difference between samples)");
    AttributeSensor<Double> PROCESS_CPU_TIME_FRACTION =
            Attributes.newAttributeSensor("java.metrics.processCpuTime.fraction", "the fraction of time (since the last event) consumed as cpu time");

    public static final Integer AVG_PROCESS_CPU_TIME_FRACTION_PERIOD = 10 * 1000;
    AttributeSensor<Double> AVG_PROCESS_CPU_TIME_FRACTION = new BasicAttributeSensor<Double>(
            Double.class, String.format("java.metrics.processCpuTime.fraction.avg.%s", AVG_PROCESS_CPU_TIME_FRACTION_PERIOD),
            String.format("Average Reqs/Sec (over the last %sms)", AVG_PROCESS_CPU_TIME_FRACTION_PERIOD));

    AttributeSensor<Integer> AVAILABLE_PROCESSORS =
            Attributes.newAttributeSensor("java.metrics.processors.available", "number of processors available to the Java virtual machine");
    AttributeSensor<Double> SYSTEM_LOAD_AVERAGE =
            Attributes.newAttributeSensor("java.metrics.systemload.average", "average system load");
    AttributeSensor<Long> TOTAL_PHYSICAL_MEMORY_SIZE =
            Attributes.newAttributeSensor("java.metrics.physicalmemory.total", "The physical memory available to the operating system");
    AttributeSensor<Long> FREE_PHYSICAL_MEMORY_SIZE =
            Attributes.newAttributeSensor("java.metrics.physicalmemory.free", "The free memory available to the operating system");

    // GC attributes
    AttributeSensor<Map<?, ?>> GARBAGE_COLLECTION_TIME = Attributes.newAttributeSensor("java.metrics.gc.time", "garbage collection time");

}