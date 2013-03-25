package brooklyn.entity.webapp;

import brooklyn.entity.basic.Attributes;
import brooklyn.entity.group.DynamicFabric;
import brooklyn.entity.proxying.ImplementedBy;
import brooklyn.event.AttributeSensor;

/**
 * DynamicWebAppFabric provide a fabric of clusters, aggregating the entity attributes.
 * <p>
 * Currently totals and averages:
 * <ul>
 *   <li>Entity request counts</li>
 *   <li>Entity error counts</li>
 *   <li>Requests per second</li>
 *   <li>Entity processing time</li>
 * </ul>
 */
@ImplementedBy(DynamicWebAppFabricImpl.class)
public interface DynamicWebAppFabric extends DynamicFabric, WebAppService {

    AttributeSensor<Double> REQUEST_COUNT_PER_NODE = Attributes.newAttributeSensor("webapp.reqs.total.perNode", "Fabric entity request average");

    AttributeSensor<Integer> ERROR_COUNT_PER_NODE = Attributes.newAttributeSensor("webapp.reqs.errors.perNode", "Fabric entity request error average");

    AttributeSensor<Double> REQUESTS_PER_SECOND_LAST_PER_NODE = Attributes.newAttributeSensor("webapp.reqs.perSec.last.perNode", "Reqs/sec (last datapoint) averaged over all nodes");

    AttributeSensor<Double> REQUESTS_PER_SECOND_IN_WINDOW_PER_NODE = Attributes.newAttributeSensor("webapp.reqs.perSec.windowed.perNode", "Reqs/sec (over time window) averaged over all nodes");

    AttributeSensor<Integer> TOTAL_PROCESSING_TIME_PER_NODE = Attributes.newAttributeSensor("webapp.reqs.processingTime.perNode", "Total processing time per node");

}
