/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-29
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.dto;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.ExtendsDBO;
import com.pyx4j.entity.annotations.Transient;

import com.propertyvista.domain.financial.billing.Bill;
import com.propertyvista.domain.tenant.lease.Lease;

@Transient
@ExtendsDBO
public interface BillDTO extends Bill {

    @Override
    Lease lease();

    @Caption(name = "Service Charges")
    InvoiceLineItemGroupDTO serviceChargeLineItems();

    @Caption(name = "Recurring Feature Charges")
    InvoiceLineItemGroupDTO recurringFeatureChargeLineItems();

    @Caption(name = "One-Time Feature Charges")
    InvoiceLineItemGroupDTO onetimeFeatureChargeLineItems();

    @Caption(name = "Credits")
    InvoiceLineItemGroupDTO productCreditLineItems();

    @Caption(name = "Deposits")
    InvoiceLineItemGroupDTO depositLineItems();

    @Caption(name = "Deposit Refunds")
    InvoiceLineItemGroupDTO depositRefundLineItems();

    //Debit and credit combined
    @Caption(name = "Immediate Account Adjustments")
    InvoiceLineItemGroupDTO immediateAccountAdjustmentLineItems();

    // Debit and credit combined
    @Caption(name = "Pending Account Adjustments")
    InvoiceLineItemGroupDTO pendingAccountAdjustmentLineItems();

    @Caption(name = "NSF Charges")
    InvoiceLineItemGroupDTO nsfChargeLineItems();

    @Caption(name = "Late Payment Fees")
    InvoiceLineItemGroupDTO latePaymentFeeLineItems();

    @Caption(name = "Withdrawals")
    InvoiceLineItemGroupDTO withdrawalLineItems();

    @Caption(name = "Rejected Payments")
    InvoiceLineItemGroupDTO rejectedPaymentLineItems();

    @Caption(name = "Payments Received")
    InvoiceLineItemGroupDTO paymentLineItems();
}
