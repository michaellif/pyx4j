/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-05-26
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

public class PadSimReconciliationFileWriter implements Closeable {

    private final PadSimFile padFile;

    private final Writer writer;

    public PadSimReconciliationFileWriter(PadSimFile padFile, File file) throws IOException {
        this.padFile = padFile;
        writer = new FileWriter(file);
    }

    public void write() throws IOException {
        for (PadSimBatch padBatch : padFile.batches()) {
            if (padBatch.acknowledgmentStatusCode().isNull()) {
                writeBatchRecord(padBatch);
                for (PadSimDebitRecord record : padBatch.records()) {
                    if (record.acknowledgmentStatusCode().isNull()) {
                        writeDebitRecord(record);
                    }
                }
            }
        }
    }

    private void writeBatchRecord(PadSimBatch padBatch) throws IOException {
        // Record Type
        writer.append("SUMM").append(",");
        writer.append(padBatch.padFile().fileCreationDate().getStringView()).append(",");
        writer.append(padBatch.terminalId().getStringView()).append(",");

        writer.append(padBatch.grossPaymentAmount().getStringView()).append(",");
        writer.append(padBatch.grossPaymentCount().getStringView()).append(",");
        writer.append(padBatch.grossPaymentFee().getStringView()).append(",");

        writer.append(padBatch.rejectItemsAmount().getStringView()).append(",");
        writer.append(padBatch.rejectItemsCount().getStringView()).append(",");
        writer.append(padBatch.rejectItemsFee().getStringView()).append(",");

        writer.append(padBatch.returnItemsAmount().getStringView()).append(",");
        writer.append(padBatch.returnItemsCount().getStringView()).append(",");
        writer.append(padBatch.returnItemsFee().getStringView()).append(",");

        writer.append(padBatch.netAmount().getStringView()).append(",");
        writer.append(padBatch.adjustments().getStringView()).append(",");
        writer.append(padBatch.previousBalance().getStringView()).append(",");
        writer.append(padBatch.merchantBalance().getStringView()).append(",");
        writer.append(padBatch.fundsReleased().getStringView()).append(",");

        writer.append(padBatch.reconciliationStatus().getStringView());

        writer.append("\n");
    }

    private void writeDebitRecord(PadSimDebitRecord record) throws IOException {
        // Record Type
        writer.append("TDTL").append(",");
        writer.append(record.paymentDate().getStringView()).append(",");
        writer.append(record.padBatch().terminalId().getStringView()).append(",");
        writer.append(record.clientId().getStringView()).append(",");
        writer.append(record.transactionId().getStringView()).append(",");
        writer.append(record.amount().getStringView()).append(",");

        writer.append(record.reconciliationStatus().getStringView()).append(",");
        writer.append(record.reasonCode().getStringView()).append(",");
        writer.append(record.reasonText().getStringView()).append(",");
        writer.append(record.fee().getStringView());

        writer.append("\n");
    }

    @Override
    public void close() throws IOException {
        writer.flush();
        writer.close();
    }

}
