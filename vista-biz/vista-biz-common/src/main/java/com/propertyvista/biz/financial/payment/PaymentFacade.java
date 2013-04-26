/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 1, 2012
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.biz.financial.payment;

import java.util.Collection;

import com.propertyvista.domain.financial.AggregatedTransfer;
import com.propertyvista.domain.financial.BillingAccount;
import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.payment.PaymentType;
import com.propertyvista.domain.security.common.VistaApplication;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseTerm;

public interface PaymentFacade {

    boolean isPaymentsAllowed(BillingAccount billingAccountId);

    Collection<PaymentType> getAllowedPaymentTypes(BillingAccount billingAccountId, VistaApplication vistaApplication);

    boolean isElectronicPaymentsSetup(BillingAccount billingAccountId);

    boolean isElectronicPaymentsSetup(Lease leaseId);

    boolean isElectronicPaymentsSetup(LeaseTerm leaseTermId);

    //boolean isPaymentsAllowedInPortal(BillingAccount billingAccountId);

    PaymentRecord persistPayment(PaymentRecord paymentRecord);

    PaymentRecord schedulePayment(PaymentRecord paymentId);

    /**
     * Cash: automatically -> Received (AR. Posted)
     * Check: Submitted, -> by targetDate Processing (AR. Posted), -> Received or Rejected (AR. Reject)
     * CreditCard: automatically -> Processing (No Posting) -> Received (AR. Posted) or Rejected
     * Interac : As CC.
     * Echeck: automatically -> Processing (AR. Posted) , -> Received or Rejected (AR. Reject)
     * EFT: automatically -> Received (AR. Posted)
     * 
     */
    PaymentRecord processPayment(PaymentRecord paymentId) throws PaymentException;

    PaymentRecord cancel(PaymentRecord paymentId);

    PaymentRecord clear(PaymentRecord paymentId);

    PaymentRecord reject(PaymentRecord paymentId, boolean applyNSF);

    // TODO Gap: Make Refunds

    // PMC AggregatedTransfer management

    public void cancelAggregatedTransfer(AggregatedTransfer aggregatedTransferStub);

}
