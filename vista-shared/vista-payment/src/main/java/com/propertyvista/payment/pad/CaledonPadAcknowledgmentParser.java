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
package com.propertyvista.payment.pad;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.validator.EntityValidator;
import com.pyx4j.essentials.server.csv.CSVLoad;
import com.pyx4j.essentials.server.csv.CSVParser;
import com.pyx4j.essentials.server.csv.CSVReciver;

import com.propertyvista.payment.pad.data.PadAkBatch;
import com.propertyvista.payment.pad.data.PadAkDebitRecord;
import com.propertyvista.payment.pad.data.PadAkFile;

public class CaledonPadAcknowledgmentParser {

    public PadAkFile parsReport(File file) {
        final PadAkFile akFile = EntityFactory.create(PadAkFile.class);

        InputStream is;
        try {
            is = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            throw new RuntimeException("File not found");
        }
        CSVParser parser = new CSVParser();

        akFile.version().setValue(1);

        CSVLoad.loadFile(is, parser, new CSVReciver() {

            @Override
            public boolean onHeader(String[] headers) {
                if (!headers[0].equals("FHD")) {
                    throw new Error("Wrong file header  '" + headers[0] + "' format");
                }
                int v = 1;
                akFile.companyId().setValue(headers[v++]);
                akFile.fileCreationNumber().setValue(headers[v++]);
                akFile.fileCreationDate().setValue(headers[v++]);
                akFile.batcheCount().setValue(headers[v++]);
                akFile.recordsCount().setValue(headers[v++]);
                akFile.fileAmount().setValue(headers[v++]);
                akFile.acknowledgmentStatusCode().setValue(headers[v++]);
                if (headers.length > v) {
                    akFile.acknowledgmentRejectReasonMessage().setValue(headers[v++]);
                }
                EntityValidator.validate(akFile);
                return true;
            }

            @Override
            public void onRow(String[] values) {
                if (values[0].equals("BRJ")) {
                    PadAkBatch batch = EntityFactory.create(PadAkBatch.class);
                    int v = 1;
                    batch.batchId().setValue(values[v++]);
                    batch.terminalId().setValue(values[v++]);
                    batch.acknowledgmentStatusCode().setValue(values[v++]);
                    batch.batchAmount().setValue(values[v++]);
                    EntityValidator.validate(batch);
                    akFile.batches().add(batch);
                } else if (values[0].equals("TRJ")) {
                    PadAkDebitRecord record = EntityFactory.create(PadAkDebitRecord.class);
                    int v = 1;
                    record.terminalId().setValue(values[v++]);
                    record.clientId().setValue(values[v++]);
                    record.transactionId().setValue(values[v++]);
                    record.amount().setValue(values[v++]);
                    record.acknowledgmentStatusCode().setValue(values[v++]);
                    EntityValidator.validate(record);
                    akFile.records().add(record);
                } else {
                    throw new Error("Wrong file record type  '" + values[0] + "'");
                }
            }

            @Override
            public boolean canContuneLoad() {
                return true;
            }

        });

        return akFile;
    }
}
