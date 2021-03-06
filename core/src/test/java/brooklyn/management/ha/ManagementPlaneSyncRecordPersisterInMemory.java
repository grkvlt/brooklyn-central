package brooklyn.management.ha;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import brooklyn.entity.rebind.persister.InMemoryObjectStore;
import brooklyn.util.time.Duration;

import com.google.common.annotations.VisibleForTesting;

/** @deprecated since 0.7.0 use {@link ManagementPlaneSyncRecordPersisterToObjectStore}
 * with {@link InMemoryObjectStore}
 * <code>
 * new ManagementPlaneSyncRecordPersisterToObjectStore(new InMemoryObjectStore(), classLoader)
 * </code> */
@Deprecated
public class ManagementPlaneSyncRecordPersisterInMemory implements ManagementPlaneSyncRecordPersister {

    private static final Logger LOG = LoggerFactory.getLogger(ManagementPlaneSyncRecordPersisterInMemory.class);

    private final MutableManagementPlaneSyncRecord memento = new MutableManagementPlaneSyncRecord();
    
    private volatile boolean running = true;
    
    @Override
    public synchronized void stop() {
        running = false;
    }

    @Override
    public ManagementPlaneSyncRecord loadSyncRecord() throws IOException {
        if (!running) {
            throw new IllegalStateException("Persister not running; cannot load memento");
        }
        
        return memento.snapshot();
    }
    
    @VisibleForTesting
    @Override
    public synchronized void waitForWritesCompleted(Duration timeout) throws InterruptedException, TimeoutException {
        // The synchronized is sufficient - guarantee that no concurrent calls
        return;
    }

    @Override
    public synchronized void delta(Delta delta) {
        if (!running) {
            if (LOG.isDebugEnabled()) LOG.debug("Persister not running; ignoring checkpointed delta of manager-memento");
            return;
        }
        
        for (ManagementNodeSyncRecord m : delta.getNodes()) {
            memento.addNode(m);
        }
        for (String id : delta.getRemovedNodeIds()) {
            memento.deleteNode(id);
        }
        switch (delta.getMasterChange()) {
        case NO_CHANGE:
            break; // no-op
        case SET_MASTER:
            memento.setMasterNodeId(checkNotNull(delta.getNewMasterOrNull()));
            break;
        case CLEAR_MASTER:
            memento.setMasterNodeId(null);
            break; // no-op
        default:
            throw new IllegalStateException("Unknown state for master-change: "+delta.getMasterChange());
        }
    }

}
