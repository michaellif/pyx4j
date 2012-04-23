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

import java.util.List;

import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.financial.billing.Bill;
import com.propertyvista.domain.financial.billing.InvoiceAccountCharge;
import com.propertyvista.domain.financial.billing.InvoiceAccountCredit;
import com.propertyvista.domain.financial.billing.InvoiceCharge;
import com.propertyvista.domain.financial.billing.InvoiceCredit;
import com.propertyvista.domain.financial.billing.InvoiceDebit;
import com.propertyvista.domain.financial.billing.InvoiceDeposit;
import com.propertyvista.domain.financial.billing.InvoiceDepositRefund;
import com.propertyvista.domain.financial.billing.InvoiceInitialDebit;
import com.propertyvista.domain.financial.billing.InvoiceLineItem;
import com.propertyvista.domain.financial.billing.InvoicePayment;
import com.propertyvista.domain.financial.billing.InvoicePaymentBackOut;
import com.propertyvista.domain.financial.billing.InvoiceProductCharge;
import com.propertyvista.domain.financial.billing.InvoiceProductCredit;
import com.propertyvista.domain.financial.billing.InvoiceWithdrawal;
import com.propertyvista.domain.payment.CreditCardInfo;

/**
 * Default DebitCredit link rule:
 * 
 * CREDITS:
 * 
 * - {@link InvoicePayment} covers {@link InvoiceDeposit}(s), {@link InvoiceProductCharge}(s) of type 'features', {@link InvoiceAccountCharge}(s),
 * {@link InvoiceProductCharge}(s) of type 'service'
 * 
 * - {@link InvoiceProductCredit} behaves same as {@link InvoicePayment}
 * 
 * - {@link InvoiceAccountCredit} behaves same as {@link InvoicePayment}
 * 
 * - {@link InvoiceInitialCredit} behaves same as {@link InvoicePayment}
 * 
 * - {@link InvoiceDepositRefund} covers {@link InvoiceProductCharge}(s) of the same origin (BillableItem) as deposit. If it is still not consumed apply
 * according to {@link InvoicePayment} rules.
 * 
 * DEBITS:
 * 
 * - {@link InvoiceInitialDebit} is covered by any available credit
 * 
 * - {@link InvoicePaymentBackOut} is covered by {@link InvoicePayment} that is originated from same {@link PaymentRecord}
 * 
 * - {@link InvoiceDeposit} is covered by any available credit
 * 
 * - {@link InvoiceWithdrawal} is covered by any credit.
 * 
 * - {@link InvoiceCharge} is covered by any available credit
 * 
 * @author michaellif
 * 
 */
public class ARCreditDebitLinkManager {

    static void consumeCredit(InvoiceCredit credit) {
        // TODO Auto-generated method stub

    }

    static void consumeCredit(InvoiceCredit credit, List<InvoiceDebit> debits) {
        // TODO Auto-generated method stub

    }

    static void coverDebit(InvoiceDebit debit) {
        // TODO Auto-generated method stub

    }

    static void coverDebit(InvoiceDebit debit, List<InvoiceCredit> credits) {
        // TODO Auto-generated method stub

    }

    static void updateCreditDebitLinks(Bill bill) {
        // TODO Auto-generated method stub

    }

}
