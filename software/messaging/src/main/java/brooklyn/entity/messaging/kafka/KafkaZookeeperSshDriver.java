/*
 * Copyright 2013 by Cloudsoft Corp.
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
package brooklyn.entity.messaging.kafka;

import java.util.Map;

import brooklyn.config.ConfigKey;
import brooklyn.location.basic.SshMachineLocation;
import brooklyn.util.MutableMap;

public class KafkaZookeeperSshDriver extends AbstractfKafkaSshDriver implements KafkaZookeeperDriver {

    public KafkaZookeeperSshDriver(KafkaZookeeperImpl entity, SshMachineLocation machine) {
        super(entity, machine);
    }

    @Override
    protected Map<String, Integer> getPortMap() {
        return MutableMap.of("zookeeperPort", getZookeeperPort());
    }

    @Override
    protected ConfigKey<String> getConfigTemplateKey() {
        return KafkaZookeeper.KAFKA_ZOOKEEPER_CONFIG_TEMPLATE;
    }

    @Override
    protected String getConfigFileName() {
        return "zookeeper.properties";
    }

    @Override
    protected String getLaunchScriptName() {
        return "zookeeper-server-start.sh";
    }

    @Override
    protected String getProcessIdentifier() {
        return "quorum\\.QuorumPeerMain";
    }

    @Override
    public Integer getZookeeperPort() {
        return getEntity().getAttribute(KafkaZookeeper.ZOOKEEPER_PORT);
    }

}
