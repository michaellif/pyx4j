/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 16, 2012
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.biz.financial.ar;

import java.util.List;

import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.financial.billing.InvoiceCredit;
import com.propertyvista.domain.financial.billing.InvoiceDebit;
import com.propertyvista.domain.financial.billing.InvoiceLineItem;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseAdjustment;
import com.propertyvista.dto.TransactionHistoryDTO;

public class ARFacadeImpl implements ARFacade {

    public void postInvoiceLineItem(InvoiceLineItem invoiceLineItem) {
        ARTransactionManager.postInvoiceLineItem(invoiceLineItem);
    }

    @Override
    public void postPayment(PaymentRecord paymentRecord) {
        new ARPaymentProcessor().postPayment(paymentRecord);
    }

    @Override
    public void rejectPayment(PaymentRecord paymentRecord) {
        new ARPaymentProcessor().rejectPayment(paymentRecord);
    }

    @Override
    public void postImmediateAdjustment(LeaseAdjustment adjustment) {
        new ARLeaseAdjustmentProcessor().postImmediateAdjustment(adjustment);
    }

    @Override
    public TransactionHistoryDTO getTransactionHistory(Lease lease) {
        return ARTransactionManager.getTransactionHistory(lease);
    }

    @Override
    public List<InvoiceDebit> getNotCoveredDebitInvoiceLineItems(Lease lease) {
        return ARTransactionManager.getNotCoveredDebitInvoiceLineItems(lease);
    }

    @Override
    public List<InvoiceCredit> getNotConsumedCreditInvoiceLineItems(Lease lease) {
        return ARTransactionManager.getNotConsumedCreditInvoiceLineItems(lease);
    }

    public void consumeCredit(InvoiceCredit credit) {
        ARCreditDebitLinkManager.consumeCredit(credit);
    }

    public void consumeCredit(InvoiceCredit credit, List<InvoiceDebit> debits) {
        ARCreditDebitLinkManager.consumeCredit(credit, debits);
    }

    public void coverDebit(InvoiceDebit debit) {
        ARCreditDebitLinkManager.coverDebit(debit);
    }

    public void coverDebit(InvoiceDebit debit, List<InvoiceCredit> credits) {
        ARCreditDebitLinkManager.coverDebit(debit, credits);
    }

}
