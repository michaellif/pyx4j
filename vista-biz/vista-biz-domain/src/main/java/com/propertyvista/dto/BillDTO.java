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
import com.pyx4j.entity.annotations.ExtendsBO;
import com.pyx4j.entity.annotations.Transient;

import com.propertyvista.domain.financial.billing.Bill;
import com.propertyvista.domain.tenant.lease.Lease;

@Transient
@ExtendsBO
public interface BillDTO extends Bill {

    @Override
    Lease lease();

    @Caption(name = "Lease Charges")
    InvoiceLineItemGroupDTO serviceChargeLineItems();

    @Caption(name = "Additional Recurring Charges")
    InvoiceLineItemGroupDTO recurringFeatureChargeLineItems();

    @Caption(name = "One-Time Charges")
    InvoiceLineItemGroupDTO onetimeFeatureChargeLineItems();

    @Caption(name = "Credits")
    InvoiceLineItemGroupDTO productCreditLineItems();

    @Caption(name = "Deposits")
    InvoiceLineItemGroupDTO depositLineItems();

    @Caption(name = "Deposit Refund(s)")
    InvoiceLineItemGroupDTO depositRefundLineItems();

    //Debit and credit combined
    @Caption(name = "Immediate Adjustment(s)")
    InvoiceLineItemGroupDTO immediateAccountAdjustmentLineItems();

    // Debit and credit combined
    @Caption(name = "Pending Account Adjustments")
    InvoiceLineItemGroupDTO pendingAccountAdjustmentLineItems();

    @Caption(name = "Previous Charge Refunds")
    InvoiceLineItemGroupDTO previousChargeRefundLineItems();

    @Caption(name = "NSF Charge(s)")
    InvoiceLineItemGroupDTO nsfChargeLineItems();

    @Caption(name = "Withdrawal(s)")
    InvoiceLineItemGroupDTO withdrawalLineItems();

    @Caption(name = "Rejected Payment(s)")
    InvoiceLineItemGroupDTO rejectedPaymentLineItems();

    @Caption(name = "Payment(s) Received")
    InvoiceLineItemGroupDTO paymentLineItems();
}
