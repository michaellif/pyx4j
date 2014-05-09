/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-11-25
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.biz.financial.payment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.Validate;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.config.server.SystemDateManager;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.biz.financial.billingcycle.BillingCycleFacade;
import com.propertyvista.biz.system.AuditFacade;
import com.propertyvista.crm.rpc.dto.financial.autopayreview.ReviewedAutopayAgreementDTO;
import com.propertyvista.domain.financial.BillingAccount;
import com.propertyvista.domain.financial.BillingAccount.BillingPeriod;
import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.financial.billing.BillingCycle;
import com.propertyvista.domain.payment.AutopayAgreement;
import com.propertyvista.domain.payment.CreditCardInfo;
import com.propertyvista.domain.payment.CreditCardInfo.CreditCardType;
import com.propertyvista.domain.payment.InsurancePaymentMethod;
import com.propertyvista.domain.payment.LeasePaymentMethod;
import com.propertyvista.domain.payment.PaymentType;
import com.propertyvista.domain.pmc.Pmc;
import com.propertyvista.domain.pmc.PmcPaymentMethod;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.security.common.VistaApplication;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.Lease.Status;
import com.propertyvista.domain.tenant.lease.LeaseParticipant;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant;
import com.propertyvista.domain.tenant.lease.Tenant;
import com.propertyvista.dto.payment.AutoPayReviewLeaseDTO;

public class PaymentMethodFacadeImpl implements PaymentMethodFacade {

    @Override
    public boolean isCompletePaymentMethod(LeasePaymentMethod paymentMethod) {
        return PaymentMethodPersister.isCompletePaymentMethod(paymentMethod);
    }

    @Override
    public LeasePaymentMethod persistLeasePaymentMethod(LeasePaymentMethod paymentMethod, Building building) {
        return PaymentMethodPersister.persistLeasePaymentMethod(building, paymentMethod);
    }

    @Override
    public void deleteLeasePaymentMethod(LeasePaymentMethod paymentMethodId) {
        LeasePaymentMethod paymentMethod = Persistence.service().retrieve(LeasePaymentMethod.class, paymentMethodId.getPrimaryKey());
        paymentMethod.isDeleted().setValue(Boolean.TRUE);
        paymentMethod.isProfiledMethod().setValue(Boolean.FALSE);
        Persistence.service().merge(paymentMethod);
        ServerSideFactory.create(AuditFacade.class).updated(paymentMethod, "Deleted");
        new ScheduledPaymentsManager().cancelScheduledPayments(paymentMethod);
        // delete associated PreauthorizedPayments
        new AutopayAgreementMananger().deletePreauthorizedPayments(paymentMethod);
    }

    @Override
    public List<LeasePaymentMethod> retrieveLeasePaymentMethods(LeaseTermParticipant<? extends LeaseParticipant<?>> participantId, PaymentMethodUsage usage,
            VistaApplication vistaApplication) {
        Persistence.ensureRetrieve(participantId, AttachLevel.Attached);
        return retrieveLeasePaymentMethods(participantId.leaseParticipant(), usage, vistaApplication);
    }

    @Override
    public List<LeasePaymentMethod> retrieveLeasePaymentMethods(LeaseParticipant<?> participantId, PaymentMethodUsage usage, VistaApplication vistaApplication) {

        List<LeasePaymentMethod> allMethods;
        {
            EntityQueryCriteria<LeasePaymentMethod> criteria = new EntityQueryCriteria<LeasePaymentMethod>(LeasePaymentMethod.class);
            criteria.eq(criteria.proto().customer()._tenantInLease(), participantId);
            criteria.eq(criteria.proto().isProfiledMethod(), Boolean.TRUE);
            criteria.eq(criteria.proto().isDeleted(), Boolean.FALSE);
            allMethods = Persistence.secureQuery(criteria);
        }

        // Optimization
        if (allMethods.isEmpty()) {
            return Collections.emptyList();
        }

        if (usage == PaymentMethodUsage.InProfile) {
            // Show all Already saved Method,  Do not apply filtering
            return allMethods;
        }

        BillingAccount billingAccount;
        {
            EntityQueryCriteria<BillingAccount> criteria = new EntityQueryCriteria<BillingAccount>(BillingAccount.class);
            criteria.eq(criteria.proto().lease().leaseParticipants(), participantId);
            billingAccount = Persistence.service().retrieve(criteria);
        }

        Collection<PaymentType> allowedTypes = ServerSideFactory.create(PaymentFacade.class).getAllowedPaymentTypes(billingAccount, vistaApplication);
        // get payer's payment methods and remove non-allowed ones:

        Collection<CreditCardType> allowedCardTypes = ServerSideFactory.create(PaymentFacade.class).getAllowedCardTypes(billingAccount, vistaApplication);

        if (usage == PaymentMethodUsage.AutopayAgreementSetup) {
            Collection<CreditCardType> restrictedCardTypes = ServerSideFactory.create(PaymentFacade.class).getConvenienceFeeApplicableCardTypes(billingAccount,
                    vistaApplication);
            allowedCardTypes = new ArrayList<CreditCardType>(allowedCardTypes);
            allowedCardTypes.removeAll(restrictedCardTypes);
        }

        List<LeasePaymentMethod> filteredMethods = new ArrayList<LeasePaymentMethod>();
        for (LeasePaymentMethod pm : allMethods) {
            if (!allowedTypes.contains(pm.type().getValue())) {
                continue;
            }
            if ((pm.type().getValue() == PaymentType.CreditCard) && (!allowedCardTypes.contains(pm.details().<CreditCardInfo> cast().cardType().getValue()))) {
                continue;
            }
            filteredMethods.add(pm);
        }

        return filteredMethods;
    }

