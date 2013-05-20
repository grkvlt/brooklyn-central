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

import javax.annotation.Nullable;

import brooklyn.event.AttributeSensor;
import brooklyn.event.feed.PollConfig;
import brooklyn.event.feed.http.HttpPollValue;
import brooklyn.event.feed.http.HttpValueFunctions;
import brooklyn.event.feed.http.JsonFunctions;
import brooklyn.event.feed.http.XmlFunctions;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.gson.JsonElement;

public class ZabbixPollConfig<T> extends PollConfig<HttpPollValue, T, ZabbixPollConfig<T>> {

    private String itemKey;

    public ZabbixPollConfig(AttributeSensor<T> sensor) {
        super(sensor);
        // Extract the last value of the item
        onSuccess(
                Functions.compose(JsonFunctions.cast(getSensor().getType()),
                    Functions.compose(new Function<JsonElement, JsonElement>() {
                        @Override
                        public JsonElement apply(@Nullable JsonElement input) {
                            Preconditions.checkNotNull(input, "JSON input");
                            return input.getAsJsonObject().get("result")
                                    .getAsJsonArray().get(0)
                                    .getAsJsonObject().get("lastvalue");
                        }
                    }, HttpValueFunctions.jsonContents())));
        // Return a default (zero) value on error
        onError((Function) XmlFunctions.defaultValue(getSensor().getType()));
    }

    public ZabbixPollConfig(ZabbixPollConfig<T> other) {
        super(other);
        this.itemKey = other.getItemKey();
    }

    public String getItemKey() {
        return itemKey;
    }

    public ZabbixPollConfig<T> itemKey(String val) {
        this.itemKey = val;
        return this;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(super.hashCode(), itemKey);
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof ZabbixPollConfig)) {
            return false;
        }
        ZabbixPollConfig<?> o = (ZabbixPollConfig<?>) other;
        return super.equals(o)
                && Objects.equal(itemKey, o.itemKey);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(getClass())
                .add("itemKey", itemKey)
                .toString();
    }

}
