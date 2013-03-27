package brooklyn.location.jclouds;

import java.io.File;
import java.util.Collection;

import org.jclouds.Constants;
import org.jclouds.compute.domain.TemplateBuilder;
import org.jclouds.domain.LoginCredentials;

import brooklyn.config.ConfigKey;
import brooklyn.entity.basic.ConfigKeys;
import brooklyn.location.basic.LocationConfigKeys;
import brooklyn.location.cloud.CloudLocationConfig;
import brooklyn.location.jclouds.JcloudsLocationCustomizer;
import brooklyn.util.internal.ssh.SshTool;

public interface JcloudsLocationConfig extends CloudLocationConfig {

    ConfigKey<String> CLOUD_PROVIDER = LocationConfigKeys.CLOUD_PROVIDER;

    ConfigKey<Boolean> RUN_AS_ROOT = ConfigKeys.newConfigKey("runAsRoot", 
            "Whether to run initial setup as root (default true)", null);
    ConfigKey<String> LOGIN_USER = ConfigKeys.newConfigKey("loginUser", 
            "Override the user who logs in initially to perform setup " +
            "(otherwise it is detected from the cloud or known defaults in cloud or VM OS)", null);
    ConfigKey<String> LOGIN_USER_PASSWORD = ConfigKeys.newConfigKey("loginUser.password", 
            "Custom password for the user who logs in initially", null);
    ConfigKey<String> LOGIN_USER_PRIVATE_KEY_DATA = ConfigKeys.newConfigKey("loginUser.privateKeyData", 
            "Custom private key for the user who logs in initially", null);

    ConfigKey<String> EXTRA_PUBLIC_KEY_DATA_TO_AUTH = ConfigKeys.newConfigKey("extraSshPublicKeyData", 
            "Additional public key data to add to authorized_keys", null);

    ConfigKey<Boolean> DONT_CREATE_USER = ConfigKeys.newConfigKey("dontCreateUser", 
            "Whether to skip creation of 'user' when provisioning machines (default false)", false);

    ConfigKey<LoginCredentials> CUSTOM_CREDENTIALS = ConfigKeys.newConfigKey(
            "customCredentials", "Custom jclouds LoginCredentials object to be used to connect to the VM", null);

    ConfigKey<String> GROUP_ID = ConfigKeys.newConfigKey("groupId");

    // jclouds compatibility
    ConfigKey<String> JCLOUDS_KEY_USERNAME = ConfigKeys.newConfigKey("userName", "Equivalent to 'user'; provided for jclouds compatibility", null);
    ConfigKey<String> JCLOUDS_KEY_ENDPOINT = ConfigKeys.newConfigKey(Constants.PROPERTY_ENDPOINT, "Equivalent to 'endpoint'; provided for jclouds compatibility", null);

    ConfigKey<String> WAIT_FOR_SSHABLE = ConfigKeys.newConfigKey("waitForSshable", 
            "Whether and how long to wait for a newly provisioned VM to be accessible via ssh; " +
            "if 'false', won't check; if 'true' uses default duration; otherwise accepts a time string e.g. '5m' (the default) or a number of milliseconds", "5m");

    ConfigKey<Integer> MIN_RAM = ConfigKeys.newConfigKey("minRam", 
            "Minimum amount of RAM (in MB), for use in selecting the machine/hardware profile", null);
    ConfigKey<Integer> MIN_CORES = ConfigKeys.newConfigKey("minCores", 
            "Minimum number of cores, for use in selecting the machine/hardware profile", null);
    ConfigKey<String> HARDWARE_ID = ConfigKeys.newConfigKey("hardwareId", 
            "A system-specific identifier for the hardware profile or machine type to be used when creating a VM", null);

    ConfigKey<String> IMAGE_ID = ConfigKeys.newConfigKey("imageId", 
            "A system-specific identifier for the VM image to be used when creating a VM", null);
    ConfigKey<String> IMAGE_NAME_REGEX = ConfigKeys.newConfigKey("imageNameRegex", 
            "A regular expression to be compared against the 'name' when selecting the VM image to be used when creating a VM", null);
    ConfigKey<String> IMAGE_DESCRIPTION_REGEX = ConfigKeys.newConfigKey("imageDescriptionRegex", 
            "A regular expression to be compared against the 'description' when selecting the VM image to be used when creating a VM", null);

    ConfigKey<String> TEMPLATE_SPEC = ConfigKeys.newConfigKey("templateSpec", 
            "A jclouds 'spec' string consisting of properties and values to be used when creating a VM " +
            "(in most cases the properties can, and should, be specified individually using other Brooklyn location config keys)", null);

    ConfigKey<String> DEFAULT_IMAGE_ID = ConfigKeys.newConfigKey("defaultImageId", 
            "A system-specific identifier for the VM image to be used by default when creating a VM " +
            "(if no other VM image selection criteria are supplied)", null);

    ConfigKey<TemplateBuilder> TEMPLATE_BUILDER = ConfigKeys.newConfigKey("templateBuilder", 
            "A TemplateBuilder instance provided programmatically, to be used when creating a VM", null);


    ConfigKey<Object> SECURITY_GROUPS = ConfigKeys.newConfigKey("securityGroups", 
            "Security groups to be applied when creating a VM, on supported clouds " +
            "(either a single group identifier as a String, or an Iterable<String> or String[])", null);

    ConfigKey<String> USER_DATA_UUENCODED = ConfigKeys.newConfigKey("userData", 
            "Arbitrary user data, as a uuencoded string, on supported clouds", null);

    ConfigKey<Object> INBOUND_PORTS = ConfigKeys.newConfigKey("inboundPorts", 
            "Inbound ports to be applied when creating a VM, on supported clouds " +
            "(either a single port as a String, or an Iterable<Integer> or Integer[])", null);

    ConfigKey<Object> USER_METADATA = ConfigKeys.newConfigKey("userMetadata", 
            "Arbitrary user metadata, as a map (or String of comma-separated key=value pairs), on supported clouds", null);

    ConfigKey<JcloudsLocationCustomizer> JCLOUDS_LOCATION_CUSTOMIZER = ConfigKeys.newConfigKey(
            "customizer", "Optional location customizer", null);

    ConfigKey<Collection<JcloudsLocationCustomizer>> JCLOUDS_LOCATION_CUSTOMIZERS = 
            ConfigKeys.newConfigKey("customizers", "Optional location customizers", null);

    ConfigKey<File> LOCAL_TEMP_DIR = SshTool.PROP_LOCAL_TEMP_DIR;

    // TODO
    // "noDefaultSshKeys" - hints that local ssh keys should not be read as defaults
    // this would be useful when we need to indicate a password

}
