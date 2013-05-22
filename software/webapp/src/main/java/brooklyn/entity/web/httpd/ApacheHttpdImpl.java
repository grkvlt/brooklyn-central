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

import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.base.Optional;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;

import brooklyn.entity.basic.SoftwareProcessImpl;
import brooklyn.event.feed.ssh.SshFeed;
import brooklyn.event.feed.ssh.SshPollConfig;
import brooklyn.event.feed.ssh.SshValueFunctions;
import brooklyn.location.Location;
import brooklyn.location.basic.SshMachineLocation;

/**
 * An implementation of {@link ApacheHttpd}.
 */
public class ApacheHttpdImpl extends SoftwareProcessImpl implements ApacheHttpd {

    private static final long serialVersionUID = -5043673112366467445L;
    private static final Logger log = LoggerFactory.getLogger(ApacheHttpdImpl.class);

    private transient SshFeed sshFeed;

    @Override
    public Class<ApacheHttpdDriver> getDriverInterface() {
        return ApacheHttpdDriver.class;
    }

    public Integer getHttpPort() { return getAttribute(HTTP_PORT); }


    @Override
    protected void connectSensors() {
        super.connectSensors();
        connectServiceUpIsRunning();

        Optional<Location> location = Iterables.tryFind(getLocations(), Predicates.instanceOf(SshMachineLocation.class));
        if (location.isPresent()) {
            SshMachineLocation machine = (SshMachineLocation) location.get();
            sshFeed = SshFeed.builder()
                    .entity(this)
                    .machine((SshMachineLocation) machine)
                    .poll(new SshPollConfig<Integer>(REQUEST_COUNT)
                            .period(1000)
                            .command("curl --insecure -f -L \"http://127.0.0.1/server-status?auto\"")
                            .failOnNonZeroResultCode()
                            .onError(Functions.constant(-1))
                            .onSuccess(Functions.compose(
                                    new Function<String, Integer>() {
                                        @Override
                                        public Integer apply(@Nullable String stdout) {
                                            for (String line : stdout.split("\n")) {
                                                if (line.contains("Total Accesses")) {
                                                    String val = line.split(":")[1].trim();
                                                    return Integer.parseInt(val);
                                                }
                                            }
                                            LOG.info("Total Accesses not found in server-status, returning -1 (stdout="+stdout+")");
                                            return -1;
                                        }
                                    }, SshValueFunctions.stdout())))
                    .build();
        } else {
            LOG.warn("No ssh-able machine found: {}", getLocations());
        }
    }

    @Override
    protected void disconnectSensors() {
        super.disconnectSensors();
        disconnectServiceUpIsRunning();
        if (sshFeed != null && sshFeed.isActivated()) sshFeed.stop();
    }

}

