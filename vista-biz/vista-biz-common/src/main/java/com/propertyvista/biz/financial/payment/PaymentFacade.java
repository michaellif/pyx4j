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

import java.util.List;

import com.propertyvista.domain.financial.AggregatedTransfer;
import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.payment.PaymentMethod;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.tenant.lease.LeaseParticipant;

public interface PaymentFacade {

    PaymentMethod persistPaymentMethod(Building building, PaymentMethod paymentMethod);

    void deletePaymentMethod(PaymentMethod paymentMethod);

    List<PaymentMethod> retrievePaymentMethods(LeaseParticipant participant);

    PaymentRecord persistPayment(PaymentRecord paymentRecord);

    PaymentRecord schedulePayment(PaymentRecord paymentStub);

    /**
     * Cash: automatically -> Received (AR. Posted)
     * Check: Submitted, -> by targetDate Processing (AR. Posted), -> Received or Rejected (AR. Reject)
     * CreditCard: automatically -> Processing (No Posting) -> Received (AR. Posted) or Rejected
     * Interac : As CC.
     * Echeck: automatically -> Processing (AR. Posted) , -> Received or Rejected (AR. Reject)
     * EFT: automatically -> Received (AR. Posted)
     * 
     */
    PaymentRecord processPayment(PaymentRecord paymentStub);

    PaymentRecord cancel(PaymentRecord paymentStub);

    PaymentRecord clear(PaymentRecord paymentStub);

    PaymentRecord reject(PaymentRecord paymentStub);

    // TODO Gap: Make Refunds

    // PMC AggregatedTransfer management

    public void cancelAggregatedTransfer(AggregatedTransfer aggregatedTransferStub);

}
