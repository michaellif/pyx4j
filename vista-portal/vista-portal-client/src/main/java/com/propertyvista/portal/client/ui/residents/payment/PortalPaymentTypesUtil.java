/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-02-12
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.client.ui.residents.payment;

import java.util.EnumSet;
import java.util.Set;

import com.pyx4j.security.shared.SecurityController;

import com.propertyvista.domain.payment.PaymentType;
import com.propertyvista.domain.security.VistaCustomerPaymentTypeBehavior;

public class PortalPaymentTypesUtil {

    public static Set<PaymentType> avalableInProfile() {
        Set<PaymentType> allowedTypes = EnumSet.noneOf(PaymentType.class);

        for (VistaCustomerPaymentTypeBehavior behavior : VistaCustomerPaymentTypeBehavior.values()) {
            if (SecurityController.checkBehavior(behavior)) {
                switch (behavior) {
                case CreditCardPaymentsAllowed:
                    allowedTypes.add(PaymentType.CreditCard);
                    break;
                case EcheckPaymentsAllowed:
                    allowedTypes.add(PaymentType.Echeck);
                    break;
                case InteracPaymentsAllowed:
                    break;
                case DirectBankingPaymentsAllowed:
                    break;
                }
            }
        }
        return allowedTypes;
    }

    public static Set<PaymentType> getAllowedPaymentTypes() {
        // set allowed for the lease payments types selection:
        Set<PaymentType> allowedTypes = EnumSet.noneOf(PaymentType.class);

        for (VistaCustomerPaymentTypeBehavior behavior : VistaCustomerPaymentTypeBehavior.values()) {
            if (SecurityController.checkBehavior(behavior)) {
                switch (behavior) {
                case CreditCardPaymentsAllowed:
                    allowedTypes.add(PaymentType.CreditCard);
                    break;
                case EcheckPaymentsAllowed:
                    allowedTypes.add(PaymentType.Echeck);
                    break;
                case InteracPaymentsAllowed:
                    allowedTypes.add(PaymentType.Interac);
                    break;
                case DirectBankingPaymentsAllowed:
                    allowedTypes.add(PaymentType.DirectBanking);
                    break;
                }
            }
        }
        return allowedTypes;
    }

    public static boolean isPreauthorizedPaumentAllowed() {
        Set<PaymentType> available = getAllowedPaymentTypes();
        available.retainAll(EnumSet.of(PaymentType.Echeck, PaymentType.CreditCard));
        return !available.isEmpty();
    }
}
