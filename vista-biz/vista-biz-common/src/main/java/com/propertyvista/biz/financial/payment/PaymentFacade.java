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

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.gwt.server.deferred.AbstractDeferredProcess.RunningProcess;

import com.propertyvista.domain.financial.AggregatedTransfer;
import com.propertyvista.domain.financial.AllowedPaymentsSetup;
import com.propertyvista.domain.financial.BillingAccount;
import com.propertyvista.domain.financial.PaymentPostingBatch;
import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.payment.CreditCardInfo.CreditCardType;
import com.propertyvista.domain.payment.LeasePaymentMethod;
import com.propertyvista.domain.payment.PaymentType;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.security.common.VistaApplication;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseTerm;
import com.propertyvista.dto.payment.ConvenienceFeeCalculationResponseTO;

public interface PaymentFacade {

    boolean isPaymentsAllowed(BillingAccount billingAccountId);

    AllowedPaymentsSetup getAllowedPaymentsSetup(BillingAccount billingAccountId, VistaApplication vistaApplication);

    AllowedPaymentsSetup getAllowedPaymentsSetup(Building policyNode, VistaApplication vistaApplication);

    Collection<PaymentType> getAllowedPaymentTypes(BillingAccount billingAccountId, VistaApplication vistaApplication);

    Collection<CreditCardType> getAllowedCardTypes(BillingAccount billingAccountId, VistaApplication vistaApplication);

    Collection<CreditCardType> getConvenienceFeeApplicableCardTypes(BillingAccount billingAccountId, VistaApplication vistaApplication);

    ConvenienceFeeCalculationResponseTO getConvenienceFee(BillingAccount billingAccountId, CreditCardType cardType, BigDecimal amount);

    void validatePaymentMethod(BillingAccount billingAccount, LeasePaymentMethod paymentMethod, VistaApplication vistaApplication);

    void validatePayment(PaymentRecord paymentRecord, VistaApplication vistaApplication);

    boolean isElectronicPaymentsSetup(BillingAccount billingAccountId);

    boolean isElectronicPaymentsSetup(Lease leaseId);

    boolean isElectronicPaymentsSetup(LeaseTerm leaseTermId);

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
     * @param paymentBatchContext
     *            optional BatchContext
     * 
     */
    PaymentRecord processPayment(PaymentRecord paymentId, PaymentBatchContext paymentBatchContext) throws PaymentException;

    PaymentRecord cancel(PaymentRecord paymentId);

    PaymentRecord clear(PaymentRecord paymentId);

    PaymentRecord reject(PaymentRecord paymentId, boolean applyNSF);

    // TODO Gap: Make Refunds

    // PMC AggregatedTransfer management

    void cancelAggregatedTransfer(AggregatedTransfer aggregatedTransferStub);

    /** get payments for the last 3 months */
    List<PaymentRecord> getLatestPaymentActivity(BillingAccount billingAccount);

    PaymentPostingBatch createPostingBatch(Building buildingId, LogicalDate receiptDate);

    void cancelPostingBatch(PaymentPostingBatch paymentPostingBatchId, RunningProcess progress);

    void processPostingBatch(PaymentPostingBatch paymentPostingBatchId, RunningProcess progress);

}
