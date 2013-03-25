/*
 * Copyright 2012-2013 by Cloudsoft Corp.
 */
package brooklyn.entity.nosql.cassandra;

import brooklyn.config.ConfigKey;
import brooklyn.entity.basic.Attributes;
import brooklyn.entity.basic.ConfigKeys;
import brooklyn.entity.basic.SoftwareProcess;
import brooklyn.entity.java.UsesJmx;
import brooklyn.entity.proxying.ImplementedBy;
import brooklyn.event.AttributeSensor;
import brooklyn.event.basic.BasicAttributeSensorAndConfigKey;
import brooklyn.event.basic.PortAttributeSensorAndConfigKey;
import brooklyn.location.basic.PortRanges;
import brooklyn.util.flags.SetFromFlag;

/**
 * An {@link brooklyn.entity.Entity} that represents a Cassandra node in a {@link CassandraCluster}.
 */
@ImplementedBy(CassandraNodeImpl.class)
public interface CassandraNode extends SoftwareProcess, UsesJmx {

    @SetFromFlag("version")
    ConfigKey<String> SUGGESTED_VERSION = ConfigKeys.newConfigKey(SoftwareProcess.SUGGESTED_VERSION, "1.2.2");

    @SetFromFlag("downloadUrl")
    BasicAttributeSensorAndConfigKey<String> DOWNLOAD_URL = ConfigKeys.newAttributeSensorAndConfigKey(
            SoftwareProcess.DOWNLOAD_URL, "${driver.mirrorUrl}/${version}/apache-cassandra-${version}-bin.tar.gz");

    /** download mirror, if desired */
    @SetFromFlag("mirrorUrl")
    ConfigKey<String> MIRROR_URL = ConfigKeys.newConfigKey("cassandra.install.mirror.url", "URL of mirror", "http://www.mirrorservice.org/sites/ftp.apache.org/cassandra");

    @SetFromFlag("tgzUrl")
    ConfigKey<String> TGZ_URL = ConfigKeys.newConfigKey("cassandra.install.tgzUrl", "URL of TGZ download file");

    @SetFromFlag("clusterName")
    BasicAttributeSensorAndConfigKey<String> CLUSTER_NAME = CassandraCluster.CLUSTER_NAME;

    @SetFromFlag("gossipPort")
    PortAttributeSensorAndConfigKey GOSSIP_PORT = ConfigKeys.newPortAttributeSensorAndConfigKey("cassandra.gossip.port", "Cassandra Gossip communications port", PortRanges.fromString("7000+"));

    @SetFromFlag("sslGgossipPort")
    PortAttributeSensorAndConfigKey SSL_GOSSIP_PORT = ConfigKeys.newPortAttributeSensorAndConfigKey("cassandra.ssl-gossip.port", "Cassandra Gossip SSL communications port", PortRanges.fromString("7001+"));

    @SetFromFlag("thriftPort")
    PortAttributeSensorAndConfigKey THRIFT_PORT = ConfigKeys.newPortAttributeSensorAndConfigKey("cassandra.thrift.port", "Cassandra Thrift RPC port", PortRanges.fromString("9160+"));

    @SetFromFlag("cassandraConfigTemplateUrl")
    BasicAttributeSensorAndConfigKey<String> CASSANDRA_CONFIG_TEMPLATE_URL = ConfigKeys.newAttributeSensorAndConfigKey(
            "cassandra.config.templateUrl", "Template file (in freemarker format) for the cassandra.yaml config file", 
            "classpath://brooklyn/entity/nosql/cassandra/cassandra.yaml");

    @SetFromFlag("cassandraConfigFileName")
    BasicAttributeSensorAndConfigKey<String> CASSANDRA_CONFIG_FILE_NAME = ConfigKeys.newAttributeSensorAndConfigKey(
            "cassandra.config.fileName", "Name for the copied config file", "cassandra.yaml");

    AttributeSensor<Long> TOKEN = Attributes.newAttributeSensor("cassandra.token", "Cassandra Token");

    AttributeSensor<Integer> PEERS = Attributes.newAttributeSensor("cassandra.peers", "Number of peers in cluster");

    /* Metrics for read/write performance. */

    AttributeSensor<Long> READ_PENDING = Attributes.newAttributeSensor("cassandra.read.pending", "Current pending ReadStage tasks");
    AttributeSensor<Integer> READ_ACTIVE = Attributes.newAttributeSensor("cassandra.read.active", "Current active ReadStage tasks");
    AttributeSensor<Long> READ_COMPLETED = Attributes.newAttributeSensor("cassandra.read.completed", "Total completed ReadStage tasks");
    AttributeSensor<Long> WRITE_PENDING = Attributes.newAttributeSensor("cassandra.write.pending", "Current pending MutationStage tasks");
    AttributeSensor<Integer> WRITE_ACTIVE = Attributes.newAttributeSensor("cassandra.write.active", "Current active MutationStage tasks");
    AttributeSensor<Long> WRITE_COMPLETED = Attributes.newAttributeSensor("cassandra.write.completed", "Total completed MutationStage tasks");

    ConfigKey<String> SEEDS = CassandraCluster.SEEDS;

    Integer getGossipPort();

    Integer getSslGossipPort();

    Integer getThriftPort();

    String getClusterName();

    String getSeeds();

    Long getToken();

    void setToken(String token);
}
