package brooklyn.entity.basic;

import java.util.Map;

import brooklyn.config.ConfigKey;
import brooklyn.entity.Entity;
import brooklyn.entity.trait.Startable;
import brooklyn.event.AttributeSensor;
import brooklyn.event.basic.BasicAttributeSensorAndConfigKey;
import brooklyn.event.basic.MapConfigKey;
import brooklyn.location.MachineProvisioningLocation;
import brooklyn.util.MutableMap;
import brooklyn.util.flags.SetFromFlag;

public interface SoftwareProcess extends Entity, Startable {

    @SetFromFlag("startTimeout")
    ConfigKey<Integer> START_TIMEOUT = ConfigKeys.START_TIMEOUT;

    @SetFromFlag("startLatch")
    ConfigKey<Boolean> START_LATCH = ConfigKeys.START_LATCH;

    @SetFromFlag("installLatch")
    ConfigKey<Boolean> INSTALL_LATCH = ConfigKeys.INSTALL_LATCH;

    @SetFromFlag("customizeLatch")
    ConfigKey<Boolean> CUSTOMIZE_LATCH = ConfigKeys.CUSTOMIZE_LATCH;

    @SetFromFlag("launchLatch")
    ConfigKey<Boolean> LAUNCH_LATCH = ConfigKeys.LAUNCH_LATCH;

    @SetFromFlag("version")
    ConfigKey<String> SUGGESTED_VERSION = ConfigKeys.SUGGESTED_VERSION;

    @SetFromFlag("downloadUrl")
    BasicAttributeSensorAndConfigKey<String> DOWNLOAD_URL = Attributes.DOWNLOAD_URL;

    @SetFromFlag("downloadAddonUrls")
    BasicAttributeSensorAndConfigKey<Map<String,String>> DOWNLOAD_ADDON_URLS = Attributes.DOWNLOAD_ADDON_URLS;

    @SetFromFlag("installDir")
    ConfigKey<String> SUGGESTED_INSTALL_DIR = ConfigKeys.SUGGESTED_INSTALL_DIR;

    @SetFromFlag("runDir")
    ConfigKey<String> SUGGESTED_RUN_DIR = ConfigKeys.SUGGESTED_RUN_DIR;

    @SetFromFlag("env")
    MapConfigKey<?> SHELL_ENVIRONMENT = ConfigKeys.newMapConfigKey(
            "shell.env", "Map of environment variables to pass to the runtime shell", MutableMap.<String, Object>of());

    @SetFromFlag("provisioningProperties")
    MapConfigKey<?> PROVISIONING_PROPERTIES = ConfigKeys.newMapConfigKey(
            "provisioning.properties",
            "Custom properties to be passed in when provisioning a new machine", MutableMap.<String, Object>of());

    AttributeSensor<String> HOSTNAME = Attributes.HOSTNAME;
    AttributeSensor<String> ADDRESS = Attributes.ADDRESS;

    AttributeSensor<MachineProvisioningLocation> PROVISIONING_LOCATION = Attributes.newAttributeSensor(
            "softwareservice.provisioningLocation", "Location used to provision a machine where this is running");

    AttributeSensor<Lifecycle> SERVICE_STATE = Attributes.SERVICE_STATE;

}
