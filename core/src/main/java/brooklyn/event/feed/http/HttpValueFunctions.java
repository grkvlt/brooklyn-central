/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package brooklyn.event.feed.http;

import java.util.List;

import javax.annotation.Nullable;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.base.Predicates;
import com.google.common.collect.Lists;
import com.google.gson.JsonElement;

public class HttpValueFunctions {

    private HttpValueFunctions() {} // instead use static utility methods
    
    public static Function<HttpPollValue, Integer> responseCode() {
        return new Function<HttpPollValue, Integer>() {
            @Override public Integer apply(HttpPollValue input) {
                return input.getResponseCode();
            }
        };
    }

    public static Function<HttpPollValue, Boolean> responseCodeEquals(final int expected) {
        return chain(HttpValueFunctions.responseCode(), Functions.forPredicate(Predicates.equalTo(expected)));
    }
    
    public static Function<HttpPollValue, Boolean> responseCodeEquals(final int... expected) {
        List<Integer> expectedList = Lists.newArrayList();
        for (int e : expected) {
            expectedList.add((Integer)e);
        }
        return chain(HttpValueFunctions.responseCode(), Functions.forPredicate(Predicates.in(expectedList)));
    }
    
    public static Function<HttpPollValue, String> stringContentsFunction() {
        return new Function<HttpPollValue, String>() {
            @Override public String apply(HttpPollValue input) {
                // TODO Charset?
                return new String(input.getContent());
            }
        };
    }
    
    public static Function<HttpPollValue, JsonElement> jsonContents() {
        return chain(stringContentsFunction(), JsonFunctions.asJson());
    }
    
    public static <T> Function<HttpPollValue, T> jsonContents(String element, Class<T> expected) {
        return jsonContents(new String[] {element}, expected);
    }
    
    public static <T> Function<HttpPollValue, T> jsonContents(String[] elements, Class<T> expected) {
        return chain(jsonContents(), JsonFunctions.walk(elements), JsonFunctions.cast(expected));
    }
    
    public static Function<HttpPollValue, Long> latency() {
        return new Function<HttpPollValue, Long>() {
            public Long apply(HttpPollValue input) {
                return input.getLatencyFullContent();
            }
        };
    }
    
    public static <A,B,C> Function<A,C> chain(final Function<A,? extends B> f1, final Function<B,C> f2) {
        return new Function<A,C>() {
            @Override public C apply(@Nullable A input) {
                return f2.apply(f1.apply(input));
            }
        };
    }
    
    public static <A,B,C,D> Function<A,D> chain(final Function<A,? extends B> f1, final Function<B,? extends C> f2, final Function<C,D> f3) {
        return new Function<A,D>() {
            @Override public D apply(@Nullable A input) {
                return f3.apply(f2.apply(f1.apply(input)));
            }
        };
    }
}
