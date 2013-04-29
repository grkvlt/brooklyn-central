package brooklyn.entity.database.rubyrep;

import brooklyn.catalog.Catalog;
import brooklyn.config.ConfigKey;
import brooklyn.entity.basic.SoftwareProcess;
import brooklyn.entity.database.DatabaseNode;
import brooklyn.entity.proxying.ImplementedBy;
import brooklyn.event.basic.BasicAttributeSensorAndConfigKey;
import brooklyn.event.basic.BasicConfigKey;
import brooklyn.util.flags.SetFromFlag;

@Catalog(name = "RubyRep Node", description = "RubyRep is a database replication system", iconUrl = "classpath:///rubyrep-logo.jpeg")
@ImplementedBy(RubyRepNodeImpl.class)
public interface RubyRepNode extends SoftwareProcess {
    // TODO see RubyRepSshDriver#install()
    @SetFromFlag("version")
    ConfigKey<String> SUGGESTED_VERSION = new BasicConfigKey<String>(SoftwareProcess.SUGGESTED_VERSION, "1.2.0");

    @SetFromFlag("configurationScriptUrl")
    ConfigKey<String> CONFIGURATION_SCRIPT_URL = new BasicConfigKey<String>("database.rubyrep.configScriptUrl",
            "URL where RubyRep configuration can be found - disables other configuration options (except version)");

    @SetFromFlag("templateUrl")
    BasicAttributeSensorAndConfigKey<String> TEMPLATE_CONFIGURATION_URL = new BasicAttributeSensorAndConfigKey<String>(
            "database.rubyrep.templateConfigurationUrl", "Template file (in freemarker format) for the rubyrep.conf file",
            "classpath://brooklyn/entity/database/rubyrep/rubyrep.conf");

    @SetFromFlag("tables")
    ConfigKey<String> TABLE_REGEXP = new BasicConfigKey<String>(
            "database.rubyrep.tableRegex", "Regular expression to select tables to sync using RubyRep", ".");

    @SetFromFlag("replicationInterval")
    ConfigKey<Integer> REPLICATION_INTERVAL = new BasicConfigKey<Integer>(
            "database.rubyrep.replicationInterval", "Replication Interval", 30);

    @SetFromFlag("leftUrl")
    BasicAttributeSensorAndConfigKey<String> LEFT_DATABASE_URL = new BasicAttributeSensorAndConfigKey<String>(
            "database.rubyrep.leftDatabaseUrl", "URL of the left database");

    @SetFromFlag("leftDatabase")
    ConfigKey<? extends DatabaseNode> LEFT_DATABASE = new BasicConfigKey<DatabaseNode>(
            "database.rubyrep.leftDatabase", "Brooklyn database entity to use as the left DBMS");

    @SetFromFlag("leftDatabaseName")
    ConfigKey<String> LEFT_DATABASE_NAME = new BasicConfigKey<String>(
            "database.rubyrep.leftDatabaseName", "name of database to use for left db");

    @SetFromFlag("leftUsername")
    ConfigKey<String> LEFT_USERNAME = new BasicConfigKey<String>(
            "database.rubyrep.leftUsername", "username to connect to left db");

    @SetFromFlag("leftPassword")
    ConfigKey<String> LEFT_PASSWORD = new BasicConfigKey<String>(
            "database.rubyrep.leftPassword", "password to connect to left db");

    @SetFromFlag("rightUrl")
    BasicAttributeSensorAndConfigKey<String> RIGHT_DATABASE_URL = new BasicAttributeSensorAndConfigKey<String>(
            "database.rubyrep.rightDatabaseUrl", "Right database URL");

    @SetFromFlag("rightDatabase")
    ConfigKey<? extends DatabaseNode> RIGHT_DATABASE = new BasicConfigKey<DatabaseNode>(
            "database.rubyrep.rightDatabase");

    @SetFromFlag("rightDatabaseName")
    ConfigKey<String> RIGHT_DATABASE_NAME = new BasicConfigKey<String>(
            "database.rubyrep.rightDatabaseName", "name of database to use for left db");

    @SetFromFlag("rightUsername")
    ConfigKey<String> RIGHT_USERNAME = new BasicConfigKey<String>(
            "database.rubyrep.rightUsername", "username to connect to right db");

    @SetFromFlag("rightPassword")
    ConfigKey<String> RIGHT_PASSWORD = new BasicConfigKey<String>(
            "database.rubyrep.rightPassword", "password to connect to right db");

}
