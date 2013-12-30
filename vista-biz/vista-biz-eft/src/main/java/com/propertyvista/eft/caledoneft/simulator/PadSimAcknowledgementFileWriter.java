/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-05-24
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.eft.caledoneft.simulator;

import java.io.Closeable;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import com.propertyvista.operations.domain.eft.caledoneft.simulator.PadSimBatch;
import com.propertyvista.operations.domain.eft.caledoneft.simulator.PadSimDebitRecord;
import com.propertyvista.operations.domain.eft.caledoneft.simulator.PadSimFile;

public class PadSimAcknowledgementFileWriter implements Closeable {

    private final PadSimFile padFile;

    private final Writer writer;

    public PadSimAcknowledgementFileWriter(PadSimFile padFile, File file) throws IOException {
        this.padFile = padFile;
        writer = new FileWriter(file);
    }

    public void write() throws IOException {
        writeFileHeader();
        for (PadSimBatch padBatch : padFile.batches()) {
            if (!padBatch.acknowledgmentStatusCode().isNull()) {
                writeBatchRecord(padBatch);
            } else {
                for (PadSimDebitRecord record : padBatch.records()) {
                    if (!record.acknowledgmentStatusCode().isNull()) {
                        writeDebitRecord(record);
                    }
                }
            }
        }
    }

    private void writeFileHeader() throws IOException {
        // Record Type
        writer.append("FHD").append(",");

        // Company ID 
        writer.append(padFile.companyId().getStringView()).append(",");

        // File Creation Number
        writer.append(padFile.fileCreationNumber().getStringView()).append(",");

        //File Creation Date 
        writer.append(padFile.fileCreationDate().getStringView()).append(",");

        //Batch Header Record  Count 
        writer.append(padFile.batchRecordsCount().getStringView()).append(",");

        //Detail Record Count 
        writer.append(padFile.recordsCount().getStringView()).append(",");

        //Total File Amount
        writer.append(padFile.fileAmount().getStringView()).append(",");

        //File Status Code
        writer.append(padFile.acknowledgmentStatusCode().getStringView()).append(",");

        // Reject Reason
        writer.append(padFile.acknowledgmentRejectReasonMessage().getStringView());

        writer.append("\n");
    }

    private void writeBatchRecord(PadSimBatch padBatch) throws IOException {
        // Record Type
        writer.append("BRJ").append(",");

        //Batch Number - Value from the PAD Batch Header Record 
        writer.append(padBatch.batchNumber().getStringView()).append(",");

        //Terminal ID  - Value from the PAD Batch Header Record 
        writer.append(padBatch.terminalId().getStringView()).append(",");

        //Batch Reject Reason Code
        writer.append(padBatch.acknowledgmentStatusCode().getStringView()).append(",");

        //Batch Total
        writer.append(padBatch.batchAmount().getStringView());

        writer.append("\n");
    }

    private void writeDebitRecord(PadSimDebitRecord record) throws IOException {
        // Record Type
        writer.append("TRJ").append(",");

        //Terminal ID  - Value from the PAD Batch Header Record 
        writer.append(record.padBatch().terminalId().getStringView()).append(",");

        //Client ID -  Value from the PAD Detail Debit Record 
        writer.append(record.clientId().getStringView()).append(",");

        //Reference Number  -  Value from the PAD Detail Debit Record 
        writer.append(record.transactionId().getStringView()).append(",");

        //Amount - Total Transaction amount
        writer.append(record.amount().getStringView()).append(",");

        //Transaction Reject Reason Code 
        writer.append(record.acknowledgmentStatusCode().getStringView());

        writer.append("\n");
    }

    @Override
    public void close() throws IOException {
        writer.flush();
        writer.close();
    }

}
