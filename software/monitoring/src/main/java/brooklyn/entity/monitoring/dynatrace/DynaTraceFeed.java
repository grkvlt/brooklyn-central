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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.impl.NoConnectionReuseStrategy;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import brooklyn.entity.basic.EntityLocal;
import brooklyn.event.feed.AbstractFeed;
import brooklyn.event.feed.AttributePollHandler;
import brooklyn.event.feed.DelegatingPollHandler;
import brooklyn.event.feed.Poller;
import brooklyn.event.feed.http.HttpPollValue;
import brooklyn.util.exceptions.Exceptions;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;

public class DynaTraceFeed extends AbstractFeed {

    private static final Logger log = LoggerFactory.getLogger(DynaTraceFeed.class);
    private static final AtomicInteger id = new AtomicInteger(0);

    public static Builder<DynaTraceFeed, ?> builder() {
        return new ConcreteBuilder();
    }

    private static class ConcreteBuilder extends Builder<DynaTraceFeed, ConcreteBuilder> {
    }

    public static class Builder<T extends DynaTraceFeed, B extends Builder<T,B>> {
        private EntityLocal entity;
        private long period = 500;
        private TimeUnit periodUnits = TimeUnit.MILLISECONDS;
        private List<DynaTracePollConfig<?>> polls = Lists.newArrayList();
        private boolean suspended = false;
        private volatile boolean built;
        private DynaTraceServer server;
        private String serverName;
        private Integer restApiPort;
        private String username;
        private String password;
        private String reportName;
        private String agentName;

        @SuppressWarnings("unchecked")
        protected B self() {
           return (B) this;
        }

        public B entity(EntityLocal val) {
            this.entity = val;
            return self();
        }
        public B period(long millis) {
            return period(millis, TimeUnit.MILLISECONDS);
        }
        public B period(long val, TimeUnit units) {
            this.period = val;
            this.periodUnits = units;
            return self();
        }
        public B poll(DynaTracePollConfig<?> config) {
            polls.add(config);
            return self();
        }
        public B suspended() {
            return suspended(true);
        }
        public B suspended(boolean startsSuspended) {
            this.suspended = startsSuspended;
            return self();
        }

        public B server(final DynaTraceServer server) {
            this.server = server;
            serverName(server.getConfig(DynaTraceServer.DYNATRACE_SERVER_NAME));
            restApiPort(server.getConfig(DynaTraceServer.DYNATRACE_REST_API_PORT));
            username(server.getConfig(DynaTraceServer.DYNATRACE_SERVER_USERNAME));
            password(server.getConfig(DynaTraceServer.DYNATRACE_SERVER_PASSWORD));
            return self();
        }
        public B serverName(String serverName) {
            this.serverName = serverName;
            return self();
        }
        public B restApiPort(Integer restApiPort) {
            this.restApiPort = restApiPort;
            return self();
        }
        public B username(String username) {
            this.username = username;
            return self();
        }
        public B password(String password) {
            this.password = password;
            return self();
        }
        public B report(String reportName) {
            this.reportName = reportName;
            return self();
        }
        public B agentName(String agentName) {
            this.agentName = agentName;
            return self();
        }

        @SuppressWarnings("unchecked")
        public T build() {
            // Set the agent name attribute on the entity
            String agentNameAttribute = entity.getConfig(DynaTraceMonitored.DYNATRACE_AGENT_NAME_FUNCTION).apply(entity);
            entity.setAttribute(DynaTraceMonitored.DYNATRACE_AGENT_NAME, agentNameAttribute);

            // If server not set and other config not available, try to obtain server from entity config
            if (server == null &&
                    (serverName == null || restApiPort == null || username == null || password == null)) {
                DynaTraceServer server = Preconditions.checkNotNull(entity.getConfig(DynaTraceMonitored.DYNATRACE_SERVER),
                        "The DYNATRACE_SERVER config key must be set on the entity");
                log.warn("Setting properties for feed based on DynaTraceServer configuration: {}", server);
                server(server);
            }
            if (reportName == null) {
                report(entity.getConfig(DynaTraceMonitored.DYNATRACE_XML_REPORT_NAME));
                log.warn("Setting dynatrace report name based on DynaTraceMonitored entity configuration: {}, {}", entity, reportName);
            }
            if (agentName == null) {
                agentName(agentNameAttribute);
                log.warn("Setting dynatrace agent name based on DynaTraceMonitored entity configuration: {}, {}", entity, agentName);
            }
            // Now create feed
            T result = (T) new DynaTraceFeed(this);
            built = true;
            if (suspended) result.suspend();
            result.start();
            return result;
        }
        @Override
        protected void finalize() {
            if (!built) log.warn("DynaTraceFeed.Builder created, but build() never called");
        }
    }

