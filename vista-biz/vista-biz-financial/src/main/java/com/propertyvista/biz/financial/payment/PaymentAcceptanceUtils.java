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
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;

import com.propertyvista.domain.financial.MerchantAccount.ElectronicPaymentSetup;
import com.propertyvista.domain.payment.CreditCardInfo.CreditCardType;
import com.propertyvista.domain.payment.PaymentType;
import com.propertyvista.domain.policy.policies.PaymentTypeSelectionPolicy;
import com.propertyvista.domain.security.common.VistaApplication;
import com.propertyvista.misc.VistaTODO;

public class PaymentAcceptanceUtils {

    @I18n(strategy = I18n.I18nStrategy.IgnoreAll)
    @Transient
    public interface ElectronicPaymentMethodSelection extends PaymentTypeSelectionPolicy {

        ElectronicPaymentSetup setup();

        IPrimitive<Boolean> notCashEquivalent();
    }

    private static abstract class BooleanCriterion {

        abstract boolean accept(IEntity selection);

        protected boolean isTrue(IEntity selection, IPrimitive<Boolean> member) {
            return (selection.getMember(member.getPath()).getValue() == Boolean.TRUE);
        }
    }

    private static class CriterionAnd extends BooleanCriterion {

        Collection<IPrimitive<Boolean>> require;

        CriterionAnd(Collection<IPrimitive<Boolean>> require) {
            this.require = require;
        }

        @Override
        boolean accept(IEntity selection) {
            for (IPrimitive<Boolean> member : require) {
                if (!isTrue(selection, member)) {
                    return false;
                }
            }
            return true;
        }

    }

    private static class CriterionOr extends BooleanCriterion {

        IPrimitive<Boolean> require1;

        IPrimitive<Boolean> require2;

        CriterionOr(IPrimitive<Boolean> require1, IPrimitive<Boolean> require2) {
            this.require1 = require1;
            this.require2 = require2;
        }

        @Override
        public boolean accept(IEntity selection) {
            return isTrue(selection, require1) || isTrue(selection, require2);
        }

    }

    private static class CriterionNot extends BooleanCriterion {

        IPrimitive<Boolean> require;

        CriterionNot(IPrimitive<Boolean> require) {
            this.require = require;
        }

        @Override
        public boolean accept(IEntity selection) {
            return !isTrue(selection, require);
        }

    }

    @SuppressWarnings("unchecked")
    private static class RequireBuilder<E extends RequireBuilder<?>> {

        Collection<BooleanCriterion> require;

        RequireBuilder() {
            this.require = new ArrayList<BooleanCriterion>();
        }

        E and(IPrimitive<Boolean>... require) {
            this.require.add(new CriterionAnd(Arrays.asList(require)));
            return (E) this;
        }

        E or(IPrimitive<Boolean> require1, IPrimitive<Boolean> require2) {
            this.require.add(new CriterionOr(require1, require2));
            return (E) this;
        }

        E not(IPrimitive<Boolean> require) {
            this.require.add(new CriterionNot(require));
            return (E) this;
        }

        public boolean accept(IEntity selection) {
            for (BooleanCriterion criterion : require) {
                if (!criterion.accept(selection)) {
                    return false;
                }
            }
            return true;
        }
    }

    private static class PaymentTypeAcceptance extends RequireBuilder<PaymentTypeAcceptance> {

        PaymentType paymentType;

        public PaymentTypeAcceptance(PaymentType paymentType) {
            this.paymentType = paymentType;
        }

    }

    private static class CardTypeAcceptance extends RequireBuilder<CardTypeAcceptance> {

        CreditCardType cardType;

        public CardTypeAcceptance(CreditCardType paymentType) {
            this.cardType = paymentType;
        }

    }

    private static Collection<PaymentTypeAcceptance> crmPaymentTypeRequire = buildPaymentAcceptanceMatrixCrm();

    private static Collection<PaymentTypeAcceptance> residentPortalPaymentTypeRequire = buildPaymentAcceptanceMatrixPortal();

    private static Collection<CardTypeAcceptance> crmCardRequire = buildCardAcceptanceMatrixCrm();

    private static Collection<CardTypeAcceptance> residentPortalCardRequire = buildCardAcceptanceMatrixPortal();

    private static Collection<CardTypeAcceptance> residentPortalCardWithConvenienceFee = buildCardAcceptanceMatrixWithConvenienceFeePortal();

    static Collection<PaymentType> getAllowedPaymentTypes(VistaApplication vistaApplication, ElectronicPaymentSetup setup, boolean requireCashEquivalent,
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
        selection.setup().set(setup);
        selection.notCashEquivalent().setValue(!requireCashEquivalent);

        Collection<PaymentType> allowedPaymentTypes = new ArrayList<PaymentType>();
        for (PaymentTypeAcceptance acceptance : requireAcceptance) {
            if (acceptance.accept(selection)) {
                allowedPaymentTypes.add(acceptance.paymentType);
            }
        }

        return Collections.unmodifiableCollection(allowedPaymentTypes);
    }

