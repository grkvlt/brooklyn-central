package brooklyn.event.feed.http;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import brooklyn.entity.basic.Entities;
import brooklyn.entity.basic.EntityLocal;
import brooklyn.event.feed.AbstractFeed;
import brooklyn.event.feed.AttributePollHandler;
import brooklyn.event.feed.DelegatingPollHandler;
import brooklyn.event.feed.Poller;
import brooklyn.util.exceptions.Exceptions;

import com.google.common.base.Objects;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.Sets;

/**
 * Http feed identifier.
 */
public class HttpPollIdentifier {

    final String method;
    final Supplier<URI> uriProvider;
    final Map<String,String> headers;
    final byte[] body;

    public HttpPollIdentifier(String method, URI uri, Map<String,String> headers, byte[] body) {
        this(method, Suppliers.ofInstance(uri), headers, body);
    }
    public HttpPollIdentifier(String method, Supplier<URI> uriProvider, Map<String,String> headers, byte[] body) {
        this.method = checkNotNull(method, "method").toLowerCase();
        this.uriProvider = checkNotNull(uriProvider, "uriProvider");
        this.headers = checkNotNull(headers, "headers");
        this.body = body;
            
        if (!(this.method.equals("get") || this.method.equals("post"))) {
            throw new IllegalArgumentException("Unsupported HTTP method (only supports GET and POST): "+method);
        }
        if (body != null && method.equalsIgnoreCase("get")) {
            throw new IllegalArgumentException("Must not set body for http GET method");
        }
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(method, uriProvider, headers, body);
    }
        
    @Override
    public boolean equals(Object other) {
        if (!(other instanceof HttpPollIdentifier)) {
            return false;
        }
        HttpPollIdentifier o = (HttpPollIdentifier) other;
        return Objects.equal(method, o.method) &&
                Objects.equal(uriProvider, o.uriProvider) &&
                Objects.equal(headers, o.headers) &&
                Objects.equal(body, o.body);
    }
}