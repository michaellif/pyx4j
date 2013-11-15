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

import com.propertyvista.domain.payment.CreditCardInfo.CreditCardType;
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

    private static class PaymentTypeAcceptance {

        PaymentType paymentType;

        Collection<IPrimitive<Boolean>> require;

        public PaymentTypeAcceptance(PaymentType paymentType, IPrimitive<Boolean>... require) {
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

    private static class CardTypeAcceptance {

        CreditCardType cardType;

        Collection<IPrimitive<Boolean>> require;

        public CardTypeAcceptance(CreditCardType paymentType, IPrimitive<Boolean>... require) {
            super();
            this.cardType = paymentType;
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

    private static Collection<PaymentTypeAcceptance> crmPaymentTypeRequire = buildPaymentAcceptanceMatrixCrm();

    private static Collection<PaymentTypeAcceptance> residentPortalPaymentTypeRequire = buildPaymentAcceptanceMatrixPortal();

    private static Collection<CardTypeAcceptance> crmCardRequire = buildCardAcceptanceMatrixCrm();

    private static Collection<CardTypeAcceptance> residentPortalCardRequire = VistaTODO.convenienceFeeEnabled ? buildCardAcceptanceMatrixWithConvenienceFeePortal()
            : buildCardAcceptanceMatrixPortal();

    private static Collection<CardTypeAcceptance> residentPortalCardWithoutConvenienceFee = VistaTODO.convenienceFeeEnabled ? buildCardAcceptanceMatrixWithoutConvenienceFeePortal()
            : Collections.<CardTypeAcceptance> emptyList();

    static Collection<PaymentType> getAllowedPaymentTypes(VistaApplication vistaApplication, boolean electronicPaymentsAllowed, boolean requireCashEquivalent,
            PaymentTypeSelectionPolicy paymentMethodSelectionPolicy) {

        Collection<PaymentTypeAcceptance> requireAcceptance;
        switch (vistaApplication) {
        case resident:
            requireAcceptance = residentPortalPaymentTypeRequire;
            break;
        case crm:
            requireAcceptance = crmPaymentTypeRequire;
            break;
        default:
            throw new IllegalArgumentException();
        }

        ElectronicPaymentMethodSelection selection = paymentMethodSelectionPolicy.duplicate(ElectronicPaymentMethodSelection.class);
        selection.electronicPayments().setValue(electronicPaymentsAllowed);
        selection.notCashEquivalent().setValue(!requireCashEquivalent);

        Collection<PaymentType> allowedPaymentTypes = new ArrayList<PaymentType>();
        for (PaymentTypeAcceptance acceptance : requireAcceptance) {
            if (acceptance.accept(selection)) {
                allowedPaymentTypes.add(acceptance.paymentType);
            }
        }

        return Collections.unmodifiableCollection(allowedPaymentTypes);
    }

    public static Collection<CreditCardType> getAllowedCreditCardTypes(VistaApplication vistaApplication, boolean requireCashEquivalent,
            PaymentTypeSelectionPolicy paymentMethodSelectionPolicy, boolean forConvenienceFeeOnly) {
        Collection<CardTypeAcceptance> requireAcceptance;
        Collection<CardTypeAcceptance> feeAcceptance;
        switch (vistaApplication) {
        case resident:
            requireAcceptance = residentPortalCardRequire;
            feeAcceptance = residentPortalCardWithoutConvenienceFee;
            break;
        case crm:
            requireAcceptance = crmCardRequire;
            feeAcceptance = Collections.<CardTypeAcceptance> emptyList();
            break;
        default:
            throw new IllegalArgumentException();
        }

        ElectronicPaymentMethodSelection selection = paymentMethodSelectionPolicy.duplicate(ElectronicPaymentMethodSelection.class);
        selection.notCashEquivalent().setValue(!requireCashEquivalent);

        Collection<CreditCardType> allowedPaymentTypes = new ArrayList<CreditCardType>();
        for (CardTypeAcceptance acceptance : requireAcceptance) {
            if (acceptance.accept(selection)) {
                allowedPaymentTypes.add(acceptance.cardType);
            }
        }
        if (forConvenienceFeeOnly) {
            // From accepted cards select the one the fee are no accepted by PMC, e.g. for Vista Convenience Fee
            Collection<CreditCardType> convenienceFeePaymentTypes = new ArrayList<CreditCardType>();
            for (CardTypeAcceptance acceptance : feeAcceptance) {
                if (allowedPaymentTypes.contains(acceptance.cardType) && (!acceptance.accept(selection))) {
                    convenienceFeePaymentTypes.add(acceptance.cardType);
                }
            }
            return Collections.unmodifiableCollection(convenienceFeePaymentTypes);
        } else {
            return Collections.unmodifiableCollection(allowedPaymentTypes);
        }
    }

    @SuppressWarnings("unchecked")
    private static Collection<PaymentTypeAcceptance> buildPaymentAcceptanceMatrixCrm() {
        Collection<PaymentTypeAcceptance> require = new ArrayList<PaymentTypeAcceptance>();
        ElectronicPaymentMethodSelection p = EntityFactory.getEntityPrototype(ElectronicPaymentMethodSelection.class);

        require.add(new PaymentTypeAcceptance(PaymentType.Cash, p.acceptedCash(), p.notCashEquivalent()));
        require.add(new PaymentTypeAcceptance(PaymentType.Cash, p.acceptedCash(), p.cashEquivalentCash()));

        require.add(new PaymentTypeAcceptance(PaymentType.Check, p.acceptedCheck(), p.notCashEquivalent()));
        require.add(new PaymentTypeAcceptance(PaymentType.Check, p.acceptedCheck(), p.cashEquivalentCheck()));

        require.add(new PaymentTypeAcceptance(PaymentType.Echeck, p.electronicPayments(), p.acceptedEcheck(), p.notCashEquivalent()));
        require.add(new PaymentTypeAcceptance(PaymentType.Echeck, p.electronicPayments(), p.acceptedEcheck(), p.cashEquivalentEcheck()));

        require.add(new PaymentTypeAcceptance(PaymentType.CreditCard, p.electronicPayments(), p.acceptedCreditCardMasterCard(), p.notCashEquivalent()));
        require.add(new PaymentTypeAcceptance(PaymentType.CreditCard, p.electronicPayments(), p.acceptedCreditCardMasterCard(), p
                .cashEquivalentCreditCardMasterCard()));

        require.add(new PaymentTypeAcceptance(PaymentType.CreditCard, p.electronicPayments(), p.acceptedCreditCardVisa(), p.notCashEquivalent()));
        require.add(new PaymentTypeAcceptance(PaymentType.CreditCard, p.electronicPayments(), p.acceptedCreditCardVisa(), p.cashEquivalentCreditCardVisa()));

        require.add(new PaymentTypeAcceptance(PaymentType.CreditCard, p.electronicPayments(), p.acceptedVisaDebit(), p.notCashEquivalent()));
        require.add(new PaymentTypeAcceptance(PaymentType.CreditCard, p.electronicPayments(), p.acceptedVisaDebit(), p.cashEquivalentVisaDebit()));

        return require;
    }

    @SuppressWarnings("unchecked")
    private static Collection<PaymentTypeAcceptance> buildPaymentAcceptanceMatrixPortal() {
        Collection<PaymentTypeAcceptance> require = new ArrayList<PaymentTypeAcceptance>();
        ElectronicPaymentMethodSelection p = EntityFactory.getEntityPrototype(ElectronicPaymentMethodSelection.class);

        require.add(new PaymentTypeAcceptance(PaymentType.Echeck, p.electronicPayments(), p.acceptedEcheck(), p.residentPortalEcheck(), p.notCashEquivalent()));
        require.add(new PaymentTypeAcceptance(PaymentType.Echeck, p.electronicPayments(), p.acceptedEcheck(), p.residentPortalEcheck(), p
                .cashEquivalentEcheck()));

        require.add(new PaymentTypeAcceptance(PaymentType.CreditCard, p.electronicPayments(), p.acceptedCreditCardMasterCard(), p
                .residentPortalCreditCardMasterCard(), p.notCashEquivalent()));
        require.add(new PaymentTypeAcceptance(PaymentType.CreditCard, p.electronicPayments(), p.acceptedCreditCardMasterCard(), p
                .residentPortalCreditCardMasterCard(), p.cashEquivalentCreditCardMasterCard()));

        require.add(new PaymentTypeAcceptance(PaymentType.CreditCard, p.electronicPayments(), p.acceptedCreditCardVisa(), p.residentPortalCreditCardVisa(), p
                .notCashEquivalent()));
        require.add(new PaymentTypeAcceptance(PaymentType.CreditCard, p.electronicPayments(), p.acceptedCreditCardVisa(), p.residentPortalCreditCardVisa(), p
                .cashEquivalentCreditCardVisa()));

        require.add(new PaymentTypeAcceptance(PaymentType.CreditCard, p.electronicPayments(), p.acceptedVisaDebit(), p.residentPortalVisaDebit(), p
                .notCashEquivalent()));
        require.add(new PaymentTypeAcceptance(PaymentType.CreditCard, p.electronicPayments(), p.acceptedVisaDebit(), p.residentPortalVisaDebit(), p
                .cashEquivalentVisaDebit()));

        require.add(new PaymentTypeAcceptance(PaymentType.DirectBanking, p.electronicPayments(), p.acceptedDirectBanking(), p.residentPortalDirectBanking(), p
                .notCashEquivalent()));
        require.add(new PaymentTypeAcceptance(PaymentType.DirectBanking, p.electronicPayments(), p.acceptedDirectBanking(), p.residentPortalDirectBanking(), p
                .cashEquivalentDirectBanking()));

        if (!VistaTODO.removedForProduction && false) {
            require.add(new PaymentTypeAcceptance(PaymentType.Interac, p.electronicPayments(), p.acceptedInterac(), p.residentPortalInterac(), p
                    .notCashEquivalent()));
            require.add(new PaymentTypeAcceptance(PaymentType.Interac, p.electronicPayments(), p.acceptedInterac(), p.residentPortalInterac(), p
                    .cashEquivalentInterac()));

        }

        return require;
    }

    @SuppressWarnings("unchecked")
    private static Collection<CardTypeAcceptance> buildCardAcceptanceMatrixCrm() {
        Collection<CardTypeAcceptance> require = new ArrayList<CardTypeAcceptance>();
        ElectronicPaymentMethodSelection p = EntityFactory.getEntityPrototype(ElectronicPaymentMethodSelection.class);

        require.add(new CardTypeAcceptance(CreditCardType.MasterCard, p.acceptedCreditCardMasterCard(), p.notCashEquivalent()));
        require.add(new CardTypeAcceptance(CreditCardType.MasterCard, p.acceptedCreditCardMasterCard(), p.cashEquivalentCreditCardMasterCard()));

        require.add(new CardTypeAcceptance(CreditCardType.Visa, p.acceptedCreditCardVisa(), p.notCashEquivalent()));
        require.add(new CardTypeAcceptance(CreditCardType.Visa, p.acceptedCreditCardVisa(), p.cashEquivalentCreditCardVisa()));

        require.add(new CardTypeAcceptance(CreditCardType.VisaDebit, p.acceptedVisaDebit(), p.notCashEquivalent()));
        require.add(new CardTypeAcceptance(CreditCardType.VisaDebit, p.acceptedVisaDebit(), p.cashEquivalentVisaDebit()));

        return require;
    }

    @SuppressWarnings("unchecked")
    private static Collection<CardTypeAcceptance> buildCardAcceptanceMatrixPortal() {
        Collection<CardTypeAcceptance> require = new ArrayList<CardTypeAcceptance>();
        ElectronicPaymentMethodSelection p = EntityFactory.getEntityPrototype(ElectronicPaymentMethodSelection.class);

        require.add(new CardTypeAcceptance(CreditCardType.MasterCard, p.acceptedCreditCardMasterCard(), p.residentPortalCreditCardMasterCard(), p
                .notCashEquivalent()));
        require.add(new CardTypeAcceptance(CreditCardType.MasterCard, p.acceptedCreditCardMasterCard(), p.residentPortalCreditCardMasterCard(), p
                .cashEquivalentCreditCardMasterCard()));

        require.add(new CardTypeAcceptance(CreditCardType.Visa, p.acceptedCreditCardVisa(), p.residentPortalCreditCardVisa(), p.notCashEquivalent()));
        require.add(new CardTypeAcceptance(CreditCardType.Visa, p.acceptedCreditCardVisa(), p.residentPortalCreditCardVisa(), p.cashEquivalentCreditCardVisa()));

        require.add(new CardTypeAcceptance(CreditCardType.VisaDebit, p.acceptedVisaDebit(), p.residentPortalVisaDebit(), p.notCashEquivalent()));
        require.add(new CardTypeAcceptance(CreditCardType.VisaDebit, p.acceptedVisaDebit(), p.residentPortalVisaDebit(), p.cashEquivalentVisaDebit()));

        return require;
    }

    @SuppressWarnings("unchecked")
    private static Collection<CardTypeAcceptance> buildCardAcceptanceMatrixWithConvenienceFeePortal() {
        Collection<CardTypeAcceptance> require = new ArrayList<CardTypeAcceptance>();
        ElectronicPaymentMethodSelection p = EntityFactory.getEntityPrototype(ElectronicPaymentMethodSelection.class);

        require.add(new CardTypeAcceptance(CreditCardType.MasterCard, p.notCashEquivalent()));
        require.add(new CardTypeAcceptance(CreditCardType.MasterCard, p.cashEquivalentCreditCardMasterCard()));

        require.add(new CardTypeAcceptance(CreditCardType.Visa, p.notCashEquivalent()));
        require.add(new CardTypeAcceptance(CreditCardType.Visa, p.cashEquivalentCreditCardVisa()));

        require.add(new CardTypeAcceptance(CreditCardType.VisaDebit, p.notCashEquivalent()));
        require.add(new CardTypeAcceptance(CreditCardType.VisaDebit, p.cashEquivalentVisaDebit()));

        return require;
    }

    @SuppressWarnings("unchecked")
    private static Collection<CardTypeAcceptance> buildCardAcceptanceMatrixWithoutConvenienceFeePortal() {
        Collection<CardTypeAcceptance> require = new ArrayList<CardTypeAcceptance>();
        ElectronicPaymentMethodSelection p = EntityFactory.getEntityPrototype(ElectronicPaymentMethodSelection.class);

        require.add(new CardTypeAcceptance(CreditCardType.MasterCard, p.acceptedCreditCardMasterCard(), p.residentPortalCreditCardMasterCard()));

        require.add(new CardTypeAcceptance(CreditCardType.Visa, p.acceptedCreditCardVisa(), p.residentPortalCreditCardVisa()));

        require.add(new CardTypeAcceptance(CreditCardType.VisaDebit, p.acceptedVisaDebit(), p.residentPortalVisaDebit()));

        return require;
    }

}
