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
 */
package com.propertyvista.biz.financial.payment;

import static com.propertyvista.biz.financial.payment.PaymentMethodTarget.OneTimePayment;
import static com.propertyvista.biz.financial.payment.PaymentMethodTarget.StoreInProfile;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;

import com.propertyvista.domain.financial.MerchantAccount.MerchantElectronicPaymentSetup;
import com.propertyvista.domain.payment.CreditCardInfo.CreditCardType;
import com.propertyvista.domain.payment.PaymentType;
import com.propertyvista.domain.pmc.fee.AbstractPaymentSetup;
import com.propertyvista.domain.policy.policies.PaymentTypeSelectionPolicy;
import com.propertyvista.domain.security.common.VistaApplication;
import com.propertyvista.misc.VistaTODO;

public class PaymentAcceptanceUtils {

    @I18n(strategy = I18n.I18nStrategy.IgnoreAll)
    @Transient
    public interface ElectronicPaymentMethodSelection extends PaymentTypeSelectionPolicy {

        MerchantElectronicPaymentSetup merchantSetup();

        AbstractPaymentSetup systemSetup();

        IPrimitive<Boolean> notCashEquivalent();

        IPrimitive<PaymentMethodTarget> target();
    }

    private static abstract class BooleanCriterion {

        abstract boolean accept(IEntity selection);

    }

    @SuppressWarnings("unchecked")
    private static final boolean isTrue(IEntity selection, IPrimitive<Boolean> member) {
        return (((IPrimitive<Boolean>) selection.getMember(member.getPath())).getValue(Boolean.FALSE));
    }

    private static class BooleanProtoCriterion extends BooleanCriterion {

        IPrimitive<Boolean> require;

        BooleanProtoCriterion(IPrimitive<Boolean> protoMemeber) {
            this.require = protoMemeber;
        }

        @Override
        boolean accept(IEntity selection) {
            return isTrue(selection, require);
        }

    }

    static Collection<BooleanCriterion> asCriterion(Collection<IPrimitive<Boolean>> protos) {
        Collection<BooleanCriterion> requires = new ArrayList<>();
        for (IPrimitive<Boolean> proto : protos) {
            requires.add(new BooleanProtoCriterion(proto));
        }
        return requires;
    }

    static Collection<BooleanCriterion> asCriterion(@SuppressWarnings("unchecked") IPrimitive<Boolean>... protos) {
        Collection<BooleanCriterion> requires = new ArrayList<>();
        for (IPrimitive<Boolean> proto : protos) {
            requires.add(new BooleanProtoCriterion(proto));
        }
        return requires;
    }

    static class CriterionAnd extends BooleanCriterion {

        private final Collection<BooleanCriterion> requires;

        CriterionAnd(Collection<BooleanCriterion> requires) {
            this.requires = requires;
        }

        CriterionAnd(BooleanCriterion... requires) {
            this.requires = Arrays.asList(requires);
        }

        @Override
        boolean accept(IEntity selection) {
            for (BooleanCriterion require : requires) {
                if (!require.accept(selection)) {
                    return false;
                }
            }
            return true;
        }

    }

    private static BooleanCriterion and(BooleanCriterion criterion, @SuppressWarnings("unchecked") IPrimitive<Boolean>... require) {
        return new CriterionAnd(criterion, new CriterionAnd(asCriterion(require)));
    }

    private static BooleanCriterion and(@SuppressWarnings("unchecked") IPrimitive<Boolean>... require) {
        return new CriterionAnd(asCriterion(require));
    }

    static class CriterionOr extends BooleanCriterion {

        private final Collection<BooleanCriterion> requires;

        CriterionOr(Collection<BooleanCriterion> requires) {
            this.requires = requires;
        }

        CriterionOr(BooleanCriterion... requires) {
            this.requires = Arrays.asList(requires);
        }

        @Override
        boolean accept(IEntity selection) {
            for (BooleanCriterion require : requires) {
                if (require.accept(selection)) {
                    return true;
                }
            }
            return false;
        }

    }

    static class CriterionNot extends BooleanCriterion {

        BooleanCriterion criterion;

        CriterionNot(IPrimitive<Boolean> require) {
            this.criterion = new BooleanProtoCriterion(require);
        }

