/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 23, 2014
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.biz.policy;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.shared.utils.EntityGraph;

import com.propertyvista.config.VistaDeployment;
import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.payment.CreditCardInfo;
import com.propertyvista.domain.payment.CreditCardInfo.CreditCardType;
import com.propertyvista.domain.payment.PaymentType;
import com.propertyvista.domain.policy.framework.OrganizationPoliciesNode;
import com.propertyvista.domain.policy.policies.IdAssignmentPolicy;
import com.propertyvista.domain.policy.policies.domain.IdAssignmentPaymentType;

class PaymentsIdAssignmentManager {

    void assignDocumentNumber(PaymentRecord paymentRecord) {
        IdAssignmentPolicy policy = ServerSideFactory.create(PolicyFacade.class).obtainEffectivePolicy(EntityFactory.create(OrganizationPoliciesNode.class),
                IdAssignmentPolicy.class);

        IdAssignmentPaymentType paymentTypes = policy.paymentTypes();

        EntityGraph.setDefaults(paymentTypes, getPaymentTypesDefaults()//
                , paymentTypes.autopayPrefix()//
                , paymentTypes.oneTimePrefix()//
                , paymentTypes.cashPrefix()//
                , paymentTypes.checkPrefix()//
                , paymentTypes.echeckPrefix()//
                , paymentTypes.directBankingPrefix()//
                , paymentTypes.creditCardVisaPrefix()//
                , paymentTypes.creditCardMasterCardPrefix()//
                , paymentTypes.visaDebitPrefix()//
                );

        StringBuilder b = new StringBuilder();
        switch (paymentRecord.paymentMethod().type().getValue()) {
        case Cash:
            b.append(paymentTypes.cashPrefix().getStringView());
            break;
        case Check:
            b.append(paymentTypes.checkPrefix().getStringView());
            break;
        case CreditCard:
            CreditCardType cardType = paymentRecord.paymentMethod().details().<CreditCardInfo> cast().cardType().getValue();
            switch (cardType) {
            case Visa:
                b.append(paymentTypes.creditCardVisaPrefix().getStringView());
                break;
            case VisaDebit:
                b.append(paymentTypes.visaDebitPrefix().getStringView());
                break;
            case MasterCard:
                b.append(paymentTypes.creditCardMasterCardPrefix().getStringView());
                break;
            }
            break;
        case DirectBanking:
            b.append(paymentTypes.directBankingPrefix().getStringView());
            break;
        case Echeck:
            b.append(paymentTypes.echeckPrefix().getStringView());
            break;
        default:
            break;
        }

        if (!paymentRecord.preauthorizedPayment().isNull()) {
            if (!paymentTypes.autopayPrefix().isNull()) {
                b.append(paymentTypes.autopayPrefix().getStringView());
            }
        } else {
            if (!paymentTypes.oneTimePrefix().isNull()) {
                b.append(paymentTypes.oneTimePrefix().getStringView());
            }
        }

        paymentRecord.yardiDocumentNumber().setValue(b.toString());
    }

    IdAssignmentPaymentType getPaymentTypesDefaults() {
        IdAssignmentPaymentType def = EntityFactory.create(IdAssignmentPaymentType.class);
        def.cashPrefix().setValue(PaymentType.Cash.name());
        def.checkPrefix().setValue(PaymentType.Check.name());

        switch (VistaDeployment.getCurrentPmc().features().countryOfOperation().getValue()) {
        case Canada:
            def.echeckPrefix().setValue("eCheque (EFT)");
            break;
        case US:
            def.echeckPrefix().setValue("eCheck (ACH)");
            break;
        default:
            def.echeckPrefix().setValue("eCheck");
            break;
        }

        def.directBankingPrefix().setValue(PaymentType.DirectBanking.name());
        def.creditCardVisaPrefix().setValue(CreditCardType.Visa.name());
        def.creditCardMasterCardPrefix().setValue(CreditCardType.MasterCard.name());
        def.visaDebitPrefix().setValue(CreditCardType.VisaDebit.name());
        return def;
    }

}
