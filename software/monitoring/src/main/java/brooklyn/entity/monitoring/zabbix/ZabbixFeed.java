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
package brooklyn.entity.monitoring.zabbix;

import static com.google.common.base.Preconditions.*;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.NoConnectionReuseStrategy;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import brooklyn.entity.basic.Attributes;
import brooklyn.entity.basic.EntityLocal;
import brooklyn.event.feed.AbstractFeed;
import brooklyn.event.feed.AttributePollHandler;
import brooklyn.event.feed.PollHandler;
import brooklyn.event.feed.Poller;
import brooklyn.event.feed.http.HttpPollValue;
import brooklyn.event.feed.http.HttpValueFunctions;
import brooklyn.location.Location;
import brooklyn.location.access.BrooklynAccessUtils;
import brooklyn.location.basic.SupportsPortForwarding;
import brooklyn.util.exceptions.Exceptions;
import brooklyn.util.net.Cidr;

import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicates;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.net.HostAndPort;
import com.google.gson.JsonObject;

public class ZabbixFeed extends AbstractFeed {

    public static final Logger log = LoggerFactory.getLogger(ZabbixFeed.class);

    public static final String JSON_ITEM_GET =
            "{ \"jsonrpc\":\"2.0\",\"method\":\"item.get\"," +
                "\"params\":{\"output\":\"extend\"," +
                    "\"filter\":{\"hostid\":[\"{{hostId}}\"],\"key_\":\"{{itemKey}}\"}}," +
                "\"auth\":\"{{token}}\",\"id\":{{id}}}";
    public static final String JSON_USER_LOGIN =
            "{ \"jsonrpc\":\"2.0\",\"method\":\"user.login\"," +
                "\"params\":{\"user\":\"{{username}}\",\"password\":\"{{password}}\"}," +
                "\"id\":0 }";
    public static final String JSON_HOST_CREATE =
            "{ \"jsonrpc\":\"2.0\",\"method\":\"host.create\"," +
                "\"params\":{\"host\":\"{{host}}\"," +
                    "\"interfaces\":[{\"type\":1,\"main\":1,\"useip\":1,\"ip\":\"{{ip}}\",\"dns\":\"\",\"port\":\"{{port}}\"}]," +
                    "\"groups\":[{\"groupid\":\"{{groupId}}\"}]," +
                    "\"templates\":[{\"templateid\":\"{{templateId}}\"}]}," +
                "\"auth\":\"{{token}}\",\"id\":{{id}}}";

    private static final AtomicInteger id = new AtomicInteger(0);

    public static Builder<ZabbixFeed, ?> builder() {
        return new ConcreteBuilder();
    }

    private static class ConcreteBuilder extends Builder<ZabbixFeed, ConcreteBuilder> {
    }

    public static class Builder<T extends ZabbixFeed, B extends Builder<T,B>> {
        private EntityLocal entity;
        private long period = 500;
        private TimeUnit periodUnits = TimeUnit.MILLISECONDS;
        private List<ZabbixPollConfig<?>> polls = Lists.newArrayList();
        private URI baseUri;
        private boolean suspended = false;
        private volatile boolean built;
        private ZabbixServer server;
        private String username;
        private String password;
        private Integer sessionTimeout;
        private Integer groupId;
        private Integer templateId;

        @SuppressWarnings("unchecked")
        protected B self() {
           return (B) this;
        }

