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

import brooklyn.event.AttributeSensor;
import brooklyn.event.feed.PollConfig;
import brooklyn.event.feed.http.HttpPollValue;
import brooklyn.event.feed.http.HttpValueFunctions;
import brooklyn.event.feed.http.XmlFunctions;

import com.google.common.base.Function;
import com.google.common.base.Functions;

public class MonitoringPollConfig<T> extends PollConfig<HttpPollValue, T, MonitoringPollConfig<T>> {

    public MonitoringPollConfig(AttributeSensor<T> sensor) {
        super(sensor);

        setupFunctions();
    }

    public MonitoringPollConfig(MonitoringPollConfig<T> other) {
        super(other);

        setupFunctions();
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private void setupFunctions() {
        onSuccess(Functions.compose(XmlFunctions.defaultValue((Class) getSensor().getType()),
                    Functions.compose(XmlFunctions.debug("poll"),
                        HttpValueFunctions.stringContentsFunction())));

        // Error always returns zero
        onError((Function) XmlFunctions.defaultValue(getSensor().getType()));
    }

}
