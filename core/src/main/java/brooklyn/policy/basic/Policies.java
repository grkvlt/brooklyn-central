package brooklyn.policy.basic;

import brooklyn.entity.basic.Lifecycle;
import brooklyn.policy.Policy;

public class Policies {

    public static Lifecycle getPolicyStatus(Policy p) {
        if (p.isRunning()) return Lifecycle.RUNNING;
        if (p.isDestroyed()) return Lifecycle.DESTROYED;
        if (p.isSuspended()) return Lifecycle.STOPPED;
        // TODO could policy be in an error state?
        return Lifecycle.CREATED;        
    }
    
}
