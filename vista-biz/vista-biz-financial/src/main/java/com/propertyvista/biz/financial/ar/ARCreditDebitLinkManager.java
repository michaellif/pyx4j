/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Apr 22, 2012
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.biz.financial.ar;

import com.propertyvista.domain.financial.billing.Bill;
import com.propertyvista.domain.financial.billing.InvoiceAccountCharge;
import com.propertyvista.domain.financial.billing.InvoiceDeposit;
import com.propertyvista.domain.financial.billing.InvoiceDepositRefund;
import com.propertyvista.domain.financial.billing.InvoiceLineItem;
import com.propertyvista.domain.financial.billing.InvoicePayment;
import com.propertyvista.domain.financial.billing.InvoiceProductCharge;
import com.propertyvista.domain.financial.billing.InvoiceProductCredit;
import com.propertyvista.domain.financial.billing.InvoiceWithdrawal;

/**
 * Default DebitCredit link rule:
 * 
 * - {@link InvoicePayment} covers {@link InvoiceDeposit}(s), {@link InvoiceProductCharge}(s) of type 'features', {@link InvoiceAccountCharge}(s),
 * {@link InvoiceProductCharge}(s) of type 'service'
 * 
 * - {@link InvoiceProductCredit} behaves same as {@link InvoicePayment}
 * 
 * - {@link InvoiceDepositRefund} covers {@link InvoiceProductCharge}(s) of the same type as deposit
 * 
 * 
 * - {@link InvoiceWithdrawal} is linked to {@link InvoiceProductCredit}
 * 
 * @author michaellif
 * 
 */
public class ARCreditDebitLinkManager {

    static void updateCreditDebitLinks(InvoiceLineItem invoiceLineItem) {
        // TODO Auto-generated method stub

    }

    static void updateCreditDebitLinks(Bill bill) {
        // TODO Auto-generated method stub

    }

}
