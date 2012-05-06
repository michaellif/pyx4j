/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 3, 2012
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.payment.pad;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;

import com.pyx4j.config.shared.ApplicationMode;

import com.propertyvista.admin.domain.payment.pad.PadBatch;
import com.propertyvista.admin.domain.payment.pad.PadDebitRecord;
import com.propertyvista.admin.domain.payment.pad.PadFile;
import com.propertyvista.config.VistaDeployment;
import com.propertyvista.shared.VistaSystemIdentification;

public class CaledonPadFileWriter {

    private final PadFile padFile;

    private final Writer writer;

    public CaledonPadFileWriter(PadFile padFile, File file) throws IOException {
        this.padFile = padFile;
        writer = new FileWriter(file);
    }

    public void write() throws IOException {
        writeFileHeader(padFile);
        BigDecimal fileAmount = new BigDecimal("0");
        int recordsCount = 0;
        for (PadBatch padBatch : padFile.batches()) {
            BigDecimal batchAmount = writeBatch(padBatch);
            fileAmount = fileAmount.add(batchAmount);
            recordsCount += padBatch.records().size();
        }
        writeFileTrailer(fileAmount, recordsCount);
    }

    public void writeFileHeader(PadFile padFile) throws IOException {
        // Record Type
        writer.append("A").append(",");

        //Customer ID
        writer.append("BIRCHWOOD").append(",");

        //File Creation Number
        writer.append(padFile.id().getStringView()).append(",");

        String fileCreationDate = new SimpleDateFormat("yyyyMMdd").format(padFile.created().getValue());
        writer.append(fileCreationDate).append(",");

        String fileTypeIndicator = "TEST";
        if ((!ApplicationMode.isDevelopment()) && (VistaSystemIdentification.production == VistaDeployment.getSystemIdentification())) {
            fileTypeIndicator = "PROD";
        }
        writer.append(fileTypeIndicator).append(",");

        //Version Indicator
        writer.append("0010");
    }

    private void writeFileTrailer(BigDecimal fileAmount, int recordsCount) throws IOException {
        // Record Type
        writer.append("A").append(",");

        // Total number of detail debit records in the file
        writer.append(String.valueOf(recordsCount)).append(",");

        // Total value of the batch - 14 digit field with 2 implied decimal places! ($1.00 would be represented by 100). This field cannot contain a decimal or dollar ($) sign
        writer.append(formatAmount(fileAmount));
    }

    private BigDecimal writeBatch(PadBatch padBatch) throws IOException {
        writeBatchHeader(padBatch);
        BigDecimal batchAmount = new BigDecimal("0");
        for (PadDebitRecord record : padBatch.records()) {
            writeDebitRecord(record);
            batchAmount = batchAmount.add(record.amount().getValue());
        }
        writeBatchTrailer(padBatch, batchAmount);
        return batchAmount;
    }

    private void writeBatchHeader(PadBatch padBatch) throws IOException {
        // Record Type
        writer.append("X").append(",");
        // Batch Payment Type | 'D' - fixed, represents debit
        writer.append("D").append(",");
        //Transaction Type Code; '431' fixed
        writer.append("431").append(",");

        writer.append(padBatch.merchantAccount().merchantTerminalId().getStringView()).append(",");

        //Description to appear on client's statement. Typically a merchant's business name.
        writer.append(padBatch.merchantAccount().chargeDescription().getStringView()).append(",");

        writer.append(padBatch.merchantAccount().bankId().getStringView()).append(",");
        writer.append(padBatch.merchantAccount().branchTransitNumber().getStringView()).append(",");
        writer.append(padBatch.merchantAccount().accountNumber().getStringView());
    }

    private void writeBatchTrailer(PadBatch padBatch, BigDecimal batchAmount) throws IOException {
        // Record Type
        writer.append("Y").append(",");
        // Batch Payment Type | 'D' - fixed, represents debit
        writer.append("D").append(",");
        // Batch Record Count
        writer.append(String.valueOf(padBatch.records().size())).append(",");

        // Total value of the batch - 14 digit field with 2 implied decimal places! ($1.00 would be represented by 100). This field cannot contain a decimal or dollar ($) sign
        writer.append(formatAmount(batchAmount));
    }

    private void writeDebitRecord(PadDebitRecord record) throws IOException {
        // Record Type
        writer.append("D").append(",");
        writer.append(record.clientId().getStringView()).append(",");
        writer.append(formatAmount(record.amount().getValue())).append(",");
        writer.append(record.bankId().getStringView()).append(",");
        writer.append(record.branchTransitNumber().getStringView()).append(",");
        writer.append(record.accountNumber().getStringView()).append(",");
        writer.append(record.transactionId().getStringView());
    }

    private CharSequence formatAmount(BigDecimal value) {
        BigDecimal centValue = value.multiply(new BigDecimal("100"));
        centValue.setScale(0);
        return centValue.toString();
    }
}
