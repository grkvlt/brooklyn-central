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
package brooklyn.entity.monitoring.dynatrace;

import static org.testng.Assert.assertEquals;

import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseFactory;
import org.apache.http.ProtocolVersion;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.DefaultHttpResponseFactory;
import org.apache.http.protocol.BasicHttpContext;
import org.testng.annotations.Test;

import brooklyn.entity.webapp.WebAppService;
import brooklyn.event.feed.http.HttpPollValue;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class DynaTraceXmlTest {

    private static HttpResponseFactory factory = new DefaultHttpResponseFactory();

    @Test
    public void testPollConfig() throws Exception {
        DynaTracePollConfig<Double> config = new DynaTracePollConfig<Double>(WebAppService.REQUESTS_PER_SECOND_IN_WINDOW)
                .agentName("web-abctelecom-brooklyn-zjcc-hcc-bdex-ipscapewebserver-wncx-41e")
                .agentType("Apache 2.2")
                .measureName("webserver request rate")
                .itemName("avg")
                .reportName("CloudSoftIntegration");

        HttpResponse response = factory.newHttpResponse(new ProtocolVersion("HTTP",  1,  0), 200, new BasicHttpContext());
        response.setEntity(new ByteArrayEntity(Resources.toString(Resources.getResource("dynatrace.xml"), Charsets.UTF_8).getBytes()));
        Double average = config.getOnSuccess().apply(new HttpPollValue(response, 0l));
        assertEquals(average.doubleValue(), 2.110344829230473d);
    }

    @Test
    public void testPollCopy() throws Exception {
        DynaTracePollConfig<Double> config = new DynaTracePollConfig<Double>(WebAppService.REQUESTS_PER_SECOND_IN_WINDOW)
                .agentType("Apache 2.2")
                .measureName("webserver request rate")
                .itemName("avg")
                .reportName("CloudSoftIntegration");
        DynaTracePollConfig<Double> copy = new DynaTracePollConfig<Double>(config)
                .agentName("web-abctelecom-brooklyn-zjcc-hcc-bdex-ipscapewebserver-wncx-41e");

        HttpResponse response = factory.newHttpResponse(new ProtocolVersion("HTTP",  1,  0), 200, new BasicHttpContext());
        response.setEntity(new ByteArrayEntity(Resources.toString(Resources.getResource("dynatrace.xml"), Charsets.UTF_8).getBytes()));
        Double average = copy.getOnSuccess().apply(new HttpPollValue(response, 0l));
        assertEquals(average.doubleValue(), 2.110344829230473d);
    }

}