        CriterionNot(BooleanCriterion criterion) {
            this.criterion = criterion;
        }

        @Override
        public boolean accept(IEntity selection) {
            return !criterion.accept(selection);
        }

    }

    private static class CriterionIn<E extends Serializable> extends BooleanCriterion {

        Collection<E> options;

        IPrimitive<E> require;

        CriterionIn(IPrimitive<E> require, E[] options) {
            this.require = require;

            // TODO remove this.  TODO is  true
            List<PaymentMethodTarget> t2 = new ArrayList<PaymentMethodTarget>(Arrays.asList((PaymentMethodTarget[]) options));
            t2.add(PaymentMethodTarget.TODO);
            this.options = (Collection<E>) t2;

            // this.options = Arrays.asList(options);
        }

        @SuppressWarnings("unchecked")
        @Override
        public boolean accept(IEntity selection) {
            return options.contains((((IPrimitive<E>) selection.getMember(require.getPath())).getValue()));
        }

    }

    static BooleanCriterion in(IPrimitive<PaymentMethodTarget> target, PaymentMethodTarget... targets) {
        return new CriterionIn<PaymentMethodTarget>(target, targets);
    }

    @SuppressWarnings("unchecked")
    private static class RequireBuilder<E extends RequireBuilder<?>> {

        Collection<BooleanCriterion> require;

        RequireBuilder() {
            this.require = new ArrayList<BooleanCriterion>();
        }

        E and(IPrimitive<Boolean>... require) {
            this.require.add(new CriterionAnd(asCriterion(require)));
            return (E) this;
        }

        E or(IPrimitive<Boolean>... require) {
            this.require.add(new CriterionOr(asCriterion(require)));
            return (E) this;
        }

        E or(IPrimitive<Boolean> require1, BooleanCriterion require2) {
            this.require.add(new CriterionOr(new BooleanProtoCriterion(require1), require2));
            return (E) this;
        }

        E not(IPrimitive<Boolean> require) {
            this.require.add(new CriterionNot(require));
            return (E) this;
        }

