package brooklyn.entity.database.mysql;

import brooklyn.catalog.Catalog;
import brooklyn.config.ConfigKey;
import brooklyn.entity.basic.Attributes;
import brooklyn.entity.basic.ConfigKeys;
import brooklyn.entity.basic.SoftwareProcess;
import brooklyn.entity.database.DatabaseNode;
import brooklyn.entity.proxying.ImplementedBy;
import brooklyn.entity.trait.HasShortName;
import brooklyn.event.AttributeSensor;
import brooklyn.event.basic.BasicAttributeSensorAndConfigKey;
import brooklyn.event.basic.BasicConfigKey;
import brooklyn.event.basic.MapConfigKey;
import brooklyn.event.basic.PortAttributeSensorAndConfigKey;
import brooklyn.location.basic.PortRanges;
import brooklyn.util.flags.SetFromFlag;

@Catalog(name="MySql Node", description="MySql is an open source relational database management system (RDBMS)", iconUrl="classpath:///mysql-logo-110x57.png")
@ImplementedBy(MySqlNodeImpl.class)
public interface MySqlNode extends DatabaseNode, HasShortName {

    // NOTE MySQL changes the minor version number of their GA release frequently, check for latest version if install fails
    @SetFromFlag("version")
    ConfigKey<String> SUGGESTED_VERSION = new BasicConfigKey<String>(SoftwareProcess.SUGGESTED_VERSION, "5.5.30");

    //http://dev.mysql.com/get/Downloads/MySQL-5.5/mysql-5.5.21-osx10.6-x86_64.tar.gz/from/http://gd.tuwien.ac.at/db/mysql/
    //http://dev.mysql.com/get/Downloads/MySQL-5.5/mysql-5.5.21-linux2.6-i686.tar.gz/from/http://gd.tuwien.ac.at/db/mysql/
    @SetFromFlag("downloadUrl")
    BasicAttributeSensorAndConfigKey<String> DOWNLOAD_URL = new BasicAttributeSensorAndConfigKey<String>(
            Attributes.DOWNLOAD_URL, "http://dev.mysql.com/get/Downloads/MySQL-5.5/mysql-${version}-${driver.osTag}.tar.gz/from/${driver.mirrorUrl}");

    /** download mirror, if desired; defaults to Austria which seems one of the fastest */
    @SetFromFlag("mirrorUrl")
    ConfigKey<String> MIRROR_URL = new BasicConfigKey<String>("mysql.install.mirror.url", "URL of mirror",
//        "http://mysql.mirrors.pair.com/"   // Pennsylvania
//        "http://gd.tuwien.ac.at/db/mysql/"
        "http://www.mirrorservice.org/sites/ftp.mysql.com/" //UK mirror service
         );

    @SetFromFlag("port")
    PortAttributeSensorAndConfigKey MYSQL_PORT = new PortAttributeSensorAndConfigKey("mysql.port", "MySQL port", PortRanges.fromString("3306, 13306+"));

    @SetFromFlag("creationScriptContents")
    ConfigKey<String> CREATION_SCRIPT_CONTENTS = new BasicConfigKey<String>("mysql.creation.script.contents", "MySQL creation script (SQL contents)", "");

    @SetFromFlag("creationScriptUrl")
    ConfigKey<String> CREATION_SCRIPT_URL = new BasicConfigKey<String>("mysql.creation.script.url", "URL where MySQL creation script can be found", "");

    @SetFromFlag("dataDir")
    ConfigKey<String> DATA_DIR = new BasicConfigKey<String>("mysql.datadir", "Directory for writing data files", null);

    @SetFromFlag("serverConf")
    MapConfigKey<Object> MYSQL_SERVER_CONF = new MapConfigKey<Object>("mysql.server.conf", "Configuration options for mysqld");

    ConfigKey<Object> MYSQL_SERVER_CONF_LOWER_CASE_TABLE_NAMES = MYSQL_SERVER_CONF.subKey("lower_case_table_names", "See MySQL guide. Set 1 to ignore case in table names (useful for OS portability)");

    @SetFromFlag("password")
    BasicAttributeSensorAndConfigKey<String> PASSWORD = new BasicAttributeSensorAndConfigKey<String>(
            "mysql.password", "Database admin password (or randomly generated if not set)", null);

    @SetFromFlag("socketUid")
    BasicAttributeSensorAndConfigKey<String> SOCKET_UID = new BasicAttributeSensorAndConfigKey<String>(
            "mysql.socketUid", "Socket uid, for use in file /tmp/mysql.sock.<uid>.3306 (or randomly generated if not set)", null);

    AttributeSensor<String> MYSQL_URL = DB_URL;

    @SetFromFlag("configurationTemplateUrl")
    BasicAttributeSensorAndConfigKey<String> TEMPLATE_CONFIGURATION_URL = new BasicAttributeSensorAndConfigKey<String>(
            "mysql.template.configuration.url", "Template file (in freemarker format) for the mysql.conf file",
            "classpath://brooklyn/entity/database/mysql/mysql.conf");

}
