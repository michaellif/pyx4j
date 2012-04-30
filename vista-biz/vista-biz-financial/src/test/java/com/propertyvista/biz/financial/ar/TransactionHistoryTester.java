/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 1, 2012
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.biz.financial.ar;

import java.math.BigDecimal;
import java.util.List;

import com.pyx4j.commons.LogicalDate;

import com.propertyvista.biz.financial.Tester;
import com.propertyvista.domain.financial.billing.InvoiceCredit;
import com.propertyvista.domain.financial.billing.InvoiceDebit;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.dto.TransactionHistoryDTO;

public class TransactionHistoryTester extends Tester {

    private final TransactionHistoryDTO transactionHistory;

    private final List<InvoiceDebit> notCoveredDebitInvoiceLineItems;

    private final List<InvoiceCredit> notConsumedCreditInvoiceLineItems;

    public TransactionHistoryTester(Lease lease) {
        transactionHistory = ARTransactionHistoryManager.getTransactionHistory(lease);
        notCoveredDebitInvoiceLineItems = ARTransactionHistoryManager.getNotCoveredDebitInvoiceLineItems(lease);
        notConsumedCreditInvoiceLineItems = ARTransactionHistoryManager.getNotConsumedCreditInvoiceLineItems(lease);
    }

    public TransactionHistoryTester lineItemSize(int size) {
        assertEquals("Line item size", size, transactionHistory.lineItems().size());
        return this;
    }

    public TransactionHistoryTester notCoveredDebitLineItemSize(int size) {
//        assertEquals("Line item size", size, notCoveredDebitInvoiceLineItems.size());
        return this;
    }

    public TransactionHistoryTester notConsumedCreditInvoiceItemSize(int size) {
//        assertEquals("Line item size", size, notConsumedCreditInvoiceLineItems.size());
        return this;
    }

    public TransactionHistoryTester lineItemAmount(BigDecimal amount, int index) {
        assertEquals("Line item amount", amount, transactionHistory.lineItems().get(index).amount());
        return this;
    }

    public TransactionHistoryTester lineItemDescription(String descr, int index) {
        assertEquals("Line item description", descr, transactionHistory.lineItems().get(index).description());
        return this;
    }

    public TransactionHistoryTester lineItemPostedDate(LogicalDate date, int index) {
        assertEquals("Line item date", date, transactionHistory.lineItems().get(index).postDate());
        return this;
    }

    public TransactionHistoryTester agingBucketsCurrent(BigDecimal amount, int index) {
        assertEquals("Total current amount", amount, transactionHistory.agingBuckets().get(index).current().getValue());
        return this;
    }

    public TransactionHistoryTester agingBuckets30(BigDecimal amount, int index) {
        assertEquals("Total current amount", amount, transactionHistory.agingBuckets().get(index).bucket30().getValue());
        return this;
    }

    public TransactionHistoryTester agingBuckets60(BigDecimal amount, int index) {
        assertEquals("Total current amount", amount, transactionHistory.agingBuckets().get(index).bucket60().getValue());
        return this;
    }

    public TransactionHistoryTester agingBuckets90(BigDecimal amount, int index) {
        assertEquals("Total current amount", amount, transactionHistory.agingBuckets().get(index).bucket90().getValue());
        return this;
    }

}
