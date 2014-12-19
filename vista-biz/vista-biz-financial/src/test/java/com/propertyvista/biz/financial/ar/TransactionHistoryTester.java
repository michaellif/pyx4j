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
 */
package com.propertyvista.biz.financial.ar;

import java.math.BigDecimal;
import java.util.List;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.server.ServerSideFactory;

import com.propertyvista.domain.financial.ARCode;
import com.propertyvista.domain.financial.BillingAccount;
import com.propertyvista.domain.financial.billing.AgingBuckets;
import com.propertyvista.domain.financial.billing.InvoiceCredit;
import com.propertyvista.domain.financial.billing.InvoiceDebit;
import com.propertyvista.dto.TransactionHistoryDTO;
import com.propertyvista.test.integration.Tester;

public class TransactionHistoryTester extends Tester {

    private final TransactionHistoryDTO transactionHistory;

    private final List<InvoiceDebit> notCoveredDebitInvoiceLineItems;

    private final List<InvoiceCredit> notConsumedCreditInvoiceLineItems;

    public TransactionHistoryTester(BillingAccount billingAccount) {
        super();
        BillingAccount internalBillingAccount = billingAccount;
        transactionHistory = ServerSideFactory.create(ARFacade.class).getTransactionHistory(internalBillingAccount);
        notCoveredDebitInvoiceLineItems = ServerSideFactory.create(ARFacade.class).getNotCoveredDebitInvoiceLineItems(internalBillingAccount);
        notConsumedCreditInvoiceLineItems = ServerSideFactory.create(ARFacade.class).getNotConsumedCreditInvoiceLineItems(internalBillingAccount);
    }

    public TransactionHistoryTester lineItemSize(int size) {
        assertEquals("Line item size", size, transactionHistory.lineItems().size());
        return this;
    }

    public TransactionHistoryTester notCoveredDebitLineItemSize(int size) {
        assertEquals("Not Covered Debit Line items", size, notCoveredDebitInvoiceLineItems.size());
        return this;
    }

    public TransactionHistoryTester notConsumedCreditInvoiceItemSize(int size) {
        assertEquals("Not Consumed Credit Invoice Items", size, notConsumedCreditInvoiceLineItems.size());
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

    public TransactionHistoryTester outstandingDebit(BigDecimal amount, int index) {
        assertEquals("Outstanding Debit amount", amount, ((InvoiceDebit) transactionHistory.lineItems().get(index)).outstandingDebit().getValue());
        return this;
    }

    public TransactionHistoryTester outstandingCredit(BigDecimal amount, int index) {
        assertEquals("Outstanding Credit amount", amount, ((InvoiceCredit) transactionHistory.lineItems().get(index)).outstandingCredit().getValue());
        return this;
    }

    public TransactionHistoryTester agingBucketsCurrent(BigDecimal amount, ARCode.Type debitType) {
        AgingBuckets<?> buckets = getAgingBucketsOfType(debitType);
        if (buckets != null) {
            assertEquals("Aging Buckets Current amount", amount, buckets.bucketCurrent().getValue());
        } else {
            throw new Error("Buckets for debit type " + debitType + " don't exist");
        }
        return this;
    }

    public TransactionHistoryTester agingBuckets30(BigDecimal amount, ARCode.Type debitType) {
        AgingBuckets<?> buckets = getAgingBucketsOfType(debitType);
        if (buckets != null) {
            assertEquals("bucket30 amount", amount, buckets.bucket30().getValue());
        } else {
            throw new Error("Buckets for debit type " + debitType + " don't exist");
        }
        return this;
    }

    public TransactionHistoryTester agingBuckets60(BigDecimal amount, ARCode.Type debitType) {
        AgingBuckets<?> buckets = getAgingBucketsOfType(debitType);
        if (buckets != null) {
            assertEquals("bucket60 amount", amount, buckets.bucket60().getValue());
        } else {
            throw new Error("Buckets for debit type " + debitType + " don't exist");
        }
        return this;
    }

    public TransactionHistoryTester agingBuckets90(BigDecimal amount, ARCode.Type debitType) {
        AgingBuckets<?> buckets = getAgingBucketsOfType(debitType);
        if (buckets != null) {
            assertEquals("bucket90 amount", amount, buckets.bucket90().getValue());
        } else {
            throw new Error("Buckets for debit type " + debitType + " don't exist");
        }
        return this;
    }

    public TransactionHistoryTester agingBucketOver90(BigDecimal amount, ARCode.Type debitType) {
        AgingBuckets<?> buckets = getAgingBucketsOfType(debitType);
        if (buckets != null) {
            assertEquals("bucketOver90 amount", amount, buckets.bucketOver90().getValue());
        } else {
            throw new Error("Buckets for debit type " + debitType + " don't exist");
        }
        return this;
    }

    private AgingBuckets<?> getAgingBucketsOfType(ARCode.Type debitType) {
        if (debitType == null) {
            return transactionHistory.totalAgingBuckets();
        }
        for (AgingBuckets<?> buckets : transactionHistory.agingBuckets()) {
            if (debitType == buckets.arCode().getValue()) {
                return buckets;
            }
        }
        return null;
    }

}
