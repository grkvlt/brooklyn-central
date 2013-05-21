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
package brooklyn.entity.monitoring;

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

import brooklyn.config.ConfigKey;
import brooklyn.entity.basic.ConfigKeys;
import brooklyn.entity.basic.EntityLocal;
import brooklyn.entity.webapp.WebAppService;
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

public class MonitoringFeed extends AbstractFeed {

    private static final Logger log = LoggerFactory.getLogger(MonitoringFeed.class);
    private static final AtomicInteger id = new AtomicInteger(0);

    public static Builder<MonitoringFeed, ?> builder() {
        return new ConcreteBuilder();
    }

    private static class ConcreteBuilder extends Builder<MonitoringFeed, ConcreteBuilder> {
    }

    public static class Builder<T extends MonitoringFeed, B extends Builder<T,B>> {
        private EntityLocal entity;
        private long period = 500;
        private TimeUnit periodUnits = TimeUnit.MILLISECONDS;
        private List<MonitoringPollConfig<?>> polls = Lists.newArrayList();
        private boolean suspended = false;
        private volatile boolean built;
        private MonitoringServer server;

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
        public B poll(MonitoringPollConfig<?> config) {
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

        public B server(final MonitoringServer server) {
            this.server = server;
            return self();
        }

        @SuppressWarnings("unchecked")
        public T build() {
            // If server not set, try to obtain from entity
            if (server == null) {
                MonitoringServer server = Preconditions.checkNotNull(entity.getConfig(MonitoredEntity.MONITORING_SERVER),
                        "The MONITORING_SERVER config key must be set on the entity");
                log.warn("Setting properties for feed based on server configuration: {}", server);
                server(server);
            }
            // Now create feed
            T result = (T) new MonitoringFeed(this);
            built = true;
            if (suspended) result.suspend();
            result.start();
            return result;
        }
        @Override
        protected void finalize() {
            if (!built) log.warn("MonitoringFeed.Builder created, but build() never called");
        }
    }

    protected final Set<MonitoringPollConfig<?>> polls;
    protected final MonitoringServer server;

    protected MonitoringFeed(final Builder<? extends MonitoringFeed, ?> builder) {
        super(builder.entity);

        // Copy fields from builder as required
        this.server = builder.server;

        // Build the set of polling configurations
        ImmutableSet.Builder<MonitoringPollConfig<?>> pollsBuilder = ImmutableSet.builder();
        for (MonitoringPollConfig<?> config : builder.polls) {
            @SuppressWarnings({ "rawtypes", "unchecked" })
            MonitoringPollConfig<?> configCopy = new MonitoringPollConfig(config);
            if (configCopy.getPeriod() < 0) configCopy.period(builder.period, builder.periodUnits);
            log.debug("storing DynaTrace poll configuration: {}", configCopy);
            pollsBuilder.add(configCopy);
        }
        polls = pollsBuilder.build();
    }

    @Override
    protected void preStart() {
        log.info("starting dynatrace feed for {}", entity);
        final DefaultHttpClient httpClient = new DefaultHttpClient(new ThreadSafeClientConnManager());
        httpClient.setReuseStrategy(new NoConnectionReuseStrategy());

        // Setup HTTP Basic authentication if required
        String username = server.getConfig(MonitoringServer.MONITORING_USERNAME);
        String password = server.getConfig(MonitoringServer.MONITORING_PASSWORD);
        if (!(Strings.isNullOrEmpty(username) && Strings.isNullOrEmpty(password))) {
            Credentials credentials = new UsernamePasswordCredentials(username, password);
            CredentialsProvider provider = new BasicCredentialsProvider();
            provider.setCredentials(AuthScope.ANY, credentials);
            httpClient.setCredentialsProvider(provider);
        }

        // Setup the polling job
        Callable<HttpPollValue> pollJob = new Callable<HttpPollValue>() {
            @Override
            public HttpPollValue call() throws Exception {
                int currentId = id.getAndIncrement();
                if (log.isTraceEnabled()) {
                    log.trace("Monitor polling {} ({})", entity, currentId);
                }

                HttpGet httpGet = new HttpGet(""); // TODO get appropriate value
                long startTime = System.currentTimeMillis();
                HttpResponse httpResponse = httpClient.execute(httpGet);
                try {
                    return new HttpPollValue(httpResponse, startTime);
                } finally {
                    EntityUtils.consume(httpResponse.getEntity());
                }
            }
        };

        // Create a handler for each metric
        long minPeriod = Integer.MAX_VALUE;
        List<AttributePollHandler<? super HttpPollValue>> pollHandlers = Lists.newArrayList();
        for (MonitoringPollConfig<?> config : polls) {
            pollHandlers.add(new AttributePollHandler<HttpPollValue>(config, entity, this));
            if (config.getPeriod() > 0) minPeriod = Math.min(minPeriod, config.getPeriod());
        }

        // Schedule the polling job and delegate the reponse to our handlers
        getPoller().scheduleAtFixedRate(pollJob, new DelegatingPollHandler<HttpPollValue>(pollHandlers), minPeriod);
    }

    @SuppressWarnings("unchecked")
    protected Poller<HttpPollValue> getPoller() {
        return (Poller<HttpPollValue>) poller;
    }

}
