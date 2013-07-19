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

import java.io.Closeable;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.SimpleDateFormat;

import com.propertyvista.config.VistaDeployment;
import com.propertyvista.operations.domain.payment.pad.PadBatch;
import com.propertyvista.operations.domain.payment.pad.PadDebitRecord;
import com.propertyvista.operations.domain.payment.pad.PadFile;

public class CaledonPadFileWriter implements Closeable {

    private final PadFile padFile;

    private final Writer writer;

    public CaledonPadFileWriter(PadFile padFile, File file) throws IOException {
        this.padFile = padFile;
        writer = new FileWriter(file);
    }

    public void write() throws IOException {
        writeFileHeader(padFile);
        for (PadBatch padBatch : padFile.batches()) {
            writeBatch(padBatch);
        }
        writeFileTrailer(padFile);
    }

    private void writeFileHeader(PadFile padFile) throws IOException {
        // Record Type
        writer.append("A").append(",");

        //Customer ID
        writer.append(padFile.companyId().getStringView()).append(",");

        //File Creation Number
        writer.append(padFile.fileCreationNumber().getStringView()).append(",");

        String fileCreationDate = new SimpleDateFormat("yyyyMMdd").format(padFile.created().getValue());
        writer.append(fileCreationDate).append(",");

        String fileTypeIndicator = "TEST";
        if (VistaDeployment.isVistaProduction()) {
            fileTypeIndicator = "PROD";
        }
        writer.append(fileTypeIndicator).append(",");

        //Version Indicator
        writer.append("0010");

        writer.append(",");
        writer.append(padFile.fundsTransferType().getValue().getCode());

        writer.append("\n");
    }

    private void writeFileTrailer(PadFile padFile) throws IOException {
        // Record Type
        writer.append("Z").append(",");

        // Total number of detail debit records in the file
        writer.append(String.valueOf(padFile.recordsCount().getValue())).append(",");

        // Total value of the batch - 14 digit field with 2 implied decimal places! ($1.00 would be represented by 100). This field cannot contain a decimal or dollar ($) sign
        writer.append(CaledonPadUtils.formatAmount(padFile.fileAmount().getValue()));
        writer.append("\n");
    }

    private void writeBatch(PadBatch padBatch) throws IOException {
        writeBatchHeader(padBatch);
        for (PadDebitRecord record : padBatch.records()) {
            writeDebitRecord(record);
        }
        writeBatchTrailer(padBatch);
    }

    private void writeBatchHeader(PadBatch padBatch) throws IOException {
        // Record Type
        writer.append("X").append(",");

        //Batch Number, Must be incremented by one for each batch submitted within a file
        writer.append(padBatch.batchNumber().getStringView()).append(",");

        // Batch Payment Type | 'D' - fixed, represents debit
        writer.append("D").append(",");
        //Transaction Type Code; '431' fixed
        writer.append("431").append(",");

        writer.append(padBatch.merchantTerminalId().getStringView()).append(",");

        //Description to appear on client's statement. Typically a merchant's business name.
        writer.append(padBatch.chargeDescription().getStringView()).append(",");

        writer.append(padBatch.bankId().getStringView()).append(",");
        writer.append(padBatch.branchTransitNumber().getStringView()).append(",");
        writer.append(padBatch.accountNumber().getStringView());
        writer.append("\n");
    }

    private void writeBatchTrailer(PadBatch padBatch) throws IOException {
        // Record Type
        writer.append("Y").append(",");
        // Batch Payment Type | 'D' - fixed, represents debit
        writer.append("D").append(",");
        // Batch Record Count
        writer.append(String.valueOf(padBatch.records().size())).append(",");

        // Total value of the batch - 14 digit field with 2 implied decimal places! ($1.00 would be represented by 100). This field cannot contain a decimal or dollar ($) sign
        writer.append(CaledonPadUtils.formatAmount(padBatch.batchAmount().getValue()));
        writer.append("\n");
    }

    private void writeDebitRecord(PadDebitRecord record) throws IOException {
        // Record Type
        writer.append("D").append(",");
        writer.append(record.clientId().getStringView()).append(",");
        writer.append(CaledonPadUtils.formatAmount(record.amount().getValue())).append(",");
        writer.append(record.bankId().getStringView()).append(",");
        writer.append(record.branchTransitNumber().getStringView()).append(",");
        writer.append(record.accountNumber().getStringView()).append(",");
        writer.append(record.transactionId().getStringView());
        writer.append("\n");
    }

    @Override
    public void close() throws IOException {
        writer.flush();
        writer.close();
    }
}
