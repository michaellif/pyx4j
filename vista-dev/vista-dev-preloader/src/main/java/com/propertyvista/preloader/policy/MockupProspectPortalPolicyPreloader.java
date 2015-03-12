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
 */
package com.propertyvista.preloader.policy;

import java.math.BigDecimal;

import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.entity.core.EntityFactory;

import com.propertyvista.biz.preloader.policy.AbstractPolicyPreloader;
import com.propertyvista.domain.customizations.CountryOfOperation;
import com.propertyvista.domain.policy.policies.ProspectPortalPolicy;
import com.propertyvista.domain.policy.policies.ProspectPortalPolicy.FeePayment;
import com.propertyvista.shared.config.VistaFeatures;

public class MockupProspectPortalPolicyPreloader extends AbstractPolicyPreloader<ProspectPortalPolicy> {

    public MockupProspectPortalPolicyPreloader() {
        super(ProspectPortalPolicy.class);
    }

    @Override
    protected ProspectPortalPolicy createPolicy(StringBuilder log) {
        ProspectPortalPolicy policy = EntityFactory.create(ProspectPortalPolicy.class);

        policy.maxExactMatchUnits().setValue(3);
        policy.maxPartialMatchUnits().setValue(5);
        if (ApplicationMode.isDemo()) {
            policy.unitAvailabilitySpan().setValue(99999);
        } else {
            policy.unitAvailabilitySpan().setValue(20);
        }

        // In case of demo and Canadian Buildings, Prospect Portal fee do not apply
        boolean avoidFee = (ApplicationMode.isDemo() && VistaFeatures.instance().countryOfOperation().equals(CountryOfOperation.Canada));
        if (avoidFee) {
            policy.feePayment().setValue(FeePayment.none);
        } else {
            policy.feePayment().setValue(FeePayment.perLease);
            policy.feeAmount().setValue(new BigDecimal(88.88));
        }
        log.append(policy.getStringView());
        return policy;
    }

}
