/*
 * Copyright 2012-2013 by Cloudsoft Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package brooklyn.entity.web.httpd;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import brooklyn.entity.Entity;
import brooklyn.entity.basic.SoftwareProcessImpl;

import com.google.common.collect.Maps;

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