        E and(IPrimitive<PaymentMethodTarget> target, PaymentMethodTarget... targets) {
            this.require.add(new CriterionIn<PaymentMethodTarget>(target, targets));
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

    private static Collection<PaymentTypeAcceptance> residentPortalPaymentTypeRequire = buildPaymentAcceptanceMatrixResident();

    private static Collection<PaymentTypeAcceptance> prospectPortalPaymentTypeRequire = buildPaymentAcceptanceMatrixProspect();

    private static Collection<CardTypeAcceptance> crmCardRequire = buildCardAcceptanceMatrixCrm();

    private static Collection<CardTypeAcceptance> residentPortalCardRequire = buildCardAcceptanceMatrixResident();

    private static Collection<CardTypeAcceptance> residentPortalCardWithConvenienceFee = buildCardAcceptanceMatrixWithConvenienceFeeResident();

    private static Collection<CardTypeAcceptance> prospectPortalCardRequire = buildCardAcceptanceMatrixProspect();

    private static Collection<CardTypeAcceptance> prospectPortalCardWithConvenienceFee = buildCardAcceptanceMatrixWithConvenienceFeeProspect();

    static Collection<PaymentType> getAllowedPaymentTypes(PaymentMethodTarget paymentMethodTarget, VistaApplication vistaApplication,
            MerchantElectronicPaymentSetup merchantSetup, AbstractPaymentSetup systemSetup, boolean requireCashEquivalent,
            PaymentTypeSelectionPolicy paymentMethodSelectionPolicy) {

        Collection<PaymentTypeAcceptance> requireAcceptance;
        switch (vistaApplication) {
        case prospect:
            requireAcceptance = prospectPortalPaymentTypeRequire;
            break;
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
        selection.merchantSetup().set(merchantSetup);
        selection.systemSetup().set(systemSetup);
        selection.notCashEquivalent().setValue(!requireCashEquivalent);
        selection.target().setValue(paymentMethodTarget);

        Collection<PaymentType> allowedPaymentTypes = new ArrayList<PaymentType>();
        for (PaymentTypeAcceptance acceptance : requireAcceptance) {
            if (acceptance.accept(selection)) {
                allowedPaymentTypes.add(acceptance.paymentType);
            }
        }

        return Collections.unmodifiableCollection(allowedPaymentTypes);
    }

    public static Collection<CreditCardType> getAllowedCreditCardTypes(PaymentMethodTarget paymentMethodTarget, VistaApplication vistaApplication,
            MerchantElectronicPaymentSetup merchantSetup, AbstractPaymentSetup systemSetup, boolean requireCashEquivalent,
            PaymentTypeSelectionPolicy paymentMethodSelectionPolicy, boolean forConvenienceFeeOnly) {
        Collection<CardTypeAcceptance> requireAcceptance;
        switch (vistaApplication) {
        case prospect:
            if (forConvenienceFeeOnly) {
                requireAcceptance = prospectPortalCardWithConvenienceFee;
            } else {
                requireAcceptance = prospectPortalCardRequire;
            }
            break;
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
        selection.merchantSetup().set(merchantSetup);
        selection.systemSetup().set(systemSetup);
        selection.target().setValue(paymentMethodTarget);

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

        require.add(new PaymentTypeAcceptance(PaymentType.Echeck).//
                and(p.merchantSetup().acceptedEcheck(), p.systemSetup().acceptedEcheck(), p.acceptedEcheck()). // 
                or(p.notCashEquivalent(), p.cashEquivalentEcheck()));

        require.add(new PaymentTypeAcceptance(PaymentType.CreditCard).//
                and(p.merchantSetup().acceptedCreditCard(), p.systemSetup().acceptedMasterCard(), p.acceptedCreditCardMasterCard()).//
                or(p.notCashEquivalent(), p.cashEquivalentCreditCardMasterCard()));

        require.add(new PaymentTypeAcceptance(PaymentType.CreditCard).//
                and(p.merchantSetup().acceptedCreditCard(), p.systemSetup().acceptedVisa(), p.acceptedCreditCardVisa()).//
                or(p.notCashEquivalent(), p.cashEquivalentCreditCardVisa()));

        require.add(new PaymentTypeAcceptance(PaymentType.CreditCard). //
                and(p.merchantSetup().acceptedCreditCard(), p.systemSetup().acceptedVisa(), p.merchantSetup().acceptedCreditCardVisaDebit(),
                        p.acceptedVisaDebit()).//
                or(p.notCashEquivalent(), p.cashEquivalentVisaDebit()));

        return require;
    }

    @SuppressWarnings("unchecked")
    private static Collection<PaymentTypeAcceptance> buildPaymentAcceptanceMatrixResident() {
        Collection<PaymentTypeAcceptance> require = new ArrayList<PaymentTypeAcceptance>();
        ElectronicPaymentMethodSelection p = EntityFactory.getEntityPrototype(ElectronicPaymentMethodSelection.class);

        require.add(new PaymentTypeAcceptance(PaymentType.Echeck).//
                and(p.systemSetup().acceptedEcheck(), p.merchantSetup().acceptedEcheck(), p.acceptedEcheck()). // 
                and(p.residentPortalEcheck()). //
                or(p.notCashEquivalent(), p.cashEquivalentEcheck()));

        require.add(new PaymentTypeAcceptance(PaymentType.CreditCard).//
                and(p.systemSetup().acceptedMasterCard(), p.merchantSetup().acceptedCreditCard(), p.acceptedCreditCardMasterCard()).//
                or(p.residentPortalCreditCardMasterCard(), //
                        and(in(p.target(), StoreInProfile, OneTimePayment), //
                                p.merchantSetup().acceptedCreditCardConvenienceFee(), p.systemSetup().acceptedMasterCardConvenienceFee())). //
                or(p.notCashEquivalent(), p.cashEquivalentCreditCardMasterCard()));

        require.add(new PaymentTypeAcceptance(PaymentType.CreditCard).//
                and(p.systemSetup().acceptedVisa(), p.merchantSetup().acceptedCreditCard(), p.acceptedCreditCardVisa()).//
                or(p.residentPortalCreditCardVisa(), //
                        and(in(p.target(), StoreInProfile, OneTimePayment), //
                                p.merchantSetup().acceptedCreditCardConvenienceFee(), p.systemSetup().acceptedVisaConvenienceFee())). //
                or(p.notCashEquivalent(), p.cashEquivalentCreditCardVisa()));

        require.add(new PaymentTypeAcceptance(PaymentType.CreditCard). //
                and(p.systemSetup().acceptedVisaDebit(), //
                        p.merchantSetup().acceptedCreditCard(), p.merchantSetup().acceptedCreditCardVisaDebit(), p.acceptedVisaDebit()).//
                or(p.residentPortalVisaDebit(), //
                        and(in(p.target(), StoreInProfile, OneTimePayment), //
                                p.merchantSetup().acceptedCreditCardConvenienceFee(), p.systemSetup().acceptedVisaDebitConvenienceFee())). // 
                or(p.notCashEquivalent(), p.cashEquivalentVisaDebit()));

        require.add(new PaymentTypeAcceptance(PaymentType.DirectBanking).//
                and(p.target(), OneTimePayment). //
                and(p.merchantSetup().acceptedDirectBanking(), p.systemSetup().acceptedDirectBanking(), p.acceptedDirectBanking()). //
                and(p.residentPortalDirectBanking()). //
                or(p.notCashEquivalent(), p.cashEquivalentDirectBanking()));

        if (!VistaTODO.removedForProduction && false) {
            require.add(new PaymentTypeAcceptance(PaymentType.Interac).and(p.merchantSetup().acceptedInterac(), p.acceptedInterac()).//
                    and(p.residentPortalInterac()). //
                    or(p.notCashEquivalent(), p.cashEquivalentInterac()));
        }

        return require;
    }

    @SuppressWarnings("unchecked")
    private static Collection<PaymentTypeAcceptance> buildPaymentAcceptanceMatrixProspect() {
        Collection<PaymentTypeAcceptance> require = new ArrayList<PaymentTypeAcceptance>();
        ElectronicPaymentMethodSelection p = EntityFactory.getEntityPrototype(ElectronicPaymentMethodSelection.class);

        require.add(new PaymentTypeAcceptance(PaymentType.Echeck).and(p.merchantSetup().acceptedEcheck(), p.acceptedEcheck()). // 
                and(p.prospectEcheck()));

        require.add(new PaymentTypeAcceptance(PaymentType.CreditCard).and(p.merchantSetup().acceptedCreditCard(), p.acceptedCreditCardMasterCard()).//
                or(p.prospectCreditCardMasterCard(), p.merchantSetup().acceptedCreditCardConvenienceFee()));

        require.add(new PaymentTypeAcceptance(PaymentType.CreditCard).and(p.merchantSetup().acceptedCreditCard(), p.acceptedCreditCardVisa()).//
                or(p.prospectCreditCardVisa(), p.merchantSetup().acceptedCreditCardConvenienceFee()));

        require.add(new PaymentTypeAcceptance(PaymentType.CreditCard)//
                .and(p.merchantSetup().acceptedCreditCard(), p.merchantSetup().acceptedCreditCardVisaDebit(), p.acceptedVisaDebit()).//
                or(p.prospectVisaDebit(), p.merchantSetup().acceptedCreditCardConvenienceFee()));

        return require;
    }

    @SuppressWarnings("unchecked")
    private static Collection<CardTypeAcceptance> buildCardAcceptanceMatrixCrm() {
        Collection<CardTypeAcceptance> require = new ArrayList<CardTypeAcceptance>();
        ElectronicPaymentMethodSelection p = EntityFactory.getEntityPrototype(ElectronicPaymentMethodSelection.class);

        require.add(new CardTypeAcceptance(CreditCardType.MasterCard).//
                and(p.systemSetup().acceptedMasterCard(), p.merchantSetup().acceptedCreditCard(), p.acceptedCreditCardMasterCard()).//
                or(p.notCashEquivalent(), p.cashEquivalentCreditCardMasterCard()));

        require.add(new CardTypeAcceptance(CreditCardType.Visa).//
                and(p.systemSetup().acceptedVisa(), p.merchantSetup().acceptedCreditCard(), p.acceptedCreditCardVisa()).// 
                or(p.notCashEquivalent(), p.cashEquivalentCreditCardVisa()));

        require.add(new CardTypeAcceptance(CreditCardType.VisaDebit)//
                .and(p.systemSetup().acceptedVisaDebit(), p.merchantSetup().acceptedCreditCard(), p.merchantSetup().acceptedCreditCardVisaDebit(),
                        p.acceptedVisaDebit()).// 
                or(p.notCashEquivalent(), p.cashEquivalentVisaDebit()));

        return require;
    }

    @SuppressWarnings("unchecked")
    private static Collection<CardTypeAcceptance> buildCardAcceptanceMatrixResident() {
        Collection<CardTypeAcceptance> require = new ArrayList<CardTypeAcceptance>();
        ElectronicPaymentMethodSelection p = EntityFactory.getEntityPrototype(ElectronicPaymentMethodSelection.class);

        require.add(new CardTypeAcceptance(CreditCardType.MasterCard).//
                and(p.systemSetup().acceptedMasterCard(), p.merchantSetup().acceptedCreditCard(), p.acceptedCreditCardMasterCard()).//
                or(p.residentPortalCreditCardMasterCard(), //
                        and(in(p.target(), StoreInProfile, OneTimePayment), //
                                p.systemSetup().acceptedMasterCardConvenienceFee(), p.merchantSetup().acceptedCreditCardConvenienceFee())). //
                or(p.notCashEquivalent(), p.cashEquivalentCreditCardMasterCard()));

        require.add(new CardTypeAcceptance(CreditCardType.Visa).//
                and(p.systemSetup().acceptedVisa(), p.merchantSetup().acceptedCreditCard(), p.acceptedCreditCardVisa()).// 
                or(p.residentPortalCreditCardVisa(), //
                        and(in(p.target(), StoreInProfile, OneTimePayment), //
                                p.systemSetup().acceptedVisaConvenienceFee(), p.merchantSetup().acceptedCreditCardConvenienceFee())). // 
                or(p.notCashEquivalent(), p.cashEquivalentCreditCardVisa()));

        if (VistaTODO.visaDebitHasConvenienceFee) {
            require.add(new CardTypeAcceptance(CreditCardType.VisaDebit).//
                    and(p.systemSetup().acceptedVisaDebit(), p.merchantSetup().acceptedCreditCard(), p.merchantSetup().acceptedCreditCardVisaDebit(),
                            p.acceptedVisaDebit()).// 
                    or(p.residentPortalVisaDebit(), p.merchantSetup().acceptedCreditCardConvenienceFee()). //
                    or(p.notCashEquivalent(), p.cashEquivalentVisaDebit()));
        } else {
            require.add(new CardTypeAcceptance(CreditCardType.VisaDebit).//
                    and(p.systemSetup().acceptedVisaDebit(), p.merchantSetup().acceptedCreditCard(), p.merchantSetup().acceptedCreditCardVisaDebit(),
                            p.acceptedVisaDebit()).// 
                    and(p.residentPortalVisaDebit()). //
                    or(p.notCashEquivalent(), p.cashEquivalentVisaDebit()));
        }

        return require;
    }

    @SuppressWarnings("unchecked")
    private static Collection<CardTypeAcceptance> buildCardAcceptanceMatrixWithConvenienceFeeResident() {
        Collection<CardTypeAcceptance> require = new ArrayList<CardTypeAcceptance>();
        ElectronicPaymentMethodSelection p = EntityFactory.getEntityPrototype(ElectronicPaymentMethodSelection.class);

        require.add(new CardTypeAcceptance(CreditCardType.MasterCard).//
                and(p.systemSetup().acceptedMasterCard(), p.merchantSetup().acceptedCreditCard(), //
                        p.systemSetup().acceptedMasterCardConvenienceFee(), p.merchantSetup().acceptedCreditCardConvenienceFee()).// 
                not(p.residentPortalCreditCardMasterCard()). //
                or(p.notCashEquivalent(), p.cashEquivalentCreditCardMasterCard()));

        require.add(new CardTypeAcceptance(CreditCardType.Visa).//
                and(p.systemSetup().acceptedVisa(), p.merchantSetup().acceptedCreditCard(), //
                        p.systemSetup().acceptedVisaConvenienceFee(), p.merchantSetup().acceptedCreditCardConvenienceFee()).// 
                not(p.residentPortalCreditCardVisa()). //
                or(p.notCashEquivalent(), p.cashEquivalentCreditCardVisa()));

        // VISTA-3995
        if (VistaTODO.visaDebitHasConvenienceFee) {
            require.add(new CardTypeAcceptance(CreditCardType.VisaDebit).//
                    and(p.systemSetup().acceptedVisa(), p.merchantSetup().acceptedCreditCard(),//
                            p.systemSetup().acceptedVisaConvenienceFee(), p.merchantSetup().acceptedCreditCardConvenienceFee()).// 
                    not(p.residentPortalVisaDebit()). //
                    or(p.notCashEquivalent(), p.cashEquivalentVisaDebit()));
        }

        return require;
    }

    @SuppressWarnings("unchecked")
    private static Collection<CardTypeAcceptance> buildCardAcceptanceMatrixProspect() {
        Collection<CardTypeAcceptance> require = new ArrayList<CardTypeAcceptance>();
        ElectronicPaymentMethodSelection p = EntityFactory.getEntityPrototype(ElectronicPaymentMethodSelection.class);

        require.add(new CardTypeAcceptance(CreditCardType.MasterCard).//
                and(p.systemSetup().acceptedMasterCard(), p.merchantSetup().acceptedCreditCard(), p.acceptedCreditCardMasterCard()).//
                or(p.prospectCreditCardMasterCard(), //
                        and(p.systemSetup().acceptedMasterCardConvenienceFee(), p.merchantSetup().acceptedCreditCardConvenienceFee())));

        require.add(new CardTypeAcceptance(CreditCardType.Visa).//
                and(p.systemSetup().acceptedVisa(), p.merchantSetup().acceptedCreditCard(), p.acceptedCreditCardVisa()).// 
                or(p.prospectCreditCardVisa(), //
                        and(p.systemSetup().acceptedVisaConvenienceFee(), p.merchantSetup().acceptedCreditCardConvenienceFee())));

        if (VistaTODO.visaDebitHasConvenienceFee) {
            require.add(new CardTypeAcceptance(CreditCardType.VisaDebit).//
                    and(p.systemSetup().acceptedVisaDebit(), p.merchantSetup().acceptedCreditCard(),//
                            p.merchantSetup().acceptedCreditCardVisaDebit(), p.acceptedVisaDebit()).// 
                    or(p.prospectVisaDebit(), //
                            and(p.systemSetup().acceptedVisaConvenienceFee(), p.merchantSetup().acceptedCreditCardConvenienceFee())));
        } else {
            require.add(new CardTypeAcceptance(CreditCardType.VisaDebit).//
                    and(p.systemSetup().acceptedVisaDebit(), p.merchantSetup().acceptedCreditCard(), //
                            p.merchantSetup().acceptedCreditCardVisaDebit(), p.acceptedVisaDebit()).// 
                    and(p.prospectVisaDebit()));
        }

        return require;
    }

    @SuppressWarnings("unchecked")
    private static Collection<CardTypeAcceptance> buildCardAcceptanceMatrixWithConvenienceFeeProspect() {
        Collection<CardTypeAcceptance> require = new ArrayList<CardTypeAcceptance>();
        ElectronicPaymentMethodSelection p = EntityFactory.getEntityPrototype(ElectronicPaymentMethodSelection.class);

        require.add(new CardTypeAcceptance(CreditCardType.MasterCard).//
                and(p.systemSetup().acceptedMasterCard(), p.merchantSetup().acceptedCreditCard(),//
                        p.systemSetup().acceptedMasterCardConvenienceFee(), p.merchantSetup().acceptedCreditCardConvenienceFee()).// 
                not(p.prospectCreditCardMasterCard()));

        require.add(new CardTypeAcceptance(CreditCardType.Visa).//
                and(p.systemSetup().acceptedVisa(), p.merchantSetup().acceptedCreditCard(),//
                        p.systemSetup().acceptedVisaConvenienceFee(), p.merchantSetup().acceptedCreditCardConvenienceFee()).// 
                not(p.prospectCreditCardVisa()));

        // VISTA-3995
        if (VistaTODO.visaDebitHasConvenienceFee) {
            require.add(new CardTypeAcceptance(CreditCardType.VisaDebit).//
                    and(p.systemSetup().acceptedVisaDebit(), p.merchantSetup().acceptedCreditCard(),//
                            p.systemSetup().acceptedVisaConvenienceFee(), p.merchantSetup().acceptedCreditCardConvenienceFee()).// 
                    not(p.prospectVisaDebit()));
        }

        return require;
    }

}
