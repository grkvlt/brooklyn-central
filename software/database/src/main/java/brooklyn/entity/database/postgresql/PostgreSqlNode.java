package brooklyn.entity.database.postgresql;

import brooklyn.catalog.Catalog;
import brooklyn.config.ConfigKey;
import brooklyn.entity.basic.ConfigKeys;
import brooklyn.entity.database.DatabaseNode;
import brooklyn.entity.proxying.ImplementedBy;
import brooklyn.event.basic.PortAttributeSensorAndConfigKey;
import brooklyn.location.basic.PortRanges;
import brooklyn.util.flags.SetFromFlag;

@Catalog(name="PostgreSQL Node", description="PostgreSQL is an object-relational database management system (ORDBMS)", iconUrl="classpath:///postgresql-logo.jpeg")
@ImplementedBy(PostgreSqlNodeImpl.class)
public interface PostgreSqlNode extends DatabaseNode {

    @SetFromFlag("creationScriptUrl")
    ConfigKey<String> CREATION_SCRIPT_URL =
            ConfigKeys.newConfigKey("postgresql.creation.script.url", "URL where PostgreSQL creation script can be found", null);

    @SetFromFlag("creationScriptContents")
    ConfigKey<String> CREATION_SCRIPT_CONTENTS =
            ConfigKeys.newConfigKey("postgresql.creation.script", "PostgreSQL creation script contents", "");

    @SetFromFlag("port")
    PortAttributeSensorAndConfigKey POSTGRESQL_PORT =
            ConfigKeys.newPortAttributeSensorAndConfigKey("postgresql.port", "PostgreSQL port", PortRanges.fromString("5432+"));

}
