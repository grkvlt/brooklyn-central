package brooklyn.util;

import java.util.concurrent.atomic.AtomicBoolean;

import brooklyn.location.basic.PortRanges;

public class BrooklynLanguageExtensions {

    private BrooklynLanguageExtensions() {}

    private static AtomicBoolean done = new AtomicBoolean(false);

    public synchronized static void reinit() {
        done.set(false);
        init();
    }

    /** performs the language extensions required for this project */
    public synchronized static void init() {
        if (done.getAndSet(true)) return;
        PortRanges.init();
    }

    static {
        init();
    }
}
