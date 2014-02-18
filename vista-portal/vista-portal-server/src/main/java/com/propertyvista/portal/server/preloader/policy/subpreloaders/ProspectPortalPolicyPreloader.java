/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 17, 2014
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.portal.server.preloader.policy.subpreloaders;

import java.math.BigDecimal;

import com.pyx4j.entity.core.EntityFactory;

import com.propertyvista.domain.policy.policies.ProspectPortalPolicy;
import com.propertyvista.domain.policy.policies.ProspectPortalPolicy.FeePayment;
import com.propertyvista.portal.server.preloader.policy.util.AbstractPolicyPreloader;

public class ProspectPortalPolicyPreloader extends AbstractPolicyPreloader<ProspectPortalPolicy> {

    public ProspectPortalPolicyPreloader() {
        super(ProspectPortalPolicy.class);
    }

    @Override
    protected ProspectPortalPolicy createPolicy(StringBuilder log) {
        ProspectPortalPolicy policy = EntityFactory.create(ProspectPortalPolicy.class);

        policy.maxExactMatchUnits().setValue(3);
        policy.maxPartialMatchUnits().setValue(5);
        policy.unitAvailabilitySpan().setValue(20);

        policy.feePayment().setValue(FeePayment.none);
        policy.feeAmount().setValue(BigDecimal.ZERO);

        log.append(policy.getStringView());
        return policy;
    }

}
