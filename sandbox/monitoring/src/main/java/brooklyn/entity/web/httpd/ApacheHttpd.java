package brooklyn.entity.web.httpd;

import brooklyn.entity.basic.Attributes;
import brooklyn.entity.basic.SoftwareProcess;
import brooklyn.entity.proxying.ImplementedBy;
import brooklyn.event.basic.BasicConfigKey;
import brooklyn.event.basic.PortAttributeSensorAndConfigKey;
import brooklyn.util.flags.SetFromFlag;

/**
 * An {@link brooklyn.entity.Entity} that represents a single Apache {@code httpd} instance.
 */
@ImplementedBy(ApacheHttpdImpl.class)
public interface ApacheHttpd extends SoftwareProcess {

    @SetFromFlag("version")
    BasicConfigKey<String> SUGGESTED_VERSION =
            new BasicConfigKey<String>(SoftwareProcess.SUGGESTED_VERSION, "7.0.34");

    PortAttributeSensorAndConfigKey HTTP_PORT = Attributes.HTTP_PORT;

    Integer getHttpPort();

}
