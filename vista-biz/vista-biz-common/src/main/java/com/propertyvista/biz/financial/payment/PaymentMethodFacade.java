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

import com.propertyvista.domain.financial.BillingAccount;
import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.financial.billing.BillingCycle;
import com.propertyvista.domain.payment.CreditCardInfo;
import com.propertyvista.domain.payment.InsurancePaymentMethod;
import com.propertyvista.domain.payment.LeasePaymentMethod;
import com.propertyvista.domain.payment.PreauthorizedPayment;
import com.propertyvista.domain.pmc.Pmc;
import com.propertyvista.domain.pmc.PmcPaymentMethod;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.tenant.Customer;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant;
import com.propertyvista.domain.tenant.lease.Tenant;
import com.propertyvista.dto.payment.AutoPayReviewDTO;

public interface PaymentMethodFacade {

    // Lease:

    LeasePaymentMethod persistLeasePaymentMethod(LeasePaymentMethod paymentMethod, Building building);

    void deleteLeasePaymentMethod(LeasePaymentMethod paymentMethodId);

    List<LeasePaymentMethod> retrieveLeasePaymentMethods(LeaseTermParticipant<?> participant);

    List<LeasePaymentMethod> retrieveLeasePaymentMethods(Customer customer);

    // Insurance:

    InsurancePaymentMethod persistInsurancePaymentMethod(InsurancePaymentMethod paymentMethod, Tenant tenantId);

    InsurancePaymentMethod retrieveInsurancePaymentMethod(Tenant tenantId);

    // PMC:

    PmcPaymentMethod persistPmcPaymentMethod(CreditCardInfo creditCardInfo, Pmc pmc);

    PmcPaymentMethod persistPmcPaymentMethod(PmcPaymentMethod paymentMethod);

    // PAP:

    PreauthorizedPayment persistPreauthorizedPayment(PreauthorizedPayment preauthorizedPayment, Tenant tenantId);

    void deletePreauthorizedPayment(PreauthorizedPayment preauthorizedPaymentId);

    void suspendPreauthorizedPayment(PreauthorizedPayment preauthorizedPaymentId);

    /**
     * Suspend PreauthorizedPayments if required during LeaseTerm finalize.
     * 
     * Update PreauthorizedPaymentCoveredItem to point to new BillableItem (May not be required after BillableItem version support)
     */
    void renewPreauthorizedPayments(Lease lease);

    AutoPayReviewDTO getSuspendedPreauthorizedPaymentReview(BillingAccount billingAccountId);

    List<PreauthorizedPayment> retrievePreauthorizedPayments(Tenant tenantId);

    BillingCycle getNextScheduledPreauthorizedPaymentBillingCycle(Lease lease);

    LogicalDate getNextScheduledPreauthorizedPaymentDate(Lease lease);

    LogicalDate getPreauthorizedPaymentCutOffDate(Lease lease);

    List<PaymentRecord> calulatePreauthorizedPayment(BillingCycle billingCycle, BillingAccount billingAccountId);

}
