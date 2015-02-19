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
import com.propertyvista.biz.system.Vista2PmcFacade;
import com.propertyvista.biz.tenant.lease.LeaseFacade;
import com.propertyvista.domain.financial.AllowedPaymentsSetup;
import com.propertyvista.domain.financial.BillingAccount;
import com.propertyvista.domain.financial.BillingAccount.PaymentAccepted;
import com.propertyvista.domain.financial.MerchantAccount;
import com.propertyvista.domain.financial.MerchantAccount.MerchantAccountActivationStatus;
import com.propertyvista.domain.financial.MerchantAccount.MerchantElectronicPaymentSetup;
import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.payment.CreditCardInfo.CreditCardType;
import com.propertyvista.domain.payment.EcheckInfo;
import com.propertyvista.domain.payment.PaymentType;
import com.propertyvista.domain.pmc.fee.AbstractPaymentSetup;
import com.propertyvista.domain.policy.framework.PolicyNode;
import com.propertyvista.domain.policy.policies.PaymentTypeSelectionPolicy;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.security.common.VistaApplication;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseTerm;
import com.propertyvista.domain.util.ValidationUtils;
import com.propertyvista.operations.domain.VistaSystemMaintenanceState;

class PaymentUtils {

    private static final I18n i18n = I18n.get(PaymentUtils.class);

    static boolean isElectronicPaymentsSetup(MerchantAccount merchantAccount) {
        if ((merchantAccount == null) || merchantAccount.invalid().getValue(Boolean.TRUE)) {
            return false;
        } else {
            return (merchantAccount.status().getValue() == MerchantAccountActivationStatus.Active) && (!merchantAccount.merchantTerminalId().isNull());
        }
    }

    static MerchantAccount getMerchantAccount(BillingAccount billingAccountId) {
        EntityQueryCriteria<MerchantAccount> criteria = EntityQueryCriteria.create(MerchantAccount.class);
        criteria.add(PropertyCriterion.eq(criteria.proto()._buildings().$().units().$().leases().$().billingAccount(), billingAccountId));
        MerchantAccount merchantAccount = Persistence.service().retrieve(criteria);
        if (merchantAccount != null) {
            return merchantAccount;
        } else {
            EntityQueryCriteria<Lease> criteria2 = EntityQueryCriteria.create(Lease.class);
            criteria2.eq(criteria2.proto().billingAccount(), billingAccountId);
            Lease leaseId = Persistence.service().retrieve(criteria2, AttachLevel.IdOnly);
            Building building = ServerSideFactory.create(LeaseFacade.class).getLeaseBuilding(leaseId);
            EntityQueryCriteria<MerchantAccount> criteria3 = EntityQueryCriteria.create(MerchantAccount.class);
            criteria3.eq(criteria.proto()._buildings(), building);
            return Persistence.service().retrieve(criteria3);
        }
    }

