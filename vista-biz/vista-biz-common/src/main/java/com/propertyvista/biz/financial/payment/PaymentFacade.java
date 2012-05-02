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

import com.propertyvista.domain.financial.PaymentRecord;

public interface PaymentFacade {

    /**
     * Cash: automatically -> Received (AR. Posted)
     * Check: Submitted, -> by targetDate Processing (AR. Posted), -> Received or Rejected (AR. Reject)
     * CreditCard: automatically -> Processing (No Posting) -> Received (AR. Posted) or Rejected
     * Interac : As CC.
     * Echeck: automatically -> Processing (AR. Posted) , -> Received or Rejected (AR. Reject)
     * EFT: automatically -> Received (AR. Posted)
     * 
     */
    PaymentRecord submitPayment(PaymentRecord payment);

    PaymentRecord updateCheck(PaymentRecord paymentStub);

    PaymentRecord processCheck(PaymentRecord paymentStub);

    PaymentRecord cancelCheck(PaymentRecord paymentStub);

    PaymentRecord clearCheck(PaymentRecord paymentStub);

    PaymentRecord rejectCheck(PaymentRecord paymentStub);

    // TODO Gap: Make Refunds

}
