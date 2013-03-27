package brooklyn.entity.database.rubyrep;

import brooklyn.catalog.Catalog;
import brooklyn.config.ConfigKey;
import brooklyn.entity.basic.ConfigKeys;
import brooklyn.entity.basic.SoftwareProcess;
import brooklyn.entity.database.DatabaseNode;
import brooklyn.entity.proxying.ImplementedBy;
import brooklyn.event.basic.BasicAttributeSensorAndConfigKey;
import brooklyn.util.flags.SetFromFlag;

@Catalog(name = "RubyRep Node", description = "RubyRep is a database replication system", iconUrl = "classpath:///rubyrep-logo.jpeg")
@ImplementedBy(RubyRepNodeImpl.class)
public interface RubyRepNode extends SoftwareProcess {
    // TODO see RubyRepSshDriver#install()
    @SetFromFlag("version")
    ConfigKey<String> SUGGESTED_VERSION = ConfigKeys.newConfigKey(SoftwareProcess.SUGGESTED_VERSION, "1.2.0");

    @SetFromFlag("configurationScriptUrl")
    ConfigKey<String> CONFIGURATION_SCRIPT_URL = ConfigKeys.newConfigKey("database.rubyrep.configScriptUrl",
            "URL where RubyRep configuration can be found - disables other configuration options (except version)");

    @SetFromFlag("templateUrl")
    BasicAttributeSensorAndConfigKey<String> TEMPLATE_CONFIGURATION_URL = ConfigKeys.newAttributeSensorAndConfigKey(
            "database.rubyrep.templateConfigurationUrl", "Template file (in freemarker format) for the rubyrep.conf file",
            "classpath://brooklyn/entity/database/rubyrep/rubyrep.conf");

    @SetFromFlag("tables")
    ConfigKey<String> TABLE_REGEXP = ConfigKeys.newConfigKey(
            "database.rubyrep.tableRegex", "Regular expression to select tables to sync using RubyRep", ".");

    @SetFromFlag("replicationInterval")
    ConfigKey<Integer> REPLICATION_INTERVAL = ConfigKeys.newConfigKey(
            "database.rubyrep.replicationInterval", "Replication Interval", 30);

    @SetFromFlag("leftUrl")
    BasicAttributeSensorAndConfigKey<String> LEFT_DATABASE_URL = ConfigKeys.newAttributeSensorAndConfigKey(
            "database.rubyrep.leftDatabaseUrl", "URL of the left database");

    @SetFromFlag("leftDatabase")
    ConfigKey<? extends DatabaseNode> LEFT_DATABASE = ConfigKeys.newConfigKey(
            "database.rubyrep.leftDatabase", "Brooklyn database entity to use as the left DBMS");

    @SetFromFlag("leftDatabaseName")
    ConfigKey<String> LEFT_DATABASE_NAME = ConfigKeys.newConfigKey(
            "database.rubyrep.leftDatabaseName", "name of database to use for left db");

    @SetFromFlag("leftUsername")
    ConfigKey<String> LEFT_USERNAME = ConfigKeys.newConfigKey(
            "database.rubyrep.leftUsername", "username to connect to left db");

    @SetFromFlag("leftPassword")
    ConfigKey<String> LEFT_PASSWORD = ConfigKeys.newConfigKey(
            "database.rubyrep.leftPassword", "password to connect to left db");

    @SetFromFlag("rightUrl")
    BasicAttributeSensorAndConfigKey<String> RIGHT_DATABASE_URL = ConfigKeys.newAttributeSensorAndConfigKey(
            "database.rubyrep.rightDatabaseUrl", "Right database URL");

    @SetFromFlag("rightDatabase")
    ConfigKey<? extends DatabaseNode> RIGHT_DATABASE = ConfigKeys.newConfigKey(
            "database.rubyrep.rightDatabase");

    @SetFromFlag("rightDatabaseName")
    ConfigKey<String> RIGHT_DATABASE_NAME = ConfigKeys.newConfigKey(
            "database.rubyrep.rightDatabaseName", "name of database to use for left db");

    @SetFromFlag("rightUsername")
    ConfigKey<String> RIGHT_USERNAME = ConfigKeys.newConfigKey(
            "database.rubyrep.rightUsername", "username to connect to right db");

    @SetFromFlag("rightPassword")
    ConfigKey<String> RIGHT_PASSWORD = ConfigKeys.newConfigKey(
            "database.rubyrep.rightPassword", "password to connect to right db");

}
