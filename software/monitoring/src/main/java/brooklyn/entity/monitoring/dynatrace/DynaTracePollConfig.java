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

import brooklyn.event.AttributeSensor;
import brooklyn.event.feed.PollConfig;
import brooklyn.event.feed.http.HttpPollValue;
import brooklyn.event.feed.http.HttpValueFunctions;
import brooklyn.event.feed.http.XmlFunctions;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.base.Objects;
import com.google.common.base.Supplier;

public class DynaTracePollConfig<T> extends PollConfig<HttpPollValue, T, DynaTracePollConfig<T>> {

    private String agentName;
    private String agentType;
    private String itemName;
    private String measureName;
    private String reportName;

    public DynaTracePollConfig(AttributeSensor<T> sensor) {
        super(sensor);

        setupFunctions();
    }

    public DynaTracePollConfig(DynaTracePollConfig<T> other) {
        super(other);

        this.reportName = other.getReportName();
        this.measureName = other.getMeasureName();
        this.itemName = other.getItemName();
        this.agentType = other.getAgentType();
        this.agentName = other.getAgentName();

        setupFunctions();
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private void setupFunctions() {
        // Extract the last measurement from the measure data, or zero if not available
        onSuccess(Functions.compose(XmlFunctions.cast((Class) getSensor().getType(), XmlFunctions.defaultValueSupplier(getSensor().getType()).get()),
                Functions.compose(XmlFunctions.xpath(getXPathExpression()),
                    Functions.compose(XmlFunctions.asDocument(),
                        Functions.compose(XmlFunctions.debug("dynatrace poll"),
                            HttpValueFunctions.stringContentsFunction())))));

        // Error always returns zero
        onError((Function) XmlFunctions.defaultValue(getSensor().getType()));
    }

    private Supplier<String> getXPathExpression() {
        return new Supplier<String>(){
            @Override
            public String get() {
                return String.format(
                        "/dashboardreport[@name='%s']/data/chartdashlet/measures/measure[contains(@measure, '%s[%s]') and @default='%s']/@%s",
                        getReportName(), getAgentName(), getAgentType(), getMeasureName(), getItemName());
            }
        };
    }

    public String getMeasureName() {
        return measureName;
    }

    public DynaTracePollConfig<T> measureName(String val) {
        this.measureName = val;
        return this;
    }

    public String getReportName() {
        return reportName;
    }

    public DynaTracePollConfig<T> reportName(String val) {
        this.reportName = val;
        return this;
    }

    public String getItemName() {
        return itemName;
    }

    public DynaTracePollConfig<T> itemName(String val) {
        this.itemName = val;
        return this;
    }

    public String getAgentName() {
        return agentName;
    }

    public DynaTracePollConfig<T> agentName(String val) {
        this.agentName = val;
        return this;
    }

    public String getAgentType() {
        return agentType;
    }

    public DynaTracePollConfig<T> agentType(String val) {
        this.agentType = val;
        return this;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(super.hashCode(), agentName, agentType, reportName, itemName, measureName);
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof DynaTracePollConfig)) {
            return false;
        }
        DynaTracePollConfig<?> o = (DynaTracePollConfig<?>) other;
        return super.equals(o)
                && Objects.equal(agentName, o.agentName)
                && Objects.equal(agentType, o.agentType)
                && Objects.equal(reportName, o.reportName)
                && Objects.equal(itemName, o.itemName)
                && Objects.equal(measureName, o.measureName)
                ;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(getClass())
                .add("agentType", agentName)
                .add("agentType", agentType)
                .add("reportName", reportName)
                .add("itemName", itemName)
                .add("measureName", measureName)
                .toString();
    }

}
