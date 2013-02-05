package brooklyn.entity.web.httpd;

import brooklyn.entity.basic.SoftwareProcessDriver;

public interface ApacheHttpdDriver extends SoftwareProcessDriver {

    Integer getHttpPort();

}