    public static Collection<CreditCardType> getAllowedCreditCardTypes(VistaApplication vistaApplication, ElectronicPaymentSetup setup,
            boolean requireCashEquivalent, PaymentTypeSelectionPolicy paymentMethodSelectionPolicy, boolean forConvenienceFeeOnly) {
        Collection<CardTypeAcceptance> requireAcceptance;
        switch (vistaApplication) {
        case resident:
            if (forConvenienceFeeOnly) {
                requireAcceptance = residentPortalCardWithConvenienceFee;
            } else {
                requireAcceptance = residentPortalCardRequire;
            }
            break;
        case crm:
            if (forConvenienceFeeOnly) {
                requireAcceptance = Collections.<CardTypeAcceptance> emptyList();
            } else {
                requireAcceptance = crmCardRequire;
            }
            break;
        default:
            throw new IllegalArgumentException();
        }

        ElectronicPaymentMethodSelection selection = paymentMethodSelectionPolicy.duplicate(ElectronicPaymentMethodSelection.class);
        selection.notCashEquivalent().setValue(!requireCashEquivalent);
        selection.setup().set(setup);

        Collection<CreditCardType> allowedPaymentTypes = new ArrayList<CreditCardType>();
        for (CardTypeAcceptance acceptance : requireAcceptance) {
            if (acceptance.accept(selection)) {
                allowedPaymentTypes.add(acceptance.cardType);
            }
        }
        return Collections.unmodifiableCollection(allowedPaymentTypes);
    }

    @SuppressWarnings("unchecked")
    private static Collection<PaymentTypeAcceptance> buildPaymentAcceptanceMatrixCrm() {
        Collection<PaymentTypeAcceptance> require = new ArrayList<PaymentTypeAcceptance>();
        ElectronicPaymentMethodSelection p = EntityFactory.getEntityPrototype(ElectronicPaymentMethodSelection.class);

        require.add(new PaymentTypeAcceptance(PaymentType.Cash).and(p.acceptedCash()). //
                or(p.notCashEquivalent(), p.cashEquivalentCash()));

        require.add(new PaymentTypeAcceptance(PaymentType.Check).and(p.acceptedCheck()). //
                or(p.notCashEquivalent(), p.cashEquivalentCheck()));

        require.add(new PaymentTypeAcceptance(PaymentType.Echeck).and(p.setup().acceptedEcheck(), p.acceptedEcheck()). // 
                or(p.notCashEquivalent(), p.cashEquivalentEcheck()));

        require.add(new PaymentTypeAcceptance(PaymentType.CreditCard).and(p.setup().acceptedCreditCard(), p.acceptedCreditCardMasterCard()).//
                or(p.notCashEquivalent(), p.cashEquivalentCreditCardMasterCard()));

        require.add(new PaymentTypeAcceptance(PaymentType.CreditCard).and(p.setup().acceptedCreditCard(), p.acceptedCreditCardVisa()).//
                or(p.notCashEquivalent(), p.cashEquivalentCreditCardVisa()));

        require.add(new PaymentTypeAcceptance(PaymentType.CreditCard).and(p.setup().acceptedCreditCard(), p.acceptedVisaDebit()).//
                or(p.notCashEquivalent(), p.cashEquivalentVisaDebit()));

        return require;
    }

    @SuppressWarnings("unchecked")
    private static Collection<PaymentTypeAcceptance> buildPaymentAcceptanceMatrixPortal() {
        Collection<PaymentTypeAcceptance> require = new ArrayList<PaymentTypeAcceptance>();
        ElectronicPaymentMethodSelection p = EntityFactory.getEntityPrototype(ElectronicPaymentMethodSelection.class);

        require.add(new PaymentTypeAcceptance(PaymentType.Echeck).and(p.setup().acceptedEcheck(), p.acceptedEcheck()). // 
                and(p.residentPortalEcheck()). //
                or(p.notCashEquivalent(), p.cashEquivalentEcheck()));

        require.add(new PaymentTypeAcceptance(PaymentType.CreditCard).and(p.setup().acceptedCreditCard(), p.acceptedCreditCardMasterCard()).//
                or(p.residentPortalCreditCardMasterCard(), p.setup().acceptedCreditCardConvenienceFee()). // 
                or(p.notCashEquivalent(), p.cashEquivalentCreditCardMasterCard()));

        require.add(new PaymentTypeAcceptance(PaymentType.CreditCard).and(p.setup().acceptedCreditCard(), p.acceptedCreditCardVisa()).//
                or(p.residentPortalCreditCardVisa(), p.setup().acceptedCreditCardConvenienceFee()). // 
                or(p.notCashEquivalent(), p.cashEquivalentCreditCardVisa()));

        require.add(new PaymentTypeAcceptance(PaymentType.CreditCard).and(p.setup().acceptedCreditCard(), p.acceptedVisaDebit()).//
                or(p.residentPortalVisaDebit(), p.setup().acceptedCreditCardConvenienceFee()). // 
                or(p.notCashEquivalent(), p.cashEquivalentVisaDebit()));

        require.add(new PaymentTypeAcceptance(PaymentType.DirectBanking).and(p.setup().acceptedDirectBanking(), p.acceptedDirectBanking()). //
                and(p.residentPortalDirectBanking()). //
                or(p.notCashEquivalent(), p.cashEquivalentDirectBanking()));

        if (!VistaTODO.removedForProduction && false) {
            require.add(new PaymentTypeAcceptance(PaymentType.Interac).and(p.setup().acceptedInterac(), p.acceptedInterac()).//
                    and(p.residentPortalInterac()). //
                    or(p.notCashEquivalent(), p.cashEquivalentInterac()));
        }

        return require;
    }

