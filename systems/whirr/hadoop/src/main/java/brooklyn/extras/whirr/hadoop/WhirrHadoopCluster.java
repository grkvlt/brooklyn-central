package brooklyn.extras.whirr.hadoop;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import brooklyn.config.ConfigKey;
import brooklyn.entity.proxying.ImplementedBy;
import brooklyn.event.Sensor;
import brooklyn.event.basic.BasicAttributeSensor;
import brooklyn.event.basic.BasicConfigKey;
import brooklyn.extras.whirr.core.WhirrCluster;
import brooklyn.util.flags.SetFromFlag;

@ImplementedBy(WhirrHadoopClusterImpl.class)
public interface WhirrHadoopCluster extends WhirrCluster {

    Logger log = LoggerFactory.getLogger(WhirrHadoopCluster.class);

    @SetFromFlag("name")
    ConfigKey<String> NAME = new BasicConfigKey<String>(
            "whirr.hadoop.name", "The name of the Hadoop cluster");

    @SetFromFlag("size")
    ConfigKey<Integer> SIZE = new BasicConfigKey<Integer>(
            "whirr.hadoop.size", "The size of the Hadoop cluster (including a dedicated machine for the namenode)", 2);

    @SetFromFlag("memory")
    ConfigKey<Integer> MEMORY = new BasicConfigKey<Integer>(
            "whirr.hadoop.memory", "The minimum amount of memory to use for each node (in megabytes)", 1024);

    Sensor<String> NAME_NODE_URL = new BasicAttributeSensor<String>(
            "whirr.hadoop.namenodeUrl", "URL for the Hadoop name node in this cluster (hdfs://...)");

    Sensor<String> JOB_TRACKER_HOST_PORT = new BasicAttributeSensor<String>(
            "whirr.hadoop.jobtrackerHostPort", "Hadoop Jobtracker host and port");

    Sensor<String> SOCKS_SERVER = new BasicAttributeSensor<String>(
            "whirr.hadoop.socks.server", "Local SOCKS server connection details");

    void generateWhirrClusterRecipe();

    List getUserRecipeLines();

    void addRecipeLine(String line);
}
