/*
 * Copyright 2012 by Andrew Kennedy
 */
package brooklyn.entity.monitoring.ganglia;

import java.util.Map;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import brooklyn.entity.Entity;
import brooklyn.entity.basic.SoftwareProcessImpl;
import brooklyn.event.feed.function.FunctionFeed;
import brooklyn.event.feed.function.FunctionPollConfig;

import com.google.common.base.Functions;
import com.google.common.collect.Maps;

/**
 * An implementation of {@link GangliaManager}.
 */
public class GangliaManagerImpl extends SoftwareProcessImpl implements GangliaManager {

    /** serialVersionUID */
    private static final long serialVersionUID = 4682848188913389323L;

    private static final Logger log = LoggerFactory.getLogger(GangliaManagerImpl.class);

    public GangliaManagerImpl() {
        this(Maps.newHashMap(), null);
    }

    public GangliaManagerImpl(Map<?, ?> flags) {
        this(flags, null);
    }

    public GangliaManagerImpl(Entity owner) {
        this(Maps.newHashMap(), owner);
    }

    public GangliaManagerImpl(Map<?, ?> flags, Entity owner) {
        super(flags, owner);
    }

    public Integer getGangliaPort() {
        return getAttribute(GANGLIA_PORT);
    }

    public String getClusterName() {
        return getAttribute(CLUSTER_NAME);
    }

    @Override
    public Class getDriverInterface() {
        return GangliaManagerDriver.class;
    }

    transient FunctionFeed serviceUp;

    @Override
    protected void connectSensors() {
        super.connectSensors();

        serviceUp = FunctionFeed.builder()
                .entity(this)
                .poll(new FunctionPollConfig<Object, Boolean>(SERVICE_UP)
                        .period(500)
                        .callable(new Callable<Boolean>() {
                            public Boolean call() {
                                return getDriver().isRunning();
                            }
                        })
                        .onError(Functions.constant(false)))
                .build();
    }
}
