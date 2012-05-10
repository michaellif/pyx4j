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
import com.propertyvista.domain.financial.BillingAccount;
import com.propertyvista.domain.financial.billing.AgingBuckets;
import com.propertyvista.domain.financial.billing.InvoiceCredit;
import com.propertyvista.domain.financial.billing.InvoiceDebit;
import com.propertyvista.domain.financial.billing.InvoiceDebit.DebitType;
import com.propertyvista.dto.TransactionHistoryDTO;

public class TransactionHistoryTester extends Tester {

    private final TransactionHistoryDTO transactionHistory;

    private final List<InvoiceDebit> notCoveredDebitInvoiceLineItems;

    private final List<InvoiceCredit> notConsumedCreditInvoiceLineItems;

    public TransactionHistoryTester(BillingAccount billingAccount) {
        transactionHistory = ARTransactionManager.getTransactionHistory(billingAccount);
        notCoveredDebitInvoiceLineItems = ARTransactionManager.getNotCoveredDebitInvoiceLineItems(billingAccount);
        notConsumedCreditInvoiceLineItems = ARTransactionManager.getNotConsumedCreditInvoiceLineItems(billingAccount);
    }

    public TransactionHistoryTester lineItemSize(int size) {
        assertEquals("Line item size", size, transactionHistory.lineItems().size());
        return this;
    }

    public TransactionHistoryTester notCoveredDebitLineItemSize(int size) {
        assertEquals("Line item size", size, notCoveredDebitInvoiceLineItems.size());
        return this;
    }

    public TransactionHistoryTester notConsumedCreditInvoiceItemSize(int size) {
        assertEquals("Line item size", size, notConsumedCreditInvoiceLineItems.size());
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

    public TransactionHistoryTester agingBucketsCurrent(BigDecimal amount, DebitType debitType) {
        AgingBuckets buckets = getAgingBucketsOfType(debitType);
        if (buckets != null) {
            assertEquals("Current amount", amount, buckets.bucketCurrent().getValue());
        } else {
            throw new Error("Buckets for debit type " + debitType + " don't exist");
        }
        return this;
    }

    public TransactionHistoryTester agingBuckets30(BigDecimal amount, DebitType debitType) {
        AgingBuckets buckets = getAgingBucketsOfType(debitType);
        if (buckets != null) {
            assertEquals("bucket30 amount", amount, buckets.bucket30().getValue());
        } else {
            throw new Error("Buckets for debit type " + debitType + " don't exist");
        }
        return this;
    }

    public TransactionHistoryTester agingBuckets60(BigDecimal amount, DebitType debitType) {
        AgingBuckets buckets = getAgingBucketsOfType(debitType);
        if (buckets != null) {
            assertEquals("bucket60 amount", amount, buckets.bucket60().getValue());
        } else {
            throw new Error("Buckets for debit type " + debitType + " don't exist");
        }
        return this;
    }

    public TransactionHistoryTester agingBuckets90(BigDecimal amount, DebitType debitType) {
        AgingBuckets buckets = getAgingBucketsOfType(debitType);
        if (buckets != null) {
            assertEquals("bucket90 amount", amount, buckets.bucket90().getValue());
        } else {
            throw new Error("Buckets for debit type " + debitType + " don't exist");
        }
        return this;
    }

    public TransactionHistoryTester agingBucketOver90(BigDecimal amount, DebitType debitType) {
        AgingBuckets buckets = getAgingBucketsOfType(debitType);
        if (buckets != null) {
            assertEquals("bucketOver90 amount", amount, buckets.bucketOver90().getValue());
        } else {
            throw new Error("Buckets for debit type " + debitType + " don't exist");
        }
        return this;
    }

    private AgingBuckets getAgingBucketsOfType(DebitType debitType) {
        for (AgingBuckets buckets : transactionHistory.agingBuckets()) {
            if (debitType == buckets.debitType().getValue()) {
                return buckets;
            }
        }
        return null;
    }

}
