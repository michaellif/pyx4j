/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 2, 2012
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.biz.financial.payment;

import java.util.Collection;
import java.util.Collections;

import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.essentials.server.admin.SystemMaintenance;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.biz.policy.PolicyFacade;
import com.propertyvista.domain.financial.AllowedPaymentsSetup;
import com.propertyvista.domain.financial.BillingAccount;
import com.propertyvista.domain.financial.BillingAccount.PaymentAccepted;
import com.propertyvista.domain.financial.MerchantAccount;
import com.propertyvista.domain.financial.MerchantAccount.ElectronicPaymentSetup;
import com.propertyvista.domain.financial.MerchantAccount.MerchantAccountActivationStatus;
import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.payment.CreditCardInfo.CreditCardType;
import com.propertyvista.domain.payment.PaymentType;
import com.propertyvista.domain.policy.policies.PaymentTypeSelectionPolicy;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.security.common.VistaApplication;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseTerm;
import com.propertyvista.operations.rpc.VistaSystemMaintenanceState;

class PaymentUtils {

    private static final I18n i18n = I18n.get(PaymentUtils.class);

    static boolean isElectronicPaymentsSetup(MerchantAccount merchantAccount) {
        if ((merchantAccount == null) || merchantAccount.invalid().getValue(Boolean.TRUE)) {
            return false;
        } else {
            return (merchantAccount.status().getValue() == MerchantAccountActivationStatus.Active) && (!merchantAccount.merchantTerminalId().isNull());
        }
    }

    public static boolean isPaymentsAllowed(BillingAccount billingAccountId) {
        if (PaymentRecord.merchantAccountIsRequedForPayments) {
            EntityQueryCriteria<MerchantAccount> criteria = EntityQueryCriteria.create(MerchantAccount.class);
            criteria.add(PropertyCriterion.eq(criteria.proto()._buildings().$().units().$()._Leases().$().billingAccount(), billingAccountId));
            return Persistence.service().retrieve(criteria) != null;
        } else {
            return true;
        }
    }

    public static boolean isElectronicPaymentsSetup(Building building) {
        EntityQueryCriteria<MerchantAccount> criteria = EntityQueryCriteria.create(MerchantAccount.class);
        criteria.add(PropertyCriterion.eq(criteria.proto()._buildings(), building));
        return isElectronicPaymentsSetup(Persistence.service().retrieve(criteria));
    }

    public static boolean isElectronicPaymentsSetup(BillingAccount billingAccountId) {
        EntityQueryCriteria<MerchantAccount> criteria = EntityQueryCriteria.create(MerchantAccount.class);
        criteria.add(PropertyCriterion.eq(criteria.proto()._buildings().$().units().$()._Leases().$().billingAccount(), billingAccountId));
        return isElectronicPaymentsSetup(Persistence.service().retrieve(criteria));
    }

    public static ElectronicPaymentSetup getEffectiveElectronicPaymentsSetup(MerchantAccount merchantAccount) {
        ElectronicPaymentSetup paymentsSetup;
        if (isElectronicPaymentsSetup(merchantAccount)) {
            if (merchantAccount.merchantTerminalIdConvenienceFee().isNull()) {
                merchantAccount.setup().acceptedCreditCardConvenienceFee().setValue(false);
            }
            paymentsSetup = merchantAccount.setup().duplicate();

            VistaSystemMaintenanceState systemState = (VistaSystemMaintenanceState) SystemMaintenance.getSystemMaintenanceInfo();
            if (systemState.enableCreditCardMaintenance().getValue(false)) {
                paymentsSetup.acceptedCreditCard().setValue(false);
                paymentsSetup.acceptedCreditCardConvenienceFee().setValue(false);
            } else if (systemState.enableCreditCardConvenienceFeeMaintenance().getValue(false)) {
                paymentsSetup.acceptedCreditCardConvenienceFee().setValue(false);
            }

            if (systemState.enableInteracMaintenance().getValue(false)) {
                paymentsSetup.acceptedInterac().setValue(false);
            }
        } else {
            paymentsSetup = EntityFactory.create(ElectronicPaymentSetup.class);
        }
        return paymentsSetup;
    }

    public static ElectronicPaymentSetup getEffectiveElectronicPaymentsSetup(BillingAccount billingAccountId) {
        EntityQueryCriteria<MerchantAccount> criteria = EntityQueryCriteria.create(MerchantAccount.class);
        criteria.add(PropertyCriterion.eq(criteria.proto()._buildings().$().units().$()._Leases().$().billingAccount(), billingAccountId));
        MerchantAccount merchantAccount = Persistence.service().retrieve(criteria);
        return getEffectiveElectronicPaymentsSetup(merchantAccount);
    }

    public static ElectronicPaymentSetup getEffectiveElectronicPaymentsSetup(Building buildingId) {
        EntityQueryCriteria<MerchantAccount> criteria = EntityQueryCriteria.create(MerchantAccount.class);
        criteria.add(PropertyCriterion.eq(criteria.proto()._buildings(), buildingId));
        MerchantAccount merchantAccount = Persistence.service().retrieve(criteria);
        return getEffectiveElectronicPaymentsSetup(merchantAccount);
    }

