/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-03-30
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.server.ptapp.services;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.security.shared.SecurityViolationException;

import com.propertyvista.domain.policy.framework.Policy;
import com.propertyvista.domain.policy.framework.PolicyNode;
import com.propertyvista.domain.policy.policies.TenantsAccessiblePolicy;
import com.propertyvista.portal.rpc.ptapp.services.PtPolicyRetrieveService;
import com.propertyvista.server.common.policy.PolicyRetrieveServiceImpl;

public class PtPolicyRetrieveServiceImpl extends PolicyRetrieveServiceImpl implements PtPolicyRetrieveService {

    @Override
    public void obtainEffectivePolicy(AsyncCallback<Policy> callback, PolicyNode node, Policy policyProto) {
        if (policyProto instanceof TenantsAccessiblePolicy) {
            throw new SecurityViolationException("Permission denied");
        }
        super.obtainEffectivePolicy(callback, node, policyProto);
    }

}
