/*
 * Copyright 2012-2013 by Cloudsoft Corp.
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
 * An implementation of {@link GangliaMonitor}.
 */
public class GangliaMonitorImpl extends SoftwareProcessImpl implements GangliaMonitor {

    /** serialVersionUID */
    private static final long serialVersionUID = -9113861914926145090L;

    private static final Logger log = LoggerFactory.getLogger(GangliaMonitorImpl.class);

    public GangliaMonitorImpl() {
        this(Maps.newHashMap(), null);
    }

    public GangliaMonitorImpl(Map<?, ?> flags) {
        this(flags, null);
    }

    public GangliaMonitorImpl(Entity owner) {
        this(Maps.newHashMap(), owner);
    }

    public GangliaMonitorImpl(Map<?, ?> flags, Entity owner) {
        super(flags, owner);
        setAttribute(CLUSTER_NAME, getConfig(GangliaCluster.CLUSTER_NAME));
    }

    public Integer getGangliaPort() {
        return getAttribute(GANGLIA_PORT);
    }

    public String getClusterName() {
        return getAttribute(CLUSTER_NAME);
    }

    @Override
    public Class getDriverInterface() {
        return GangliaMonitorDriver.class;
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