    public static boolean isElectronicPaymentsSetup(Lease leaseId) {
        EntityQueryCriteria<MerchantAccount> criteria = EntityQueryCriteria.create(MerchantAccount.class);
        criteria.add(PropertyCriterion.eq(criteria.proto()._buildings().$().units().$()._Leases(), leaseId));
        return isElectronicPaymentsSetup(Persistence.service().retrieve(criteria));
    }

    public static boolean isElectronicPaymentsSetup(LeaseTerm leaseTermId) {
        EntityQueryCriteria<MerchantAccount> criteria = EntityQueryCriteria.create(MerchantAccount.class);
        criteria.add(PropertyCriterion.eq(criteria.proto()._buildings().$().units().$()._Leases().$().leaseTerms(), leaseTermId));
        return isElectronicPaymentsSetup(Persistence.service().retrieve(criteria));
    }

    static MerchantAccount retrieveMerchantAccount(PaymentRecord paymentRecord) {
        EntityQueryCriteria<MerchantAccount> criteria = EntityQueryCriteria.create(MerchantAccount.class);
        criteria.add(PropertyCriterion.eq(criteria.proto()._buildings().$().units().$()._Leases().$().billingAccount(), paymentRecord.billingAccount()));
        return Persistence.service().retrieve(criteria);
    }

    static MerchantAccount retrieveValidMerchantAccount(PaymentRecord paymentRecord) {
        return retrieveValidMerchantAccount(paymentRecord.billingAccount());
    }

    static MerchantAccount retrieveValidMerchantAccount(BillingAccount billingAccountId) {
        EntityQueryCriteria<MerchantAccount> criteria = EntityQueryCriteria.create(MerchantAccount.class);
        criteria.eq(criteria.proto().invalid(), Boolean.FALSE);
        criteria.eq(criteria.proto().status(), MerchantAccountActivationStatus.Active);
        criteria.eq(criteria.proto()._buildings().$().units().$()._Leases().$().billingAccount(), billingAccountId);
        for (MerchantAccount merchantAccount : Persistence.service().query(criteria)) {
            if (!merchantAccount.merchantTerminalId().isNull()) {
                return merchantAccount;
            }
        }
        throw new UserRuntimeException(i18n.tr("No active merchantAccount found to process the payment"));
    }

    static AllowedPaymentsSetup getAllowedPaymentsSetup(BillingAccount billingAccountId, VistaApplication vistaApplication) {
        AllowedPaymentsSetup to = EntityFactory.create(AllowedPaymentsSetup.class);
        to.electronicPaymentsAllowed().setValue(isElectronicPaymentsSetup(billingAccountId));

        BillingAccount billingAccount = billingAccountId.duplicate();
        Persistence.ensureRetrieve(billingAccount, AttachLevel.Attached);
        PaymentAccepted paymentAccepted = billingAccount.paymentAccepted().getValue();
        if (paymentAccepted != PaymentAccepted.DoNotAccept) {

            ElectronicPaymentSetup setup = getEffectiveElectronicPaymentsSetup(billingAccountId);
            PaymentTypeSelectionPolicy paymentMethodSelectionPolicy;
            {
                EntityQueryCriteria<Building> criteria = EntityQueryCriteria.create(Building.class);
                criteria.add(PropertyCriterion.eq(criteria.proto().units().$()._Leases().$().billingAccount(), billingAccountId));
                Building policyNode = Persistence.service().retrieve(criteria, AttachLevel.IdOnly);
                paymentMethodSelectionPolicy = ServerSideFactory.create(PolicyFacade.class).obtainEffectivePolicy(policyNode, PaymentTypeSelectionPolicy.class);
            }
            to.allowedPaymentTypes().setCollectionValue(
                    PaymentAcceptanceUtils.getAllowedPaymentTypes(vistaApplication, setup, paymentAccepted == PaymentAccepted.CashEquivalent,
                            paymentMethodSelectionPolicy));

            if (setup.acceptedCreditCard().getValue(false)) {
                to.allowedCardTypes().setCollectionValue(
                        PaymentAcceptanceUtils.getAllowedCreditCardTypes(vistaApplication, setup, paymentAccepted == PaymentAccepted.CashEquivalent,
                                paymentMethodSelectionPolicy, false));
                to.convenienceFeeApplicableCardTypes().setCollectionValue(
                        PaymentAcceptanceUtils.getAllowedCreditCardTypes(vistaApplication, setup, paymentAccepted == PaymentAccepted.CashEquivalent,
                                paymentMethodSelectionPolicy, true));
            }
        }
        return to;
    }