    @Override
    public InsurancePaymentMethod retrieveInsurancePaymentMethod(Tenant tenantId) {
        EntityQueryCriteria<InsurancePaymentMethod> criteria = EntityQueryCriteria.create(InsurancePaymentMethod.class);
        criteria.eq(criteria.proto().tenant(), tenantId);
        criteria.eq(criteria.proto().isDeleted(), Boolean.FALSE);
        return Persistence.service().retrieve(criteria);
    }

    @Override
    public InsurancePaymentMethod persistInsurancePaymentMethod(InsurancePaymentMethod paymentMethod, Tenant tenantId) {
        Validate.isTrue(paymentMethod.tenant().equals(tenantId));
        Validate.isTrue(PaymentType.availableInInsurance().contains(paymentMethod.type().getValue()));
        return PaymentMethodPersister.persistInsurancePaymentMethod(paymentMethod);
    }

    @Override
    public PmcPaymentMethod persistPmcPaymentMethod(CreditCardInfo creditCardInfo, Pmc pmc) {
        PmcPaymentMethod pmcPaymentMethod = EntityFactory.create(PmcPaymentMethod.class);
        pmcPaymentMethod.pmc().set(pmc);
        pmcPaymentMethod.details().set(creditCardInfo);
        pmcPaymentMethod.type().setValue(PaymentType.CreditCard);
        return PaymentMethodPersister.persistPaymentMethod(pmcPaymentMethod, null, new MerchantTerminalSourceVista());
    }

    @Override
    public PmcPaymentMethod persistPmcPaymentMethod(PmcPaymentMethod paymentMethod) {
        return PaymentMethodPersister.persistPmcPaymentMethod(paymentMethod);
    }

    @Override
    public AutopayAgreement persistAutopayAgreement(AutopayAgreement preauthorizedPayment, Tenant tenantId) {
        return new AutopayAgreementMananger().persistAutopayAgreement(preauthorizedPayment, tenantId);
    }

    @Override
    public void persitAutopayAgreementReview(ReviewedAutopayAgreementDTO preauthorizedPaymentChanges) {
        new AutopayAgreementMananger().persitAutopayAgreementReview(preauthorizedPaymentChanges);
    }

    @Override
    public void deleteAutopayAgreement(AutopayAgreement preauthorizedPayment) {
        new AutopayAgreementMananger().deleteAutopayAgreement(preauthorizedPayment, true);
    }

    @Override
    public List<AutopayAgreement> retrieveAutopayAgreements(Tenant tenantId) {
        return new AutopayAgreementMananger().retrieveAutopayAgreements(tenantId);
    }

    @Override
    public List<AutopayAgreement> retrieveAutopayAgreements(Lease lease) {
        return new AutopayAgreementMananger().retrieveAutopayAgreements(lease);
    }

    @Override
    public BillingCycle getNextAutopayBillingCycle(Lease lease) {
        LogicalDate when = SystemDateManager.getLogicalDate();
        Persistence.ensureRetrieve(lease, AttachLevel.Attached);
        if (lease.status().getValue() == Status.Approved && when.before(lease.leaseFrom().getValue())) {
            when = lease.leaseFrom().getValue();
        }
        BillingCycle billingCycle = ServerSideFactory.create(BillingCycleFacade.class).getBillingCycleForDate(lease, when);
        while (!billingCycle.targetAutopayExecutionDate().getValue().after(when)) {
            billingCycle = ServerSideFactory.create(BillingCycleFacade.class).getSubsequentBillingCycle(billingCycle);
        }
        // Check that autopay was not executed for this date:
        while (!billingCycle.actualAutopayExecutionDate().isNull()) {
            billingCycle = ServerSideFactory.create(BillingCycleFacade.class).getSubsequentBillingCycle(billingCycle);
        }

        return billingCycle;
    }

    @Override
    public BillingCycle getNextAutopayBillingCycle(Building buildingId, BillingPeriod billingPeriod, Integer billingCycleStartDay) {
        LogicalDate when = SystemDateManager.getLogicalDate();
        BillingCycle billingCycle = ServerSideFactory.create(BillingCycleFacade.class).getBillingCycleForDate(buildingId, billingPeriod, billingCycleStartDay,
                when);
        while (!billingCycle.targetAutopayExecutionDate().getValue().after(when)) {
            billingCycle = ServerSideFactory.create(BillingCycleFacade.class).getSubsequentBillingCycle(billingCycle);
        }
        return billingCycle;
    }

    @Override
    public LogicalDate getNextAutopayDate(Lease lease) {
        return getNextAutopayBillingCycle(lease).targetAutopayExecutionDate().getValue();
    }

    @Override
    public void renewAutopayAgreements(Lease lease) {
        new AutopayAgreementMananger().renewPreauthorizedPayments(lease);
    }

    @Override
    public void terminateAutopayAgreements(Lease lease) {
        new AutopayAgreementMananger().terminateAutopayAgreements(lease);
    }

    @Override
    public void deleteAutopayAgreements(Lease lease, boolean sendNotification) {
        new AutopayAgreementMananger().deleteAutopayAgreements(lease, sendNotification);
    }

    @Override
    public AutoPayReviewLeaseDTO getAutopayAgreementRequiresReview(BillingAccount billingAccount) {
        return new AutopayReviewReport().getPreauthorizedPaymentRequiresReview(billingAccount);
    }

    @Override
    public List<PaymentRecord> calulatePreauthorizedPayment(BillingCycle billingCycle, BillingAccount billingAccountId) {
        return new AutopaytManager().calulatePreauthorizedPayment(billingCycle, billingAccountId);
    }

}
