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
package brooklyn.entity.rebind.dto;

import java.io.Serializable;
import java.util.Map;

import brooklyn.entity.basic.Entities;
import brooklyn.mementos.PolicyMemento;

import com.google.common.base.Objects.ToStringHelper;
import com.google.common.collect.Maps;

/**
 * The persisted state of a location.
 * 
 * @author aled
 */
public class BasicPolicyMemento extends AbstractMemento implements PolicyMemento, Serializable {

    private static final long serialVersionUID = -4025337943126838761L;
    
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder extends AbstractMemento.Builder<Builder> {
        protected Map<String,Object> flags = Maps.newLinkedHashMap();
        
        public Builder from(PolicyMemento other) {
            super.from(other);
            flags.putAll(other.getFlags());
            return this;
        }
        public Builder flags(Map<String,?> vals) {
            flags.putAll(vals); return this;
        }
        public PolicyMemento build() {
            return new BasicPolicyMemento(this);
        }
    }
    
	private Map<String,Object> flags;
    private Map<String, Object> fields;

    // Trusts the builder to not mess around with mutability after calling build()
	protected BasicPolicyMemento(Builder builder) {
	    flags = toPersistedMap(builder.flags);
	}
	
    @Override
    protected void setCustomFields(Map<String, Object> fields) {
        this.fields = toPersistedMap(fields);
    }
    
    @Override
    public Map<String, Object> getCustomFields() {
        return fromPersistedMap(fields);
    }

    @Override
    public Map<String, Object> getFlags() {
		return fromPersistedMap(flags);
	}
    
    @Override
    protected ToStringHelper newVerboseStringHelper() {
        return super.newVerboseStringHelper().add("flags", Entities.sanitize(getFlags()));
    }
}
