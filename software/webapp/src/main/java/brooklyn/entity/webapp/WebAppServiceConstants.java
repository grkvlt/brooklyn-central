package brooklyn.entity.webapp;

import java.util.Collections;
import java.util.List;

import brooklyn.config.render.RendererHints;
import brooklyn.entity.basic.Attributes;
import brooklyn.event.AttributeSensor;
import brooklyn.event.basic.BasicAttributeSensor;
import brooklyn.event.basic.BasicAttributeSensorAndConfigKey;
import brooklyn.event.basic.PortAttributeSensorAndConfigKey;
import brooklyn.util.flags.SetFromFlag;

public interface WebAppServiceConstants {

    @SetFromFlag("httpPort")
    PortAttributeSensorAndConfigKey HTTP_PORT = Attributes.HTTP_PORT;

    @SetFromFlag("httpsPort")
    PortAttributeSensorAndConfigKey HTTPS_PORT = Attributes.HTTPS_PORT;

    @SetFromFlag("enabledProtocols")
    BasicAttributeSensorAndConfigKey<List<String>> ENABLED_PROTOCOLS = new BasicAttributeSensorAndConfigKey<List<String>>("webapp.enabledProtocols", "List of enabled protocols (e.g. http, https)", Collections.singletonList("http"));

    @SetFromFlag("httpsSsl")
    BasicAttributeSensorAndConfigKey<HttpsSslConfig> HTTPS_SSL_CONFIG = new BasicAttributeSensorAndConfigKey<HttpsSslConfig>("webapp.https.ssl", "SSL Configuration for HTTPS", null);

    AttributeSensor<Integer> REQUEST_COUNT = new BasicAttributeSensor<Integer>("webapp.reqs.total", "Request count");
    AttributeSensor<Integer> ERROR_COUNT = new BasicAttributeSensor<Integer>("webapp.reqs.errors", "Request errors");
    AttributeSensor<Integer> TOTAL_PROCESSING_TIME = new BasicAttributeSensor<Integer>("webapp.reqs.processingTime.total", "Total processing time (millis)");
    AttributeSensor<Integer> MAX_PROCESSING_TIME = new BasicAttributeSensor<Integer>("webapp.reqs.processingTime.max", "Max processing time (millis)");

    AttributeSensor<Long> BYTES_RECEIVED = new BasicAttributeSensor<Long>("webapp.reqs.bytes.received", "Total bytes received by the webserver");
    AttributeSensor<Long> BYTES_SENT = new BasicAttributeSensor<Long>("webapp.reqs.bytes.sent", "Total bytes sent by the webserver");

    /** req/second computed from the delta of the last request count and an associated timestamp */
    AttributeSensor<Double> REQUESTS_PER_SECOND_LAST = new BasicAttributeSensor<Double>("webapp.reqs.perSec.last", "Reqs/sec (last datapoint)");
    /** @deprecated since 0.5.0, use REQUESTS_PER_SECOND_LAST */
    AttributeSensor<Double> REQUESTS_PER_SECOND = REQUESTS_PER_SECOND_LAST;

    // TODO make this a config key
    Integer REQUESTS_PER_SECOND_WINDOW_PERIOD = 10 * 1000;
    /** rolled-up req/second for a window */
    AttributeSensor<Double> REQUESTS_PER_SECOND_IN_WINDOW = new BasicAttributeSensor<Double>(
                    String.format("webapp.reqs.perSec.windowed", REQUESTS_PER_SECOND_WINDOW_PERIOD),
                    String.format("Reqs/sec (over time window)", REQUESTS_PER_SECOND_WINDOW_PERIOD));
    /** @deprecated since 0.5.0, use REQUESTS_PER_SECOND_WINDOW_PERIOD */
    Integer AVG_REQUESTS_PER_SECOND_PERIOD = REQUESTS_PER_SECOND_WINDOW_PERIOD;
    /** @deprecated since 0.5.0, use REQUESTS_PER_SECOND_IN_WINDOW */
    AttributeSensor<Double> AVG_REQUESTS_PER_SECOND = REQUESTS_PER_SECOND_IN_WINDOW;

    AttributeSensor<String> ROOT_URL = RootUrl.ROOT_URL;

}

//this class is added because the ROOT_URL relies on a static initialization which unfortunately can't be added to
//an interface.
class RootUrl {
    public static final AttributeSensor<String> ROOT_URL = new BasicAttributeSensor<String>(String.class, "webapp.url", "URL");

    static {
        RendererHints.register(ROOT_URL, new RendererHints.NamedActionWithUrl("Open"));
    }
}