    public static boolean isPaymentsAllowed(BillingAccount billingAccountId) {
        if (PaymentRecord.merchantAccountIsRequedForPayments) {
            return (getMerchantAccount(billingAccountId) != null);
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
        return isElectronicPaymentsSetup(getMerchantAccount(billingAccountId));
    }

    public static MerchantElectronicPaymentSetup getEffectiveElectronicPaymentsSetup(MerchantAccount merchantAccount) {
        if (isElectronicPaymentsSetup(merchantAccount)) {
            MerchantElectronicPaymentSetup paymentsSetup;
            if (merchantAccount.merchantTerminalIdConvenienceFee().isNull()) {
                merchantAccount.setup().acceptedCreditCardConvenienceFee().setValue(false);
            }
            paymentsSetup = merchantAccount.setup().duplicate();

            VistaSystemMaintenanceState systemState = (VistaSystemMaintenanceState) SystemMaintenance.getSystemMaintenanceInfo();
            if (systemState.enableCreditCardMaintenance().getValue(false)) {
                paymentsSetup.acceptedCreditCard().setValue(false);
                paymentsSetup.acceptedCreditCardConvenienceFee().setValue(false);
                paymentsSetup.acceptedCreditCardVisaDebit().setValue(false);
            } else if (systemState.enableCreditCardConvenienceFeeMaintenance().getValue(false)) {
                paymentsSetup.acceptedCreditCardConvenienceFee().setValue(false);
            }

            if (systemState.enableInteracMaintenance().getValue(false)) {
                paymentsSetup.acceptedInterac().setValue(false);
            }
            return paymentsSetup;
        } else {
            return EntityFactory.create(MerchantElectronicPaymentSetup.class);
        }
    }

    private static MerchantElectronicPaymentSetup getEffectiveElectronicPaymentsSetup(BillingAccount billingAccountId) {
        return getEffectiveElectronicPaymentsSetup(getMerchantAccount(billingAccountId));
    }

    static MerchantElectronicPaymentSetup getEffectiveElectronicPaymentsSetup(Building buildingId) {
        EntityQueryCriteria<MerchantAccount> criteria = EntityQueryCriteria.create(MerchantAccount.class);
        criteria.add(PropertyCriterion.eq(criteria.proto()._buildings(), buildingId));
        MerchantAccount merchantAccount = Persistence.service().retrieve(criteria);
        return getEffectiveElectronicPaymentsSetup(merchantAccount);
    }

    public static boolean isElectronicPaymentsSetup(Lease leaseId) {
        EntityQueryCriteria<MerchantAccount> criteria = EntityQueryCriteria.create(MerchantAccount.class);
        criteria.eq(criteria.proto()._buildings().$().units().$().leases(), leaseId);
        return isElectronicPaymentsSetup(Persistence.service().retrieve(criteria));
    }

    public static boolean isElectronicPaymentsSetup(LeaseTerm leaseTermId) {
        EntityQueryCriteria<MerchantAccount> criteria = EntityQueryCriteria.create(MerchantAccount.class);
        criteria.eq(criteria.proto()._buildings().$().units().$().leases().$().leaseTerms(), leaseTermId);
        return isElectronicPaymentsSetup(Persistence.service().retrieve(criteria));
    }

    static MerchantAccount retrieveMerchantAccount(PaymentRecord paymentRecord) {
        EntityQueryCriteria<MerchantAccount> criteria = EntityQueryCriteria.create(MerchantAccount.class);
        criteria.eq(criteria.proto()._buildings().$().units().$().leases().$().billingAccount(), paymentRecord.billingAccount());
        return Persistence.service().retrieve(criteria);
    }

    static MerchantAccount retrieveValidMerchantAccount(PaymentRecord paymentRecord) {
        EntityQueryCriteria<MerchantAccount> criteria = EntityQueryCriteria.create(MerchantAccount.class);
        criteria.eq(criteria.proto().invalid(), Boolean.FALSE);
        criteria.eq(criteria.proto().status(), MerchantAccountActivationStatus.Active);
        criteria.eq(criteria.proto()._buildings().$().units().$().leases().$().billingAccount(), paymentRecord.billingAccount());
        for (MerchantAccount merchantAccount : Persistence.service().query(criteria)) {
            if (!merchantAccount.merchantTerminalId().isNull()) {
                return merchantAccount;
            }
        }
        throw new UserRuntimeException(i18n.tr("No active merchantAccount found to process the payment"));
    }

    public static MerchantAccount retrieveValidMerchantAccount(Building buildingId) {
        EntityQueryCriteria<MerchantAccount> criteria = EntityQueryCriteria.create(MerchantAccount.class);
        criteria.eq(criteria.proto()._buildings(), buildingId);
        return Persistence.service().retrieve(criteria);
    }

    static AllowedPaymentsSetup getAllowedPaymentsSetup(BillingAccount billingAccountId, PaymentMethodTarget paymentMethodTarget,
            VistaApplication vistaApplication) {
        AllowedPaymentsSetup to = EntityFactory.create(AllowedPaymentsSetup.class);
        to.electronicPaymentsAllowed().setValue(isElectronicPaymentsSetup(billingAccountId));
        to.allowedPaymentTypes().setCollectionValue(Collections.<PaymentType> emptySet());
        to.allowedCardTypes().setCollectionValue(Collections.<CreditCardType> emptySet());
        to.convenienceFeeApplicableCardTypes().setCollectionValue(Collections.<CreditCardType> emptySet());

        BillingAccount billingAccount = billingAccountId.duplicate();
        Persistence.ensureRetrieve(billingAccount, AttachLevel.Attached);
        PaymentAccepted paymentAccepted = billingAccount.paymentAccepted().getValue();
        if (paymentAccepted != PaymentAccepted.DoNotAccept) {

            AbstractPaymentSetup systemSetup = ServerSideFactory.create(Vista2PmcFacade.class).getPaymentSetup();
            MerchantElectronicPaymentSetup merchantSetup = getEffectiveElectronicPaymentsSetup(billingAccountId);
            PaymentTypeSelectionPolicy paymentMethodSelectionPolicy;
            {
                EntityQueryCriteria<Building> criteria = EntityQueryCriteria.create(Building.class);
                criteria.add(PropertyCriterion.eq(criteria.proto().units().$().leases().$().billingAccount(), billingAccountId));
                Building policyNode = Persistence.service().retrieve(criteria, AttachLevel.IdOnly);
                paymentMethodSelectionPolicy = ServerSideFactory.create(PolicyFacade.class).obtainEffectivePolicy(policyNode, PaymentTypeSelectionPolicy.class);
            }
            to.allowedPaymentTypes().setCollectionValue(
                    PaymentAcceptanceUtils.getAllowedPaymentTypes(paymentMethodTarget, vistaApplication, merchantSetup, systemSetup,
                            paymentAccepted == PaymentAccepted.CashEquivalent, paymentMethodSelectionPolicy));

            if (merchantSetup.acceptedCreditCard().getValue(false)) {
                to.allowedCardTypes().setCollectionValue(
                        PaymentAcceptanceUtils.getAllowedCreditCardTypes(paymentMethodTarget, vistaApplication, merchantSetup, systemSetup,
                                paymentAccepted == PaymentAccepted.CashEquivalent, paymentMethodSelectionPolicy, false));
                to.convenienceFeeApplicableCardTypes().setCollectionValue(
                        PaymentAcceptanceUtils.getAllowedCreditCardTypes(paymentMethodTarget, vistaApplication, merchantSetup, systemSetup,
                                paymentAccepted == PaymentAccepted.CashEquivalent, paymentMethodSelectionPolicy, true));
            }
        }
        return to;
    }

    static AllowedPaymentsSetup getAllowedPaymentsSetup(Building policyNode, PaymentMethodTarget paymentMethodTarget, VistaApplication vistaApplication) {
        AllowedPaymentsSetup to = EntityFactory.create(AllowedPaymentsSetup.class);
        to.electronicPaymentsAllowed().setValue(isElectronicPaymentsSetup(policyNode));
        to.allowedPaymentTypes().setCollectionValue(Collections.<PaymentType> emptySet());
        to.allowedCardTypes().setCollectionValue(Collections.<CreditCardType> emptySet());
        to.convenienceFeeApplicableCardTypes().setCollectionValue(Collections.<CreditCardType> emptySet());

        AbstractPaymentSetup systemSetup = ServerSideFactory.create(Vista2PmcFacade.class).getPaymentSetup();
        MerchantElectronicPaymentSetup merchantSetup = getEffectiveElectronicPaymentsSetup(policyNode);
        PaymentTypeSelectionPolicy paymentMethodSelectionPolicy = ServerSideFactory.create(PolicyFacade.class).obtainEffectivePolicy(policyNode,
                PaymentTypeSelectionPolicy.class);

        boolean requireCashEquivalent = false;

        to.allowedPaymentTypes().setCollectionValue(
                PaymentAcceptanceUtils.getAllowedPaymentTypes(paymentMethodTarget, vistaApplication, merchantSetup, systemSetup, requireCashEquivalent,
                        paymentMethodSelectionPolicy));

        if (merchantSetup.acceptedCreditCard().getValue(false)) {
            to.allowedCardTypes().setCollectionValue(
                    PaymentAcceptanceUtils.getAllowedCreditCardTypes(paymentMethodTarget, vistaApplication, merchantSetup, systemSetup, requireCashEquivalent,
                            paymentMethodSelectionPolicy, false));
            to.convenienceFeeApplicableCardTypes().setCollectionValue(
                    PaymentAcceptanceUtils.getAllowedCreditCardTypes(paymentMethodTarget, vistaApplication, merchantSetup, systemSetup, requireCashEquivalent,
                            paymentMethodSelectionPolicy, true));
        }

        return to;
    }

    static Collection<PaymentType> getAllowedPaymentTypes(BillingAccount billingAccountId, PaymentMethodTarget paymentMethodTarget,
            VistaApplication vistaApplication) {
        BillingAccount billingAccount = billingAccountId.duplicate();
        Persistence.ensureRetrieve(billingAccount, AttachLevel.Attached);
        PaymentAccepted paymentAccepted = billingAccount.paymentAccepted().getValue();
        if (paymentAccepted == PaymentAccepted.DoNotAccept) {
            return Collections.emptyList();
        }

        AbstractPaymentSetup systemSetup = ServerSideFactory.create(Vista2PmcFacade.class).getPaymentSetup();
        MerchantElectronicPaymentSetup merchantSetup = getEffectiveElectronicPaymentsSetup(billingAccountId);
        PaymentTypeSelectionPolicy paymentMethodSelectionPolicy;
        {
            Persistence.ensureRetrieve(billingAccount.lease(), AttachLevel.IdOnly);
            PolicyNode node = ServerSideFactory.create(LeaseFacade.class).getLeasePolicyNode(billingAccount.lease());
            paymentMethodSelectionPolicy = ServerSideFactory.create(PolicyFacade.class).obtainEffectivePolicy(node, PaymentTypeSelectionPolicy.class);
        }
        return PaymentAcceptanceUtils.getAllowedPaymentTypes(paymentMethodTarget, vistaApplication, merchantSetup, systemSetup,
                paymentAccepted == PaymentAccepted.CashEquivalent, paymentMethodSelectionPolicy);
    }

    public static Collection<CreditCardType> getAllowedCardTypes(BillingAccount billingAccountId, VistaApplication vistaApplication) {
        return getCardTypes(billingAccountId, vistaApplication, false);
    }

    public static Collection<CreditCardType> getConvenienceFeeApplicableCardTypes(BillingAccount billingAccountId, VistaApplication vistaApplication) {
        return getCardTypes(billingAccountId, vistaApplication, true);
    }

    private static Collection<CreditCardType> getCardTypes(BillingAccount billingAccountId, VistaApplication vistaApplication, boolean forConvenienceFeeOnly) {
        AbstractPaymentSetup systemSetup = ServerSideFactory.create(Vista2PmcFacade.class).getPaymentSetup();
        BillingAccount billingAccount = billingAccountId.duplicate();
        Persistence.ensureRetrieve(billingAccount, AttachLevel.Attached);
        PaymentAccepted paymentAccepted = billingAccount.paymentAccepted().getValue();
        MerchantElectronicPaymentSetup merchantSetup = getEffectiveElectronicPaymentsSetup(billingAccountId);
        if ((paymentAccepted == PaymentAccepted.DoNotAccept) || !merchantSetup.acceptedCreditCard().getValue(false)) {
            return Collections.emptyList();
        }
        PaymentTypeSelectionPolicy paymentMethodSelectionPolicy;
        {
            Persistence.ensureRetrieve(billingAccount.lease(), AttachLevel.IdOnly);
            PolicyNode node = ServerSideFactory.create(LeaseFacade.class).getLeasePolicyNode(billingAccount.lease());
            paymentMethodSelectionPolicy = ServerSideFactory.create(PolicyFacade.class).obtainEffectivePolicy(node, PaymentTypeSelectionPolicy.class);
        }
        return PaymentAcceptanceUtils.getAllowedCreditCardTypes(PaymentMethodTarget.TODO, vistaApplication, merchantSetup, systemSetup,
                paymentAccepted == PaymentAccepted.CashEquivalent, paymentMethodSelectionPolicy, forConvenienceFeeOnly);
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

    public static boolean validateEcheck(EcheckInfo eci) {
        if (eci.accountNo().newNumber().isNull() || eci.bankId().isNull() || eci.branchTransitNumber().isNull()) {
            return false;
        }
        if (!ValidationUtils.isAccountNumberValid(eci.accountNo().newNumber().getValue())) {
            return false;
        }
        if (!ValidationUtils.isBankIdNumberValid(eci.bankId().getValue())) {
            return false;
        }
        if (!ValidationUtils.isBranchTransitNumberValid(eci.branchTransitNumber().getValue())) {
            return false;
        }
        return true;
    }

}
