/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 1, 2012
 * @author michaellif
 */
package com.propertyvista.biz.financial;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;

import com.pyx4j.gwt.server.IOUtils;

import com.propertyvista.domain.financial.billing.AgingBuckets;
import com.propertyvista.domain.financial.billing.InvoiceCredit;
import com.propertyvista.domain.financial.billing.InvoiceDebit;
import com.propertyvista.domain.financial.billing.InvoiceLineItem;
import com.propertyvista.dto.TransactionHistoryDTO;

public class TransactionHistoryPrinter {

    private BigDecimal balanceAmount = BigDecimal.ZERO;

    protected TransactionHistoryPrinter() {
    }

    protected static void printTransactionHistory(TransactionHistoryDTO transactionHistory) {
        new TransactionHistoryPrinter().printTransactionHistory(transactionHistory, new OutputStreamWriter(System.out));
    }

    protected static void printTransactionHistory(TransactionHistoryDTO transactionHistory, String fileName) {
        FileWriter fstream;
        try {
            fstream = new FileWriter(fileName);
        } catch (IOException e) {
            throw new Error(e);
        }
        new TransactionHistoryPrinter().printTransactionHistory(transactionHistory, fstream);
    }

    private void printTransactionHistory(TransactionHistoryDTO transactionHistory, OutputStreamWriter writer) {

        try {
            BufferedWriter out = new BufferedWriter(writer);
            out.write("Transaction History");
            out.newLine();

            out.write("\nIssue Date: " + transactionHistory.issueDate().getValue());
            out.write("\nCurrent Balance: " + transactionHistory.currentBalanceAmount().getValue());
            out.newLine();
            out.newLine();
            out.write(convertToCell("Post Date", 14, true) + convertToCell("Due Date", 14, true) + convertToCell("Description", 60, true)
                    + convertToCell("Debits", 14, true) + convertToCell("Credits", 14, true) + convertToCell("Balance", 14, true));
            out.newLine();

            for (InvoiceLineItem lineItem : transactionHistory.lineItems()) {
                out.write(createLineItem(lineItem));
                out.newLine();
            }

            out.newLine();
            out.write(convertToCell("Type", 14, true) + convertToCell("Current", 14, true) + convertToCell("1-30 Days", 14, true)
                    + convertToCell("31-60 Days", 14, true) + convertToCell("61-90 Days", 14, true) + convertToCell("Over 90 Days", 14, true));
            out.newLine();
            for (AgingBuckets<?> agingBuckets : transactionHistory.agingBuckets()) {
                out.write(createAgingBucketsLine(agingBuckets));
                out.newLine();
            }

            out.write(createAgingBucketsLine(transactionHistory.totalAgingBuckets()));
            out.newLine();

            out.close();
        } catch (IOException e) {
            throw new Error(e);
        } finally {
            IOUtils.closeQuietly(writer);
        }

    }

    private String createLineItem(InvoiceLineItem lineItem) {

        String debits = null;
        String credits = null;
        String dueDate = null;
        String balance = null;
        if (lineItem.isInstanceOf(InvoiceDebit.class)) {
            InvoiceDebit invoiceDebit = lineItem.cast();
            BigDecimal value = lineItem.amount().getValue().add(invoiceDebit.taxTotal().getValue());
            debits = convertToCell(value.toString(), 14, true);
            balanceAmount = balanceAmount.add(value);
            credits = convertToCell("", 14, true);
            if (invoiceDebit.dueDate().getValue() == null) {
                dueDate = convertToCell("N/A", 14, true);
            } else {
                dueDate = convertToCell(invoiceDebit.dueDate().getValue().toString(), 14, true);
            }
        } else if (lineItem.isInstanceOf(InvoiceCredit.class)) {
            debits = convertToCell("", 14, true);
            BigDecimal value = lineItem.amount().getValue();
            credits = convertToCell(value.negate().toString(), 14, true);
            balanceAmount = balanceAmount.add(value);
            dueDate = convertToCell("", 14, true);
        } else {
            throw new IllegalArgumentException();
        }
        balance = convertToCell(balanceAmount.toString(), 14, true);

        return convertToCell(lineItem.postDate().getValue().toString(), 14, true) + dueDate
                + convertToCell(lineItem.description().isNull() ? "" : lineItem.description().getValue().toString(), 60, true) + debits + credits + balance;
    }

    private static String createAgingBucketsLine(AgingBuckets<?> agingBuckets) {
        String bucketName = agingBuckets.arCode().isNull() ? "Total" : agingBuckets.arCode().getValue().toString();
        return convertToCell(bucketName, 14, true) + convertToCell(agingBuckets.bucketCurrent().getValue().toString(), 14, true)
                + convertToCell(agingBuckets.bucket30().getValue().toString(), 14, true)
                + convertToCell(agingBuckets.bucket60().getValue().toString(), 14, true)
                + convertToCell(agingBuckets.bucket90().getValue().toString(), 14, true)
                + convertToCell(agingBuckets.bucketOver90().getValue().toString(), 14, true);
    }

    private static String convertToCell(String str, int width, boolean alignLeft) {
        char[] chars = new char[width];
        if (str.length() < width) {
            for (int i = 0; i < width; i++) {
                if (alignLeft) {
                    if (i < str.length()) {
                        chars[i] = str.charAt(i);
                    } else {
                        chars[i] = ' ';
                    }
                } else {
                    if (width - i < str.length()) {
                        chars[i] = str.charAt(i);
                    } else {
                        chars[i] = ' ';
                    }
                }
            }
        } else {
            str = str.substring(0, width - 4) + "... ";
        }
        return String.valueOf(chars);
    }

}