    protected final Set<DynaTracePollConfig<?>> polls;
    protected final URI restUri;

    protected DynaTraceFeed(final Builder<? extends DynaTraceFeed, ?> builder) {
        super(builder.entity);

        // TODO add further configuration options for the URI
        try {
            restUri = URIUtils.createURI("http", builder.serverName, builder.restApiPort, "/rest/management/reports/create/" + builder.reportName, "type=xml", "");
        } catch (URISyntaxException use) {
            log.warn("could not create URI to contact DynaTrace REST API", use);
            throw Exceptions.propagate(use);
        }

        // Build the set of polling configurations
        ImmutableSet.Builder<DynaTracePollConfig<?>> pollsBuilder = ImmutableSet.builder();
        for (DynaTracePollConfig<?> config : builder.polls) {
            @SuppressWarnings({ "rawtypes", "unchecked" })
            DynaTracePollConfig<?> configCopy = new DynaTracePollConfig(config);
            if (Strings.isNullOrEmpty(configCopy.getAgentName())) configCopy.agentName(builder.agentName);
            if (Strings.isNullOrEmpty(configCopy.getReportName())) configCopy.reportName(builder.reportName);
            if (configCopy.getPeriod() < 0) configCopy.period(builder.period, builder.periodUnits);
            log.debug("storing DynaTrace poll configuration: {}", configCopy);
            pollsBuilder.add(configCopy);
        }
        polls = pollsBuilder.build();
    }

    @Override
    protected void preStart() {
        log.info("starting dynatrace feed for {}", entity);

        // Setup HTTP authentication
        DynaTraceServer server = entity.getConfig(DynaTraceMonitored.DYNATRACE_SERVER);
        String username = server.getConfig(DynaTraceServer.DYNATRACE_SERVER_USERNAME);
        String password = server.getConfig(DynaTraceServer.DYNATRACE_SERVER_PASSWORD);
        Credentials credentials = new UsernamePasswordCredentials(username, password);
        CredentialsProvider provider = new BasicCredentialsProvider();
        provider.setCredentials(AuthScope.ANY, credentials);

        final DefaultHttpClient httpClient = new DefaultHttpClient(new ThreadSafeClientConnManager());
        httpClient.setReuseStrategy(new NoConnectionReuseStrategy());
        httpClient.setCredentialsProvider(provider);

        // Setup the polling job
        Callable<HttpPollValue> pollJob = new Callable<HttpPollValue>() {
            @Override
            public HttpPollValue call() throws Exception {
                int currentId = id.getAndIncrement();
                if (log.isTraceEnabled()) {
                    log.trace("dynatrace polling {} at {} ({})", new Object[] { entity, restUri, currentId });
                }

                HttpGet httpGet = new HttpGet(restUri);
                long startTime = System.currentTimeMillis();
                HttpResponse httpResponse = httpClient.execute(httpGet);
                try {
                    return new HttpPollValue(httpResponse, startTime);
                } finally {
                    EntityUtils.consume(httpResponse.getEntity());
                }
            }
        };

        // Create a handler for each DynaTrace metric
        long minPeriod = Integer.MAX_VALUE;
        List<AttributePollHandler<? super HttpPollValue>> pollHandlers = Lists.newArrayList();
        for (DynaTracePollConfig<?> config : polls) {
            pollHandlers.add(new AttributePollHandler<HttpPollValue>(config, entity, this));
            if (config.getPeriod() > 0) minPeriod = Math.min(minPeriod, config.getPeriod());
        }

        // Schedule the DynaTrace polling job and delegate the reponse to our handlers
        getPoller().scheduleAtFixedRate(pollJob, new DelegatingPollHandler<HttpPollValue>(pollHandlers), minPeriod);
    }

    @SuppressWarnings("unchecked")
    protected Poller<HttpPollValue> getPoller() {
        return (Poller<HttpPollValue>) poller;
    }

}
