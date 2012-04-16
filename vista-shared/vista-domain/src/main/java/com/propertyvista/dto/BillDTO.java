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

import com.pyx4j.entity.annotations.ExtendsDBO;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.shared.IList;

import com.propertyvista.domain.financial.billing.Bill;
import com.propertyvista.domain.financial.billing.InvoiceAccountCharge;
import com.propertyvista.domain.financial.billing.InvoiceAccountCredit;
import com.propertyvista.domain.financial.billing.InvoiceDeposit;
import com.propertyvista.domain.financial.billing.InvoiceDepositRefund;
import com.propertyvista.domain.financial.billing.InvoicePayment;
import com.propertyvista.domain.financial.billing.InvoiceProductCharge;
import com.propertyvista.domain.financial.billing.InvoiceWithdrawal;
import com.propertyvista.domain.tenant.lease.Lease;

@Transient
@ExtendsDBO
public interface BillDTO extends Bill {

    Lease lease();

    IList<InvoiceProductCharge> serviceChargeLineItems();

    // Current Bill charges
    IList<InvoiceProductCharge> recurringFeatureChargeLineItems();

    IList<InvoiceProductCharge> onetimeFeatureChargeLineItems();

//    IList<InvoiceProductCredit> productCreditLineItems();

    IList<InvoiceDeposit> depositLineItems();

    // Last Bill charges and payments
    IList<InvoiceDepositRefund> depositRefundLineItems();

    IList<InvoiceAccountCredit> accountCreditLineItems();

    IList<InvoiceAccountCharge> accountChargeLineItems();

    IList<InvoiceWithdrawal> withdrawalLineItems();

    IList<InvoicePayment> rejectedPaymentLineItems();

    IList<InvoicePayment> paymentLineItems();

}
