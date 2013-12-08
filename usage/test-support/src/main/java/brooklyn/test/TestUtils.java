package brooklyn.test;

import static org.testng.Assert.fail;
import groovy.time.TimeDuration;

import java.io.File;
import java.net.URL;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.codehaus.groovy.runtime.InvokerInvocationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import brooklyn.entity.Entity;
import brooklyn.event.AttributeSensor;
import brooklyn.util.time.Duration;

import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.collect.Iterables;

/**
 * Helper functions for tests of Tomcat, JBoss and others.
 * 
 * Note that methods will migrate from here to {@link Asserts} in future releases.
 */
public class TestUtils {

    private static final Logger log = LoggerFactory.getLogger(TestUtils.class);

    private TestUtils() { }
    
    // calling groovy from java doesn't cope with generics here; stripping them from here :-(
    //      <T> void assertEventually(Map flags=[:], Supplier<? extends T> supplier, Predicate<T> predicate)
    /**
     * @deprecated since 0.5; use {@link Asserts#eventually(Map, Supplier, Predicate)}
     */
    @Deprecated
    public static void assertEventually(Map flags, Supplier supplier, Predicate predicate) {
        Asserts.eventually(flags, supplier, predicate);
    }
    
    /**
     * @deprecated since 0.5; use {@link Asserts#eventually(Map, Supplier, Predicate, String)}
     */
    @Deprecated
    public static <T> void assertEventually(Map flags, Supplier<? extends T> supplier, Predicate<T> predicate, String errMsg) {
        Asserts.eventually(flags, supplier, predicate, errMsg);
    }

    /**    
     * @deprecated since 0.5; use {@link Asserts#succeedsEventually(java.util.Map, Callable)}
     */
    @Deprecated
    public static void assertEventually(Map flags, Callable c) {
        executeUntilSucceeds(flags, c);
    }
    
    /**
     * @deprecated since 0.5; use {@link Asserts#succeedsEventually(Map, Runnable)}
     */
    @Deprecated
    public static void assertEventually(Map flags, Runnable c) {
        executeUntilSucceeds(flags, c);
    }

    /**
     * @deprecated since 0.5; use {@link Asserts#succeedsEventually(Map, Callable)}
     */
    @Deprecated
    public static void executeUntilSucceeds(Map flags, Callable c) {
        Asserts.succeedsEventually(flags, c);
    }
    
    /**
     * @deprecated since 0.5; use {@link Asserts#succeedsEventually(Map, Runnable)}
     */
    @Deprecated
    public static void executeUntilSucceeds(Map flags, Runnable r) {
        Asserts.succeedsEventually(flags, r);
    }
    
    /**
     * @deprecated since 0.5; use {@link Asserts#succeedsContinually(Map, Runnable)}
     */
    @Deprecated
    public static <T> void assertSucceedsContinually(Map flags, Runnable job) {
        assertSucceedsContinually(flags, Executors.callable(job));
    }
    
    /**
     * @deprecated since 0.5; use {@link Asserts#succeedsContinually(Map, Callable)}
     */
    @Deprecated
    public static void assertSucceedsContinually(Map flags, Callable<?> job) {
        Asserts.succeedsContinually(flags, job);
    }
    
    /**
     * @deprecated since 0.5; use {@link Asserts#continually(Map, Supplier, Predicate)}
     */
    @Deprecated
    // FIXME When calling from java, the generics declared in groovy messing things up!
    public static void assertContinuallyFromJava(Map flags, Supplier supplier, Predicate predicate) {
        Asserts.continually(flags, supplier, predicate);
    }
    
    /**
     * @deprecated since 0.5; use {@link Asserts#continually(Map, Supplier, Predicate)}
     */
    @Deprecated
    public static <T> void assertContinually(Map flags, Supplier<? extends T> supplier, Predicate<T> predicate) {
        Asserts.continually(flags, supplier, predicate, (String)null);
    }

    /**
     * @deprecated since 0.5; use {@link Asserts#continually(Map, Supplier, Predicate, String)}
     */
    @Deprecated
    public static <T> void assertContinually(Map flags, Supplier<? extends T> supplier, Predicate<T> predicate, String errMsg, long durationMs) {
        flags.put("duration", Duration.millis(durationMs));
        Asserts.continually(flags, supplier, predicate, errMsg);
    }
    
    /**
     * @deprecated since 0.5; use {@link Asserts#continually(Map, Supplier, Predicate, String)}
     */
    @Deprecated
    public static <T> void assertContinually(Map flags, Supplier<? extends T> supplier, Predicate<T> predicate, String errMsg) {
        Asserts.continually(flags, supplier, predicate, errMsg);
    }
    
    public static class BooleanWithMessage {
        final boolean value;
        final String message;
        public BooleanWithMessage(boolean value, String message) {
            this.value = value; this.message = message;
        }
        public boolean asBoolean() {
            return value;
        }
        public String toString() {
            return message;
        }
    }
    
