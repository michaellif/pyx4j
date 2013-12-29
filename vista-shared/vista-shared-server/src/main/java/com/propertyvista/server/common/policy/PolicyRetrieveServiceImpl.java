/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-03-06
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.server.common.policy;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.core.IEntity;

import com.propertyvista.biz.policy.PolicyFacade;
import com.propertyvista.domain.policy.framework.Policy;
import com.propertyvista.domain.policy.framework.PolicyNode;
import com.propertyvista.portal.rpc.shared.services.PolicyRetrieveService;

public abstract class PolicyRetrieveServiceImpl implements PolicyRetrieveService {

    @SuppressWarnings("unchecked")
    @Override
    public void obtainEffectivePolicy(AsyncCallback<Policy> callback, PolicyNode node, Policy policyProto) {
        if (policyProto == null) {
            throw new Error("A policy prototype was not provided");
        }
        callback.onSuccess(ServerSideFactory.create(PolicyFacade.class).obtainEffectivePolicy(node, (Class<Policy>) policyProto.getValueClass()));
    }

    @SuppressWarnings("unchecked")
    @Override
    public void obtainHierarchicalEffectivePolicy(AsyncCallback<Policy> callback, IEntity entity, Policy policyProto) {
        if (policyProto == null) {
            throw new Error("A policy prototype was not provided");
        }
        callback.onSuccess(ServerSideFactory.create(PolicyFacade.class).obtainHierarchicalEffectivePolicy(entity, (Class<Policy>) policyProto.getValueClass()));
    }
}