    static AllowedPaymentsSetup getAllowedPaymentsSetup(Building policyNode, VistaApplication vistaApplication) {
        AllowedPaymentsSetup to = EntityFactory.create(AllowedPaymentsSetup.class);
        to.electronicPaymentsAllowed().setValue(isElectronicPaymentsSetup(policyNode));

        ElectronicPaymentSetup setup = getEffectiveElectronicPaymentsSetup(policyNode);
        PaymentTypeSelectionPolicy paymentMethodSelectionPolicy = ServerSideFactory.create(PolicyFacade.class).obtainEffectivePolicy(policyNode,
                PaymentTypeSelectionPolicy.class);

        boolean requireCashEquivalent = false;

        to.allowedPaymentTypes().setCollectionValue(
                PaymentAcceptanceUtils.getAllowedPaymentTypes(vistaApplication, setup, requireCashEquivalent, paymentMethodSelectionPolicy));

        if (setup.acceptedCreditCard().getValue(false)) {
            to.allowedCardTypes().setCollectionValue(
                    PaymentAcceptanceUtils.getAllowedCreditCardTypes(vistaApplication, setup, requireCashEquivalent, paymentMethodSelectionPolicy, false));
            to.convenienceFeeApplicableCardTypes().setCollectionValue(
                    PaymentAcceptanceUtils.getAllowedCreditCardTypes(vistaApplication, setup, requireCashEquivalent, paymentMethodSelectionPolicy, true));
        }

        return to;
    }

    static Collection<PaymentType> getAllowedPaymentTypes(BillingAccount billingAccountId, VistaApplication vistaApplication) {
        BillingAccount billingAccount = billingAccountId.duplicate();
        Persistence.ensureRetrieve(billingAccount, AttachLevel.Attached);
        PaymentAccepted paymentAccepted = billingAccount.paymentAccepted().getValue();
        if (paymentAccepted == PaymentAccepted.DoNotAccept) {
            return Collections.emptyList();
        }

        ElectronicPaymentSetup setup = getEffectiveElectronicPaymentsSetup(billingAccountId);
        PaymentTypeSelectionPolicy paymentMethodSelectionPolicy;
        {
            EntityQueryCriteria<AptUnit> criteria = EntityQueryCriteria.create(AptUnit.class);
            criteria.add(PropertyCriterion.eq(criteria.proto()._Leases().$().billingAccount(), billingAccountId));
            paymentMethodSelectionPolicy = ServerSideFactory.create(PolicyFacade.class).obtainEffectivePolicy(Persistence.service().retrieve(criteria),
                    PaymentTypeSelectionPolicy.class);
        }
        return PaymentAcceptanceUtils.getAllowedPaymentTypes(vistaApplication, setup, paymentAccepted == PaymentAccepted.CashEquivalent,
                paymentMethodSelectionPolicy);
    }

    public static Collection<CreditCardType> getAllowedCardTypes(BillingAccount billingAccountId, VistaApplication vistaApplication) {
        return getCardTypes(billingAccountId, vistaApplication, false);
    }

    public static Collection<CreditCardType> getConvenienceFeeApplicableCardTypes(BillingAccount billingAccountId, VistaApplication vistaApplication) {
        return getCardTypes(billingAccountId, vistaApplication, true);
    }

    private static Collection<CreditCardType> getCardTypes(BillingAccount billingAccountId, VistaApplication vistaApplication, boolean forConvenienceFeeOnly) {
        BillingAccount billingAccount = billingAccountId.duplicate();
        Persistence.ensureRetrieve(billingAccount, AttachLevel.Attached);
        PaymentAccepted paymentAccepted = billingAccount.paymentAccepted().getValue();
        ElectronicPaymentSetup setup = getEffectiveElectronicPaymentsSetup(billingAccountId);
        if ((paymentAccepted == PaymentAccepted.DoNotAccept) || !setup.acceptedCreditCard().getValue(false)) {
            return Collections.emptyList();
        }
        PaymentTypeSelectionPolicy paymentMethodSelectionPolicy;
        {
            EntityQueryCriteria<AptUnit> criteria = EntityQueryCriteria.create(AptUnit.class);
            criteria.add(PropertyCriterion.eq(criteria.proto()._Leases().$().billingAccount(), billingAccountId));
            paymentMethodSelectionPolicy = ServerSideFactory.create(PolicyFacade.class).obtainEffectivePolicy(Persistence.service().retrieve(criteria),
                    PaymentTypeSelectionPolicy.class);
        }
        return PaymentAcceptanceUtils.getAllowedCreditCardTypes(vistaApplication, setup, paymentAccepted == PaymentAccepted.CashEquivalent,
                paymentMethodSelectionPolicy, forConvenienceFeeOnly);
    }

    public static MerchantAccount retrieveMerchantAccount(Building buildingStub) {
        EntityQueryCriteria<MerchantAccount> criteria = EntityQueryCriteria.create(MerchantAccount.class);
        criteria.add(PropertyCriterion.eq(criteria.proto()._buildings(), buildingStub));
        for (MerchantAccount merchantAccount : Persistence.service().query(criteria)) {
            if (!merchantAccount.merchantTerminalId().isNull()) {
                return merchantAccount;
            }
        }
        throw new UserRuntimeException(i18n.tr("No active merchantAccount found to process the payment"));
    }

}
