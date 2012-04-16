/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-04-04
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.biz.policy;

import com.propertyvista.domain.policy.framework.Policy;
import com.propertyvista.domain.policy.framework.PolicyNode;

public class PolicyFacadeImpl implements PolicyFacade {

    @Override
    public <POLICY extends Policy> POLICY obtainEffectivePolicy(PolicyNode node, final Class<POLICY> policyClass) {
        return PolicyManager.obtainEffectivePolicy(node, policyClass);
    }

}
