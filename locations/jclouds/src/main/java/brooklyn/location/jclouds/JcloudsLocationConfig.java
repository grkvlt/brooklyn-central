package brooklyn.location.jclouds;

import java.io.File;
import java.util.Collection;

import org.jclouds.Constants;
import org.jclouds.compute.domain.TemplateBuilder;
import org.jclouds.domain.LoginCredentials;

import brooklyn.config.ConfigKey;
import brooklyn.event.basic.BasicConfigKey;
import brooklyn.location.basic.LocationConfigKeys;
import brooklyn.location.cloud.CloudLocationConfig;
import brooklyn.util.internal.ssh.SshTool;

public interface JcloudsLocationConfig extends CloudLocationConfig {

    ConfigKey<String> CLOUD_PROVIDER = LocationConfigKeys.CLOUD_PROVIDER;

    ConfigKey<Boolean> RUN_AS_ROOT = new BasicConfigKey<Boolean>("runAsRoot", 
            "Whether to run initial setup as root (default true)", null);
    ConfigKey<String> LOGIN_USER = new BasicConfigKey<String>("loginUser", 
            "Override the user who logs in initially to perform setup " +
            "(otherwise it is detected from the cloud or known defaults in cloud or VM OS)", null);
    ConfigKey<String> LOGIN_USER_PASSWORD = new BasicConfigKey<String>("loginUser.password", 
            "Custom password for the user who logs in initially", null);
    ConfigKey<String> LOGIN_USER_PRIVATE_KEY_DATA = new BasicConfigKey<String>("loginUser.privateKeyData", 
            "Custom private key for the user who logs in initially", null);

    ConfigKey<String> EXTRA_PUBLIC_KEY_DATA_TO_AUTH = new BasicConfigKey<String>("extraSshPublicKeyData", 
            "Additional public key data to add to authorized_keys", null);

    ConfigKey<Boolean> DONT_CREATE_USER = new BasicConfigKey<Boolean>("dontCreateUser", 
            "Whether to skip creation of 'user' when provisioning machines (default false)", false);

    ConfigKey<LoginCredentials> CUSTOM_CREDENTIALS = new BasicConfigKey<LoginCredentials>(
            "customCredentials", "Custom jclouds LoginCredentials object to be used to connect to the VM", null);

    ConfigKey<String> GROUP_ID = new BasicConfigKey<String>("groupId");

    // jclouds compatibility
    ConfigKey<String> JCLOUDS_KEY_USERNAME = new BasicConfigKey<String>("userName", "Equivalent to 'user'; provided for jclouds compatibility", null);
    ConfigKey<String> JCLOUDS_KEY_ENDPOINT = new BasicConfigKey<String>(Constants.PROPERTY_ENDPOINT, "Equivalent to 'endpoint'; provided for jclouds compatibility", null);

    ConfigKey<String> WAIT_FOR_SSHABLE = new BasicConfigKey<String>("waitForSshable", 
            "Whether and how long to wait for a newly provisioned VM to be accessible via ssh; " +
            "if 'false', won't check; if 'true' uses default duration; otherwise accepts a time string e.g. '5m' (the default) or a number of milliseconds", "5m");

    ConfigKey<Integer> MIN_RAM = new BasicConfigKey<Integer>("minRam", 
            "Minimum amount of RAM (in MB), for use in selecting the machine/hardware profile", null);
    ConfigKey<Integer> MIN_CORES = new BasicConfigKey<Integer>("minCores", 
            "Minimum number of cores, for use in selecting the machine/hardware profile", null);
    ConfigKey<String> HARDWARE_ID = new BasicConfigKey<String>("hardwareId", 
            "A system-specific identifier for the hardware profile or machine type to be used when creating a VM", null);

    ConfigKey<String> IMAGE_ID = new BasicConfigKey<String>("imageId", 
            "A system-specific identifier for the VM image to be used when creating a VM", null);
    ConfigKey<String> IMAGE_NAME_REGEX = new BasicConfigKey<String>("imageNameRegex", 
            "A regular expression to be compared against the 'name' when selecting the VM image to be used when creating a VM", null);
    ConfigKey<String> IMAGE_DESCRIPTION_REGEX = new BasicConfigKey<String>("imageDescriptionRegex", 
            "A regular expression to be compared against the 'description' when selecting the VM image to be used when creating a VM", null);

    ConfigKey<String> TEMPLATE_SPEC = new BasicConfigKey<String>("templateSpec", 
            "A jclouds 'spec' string consisting of properties and values to be used when creating a VM " +
            "(in most cases the properties can, and should, be specified individually using other Brooklyn location config keys)", null);

    ConfigKey<String> DEFAULT_IMAGE_ID = new BasicConfigKey<String>("defaultImageId", 
            "A system-specific identifier for the VM image to be used by default when creating a VM " +
            "(if no other VM image selection criteria are supplied)", null);

    ConfigKey<TemplateBuilder> TEMPLATE_BUILDER = new BasicConfigKey<TemplateBuilder>("templateBuilder", 
            "A TemplateBuilder instance provided programmatically, to be used when creating a VM", null);


    ConfigKey<Object> SECURITY_GROUPS = new BasicConfigKey<Object>("securityGroups", 
            "Security groups to be applied when creating a VM, on supported clouds " +
            "(either a single group identifier as a String, or an Iterable<String> or String[])", null);

    ConfigKey<String> USER_DATA_UUENCODED = new BasicConfigKey<String>("userData", 
            "Arbitrary user data, as a uuencoded string, on supported clouds", null);

    ConfigKey<Object> INBOUND_PORTS = new BasicConfigKey<Object>("inboundPorts", 
            "Inbound ports to be applied when creating a VM, on supported clouds " +
            "(either a single port as a String, or an Iterable<Integer> or Integer[])", null);

    ConfigKey<Object> USER_METADATA = new BasicConfigKey<Object>("userMetadata", 
            "Arbitrary user metadata, as a map (or String of comma-separated key=value pairs), on supported clouds", null);

    ConfigKey<JcloudsLocationCustomizer> JCLOUDS_LOCATION_CUSTOMIZER = new BasicConfigKey<JcloudsLocationCustomizer>(
            "customizer", "Optional location customizer", null);

    ConfigKey<Collection<JcloudsLocationCustomizer>> JCLOUDS_LOCATION_CUSTOMIZERS = new BasicConfigKey<Collection<JcloudsLocationCustomizer>>(
            "customizers", "Optional location customizers", null);

    ConfigKey<File> LOCAL_TEMP_DIR = SshTool.PROP_LOCAL_TEMP_DIR;

    // TODO
    // "noDefaultSshKeys" - hints that local ssh keys should not be read as defaults
    // this would be useful when we need to indicate a password

}
