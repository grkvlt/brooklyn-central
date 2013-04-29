package brooklyn.entity.osgi.karaf;

import java.net.URISyntaxException;
import java.util.Map;

import brooklyn.catalog.Catalog;
import brooklyn.config.ConfigKey;
import brooklyn.entity.Effector;
import brooklyn.entity.basic.Attributes;
import brooklyn.entity.basic.Description;
import brooklyn.entity.basic.MethodEffector;
import brooklyn.entity.basic.NamedParameter;
import brooklyn.entity.basic.SoftwareProcess;
import brooklyn.entity.java.UsesJava;
import brooklyn.entity.java.UsesJmx;
import brooklyn.entity.proxying.ImplementedBy;
import brooklyn.event.AttributeSensor;
import brooklyn.event.basic.BasicAttributeSensor;
import brooklyn.event.basic.BasicAttributeSensorAndConfigKey;
import brooklyn.event.basic.BasicConfigKey;
import brooklyn.event.basic.MapConfigKey;
import brooklyn.event.basic.PortAttributeSensorAndConfigKey;
import brooklyn.util.flags.SetFromFlag;

/**
 * This sets up a Karaf OSGi container
 */
@Catalog(name="Karaf", description="Apache Karaf is a small OSGi based runtime which provides a lightweight container onto which various components and applications can be deployed.", iconUrl="classpath:///karaf-logo.png")
@ImplementedBy(KarafContainerImpl.class)
public interface KarafContainer extends SoftwareProcess, UsesJava, UsesJmx {

    // TODO Better way of setting/overriding defaults for config keys that are defined in super class SoftwareProcess

    String WRAP_SCHEME = "wrap";
    String FILE_SCHEME = "file";
    String MVN_SCHEME = "mvn";
    String HTTP_SCHEME = "http";

    Effector<Map<Long,Map<String,?>>> LIST_BUNDLES = new MethodEffector(KarafContainer.class, "listBundles");
    Effector<Long> INSTALL_BUNDLE = new MethodEffector<Long>(KarafContainer.class, "installBundle");
    Effector<Void> UNINSTALL_BUNDLE = new MethodEffector<Void>(KarafContainer.class, "uninstallBundle");
    Effector<Void> INSTALL_FEATURE = new MethodEffector<Void>(KarafContainer.class, "installFeature");
    Effector<Void> UPDATE_SERVICE_PROPERTIES = new MethodEffector<Void>(KarafContainer.class, "updateServiceProperties");

    @SetFromFlag("version")
    ConfigKey<String> SUGGESTED_VERSION = new BasicConfigKey<String>(
            SoftwareProcess.SUGGESTED_VERSION, "2.3.0");

    @SetFromFlag("downloadUrl")
    BasicAttributeSensorAndConfigKey<String> DOWNLOAD_URL = new BasicAttributeSensorAndConfigKey<String>(
            SoftwareProcess.DOWNLOAD_URL, "http://apache.mirror.anlx.net/karaf/${version}/apache-karaf-${version}.tar.gz");

    @SetFromFlag("karafName")
    BasicAttributeSensorAndConfigKey<String> KARAF_NAME = new BasicAttributeSensorAndConfigKey<String>(
            "karaf.name", "Karaf instance name", "root");

    // TODO too complicated? Used by KarafContainer; was in JavaApp; where should it be in brave new world?
    MapConfigKey<Map<String,String>> NAMED_PROPERTY_FILES = new MapConfigKey<Map<String, String>>(
            "karaf.runtime.files", "Property files to be generated, referenced by name relative to runDir");

    @SetFromFlag("jmxUser")
    BasicAttributeSensorAndConfigKey<String> JMX_USER = new BasicAttributeSensorAndConfigKey<String>(
            Attributes.JMX_USER, "karaf");

    @SetFromFlag("jmxPassword")
    BasicAttributeSensorAndConfigKey<String> JMX_PASSWORD = new BasicAttributeSensorAndConfigKey<String>(
            Attributes.JMX_PASSWORD, "karaf");

    @SetFromFlag("jmxPort")
    PortAttributeSensorAndConfigKey JMX_PORT = new PortAttributeSensorAndConfigKey(
            UsesJmx.JMX_PORT, "1099+");

    @SetFromFlag("rmiServerPort")
    PortAttributeSensorAndConfigKey RMI_SERVER_PORT = new PortAttributeSensorAndConfigKey(
            UsesJmx.RMI_SERVER_PORT, "44444+");
    @Deprecated // since 0.4 use RMI_SERVER_PORT
    PortAttributeSensorAndConfigKey RMI_PORT = RMI_SERVER_PORT;

    @SetFromFlag("jmxContext")
    BasicAttributeSensorAndConfigKey<String> JMX_CONTEXT = new BasicAttributeSensorAndConfigKey<String>(
            UsesJmx.JMX_CONTEXT, "karaf-"+KARAF_NAME.getConfigKey().getDefaultValue());

    AttributeSensor<Map<?, ?>> KARAF_INSTANCES = new BasicAttributeSensor<Map<?, ?>>("karaf.admin.instances", "Karaf admin instances");
    AttributeSensor<Boolean> KARAF_ROOT = new BasicAttributeSensor<Boolean>("karaf.admin.isRoot", "Karaf admin isRoot");
    AttributeSensor<String> KARAF_JAVA_OPTS = new BasicAttributeSensor<String>("karaf.admin.java_opts", "Karaf Java opts");
    AttributeSensor<String> KARAF_INSTALL_LOCATION  = new BasicAttributeSensor<String>("karaf.admin.location", "Karaf install location");
    AttributeSensor<Integer> KARAF_PID = new BasicAttributeSensor<Integer>("karaf.admin.pid", "Karaf instance PID");
    AttributeSensor<Integer> KARAF_SSH_PORT = new BasicAttributeSensor<Integer>("karaf.admin.ssh_port", "Karaf SSH Port");
    AttributeSensor<Integer> KARAF_RMI_REGISTRY_PORT = new BasicAttributeSensor<Integer>("karaf.admin.rmi_registry_port", "Karaf instance RMI registry port");
    AttributeSensor<Integer> KARAF_RMI_SERVER_PORT = new BasicAttributeSensor<Integer>("karaf.admin.rmi_server_port", "Karaf RMI (JMX) server port");
    AttributeSensor<String> KARAF_STATE = new BasicAttributeSensor<String>("karaf.admin.state", "Karaf instance state");

    @Description("Updates the OSGi Service's properties, adding (and overriding) the given key-value pairs")
    void updateServiceProperties(
            @NamedParameter("serviceName") @Description("Name of the OSGi service") String serviceName,
            Map<String,String> additionalVals);

    @Description("Installs the given OSGi feature")
    void installFeature(
            @NamedParameter("featureName") @Description("Name of the feature - see org.apache.karaf:type=features#installFeature()") final String featureName)
            throws Exception;

    @Description("Lists all the karaf bundles")
    Map<Long,Map<String, ?>> listBundles();

    /**
     * throws URISyntaxException If bundle name is not a valid URI
     */
    @Description("Deploys the given bundle, returning the bundle id - see osgi.core:type=framework#installBundle()")
    long installBundle(
            @NamedParameter("bundle") @Description("URI of bundle to be deployed") String bundle) throws URISyntaxException;

    @Description("Undeploys the bundle with the given id")
    void uninstallBundle(
            @NamedParameter("bundleId") @Description("Id of the bundle") Long bundleId);
}
