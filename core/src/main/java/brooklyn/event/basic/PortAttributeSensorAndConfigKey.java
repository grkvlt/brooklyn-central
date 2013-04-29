/*
 * Copyright 2011-2013 by Cloudsoft Corp.
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
package brooklyn.event.basic;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import brooklyn.config.ConfigKey;
import brooklyn.entity.Entity;
import brooklyn.event.Sensor;
import brooklyn.location.Location;
import brooklyn.location.PortRange;
import brooklyn.location.PortSupplier;

/**
 * A {@link Sensor} describing a port on a system,
 * with a {@link ConfigKey} which can be configured with a port range
 * (either a number e.g. 80, or a string e.g. "80" or "8080-8089" or even "80, 8080-8089, 8800+", or a list of these).
 * <p>
 * To convert at runtime a single port is chosen, respecting the entity.
 */
public class PortAttributeSensorAndConfigKey extends AttributeSensorAndConfigKey<PortRange,Integer> {
    private static final long serialVersionUID = 4680651022807491321L;

    public static final Logger LOG = LoggerFactory.getLogger(PortAttributeSensorAndConfigKey.class);

    public PortAttributeSensorAndConfigKey(String name) {
        this(name, name, null);
    }

    public PortAttributeSensorAndConfigKey(String name, String description) {
        this(name, description, null);
    }

    public PortAttributeSensorAndConfigKey(String name, String description, Object defaultValue) {
        super(Integer.class, name, description, defaultValue);
    }

    public PortAttributeSensorAndConfigKey(PortAttributeSensorAndConfigKey orig, Object defaultValue) {
        super(orig, defaultValue);
    }

    protected Integer convertConfigToSensor(PortRange value, Entity entity) {
        if (value==null) return null;
        Collection<Location> locations = entity.getLocations();
        if (!locations.isEmpty()) {
            if (locations.size()==1) {
                Location l = locations.iterator().next();
                if (l instanceof PortSupplier) {
                    int p = ((PortSupplier)l).obtainPort(value);
                    if (p!=-1) {
                        LOG.debug(""+entity+" choosing port "+p+" for "+getName());
                        return p;
                    }
                    LOG.warn(""+entity+" no port available for "+getName()+" in range "+value);
                    // definitively, no ports available
                    return null;
                }
                // ports may be available, we just can't tell from the location
                Integer v = (value.isEmpty() ? null : value.iterator().next());
                LOG.debug(""+entity+" choosing port "+v+" (unconfirmed) for "+getName());
                return v;
            } else {
                LOG.warn(""+entity+" ports not applicable, or not yet applicable, because has multiple locations "+locations+"; ignoring "+getName());       
            }
        } else {
            LOG.warn(""+entity+" ports not applicable, or not yet applicable, bacause has no locations, ignoring "+getName());
        }
        return null;
    }
}
