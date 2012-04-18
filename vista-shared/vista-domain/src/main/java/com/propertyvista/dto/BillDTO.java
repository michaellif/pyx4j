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
import com.propertyvista.domain.financial.billing.InvoiceLineItemDetailsDTO;
import com.propertyvista.domain.tenant.lease.Lease;

@Transient
@ExtendsDBO
public interface BillDTO extends Bill {

    Lease lease();

    @Caption(name = "Service Charges")
    InvoiceLineItemDetailsDTO serviceChargeLineItems();

    // Current Bill charges
    @Caption(name = "Product Charges")
    InvoiceLineItemDetailsDTO recurringFeatureChargeLineItems();

    @Caption(name = "One-Time Product Charges")
    InvoiceLineItemDetailsDTO onetimeFeatureChargeLineItems();

//    IList<InvoiceProductCredit> productCreditLineItems();

    @Caption(name = "Deposits")
    InvoiceLineItemDetailsDTO depositLineItems();

    // Last Bill charges and payments
    @Caption(name = "Deposit Refunds")
    InvoiceLineItemDetailsDTO depositRefundLineItems();

    //Both Debit and Credit types of Lease Adjustments
    @Caption(name = "Immediate Adjustments")
    InvoiceLineItemDetailsDTO immediateAdjustmentLineItems();

    // Debit and credit combined
    @Caption(name = "Pending Adjustments")
    InvoiceLineItemDetailsDTO pendingAdjustmentLineItems();

    @Caption(name = "Withdrawals")
    InvoiceLineItemDetailsDTO withdrawalLineItems();

    @Caption(name = "Rejected Payments")
    InvoiceLineItemDetailsDTO rejectedPaymentLineItems();

    @Caption(name = "Payments Received")
    InvoiceLineItemDetailsDTO paymentLineItems();

}
