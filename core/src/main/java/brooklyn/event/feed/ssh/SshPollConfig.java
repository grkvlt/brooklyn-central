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
package brooklyn.event.feed.ssh;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;

import javax.annotation.Nullable;

import brooklyn.event.AttributeSensor;
import brooklyn.event.feed.PollConfig;

import com.google.common.base.Predicate;
import com.google.common.collect.Maps;

public class SshPollConfig<T> extends PollConfig<SshPollValue, T, SshPollConfig<T>> {

    private String command;
    private Map<String,String> env = Maps.newLinkedHashMap();

    public static final Predicate<SshPollValue> DEFAULT_SUCCESS = new Predicate<SshPollValue>() {
        @Override
        public boolean apply(@Nullable SshPollValue input) {
            return input != null && input.getExitStatus() == 0;
        }};

    public SshPollConfig(AttributeSensor<T> sensor) {
        super(sensor);
        super.checkSuccess(DEFAULT_SUCCESS);
    }

    public SshPollConfig(SshPollConfig<T> other) {
        super(other);
        command = other.command;
        env = other.env;
    }
    
    public String getCommand() {
        return command;
    }
    
    public Map<String, String> getEnv() {
        return env;
    }

    public SshPollConfig<T> command(String val) {
        this.command = val;
        return this;
    }

    public SshPollConfig<T> env(String key, String val) {
        env.put(checkNotNull(key, "key"), checkNotNull(val, "val"));
        return this;
    }
    
    public SshPollConfig<T> env(Map<String,String> val) {
        for (Map.Entry<String, String> entry : checkNotNull(val, "map").entrySet()) {
            env(entry.getKey(), entry.getValue());
        }
        return this;
    }
}
