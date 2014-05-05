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
package brooklyn.entity.rebind;

import java.util.Collections;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import brooklyn.entity.rebind.dto.MementosGenerators;
import brooklyn.mementos.PolicyMemento;
import brooklyn.policy.basic.AbstractPolicy;

public class BasicPolicyRebindSupport implements RebindSupport<PolicyMemento> {

    private static final Logger LOG = LoggerFactory.getLogger(BasicPolicyRebindSupport.class);
    
    private final AbstractPolicy policy;
    
    public BasicPolicyRebindSupport(AbstractPolicy policy) {
        this.policy = policy;
    }
    
    @Override
    public PolicyMemento getMemento() {
        return getMementoWithProperties(Collections.<String,Object>emptyMap());
    }

    protected PolicyMemento getMementoWithProperties(Map<String,?> props) {
        PolicyMemento memento = MementosGenerators.newPolicyMementoBuilder(policy).customFields(props).build();
    	if (LOG.isTraceEnabled()) LOG.trace("Creating memento for policy: {}", memento.toVerboseString());
    	return memento;
    }

    @Override
    public void reconstruct(RebindContext rebindContext, PolicyMemento memento) {
    	if (LOG.isTraceEnabled()) LOG.trace("Reconstructing policy: {}", memento.toVerboseString());

    	// Note that the flags have been set in the constructor
        policy.setName(memento.getDisplayName());
        
        doReconsruct(rebindContext, memento);
    }

    /**
     * For overriding, to give custom reconsruct behaviour.
     */
    protected void doReconsruct(RebindContext rebindContext, PolicyMemento memento) {
        // default is no-op
    }
}