        public B entity(EntityLocal val) {
            this.entity = val;
            return self();
        }
        public B baseUri(URI val) {
            this.baseUri = val;
            return self();
        }
        public B baseUrl(URL val) {
            return baseUri(URI.create(val.toString()));
        }
        public B baseUri(String val) {
            return baseUri(URI.create(val));
        }
        public B period(long millis) {
            return period(millis, TimeUnit.MILLISECONDS);
        }
        public B period(long val, TimeUnit units) {
            this.period = val;
            this.periodUnits = units;
            return self();
        }
        public B poll(ZabbixPollConfig<?> config) {
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

        public B server(final ZabbixServer server) {
            this.server = server;
            baseUri(URI.create(server.getConfig(ZabbixServer.ZABBIX_SERVER_API_URL)));
            username(server.getConfig(ZabbixServer.ZABBIX_SERVER_USERNAME));
            password(server.getConfig(ZabbixServer.ZABBIX_SERVER_PASSWORD));
            sessionTimeout(server.getConfig(ZabbixServer.ZABBIX_SESSION_TIMEOUT));
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
        public B sessionTimeout(Integer sessionTimeout) {
            this.sessionTimeout = sessionTimeout;
            return self();
        }
        public B groupId(Integer groupId) {
            this.groupId = groupId;
            return self();
        }
        public B templateId(Integer templateId) {
            this.templateId = templateId;
            return self();
        }
        public B register(Integer groupId, Integer templateId) {
            this.groupId = groupId;
            this.templateId = templateId;
            return self();
        }

        @SuppressWarnings("unchecked")
        public T build() {
            // If server not set and other config not available, try to obtain from entity config
            if (server == null &&
                    (baseUri == null || username == null || password == null)) {
                ZabbixServer server = Preconditions.checkNotNull(entity.getConfig(ZabbixMonitored.ZABBIX_SERVER),
                        "The ZABBIX_SERVER config key must be set on the entity");
                log.warn("Setting properties for feed based on ZabbixServer entity {}", server);
                server(server);
            }
            // Now create feed
            T result = (T) new ZabbixFeed(this);
            built = true;
            if (suspended) result.suspend();
            result.start();
            return result;
        }

        @Override
        protected void finalize() {
            if (!built) log.warn("ZabbixFeed.Builder created, but build() never called");
        }
    }

    protected static class ZabbixPollIdentifier {
        final String itemName;

        protected ZabbixPollIdentifier(String itemName) {
            this.itemName = checkNotNull(itemName, "itemName");
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(itemName);
        }

        @Override
        public boolean equals(Object other) {
            if (!(other instanceof ZabbixPollIdentifier)) {
                return false;
            }
            ZabbixPollIdentifier o = (ZabbixPollIdentifier) other;
            return Objects.equal(itemName, o.itemName);
        }
    }

    protected final Set<ZabbixPollConfig<?>> polls;
    protected final URI apiUri;
    protected final Integer groupId;
    protected final Integer templateId;
    protected final AtomicBoolean registered = new AtomicBoolean(false);

    protected ZabbixFeed(final Builder<? extends ZabbixFeed, ?> builder) {
        super(builder.entity);

        apiUri = checkNotNull(builder.baseUri, "Zabbix base URI must be set");
        groupId = checkNotNull(builder.groupId, "Zabbix groupId must be set");
        templateId = checkNotNull(builder.templateId, "Zabbix templateId must be set");

        // Build the set of polling configurations
        ImmutableSet.Builder<ZabbixPollConfig<?>> pollsBuilder = ImmutableSet.builder();
        for (ZabbixPollConfig<?> config : builder.polls) {
            @SuppressWarnings({ "unchecked", "rawtypes" })
            ZabbixPollConfig<?> configCopy = new ZabbixPollConfig(config);
            if (configCopy.getPeriod() < 0) configCopy.period(builder.period, builder.periodUnits);
            pollsBuilder.add(configCopy);
        }
        polls = pollsBuilder.build();
    }

    @Override
    protected void preStart() {
        log.info("starting zabbix feed for {}", entity);

        final DefaultHttpClient httpClient = new DefaultHttpClient(new PoolingClientConnectionManager());
        httpClient.setReuseStrategy(new NoConnectionReuseStrategy());
        try {
            registerSslSocketFactoryIfRequired(apiUri, httpClient);
        } catch (Exception e) {
            log.warn("Error in ZabbixFeed of {}, setting HTTP trust for uri {}", entity, apiUri);
            throw Exceptions.propagate(e);
        }

        // Registration job, calls Zabbix host.create API
        final Callable<HttpPollValue> registerJob = new Callable<HttpPollValue>() {
            @Override
            public HttpPollValue call() throws Exception {
                if (!registered.get()) {
                    // Set the agent name attribute on the entity
                    String agentName = entity.getConfig(ZabbixMonitored.ZABBIX_AGENT_NAME_FUNCTION).apply(entity);
                    entity.setAttribute(ZabbixMonitored.ZABBIX_AGENT_HOSTNAME, agentName);

                    // Select address and port using port-forwarding if available
                    String address = entity.getAttribute(Attributes.ADDRESS);
                    Integer port = entity.getAttribute(ZabbixMonitored.ZABBIX_AGENT_PORT);
                    Optional<Location> location = Iterables.tryFind(entity.getLocations(), Predicates.instanceOf(SupportsPortForwarding.class));
                    if (location.isPresent()) {
                        Cidr management = entity.getConfig(BrooklynAccessUtils.MANAGEMENT_ACCESS_CIDR);
                        HostAndPort forwarded = ((SupportsPortForwarding) location.get()).getSocketEndpointFor(management, port);
                        address = forwarded.getHostText();
                        port = forwarded.getPort();
                    }

                    // Fill in the JSON template and POST it
                    byte[] body = JSON_HOST_CREATE
                            .replace("{{token}}", entity.getConfig(ZabbixMonitored.ZABBIX_SERVER).getAttribute(ZabbixServer.ZABBIX_TOKEN))
                            .replace("{{host}}", entity.getAttribute(ZabbixMonitored.ZABBIX_AGENT_HOSTNAME))
                            .replace("{{ip}}", address)
                            .replace("{{port}}", Integer.toString(port))
                            .replace("{{groupId}}", Integer.toString(groupId))
                            .replace("{{templateId}}", Integer.toString(templateId))
                            .replace("{{id}}", Integer.toString(id.incrementAndGet()))
                            .getBytes();
                    return httpPost(httpClient, apiUri, body);
                }
                return null;
            }
        };

        // The handler for the registration job
        PollHandler<? super HttpPollValue> registrationHandler = new PollHandler<HttpPollValue>() {
            @Override
            public void onSuccess(HttpPollValue val) {
                if (registered.get() || val == null) {
                    return; // Skip if we are registered already or no data from job
                }
                if (val.getResponseCode() == 200) {
                    JsonObject response = HttpValueFunctions.jsonContents().apply(val).getAsJsonObject();
                    if (response.has("error")) {
                        // Parse the JSON error object and log the message
                        JsonObject error = response.get("error").getAsJsonObject();
                        String message = error.get("message").getAsString();
                        String data = error.get("data").getAsString();
                        log.warn("zabbix failed registering host - {}: {}", message, data);
                    } else if (response.has("result")) {
                        // Parse the JSON result object and save the hostId
                        JsonObject result = response.get("result").getAsJsonObject();
                        String hostId = result.get("hostids").getAsJsonArray().get(0).getAsString();
                        // Update the registered status if not set
                        if (registered.compareAndSet(false, true)) {
                            entity.setAttribute(ZabbixMonitored.ZABBIX_AGENT_HOSTID, hostId);
                            log.info("zabbix registered host as id {}", hostId);
                        }
                    } else {
                        throw new IllegalStateException(String.format("zabbix host registration returned invalid result: %s", response.toString()));
                    }
                } else {
                    throw new IllegalStateException(String.format("zabbix sever returned failure code: %d", val.getResponseCode()));
                }
            }
            @Override
            public void onError(Exception error) {
                log.warn("zabbix exception registering host", error);
            }
            @Override
            public boolean checkSuccess(HttpPollValue val) {
                return (val.getResponseCode() == 200);
            }
            @Override
            public void onFailure(HttpPollValue val) {
                log.warn("zabbix sever returned failure code: {}", val.getResponseCode());
            }
            @Override
            public void onException(Exception exception) {
                log.warn("zabbix exception registering host", exception);
            }
        };

        // Schedule registration attempt once per second
        getPoller().scheduleAtFixedRate(registerJob, registrationHandler, 1000l); // TODO make configurable

        // Create a polling job for each Zabbix metric
        for (final ZabbixPollConfig<?> config : polls) {
            Callable<HttpPollValue> pollJob = new Callable<HttpPollValue>() {
                @Override
                public HttpPollValue call() throws Exception {
                    if (registered.get()) {
                        if (log.isTraceEnabled()) log.trace("zabbix polling {} for {}", entity, config);
                        byte[] body = JSON_ITEM_GET
                                .replace("{{token}}", entity.getConfig(ZabbixMonitored.ZABBIX_SERVER).getAttribute(ZabbixServer.ZABBIX_TOKEN))
                                .replace("{{hostId}}", entity.getAttribute(ZabbixMonitored.ZABBIX_AGENT_HOSTID))
                                .replace("{{itemKey}}", config.getItemKey())
                                .replace("{{id}}", Integer.toString(id.incrementAndGet()))
                                .getBytes();
                        return httpPost(httpClient, apiUri, body);
                    } else {
                        throw new IllegalStateException("zabbix agent not yet registered");
                    }
                }
            };

            // Schedule the Zabbix polling job
            AttributePollHandler<? super HttpPollValue> pollHandler = new AttributePollHandler<HttpPollValue>(config, entity, this);
            long minPeriod = Integer.MAX_VALUE; // TODO make configurable
            if (config.getPeriod() > 0) minPeriod = Math.min(minPeriod, config.getPeriod());
            getPoller().scheduleAtFixedRate(pollJob, pollHandler, minPeriod);
        }

    }

    @SuppressWarnings("unchecked")
    protected Poller<HttpPollValue> getPoller() {
        return (Poller<HttpPollValue>) poller;
    }

    public static void registerSslSocketFactoryIfRequired(URI uri, HttpClient httpClient) throws Exception {
        if (uri!=null && "https".equalsIgnoreCase(uri.getScheme())) {
            int port = (uri.getPort() >= 0) ? uri.getPort() : 443;
            SSLSocketFactory socketFactory = new SSLSocketFactory(new TrustAllStrategy(), SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            Scheme sch = new Scheme("https", port, socketFactory);
            httpClient.getConnectionManager().getSchemeRegistry().register(sch);
        }
    }

    protected HttpPollValue httpPost(HttpClient httpClient, URI uri, byte[] body) throws ClientProtocolException, IOException {
        HttpPost httpPost = new HttpPost(uri);
        httpPost.addHeader("Content-Type", "application/json");
        HttpEntity httpEntity = new ByteArrayEntity(body);
        httpPost.setEntity(httpEntity);

        long startTime = System.currentTimeMillis();
        HttpResponse httpResponse = httpClient.execute(httpPost);

        try {
            return new HttpPollValue(httpResponse, startTime);
        } finally {
            EntityUtils.consume(httpResponse.getEntity());
        }
    }

    private static class TrustAllStrategy implements TrustStrategy {
        @Override
        public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            return true;
        }
    }

}
