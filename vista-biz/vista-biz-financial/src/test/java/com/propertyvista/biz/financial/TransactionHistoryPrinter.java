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
 * @version $Id$
 */
package com.propertyvista.biz.financial;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import com.pyx4j.gwt.server.IOUtils;

import com.propertyvista.domain.financial.billing.AgingBuckets;
import com.propertyvista.domain.financial.billing.InvoiceCredit;
import com.propertyvista.domain.financial.billing.InvoiceDebit;
import com.propertyvista.domain.financial.billing.InvoiceDebit.DebitType;
import com.propertyvista.domain.financial.billing.InvoiceLineItem;
import com.propertyvista.dto.TransactionHistoryDTO;

public class TransactionHistoryPrinter {

    protected static void printTransactionHistory(TransactionHistoryDTO transactionHistory, String fileName) {
        FileWriter fstream;
        try {
            fstream = new FileWriter(fileName);
        } catch (IOException e) {
            throw new Error(e);
        }
        try {
            BufferedWriter out = new BufferedWriter(fstream);
            out.write("Transaction History");
            out.newLine();
            out.write("\nFrom Date: " + transactionHistory.fromDate().getValue());
            out.write("\nIssue Date: " + transactionHistory.issueDate().getValue());
            out.newLine();
            out.write(convertToCell("Date", 14, true) + convertToCell("Description", 60, true) + convertToCell("Debits", 14, true)
                    + convertToCell("Credits", 14, true));
            out.newLine();

            for (InvoiceLineItem lineItem : transactionHistory.lineItems()) {
                out.write(createLineItem(lineItem));
                out.newLine();
            }

            out.newLine();
            out.write(convertToCell("Type", 14, true) + convertToCell("Current", 14, true) + convertToCell("1-30 Days", 14, true)
                    + convertToCell("31-60 Days", 14, true) + convertToCell("61-90 Days", 14, true) + convertToCell("Over 90 Days", 14, true));
            out.newLine();
            for (AgingBuckets agingBuckets : transactionHistory.agingBuckets()) {
                out.write(createAgingBucketsLine(agingBuckets));
                out.newLine();
            }

            out.write(createAgingBucketsLine(transactionHistory.totalAgingBuckets()));
            out.newLine();

            out.close();
        } catch (IOException e) {
            throw new Error(e);
        } finally {
            IOUtils.closeQuietly(fstream);
        }

    }

    private static String createLineItem(InvoiceLineItem lineItem) {

        String debits = null;
        String credits = null;
        if (lineItem.isInstanceOf(InvoiceDebit.class)) {
            InvoiceDebit invoiceDebit = lineItem.cast();
            debits = convertToCell(lineItem.amount().getValue().add(invoiceDebit.taxTotal().getValue()).toString(), 14, true);
            credits = convertToCell("", 14, true);
        } else if (lineItem.isInstanceOf(InvoiceCredit.class)) {
            debits = convertToCell("", 14, true);
            credits = convertToCell(lineItem.amount().getValue().negate().toString(), 14, true);
        } else {
            throw new IllegalArgumentException();
        }
        return convertToCell(lineItem.postDate().getValue().toString(), 14, true)
                + convertToCell(lineItem.description().isNull() ? "" : lineItem.description().getValue().toString(), 60, true) + debits + credits;
    }

    private static String createAgingBucketsLine(AgingBuckets agingBuckets) {
        return convertToCell(agingBuckets.debitType().getValue().toString(), 14, true)
                + convertToCell(agingBuckets.bucketCurrent().getValue().toString(), 14, true)
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
