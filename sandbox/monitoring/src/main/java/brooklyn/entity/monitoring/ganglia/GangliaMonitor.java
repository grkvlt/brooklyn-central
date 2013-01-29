/*
 * Copyright 2012 by Andrew Kennedy
 */
package brooklyn.entity.monitoring.ganglia;

import java.util.Map;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import brooklyn.config.ConfigKey;
import brooklyn.entity.Entity;
import brooklyn.entity.basic.SoftwareProcessEntity;
import brooklyn.event.basic.BasicAttributeSensor;
import brooklyn.event.basic.BasicConfigKey;
import brooklyn.event.basic.PortAttributeSensorAndConfigKey;
import brooklyn.event.feed.function.FunctionFeed;
import brooklyn.event.feed.function.FunctionPollConfig;
import brooklyn.location.basic.PortRanges;
import brooklyn.util.flags.SetFromFlag;

import com.google.common.base.Functions;
import com.google.common.collect.Maps;


/**
 * An {@link brooklyn.entity.Entity} that represents a Ganglia monitoring daemon, {@code gmond}.
 */
public class GangliaMonitor extends SoftwareProcessEntity {
    /** serialVersionUID */
    private static final long serialVersionUID = -9113861914926145090L;

    private static final Logger log = LoggerFactory.getLogger(GangliaMonitor.class);

    @SetFromFlag("version")
    public static final ConfigKey<String> SUGGESTED_VERSION = new BasicConfigKey<String>(SoftwareProcessEntity.SUGGESTED_VERSION, "1.1.6");
    
    @SetFromFlag("gangliaPort")
    public static final PortAttributeSensorAndConfigKey GANGLIA_PORT = new PortAttributeSensorAndConfigKey("ganglia.port", "Ganglia communications port", PortRanges.fromString("7000+"));
    
    @SetFromFlag("clusterName")
    public static final BasicAttributeSensor<String> CLUSTER_NAME = GangliaCluster.CLUSTER_NAME;

    public GangliaMonitor(Map<?, ?> flags){
        this(flags, null);
    }

    public GangliaMonitor(Entity owner){
        this(Maps.newHashMap(), owner);
    }

    public GangliaMonitor(Map<?, ?> flags, Entity owner) {
        super(flags, owner);
        setAttribute(CLUSTER_NAME, getConfig(GangliaCluster.CLUSTER_NAME));
    }
    
    public Integer getGangliaPort() { return getAttribute(GANGLIA_PORT); }
    public String getClusterName() { return getAttribute(CLUSTER_NAME); }

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
                        .callable(new Callable<Boolean>(){
                            public Boolean call() {
                                return getDriver().isRunning();
                             }
                         })
                        .onError(Functions.constant(false)))
                .build();
    }
}