    /**
     * @deprecated since 0.5; use {@link brooklyn.util.ResourceUtils}
     */
    @Deprecated
    public static File getResource(String path, ClassLoader loader) {
        URL resource = loader.getResource(path);
        if (resource==null)
            throw new IllegalArgumentException("cannot find required entity '"+path+"'");
            
        return new File(resource.getPath());
    }

    /**
     * @deprecated since 0.5; use long and {@link TimeUnit}
     */
    @Deprecated
    public static TimeDuration toTimeDuration(Object duration) {
        return toTimeDuration(duration, null);
    }
            
    /**
     * @deprecated since 0.5; use long and {@link TimeUnit}
     */
    @Deprecated
    public static TimeDuration toTimeDuration(Object duration, TimeDuration defaultVal) {
        if (duration == null) {
            return defaultVal;
        } else if (duration instanceof TimeDuration) {
            return (TimeDuration) duration;
        } else if (duration instanceof Number) {
            return new TimeDuration(0,0,0,(Integer)duration);
            // TODO would be nice to have this, but we need to sort out utils / test-utils dependency
//        } else if (duration instanceof String) {
//            return Time.parseTimeString((String)duration);
        } else {
            throw new IllegalArgumentException("Cannot convert $duration of type ${duration.class.name} to a TimeDuration");
        }
    }
    
    public static Throwable unwrapThrowable(Throwable t) {
        if (t.getCause() == null) {
            return t;
        } else if (t instanceof ExecutionException) {
            return unwrapThrowable(t.getCause());
        } else if (t instanceof InvokerInvocationException) {
            return unwrapThrowable(t.getCause());
        } else {
            return t;
        }
    }

    /**
     * @deprecated since 0.5; use {@link EntityTestUtils#assertAttributeEqualsEventually(Entity, AttributeSensor, Object)}
     */
    @Deprecated
    public static <T> void assertAttributeEventually(Entity entity, AttributeSensor<T> attribute, T expected) {
        EntityTestUtils.assertAttributeEqualsEventually(entity, attribute, expected);
    }
    
    /**
     * @deprecated since 0.5; use {@link EntityTestUtils#assertAttributeEqualsContinually(Entity, AttributeSensor, Object)}
     */
    @Deprecated
    public static <T> void assertAttributeContinually(Entity entity, AttributeSensor<T> attribute, T expected) {
        EntityTestUtils.assertAttributeEqualsContinually(entity, attribute, expected);
    }
    
    /**
     * @deprecated since 0.5; use {@link HttpTestUtils#assertHttpStatusCodeEquals(String, int)}
     */
    @Deprecated
    public static void assertUrlStatusCodeEventually(final String url, final int expected) {
        HttpTestUtils.assertHttpStatusCodeEquals(url, expected);
    }

    /**
     * @deprecated since 0.5; use {@link Asserts#assertFails(Runnable)}
     */
    @Deprecated
    public static void assertFails(Runnable c) {
        Asserts.assertFails(c);
    }
    
    /**
     * @deprecated since 0.5; use {@link Asserts#assertFailsWith(Runnable, Class, Class...)}
     */
    @Deprecated
    public static void assertFailsWith(Runnable c, final Class<? extends Throwable> validException, final Class<? extends Throwable> ...otherValidExceptions) {
        Asserts.assertFailsWith(c, validException, otherValidExceptions);
    }
    
    /**
     * @deprecated since 0.5; use {@link Asserts#assertFailsWith(Runnable, Predicate)}
     */
    @Deprecated
    public static void assertFailsWith(Runnable c, Predicate<Throwable> exceptionChecker) {
        Asserts.assertFailsWith(c, exceptionChecker);
    }

    public static void assertSetsEqual(Collection c1, Collection c2) {
        Set s = new LinkedHashSet();
        s.addAll(c1); s.removeAll(c2);
        if (!s.isEmpty()) fail("First argument contains additional contents: "+s);
        s.clear(); s.addAll(c2); s.removeAll(c1);
        if (!s.isEmpty()) fail("Second argument contains additional contents: "+s);
    }
    
    /**
     * @deprecated since 0.5; use {@code assertFalse(Iterables.isEmpty(c))}
     */
    @Deprecated
    public static <T> void assertNonEmpty(Iterable<T> c) {
        if (c.iterator().hasNext()) return;
        fail("Expected non-empty set");
    }

    /**
     * @deprecated since 0.5; use {@code assertEquals(Iterables.size(c), expectedSize)}
     */
    @Deprecated
    public static <T> void assertSize(Iterable<T> c, int expectedSize) {
        int actualSize = Iterables.size(c);
        if (actualSize==expectedSize) return;
        fail("Expected collection of size "+expectedSize+" but got size "+actualSize+": "+c);
    }

    public static void assertStringContainsLiteral(String string, String substring) {
        if (string==null) fail("String is null");
        if (substring==null) fail("Substring is null");
        if (string.indexOf(substring)>=0) return;
        fail("String '"+string+"' does not contain expected pattern '"+substring+"'");
    }
    
}
