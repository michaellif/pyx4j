/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-02-11
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.biz.financial.payment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;

import com.propertyvista.domain.payment.PaymentType;
import com.propertyvista.domain.policy.policies.PaymentTypeSelectionPolicy;
import com.propertyvista.domain.security.common.VistaApplication;
import com.propertyvista.misc.VistaTODO;

public class PaymentAcceptanceUtils {

    @I18n(strategy = I18n.I18nStrategy.IgnoreAll)
    @Transient
    public interface ElectronicPaymentMethodSelection extends PaymentTypeSelectionPolicy {

        IPrimitive<Boolean> electronicPayments();

        IPrimitive<Boolean> notCashEquivalent();
    }

    private static class Acceptance {

        PaymentType paymentType;

        Collection<IPrimitive<Boolean>> require;

        public Acceptance(PaymentType paymentType, IPrimitive<Boolean>... require) {
            super();
            this.paymentType = paymentType;
            this.require = Arrays.asList(require);
        }

        public boolean accept(ElectronicPaymentMethodSelection selection) {
            for (IPrimitive<?> member : require) {
                if (selection.getMember(member.getFieldName()).getValue() != Boolean.TRUE) {
                    return false;
                }
            }
            return true;
        }

    }

    private static Collection<Acceptance> crmRequire = buildPaymentAcceptanceMatrixCrm();

    private static Collection<Acceptance> residentPortalRequire = buildPaymentAcceptanceMatrixPortal();

    static Collection<PaymentType> getAllowedPaymentTypes(VistaApplication vistaApplication, boolean electronicPaymentsAllowed, boolean requireCashEquivalent,
            PaymentTypeSelectionPolicy paymentMethodSelectionPolicy) {
        Collection<PaymentType> allowedPaymentTypes = new ArrayList<PaymentType>();

        Collection<Acceptance> requireAcceptance;
        switch (vistaApplication) {
        case residentPortal:
            requireAcceptance = residentPortalRequire;
            break;
        case crm:
            requireAcceptance = crmRequire;
            break;
        default:
            throw new IllegalArgumentException();
        }

        ElectronicPaymentMethodSelection selection = paymentMethodSelectionPolicy.duplicate(ElectronicPaymentMethodSelection.class);
        selection.electronicPayments().setValue(electronicPaymentsAllowed);
        selection.notCashEquivalent().setValue(!requireCashEquivalent);

        for (Acceptance acceptance : requireAcceptance) {
            if (acceptance.accept(selection)) {
                allowedPaymentTypes.add(acceptance.paymentType);
            }
        }

        return Collections.unmodifiableCollection(allowedPaymentTypes);
    }

    @SuppressWarnings("unchecked")
    private static Collection<Acceptance> buildPaymentAcceptanceMatrixCrm() {
        Collection<Acceptance> require = new ArrayList<Acceptance>();
        ElectronicPaymentMethodSelection p = EntityFactory.getEntityPrototype(ElectronicPaymentMethodSelection.class);

        require.add(new Acceptance(PaymentType.Cash, p.acceptedCash(), p.notCashEquivalent()));
        require.add(new Acceptance(PaymentType.Cash, p.acceptedCash(), p.cashEquivalentCash()));

        require.add(new Acceptance(PaymentType.Check, p.acceptedCheck(), p.notCashEquivalent()));
        require.add(new Acceptance(PaymentType.Check, p.acceptedCheck(), p.cashEquivalentCheck()));

        require.add(new Acceptance(PaymentType.Echeck, p.electronicPayments(), p.acceptedEcheck(), p.notCashEquivalent()));
        require.add(new Acceptance(PaymentType.Echeck, p.electronicPayments(), p.acceptedEcheck(), p.cashEquivalentEcheck()));

        require.add(new Acceptance(PaymentType.CreditCard, p.electronicPayments(), p.acceptedCreditCardMasterCard(), p.notCashEquivalent()));
        require.add(new Acceptance(PaymentType.CreditCard, p.electronicPayments(), p.acceptedCreditCardMasterCard(), p.cashEquivalentCreditCardMasterCard()));

        require.add(new Acceptance(PaymentType.CreditCard, p.electronicPayments(), p.acceptedCreditCardVisa(), p.notCashEquivalent()));
        require.add(new Acceptance(PaymentType.CreditCard, p.electronicPayments(), p.acceptedCreditCardVisa(), p.cashEquivalentCreditCardVisa()));

        require.add(new Acceptance(PaymentType.CreditCard, p.electronicPayments(), p.acceptedVisaDebit(), p.notCashEquivalent()));
        require.add(new Acceptance(PaymentType.CreditCard, p.electronicPayments(), p.acceptedVisaDebit(), p.cashEquivalentVisaDebit()));

        return require;
    }

    @SuppressWarnings("unchecked")
    private static Collection<Acceptance> buildPaymentAcceptanceMatrixPortal() {
        Collection<Acceptance> require = new ArrayList<Acceptance>();
        ElectronicPaymentMethodSelection p = EntityFactory.getEntityPrototype(ElectronicPaymentMethodSelection.class);

        require.add(new Acceptance(PaymentType.Echeck, p.electronicPayments(), p.acceptedEcheck(), p.residentPortalEcheck(), p.notCashEquivalent()));
        require.add(new Acceptance(PaymentType.Echeck, p.electronicPayments(), p.acceptedEcheck(), p.residentPortalEcheck(), p.cashEquivalentEcheck()));

        require.add(new Acceptance(PaymentType.CreditCard, p.electronicPayments(), p.acceptedCreditCardMasterCard(), p.residentPortalCreditCardMasterCard(), p
                .notCashEquivalent()));
        require.add(new Acceptance(PaymentType.CreditCard, p.electronicPayments(), p.acceptedCreditCardMasterCard(), p.residentPortalCreditCardMasterCard(), p
                .cashEquivalentCreditCardMasterCard()));

        require.add(new Acceptance(PaymentType.CreditCard, p.electronicPayments(), p.acceptedCreditCardVisa(), p.residentPortalCreditCardVisa(), p
                .notCashEquivalent()));
        require.add(new Acceptance(PaymentType.CreditCard, p.electronicPayments(), p.acceptedCreditCardVisa(), p.residentPortalCreditCardVisa(), p
                .cashEquivalentCreditCardVisa()));

        require.add(new Acceptance(PaymentType.CreditCard, p.electronicPayments(), p.acceptedVisaDebit(), p.residentPortalVisaDebit(), p.notCashEquivalent()));
        require.add(new Acceptance(PaymentType.CreditCard, p.electronicPayments(), p.acceptedVisaDebit(), p.residentPortalVisaDebit(), p
                .cashEquivalentVisaDebit()));

        require.add(new Acceptance(PaymentType.DirectBanking, p.electronicPayments(), p.acceptedDirectBanking(), p.residentPortalDirectBanking(), p.notCashEquivalent()));
        require.add(new Acceptance(PaymentType.DirectBanking, p.electronicPayments(), p.acceptedDirectBanking(), p.residentPortalDirectBanking(), p.cashEquivalentDirectBanking()));

        if (!VistaTODO.removedForProduction && false) {
            require.add(new Acceptance(PaymentType.Interac, p.electronicPayments(), p.acceptedInterac(), p.residentPortalInterac(), p.notCashEquivalent()));
            require.add(new Acceptance(PaymentType.Interac, p.electronicPayments(), p.acceptedInterac(), p.residentPortalInterac(), p.cashEquivalentInterac()));

        }

        return require;
    }
}
