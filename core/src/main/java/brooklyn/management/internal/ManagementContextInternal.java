package brooklyn.management.internal;

import java.net.URL;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import brooklyn.config.ConfigKey;
import brooklyn.entity.Effector;
import brooklyn.entity.Entity;
import brooklyn.event.basic.BasicConfigKey;
import brooklyn.management.ManagementContext;
import brooklyn.management.Task;

public interface ManagementContextInternal extends ManagementContext {

    String EFFECTOR_TAG = "EFFECTOR";
    String NON_TRANSIENT_TASK_TAG = "NON-TRANSIENT";

    ConfigKey<String> BROOKLYN_CATALOG_URL = new BasicConfigKey<String>("brooklyn.catalog.url",
            "The URL of a catalog.xml descriptor; absent for default (~/.brooklyn/catalog.xml), " +
            "or empty for no URL (use default scanner)", "file://~/.brooklyn/catalog.xml");

    ClassLoader getBaseClassLoader();

    Iterable<URL> getBaseClassPathForScanning();

    void setBaseClassPathForScanning(Iterable<URL> urls);

    void addEntitySetListener(CollectionChangeListener<Entity> listener);

    void removeEntitySetListener(CollectionChangeListener<Entity> listener);

    void terminate();
    
    long getTotalEffectorInvocations();

    <T> T invokeEffectorMethodSync(final Entity entity, final Effector<T> eff, final Object args) throws ExecutionException;
    
    <T> Task<T> invokeEffector(final Entity entity, final Effector<T> eff, @SuppressWarnings("rawtypes") final Map parameters);
}
