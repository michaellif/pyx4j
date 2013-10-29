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

import java.util.List;

import org.apache.commons.lang.Validate;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.config.server.SystemDateManager;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.biz.financial.billingcycle.BillingCycleFacade;
import com.propertyvista.biz.system.AuditFacade;
import com.propertyvista.crm.rpc.dto.financial.autopayreview.ReviewedAutopayAgreementDTO;
import com.propertyvista.domain.financial.BillingAccount;
import com.propertyvista.domain.financial.BillingAccount.BillingPeriod;
import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.financial.billing.BillingCycle;
import com.propertyvista.domain.payment.AutopayAgreement;
import com.propertyvista.domain.payment.CreditCardInfo;
import com.propertyvista.domain.payment.InsurancePaymentMethod;
import com.propertyvista.domain.payment.LeasePaymentMethod;
import com.propertyvista.domain.payment.PaymentType;
import com.propertyvista.domain.pmc.Pmc;
import com.propertyvista.domain.pmc.PmcPaymentMethod;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.tenant.Customer;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant;
import com.propertyvista.domain.tenant.lease.Tenant;
import com.propertyvista.dto.payment.AutoPayReviewLeaseDTO;

public class PaymentMethodFacadeImpl implements PaymentMethodFacade {

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
    public List<LeasePaymentMethod> retrieveLeasePaymentMethods(LeaseTermParticipant<?> participant) {
        assert !participant.leaseParticipant().customer().isValueDetached();
        return retrieveLeasePaymentMethods(participant.leaseParticipant().customer());
    }

    @Override
    public List<LeasePaymentMethod> retrieveLeasePaymentMethods(Customer customer) {
        EntityQueryCriteria<LeasePaymentMethod> criteria = new EntityQueryCriteria<LeasePaymentMethod>(LeasePaymentMethod.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().customer(), customer));
        criteria.add(PropertyCriterion.eq(criteria.proto().isProfiledMethod(), Boolean.TRUE));
        criteria.add(PropertyCriterion.eq(criteria.proto().isDeleted(), Boolean.FALSE));

        List<LeasePaymentMethod> methods = Persistence.service().query(criteria);
        return methods;
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
        new AutopayAgreementMananger().deleteAutopayAgreement(preauthorizedPayment);
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
        LogicalDate when = new LogicalDate(SystemDateManager.getDate());
        BillingCycle billingCycle = ServerSideFactory.create(BillingCycleFacade.class).getBillingCycleForDate(lease, when);
        while (!billingCycle.targetAutopayExecutionDate().getValue().after(when)) {
            billingCycle = ServerSideFactory.create(BillingCycleFacade.class).getSubsequentBillingCycle(billingCycle);
        }
        return billingCycle;
    }

    @Override
    public BillingCycle getNextAutopayBillingCycle(Building buildingId, BillingPeriod billingPeriod, Integer billingCycleStartDay) {
        LogicalDate when = new LogicalDate(SystemDateManager.getDate());
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
    public AutoPayReviewLeaseDTO getAutopayAgreementRequiresReview(BillingAccount billingAccount) {
        return new AutopayReviewReport().getPreauthorizedPaymentRequiresReview(billingAccount);
    }

    @Override
    public List<PaymentRecord> calulatePreauthorizedPayment(BillingCycle billingCycle, BillingAccount billingAccountId) {
        return new AutopaytManager().calulatePreauthorizedPayment(billingCycle, billingAccountId);
    }
}
