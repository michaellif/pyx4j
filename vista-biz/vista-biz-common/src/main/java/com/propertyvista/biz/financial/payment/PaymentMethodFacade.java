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

import com.pyx4j.commons.LogicalDate;

import com.propertyvista.crm.rpc.dto.financial.autopayreview.ReviewedAutopayAgreementDTO;
import com.propertyvista.domain.financial.BillingAccount;
import com.propertyvista.domain.financial.BillingAccount.BillingPeriod;
import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.financial.billing.BillingCycle;
import com.propertyvista.domain.payment.AutopayAgreement;
import com.propertyvista.domain.payment.CreditCardInfo;
import com.propertyvista.domain.payment.InsurancePaymentMethod;
import com.propertyvista.domain.payment.LeasePaymentMethod;
import com.propertyvista.domain.pmc.Pmc;
import com.propertyvista.domain.pmc.PmcPaymentMethod;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.security.common.VistaApplication;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseParticipant;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant;
import com.propertyvista.domain.tenant.lease.Tenant;
import com.propertyvista.dto.payment.AutoPayReviewLeaseDTO;

public interface PaymentMethodFacade {

    // Lease:
    /**
     * Used when form allow to enter incomplete data
     * 
     * @param paymentMethod
     * @return is payment method can be saved
     */
    boolean isCompleatePaymentMethod(LeasePaymentMethod paymentMethod);

    LeasePaymentMethod persistLeasePaymentMethod(LeasePaymentMethod paymentMethod, Building building);

    void deleteLeasePaymentMethod(LeasePaymentMethod paymentMethodId);

    public enum PaymentMethodUsage {

        InProfile,

        OneTimePayments,

        AutopayAgreementSetup
    }

    List<LeasePaymentMethod> retrieveLeasePaymentMethods(LeaseTermParticipant<? extends LeaseParticipant<?>> participantId, PaymentMethodUsage usage,
            VistaApplication vistaApplication);

    List<LeasePaymentMethod> retrieveLeasePaymentMethods(LeaseParticipant<?> participantId, PaymentMethodUsage usage, VistaApplication vistaApplication);

    // Insurance:

    InsurancePaymentMethod persistInsurancePaymentMethod(InsurancePaymentMethod paymentMethod, Tenant tenantId);

    InsurancePaymentMethod retrieveInsurancePaymentMethod(Tenant tenantId);

    // PMC:

    PmcPaymentMethod persistPmcPaymentMethod(CreditCardInfo creditCardInfo, Pmc pmc);

    PmcPaymentMethod persistPmcPaymentMethod(PmcPaymentMethod paymentMethod);

    // PAP:

    AutopayAgreement persistAutopayAgreement(AutopayAgreement preauthorizedPayment, Tenant tenantId);

    void persitAutopayAgreementReview(ReviewedAutopayAgreementDTO preauthorizedPaymentChanges);

    void deleteAutopayAgreement(AutopayAgreement preauthorizedPaymentId);

    /**
     * Suspend PreauthorizedPayments if required during LeaseTerm finalize.
     * 
     * Update PreauthorizedPaymentCoveredItem to point to new BillableItem (May not be required after BillableItem version support)
     */
    void renewAutopayAgreements(Lease lease);

    /**
     * Terminate AutopayAgreements according to LeaseState and AutoPay policy during Lease persist.
     */
    void terminateAutopayAgreements(Lease lease);

    AutoPayReviewLeaseDTO getAutopayAgreementRequiresReview(BillingAccount billingAccountId);

    List<AutopayAgreement> retrieveAutopayAgreements(Tenant tenantId);

    List<AutopayAgreement> retrieveAutopayAgreements(Lease lease);

    BillingCycle getNextAutopayBillingCycle(Lease lease);

    BillingCycle getNextAutopayBillingCycle(Building buildingId, BillingPeriod billingPeriod, Integer billingCycleStartDay);

    LogicalDate getNextAutopayDate(Lease lease);

    List<PaymentRecord> calulatePreauthorizedPayment(BillingCycle billingCycle, BillingAccount billingAccountId);
}
