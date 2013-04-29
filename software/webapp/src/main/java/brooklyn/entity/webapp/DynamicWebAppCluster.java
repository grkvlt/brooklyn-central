package brooklyn.entity.webapp;

import brooklyn.entity.group.DynamicCluster;
import brooklyn.entity.proxying.ImplementedBy;
import brooklyn.event.AttributeSensor;
import brooklyn.event.basic.BasicAttributeSensor;

/**
 * DynamicWebAppClusters provide cluster-wide aggregates of entity attributes.
 * <p>
 * Currently totals and averages:
 * <ul>
 *   <li>Entity request counts</li>
 *   <li>Entity error counts</li>
 *   <li>Requests per second</li>
 *   <li>Entity processing time</li>
 * </ul>
 */
@ImplementedBy(DynamicWebAppClusterImpl.class)
public interface DynamicWebAppCluster extends DynamicCluster, WebAppService {

    AttributeSensor<Double> REQUEST_COUNT_PER_NODE = new BasicAttributeSensor<Double>("webapp.reqs.total.perNode", "Cluster entity request average");

    /** @deprecated since 0.5.0, just use WebAppServiceConstants.REQUEST_COUNT */
    AttributeSensor<Integer> TOTAL_REQUEST_COUNT = WebAppServiceConstants.REQUEST_COUNT;
    /** @deprecated since 0.5.0, just use REQUEST_COUNT_PER_NODE */
    AttributeSensor<Double> AVERAGE_REQUEST_COUNT = REQUEST_COUNT_PER_NODE;

    AttributeSensor<Double> ERROR_COUNT_PER_NODE = new BasicAttributeSensor<Double>("webapp.reqs.errors.perNode", "Cluster entity request error average");

    /** @deprecated since 0.5.0, just use WebAppServiceConstants.ERROR_COUNT */
    AttributeSensor<Integer> TOTAL_ERROR_COUNT = WebAppServiceConstants.ERROR_COUNT;
    /** @deprecated since 0.5.0, use ERROR_COUNT_PER_NODE */
    AttributeSensor<Double> AVERAGE_ERROR_COUNT = ERROR_COUNT_PER_NODE;

    AttributeSensor<Double> REQUESTS_PER_SECOND_LAST_PER_NODE = new BasicAttributeSensor<Double>("webapp.reqs.perSec.last.perNode", "Reqs/sec (last datapoint) averaged over all nodes");

    /** @deprecated since 0.5.0, just use WebAppServiceConstants.REQUESTS_PER_SECOND_LAST */
    AttributeSensor<Double> TOTAL_REQUESTS_PER_SECOND = WebAppServiceConstants.REQUESTS_PER_SECOND_LAST;
    /** @deprecated since 0.5.0, use REQUESTS_PER_SECOND_PER_NODE */
    AttributeSensor<Double> AVERAGE_REQUESTS_PER_SECOND = REQUESTS_PER_SECOND_LAST_PER_NODE;

    AttributeSensor<Double> REQUESTS_PER_SECOND_IN_WINDOW_PER_NODE = new BasicAttributeSensor<Double>("webapp.reqs.perSec.windowed.perNode", "Reqs/sec (over time window) averaged over all nodes");

    AttributeSensor<Integer> TOTAL_PROCESSING_TIME_PER_NODE = new BasicAttributeSensor<Integer>("webapp.reqs.processingTime.perNode", "Total processing time per node");

    /** @deprecated since 0.5.0, use TOTAL_PROCESSING_TIME_PER_NODE */
    AttributeSensor<Integer> AVERAGE_PROCESSING_TIME = TOTAL_PROCESSING_TIME_PER_NODE;

}
