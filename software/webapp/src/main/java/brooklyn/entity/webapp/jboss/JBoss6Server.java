package brooklyn.entity.webapp.jboss;

import brooklyn.catalog.Catalog;
import brooklyn.config.ConfigKey;
import brooklyn.entity.basic.ConfigKeys;
import brooklyn.entity.basic.SoftwareProcess;
import brooklyn.entity.java.UsesJmx;
import brooklyn.entity.proxying.ImplementedBy;
import brooklyn.entity.webapp.JavaWebAppService;
import brooklyn.entity.webapp.JavaWebAppSoftwareProcess;
import brooklyn.event.basic.BasicAttributeSensorAndConfigKey;
import brooklyn.util.flags.SetFromFlag;

@Catalog(name="JBoss Application Server 6", description="AS6: an open source Java application server from JBoss", iconUrl="classpath:///jboss-logo.png")
@ImplementedBy(JBoss6ServerImpl.class)
public interface JBoss6Server extends JavaWebAppSoftwareProcess, JavaWebAppService, UsesJmx {

    @SetFromFlag("version")
    ConfigKey<String> SUGGESTED_VERSION = ConfigKeys.newConfigKey(SoftwareProcess.SUGGESTED_VERSION, "6.0.0.Final");

    @SetFromFlag("downloadUrl")
    BasicAttributeSensorAndConfigKey<String> DOWNLOAD_URL = ConfigKeys.newAttributeSensorAndConfigKey(
            SoftwareProcess.DOWNLOAD_URL, "http://downloads.sourceforge.net/project/jboss/JBoss/JBoss-${version}/jboss-as-distribution-${version}.zip?" +
            "r=http%3A%2F%2Fsourceforge.net%2Fprojects%2Fjboss%2Ffiles%2FJBoss%2F${version}%2F&ts=1307104229&use_mirror=kent");

    @SetFromFlag("bindAddress")
    BasicAttributeSensorAndConfigKey<String> BIND_ADDRESS = ConfigKeys.newAttributeSensorAndConfigKey(
            "jboss6.bind.address", 
            "Address of interface JBoss should listen on, defaulting 0.0.0.0 (but could set e.g. to attributeWhenReady(HOSTNAME)", 
            "0.0.0.0");

    @SetFromFlag("portIncrement")
    BasicAttributeSensorAndConfigKey<Integer> PORT_INCREMENT = ConfigKeys.newAttributeSensorAndConfigKey(
            "jboss6.portincrement", "Increment to be used for all jboss ports", 0);

    @SetFromFlag("clusterName")
    BasicAttributeSensorAndConfigKey<String> CLUSTER_NAME = ConfigKeys.newAttributeSensorAndConfigKey(
            "jboss6.clusterName", "Identifier used to group JBoss instances", "");

}