    @SuppressWarnings("unchecked")
    private static Collection<CardTypeAcceptance> buildCardAcceptanceMatrixCrm() {
        Collection<CardTypeAcceptance> require = new ArrayList<CardTypeAcceptance>();
        ElectronicPaymentMethodSelection p = EntityFactory.getEntityPrototype(ElectronicPaymentMethodSelection.class);

        require.add(new CardTypeAcceptance(CreditCardType.MasterCard).and(p.setup().acceptedCreditCard(), p.acceptedCreditCardMasterCard()).//
                or(p.notCashEquivalent(), p.cashEquivalentCreditCardMasterCard()));

        require.add(new CardTypeAcceptance(CreditCardType.Visa).and(p.setup().acceptedCreditCard(), p.acceptedCreditCardVisa()).// 
                or(p.notCashEquivalent(), p.cashEquivalentCreditCardVisa()));

        require.add(new CardTypeAcceptance(CreditCardType.VisaDebit).and(p.setup().acceptedCreditCard(), p.acceptedVisaDebit()).// 
                or(p.notCashEquivalent(), p.cashEquivalentVisaDebit()));

        return require;
    }

    @SuppressWarnings("unchecked")
    private static Collection<CardTypeAcceptance> buildCardAcceptanceMatrixPortal() {
        Collection<CardTypeAcceptance> require = new ArrayList<CardTypeAcceptance>();
        ElectronicPaymentMethodSelection p = EntityFactory.getEntityPrototype(ElectronicPaymentMethodSelection.class);

        require.add(new CardTypeAcceptance(CreditCardType.MasterCard).and(p.setup().acceptedCreditCard(), p.acceptedCreditCardMasterCard()).//
                or(p.residentPortalCreditCardMasterCard(), p.setup().acceptedCreditCardConvenienceFee()). //
                or(p.notCashEquivalent(), p.cashEquivalentCreditCardMasterCard()));

        require.add(new CardTypeAcceptance(CreditCardType.Visa).and(p.setup().acceptedCreditCard(), p.acceptedCreditCardVisa()).// 
                or(p.residentPortalCreditCardVisa(), p.setup().acceptedCreditCardConvenienceFee()). // 
                or(p.notCashEquivalent(), p.cashEquivalentCreditCardVisa()));

        if (VistaTODO.visaDebitHasConvenienceFee) {
            require.add(new CardTypeAcceptance(CreditCardType.VisaDebit).and(p.setup().acceptedCreditCard(), p.acceptedVisaDebit()).// 
                    or(p.residentPortalVisaDebit(), p.setup().acceptedCreditCardConvenienceFee()). //
                    or(p.notCashEquivalent(), p.cashEquivalentVisaDebit()));
        } else {
            require.add(new CardTypeAcceptance(CreditCardType.VisaDebit).and(p.setup().acceptedCreditCard(), p.acceptedVisaDebit()).// 
                    and(p.residentPortalVisaDebit()). //
                    or(p.notCashEquivalent(), p.cashEquivalentVisaDebit()));
        }

        return require;
    }

    @SuppressWarnings("unchecked")
    private static Collection<CardTypeAcceptance> buildCardAcceptanceMatrixWithConvenienceFeePortal() {
        Collection<CardTypeAcceptance> require = new ArrayList<CardTypeAcceptance>();
        ElectronicPaymentMethodSelection p = EntityFactory.getEntityPrototype(ElectronicPaymentMethodSelection.class);

        require.add(new CardTypeAcceptance(CreditCardType.MasterCard).and(p.setup().acceptedCreditCard(), p.setup().acceptedCreditCardConvenienceFee()).// 
                not(p.residentPortalCreditCardMasterCard()). //
                or(p.notCashEquivalent(), p.cashEquivalentCreditCardMasterCard()));

        require.add(new CardTypeAcceptance(CreditCardType.Visa).and(p.setup().acceptedCreditCard(), p.setup().acceptedCreditCardConvenienceFee()).// 
                not(p.residentPortalCreditCardVisa()). //
                or(p.notCashEquivalent(), p.cashEquivalentCreditCardVisa()));

        // VISTA-3995
        if (VistaTODO.visaDebitHasConvenienceFee) {
            require.add(new CardTypeAcceptance(CreditCardType.VisaDebit).and(p.setup().acceptedCreditCard(), p.setup().acceptedCreditCardConvenienceFee()).// 
                    not(p.residentPortalVisaDebit()). //
                    or(p.notCashEquivalent(), p.cashEquivalentVisaDebit()));
        }

        return require;
    }

}
