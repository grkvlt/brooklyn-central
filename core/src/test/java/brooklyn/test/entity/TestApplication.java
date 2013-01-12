package brooklyn.test.entity;

import brooklyn.entity.Application;
import brooklyn.entity.Entity;
import brooklyn.entity.basic.EntityLocal;
import brooklyn.entity.proxying.EntitySpec;
import brooklyn.entity.proxying.ImplementedBy;
import brooklyn.entity.trait.Startable;

/**
 * Mock application for testing.
 */
//TODO Don't want to extend EntityLocal, but tests want to call app.setAttribute
@ImplementedBy(TestApplicationImpl.class)
public interface TestApplication extends Application, Startable, EntityLocal {

    public <T extends Entity> T createChild(EntitySpec<T> spec);

    public <T extends Entity> T createAndManageChild(EntitySpec<T> spec);

    /**
     * convenience for wiring in management during testing
     * 
     * @deprecated Use Entities.startManagement(app)
     */
    @Deprecated
    public void startManagement();
    
    /**
     * convenience for wiring in management during testing
     * 
     * @deprecated Use Entities.manage(entity)
     */
    @Deprecated
    public <T extends Entity> T manage(T entity);
}
