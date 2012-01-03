/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 16, 2011
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.rpc.services.policies;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.rpc.shared.IService;

import com.propertyvista.domain.policy.Policy;
import com.propertyvista.domain.policy.PolicyNode;
import com.propertyvista.domain.policy.dto.EffectivePoliciesDTO;

public interface PolicyManagerService extends IService {

    void effectivePolicies(AsyncCallback<EffectivePoliciesDTO> callback, PolicyNode node);

    void effectivePolicy(AsyncCallback<Policy> callback, PolicyNode node, Policy policyProto);
}
