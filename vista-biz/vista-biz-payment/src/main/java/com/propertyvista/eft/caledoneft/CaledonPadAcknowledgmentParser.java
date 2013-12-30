/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-05-14
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.eft.caledoneft;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.validator.EntityValidator;
import com.pyx4j.essentials.server.csv.CSVLoad;
import com.pyx4j.essentials.server.csv.CSVParser;
import com.pyx4j.essentials.server.csv.CSVReciver;

import com.propertyvista.operations.domain.eft.caledoneft.to.FundsTransferAckBatch;
import com.propertyvista.operations.domain.eft.caledoneft.to.FundsTransferAckRecord;
import com.propertyvista.operations.domain.eft.caledoneft.to.FundsTransferAckFile;

class CaledonPadAcknowledgmentParser {

    FundsTransferAckFile parsReport(File file) {
        final FundsTransferAckFile ackFile = EntityFactory.create(FundsTransferAckFile.class);

        InputStream is;
        try {
            is = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            throw new RuntimeException("File not found");
        }
        CSVParser parser = new CSVParser();

        ackFile.version().setValue(1);
        ackFile.fileName().setValue(file.getName());

        CSVLoad.loadFile(is, parser, new CSVReciver() {

            @Override
            public boolean onHeader(String[] headers) {
                if (!headers[0].equals("FHD")) {
                    throw new Error("Wrong file header  '" + headers[0] + "' format");
                }
                int v = 1;
                ackFile.companyId().setValue(headers[v++]);
                ackFile.fileCreationNumber().setValue(headers[v++]);
                ackFile.fileCreationDate().setValue(headers[v++]);
                ackFile.batcheCount().setValue(headers[v++]);
                ackFile.recordsCount().setValue(headers[v++]);
                ackFile.fileAmount().setValue(headers[v++]);
                ackFile.acknowledgmentStatusCode().setValue(headers[v++]);
                if (headers.length > v) {
                    ackFile.acknowledgmentRejectReasonMessage().setValue(headers[v++]);
                }
                EntityValidator.validate(ackFile);
                return true;
            }

            @Override
            public void onRow(String[] values) {
                if (values[0].equals("BRJ")) {
                    FundsTransferAckBatch batch = EntityFactory.create(FundsTransferAckBatch.class);
                    int v = 1;
                    batch.batchId().setValue(values[v++]);
                    batch.terminalId().setValue(values[v++]);
                    batch.acknowledgmentStatusCode().setValue(values[v++]);
                    batch.batchAmount().setValue(values[v++]);
                    EntityValidator.validate(batch);
                    ackFile.batches().add(batch);
                } else if (values[0].equals("TRJ")) {
                    FundsTransferAckRecord record = EntityFactory.create(FundsTransferAckRecord.class);
                    int v = 1;
                    record.terminalId().setValue(values[v++]);
                    record.clientId().setValue(values[v++]);
                    record.transactionId().setValue(values[v++]);
                    record.amount().setValue(values[v++]);
                    record.acknowledgmentStatusCode().setValue(values[v++]);
                    EntityValidator.validate(record);
                    ackFile.records().add(record);
                } else {
                    throw new Error("Wrong file record type  '" + values[0] + "'");
                }
            }

            @Override
            public boolean canContuneLoad() {
                return true;
            }

        });

        return ackFile;
    }
}
