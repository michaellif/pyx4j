/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 11, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.portal.server.preloader.policy.subpreloaders;

import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.domain.policy.policies.PaymentMethodSelectionPolicy;
import com.propertyvista.portal.server.preloader.policy.util.AbstractPolicyPreloader;

public class PaymentMethodSelectionPolicyPreloader extends AbstractPolicyPreloader<PaymentMethodSelectionPolicy> {

    public PaymentMethodSelectionPolicyPreloader() {
        super(PaymentMethodSelectionPolicy.class);
    }

    @Override
    protected PaymentMethodSelectionPolicy createPolicy(StringBuilder log) {
        PaymentMethodSelectionPolicy policy = EntityFactory.create(PaymentMethodSelectionPolicy.class);

        // -- Accepted In CRM and in residentPortal
        policy.acceptedCash().setValue(Boolean.TRUE);
        policy.acceptedCheck().setValue(Boolean.TRUE);
        policy.acceptedEcheck().setValue(Boolean.TRUE);
        policy.acceptedEFT().setValue(Boolean.TRUE);
        policy.acceptedCreditCard().setValue(Boolean.TRUE);
        policy.acceptedInterac().setValue(Boolean.TRUE);

        // -- Accepted In residentPortal
        policy.residentPortalEcheck().setValue(Boolean.TRUE);
        policy.residentPortalEFT().setValue(Boolean.TRUE);
        policy.residentPortalCreditCard().setValue(Boolean.TRUE);
        policy.residentPortalInterac().setValue(Boolean.TRUE);

        // -- Accepted when cashEquivalent flag on BillingAccount.paymentAccepted is set to CashEquivalent
        policy.cashEquivalentCash().setValue(Boolean.TRUE);
        policy.cashEquivalentCheck().setValue(Boolean.FALSE);
        policy.cashEquivalentEcheck().setValue(Boolean.FALSE);
        policy.cashEquivalentEFT().setValue(Boolean.TRUE);
        policy.cashEquivalentCreditCard().setValue(Boolean.TRUE);
        policy.cashEquivalentInterac().setValue(Boolean.TRUE);

        log.append(policy.getStringView());
        return policy;
    }
}
