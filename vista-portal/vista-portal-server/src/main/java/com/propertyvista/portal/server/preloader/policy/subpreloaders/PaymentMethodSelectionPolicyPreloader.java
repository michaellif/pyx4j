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

import com.pyx4j.entity.core.EntityFactory;

import com.propertyvista.domain.policy.policies.PaymentTypeSelectionPolicy;
import com.propertyvista.portal.server.preloader.policy.util.AbstractPolicyPreloader;

public class PaymentMethodSelectionPolicyPreloader extends AbstractPolicyPreloader<PaymentTypeSelectionPolicy> {

    public PaymentMethodSelectionPolicyPreloader() {
        super(PaymentTypeSelectionPolicy.class);
    }

    @Override
    protected PaymentTypeSelectionPolicy createPolicy(StringBuilder log) {
        PaymentTypeSelectionPolicy policy = EntityFactory.create(PaymentTypeSelectionPolicy.class);

        // -- Accepted In CRM and in residentPortal
        policy.acceptedCash().setValue(Boolean.TRUE);
        policy.acceptedCheck().setValue(Boolean.TRUE);
        policy.acceptedEcheck().setValue(Boolean.TRUE);
        policy.acceptedDirectBanking().setValue(Boolean.TRUE);
        policy.acceptedCreditCardMasterCard().setValue(Boolean.TRUE);
        policy.acceptedCreditCardVisa().setValue(Boolean.TRUE);
        policy.acceptedVisaDebit().setValue(Boolean.TRUE);
        policy.acceptedInterac().setValue(Boolean.TRUE);

        // -- Accepted In resident Portal
        policy.residentPortalEcheck().setValue(Boolean.TRUE);
        policy.residentPortalDirectBanking().setValue(Boolean.TRUE);
        policy.residentPortalCreditCardMasterCard().setValue(Boolean.TRUE);
        policy.residentPortalCreditCardVisa().setValue(Boolean.TRUE);
        policy.residentPortalVisaDebit().setValue(Boolean.TRUE);
        policy.residentPortalInterac().setValue(Boolean.TRUE);

        // -- Accepted In prospect Portal
        policy.prospectEcheck().setValue(Boolean.TRUE);
        policy.prospectDirectBanking().setValue(Boolean.TRUE);
        policy.prospectCreditCardMasterCard().setValue(Boolean.TRUE);
        policy.prospectCreditCardVisa().setValue(Boolean.TRUE);
        policy.prospectVisaDebit().setValue(Boolean.TRUE);
        policy.prospectInterac().setValue(Boolean.TRUE);

        // -- Accepted when cashEquivalent flag on BillingAccount.paymentAccepted is set to CashEquivalent
        policy.cashEquivalentCash().setValue(Boolean.TRUE);
        policy.cashEquivalentCheck().setValue(Boolean.FALSE);
        policy.cashEquivalentEcheck().setValue(Boolean.FALSE);
        policy.cashEquivalentDirectBanking().setValue(Boolean.TRUE);
        policy.cashEquivalentCreditCardMasterCard().setValue(Boolean.TRUE);
        policy.cashEquivalentCreditCardVisa().setValue(Boolean.TRUE);
        policy.cashEquivalentVisaDebit().setValue(Boolean.TRUE);
        policy.cashEquivalentInterac().setValue(Boolean.TRUE);

        log.append(policy.getStringView());
        return policy;
    }
}
