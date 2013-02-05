package brooklyn.entity.web.httpd;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import brooklyn.entity.Entity;
import brooklyn.entity.basic.SoftwareProcessImpl;

import com.beust.jcommander.internal.Maps;

/**
 * An implementation of {@link ApacheHttpd}.
 */
public class ApacheHttpdImpl extends SoftwareProcessImpl implements ApacheHttpd {
    private static final Logger log = LoggerFactory.getLogger(ApacheHttpdImpl.class);

    public ApacheHttpdImpl() {
        this(Maps.newHashMap(), null);
    }

    public ApacheHttpdImpl(Map flags) {
        this(flags,null);
    }

    public ApacheHttpdImpl(Entity parent) {
        this(Maps.newHashMap(), parent);
    }

    public ApacheHttpdImpl(Map flags, Entity parent) {
        super(flags, parent);
    }

    @Override
    public Class getDriverInterface() {
        return ApacheHttpdDriver.class;
    }

    public Integer getHttpPort() { return getAttribute(HTTP_PORT); }
}

