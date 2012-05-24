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

import com.propertyvista.payment.pad.ak.PadAkBatch;
import com.propertyvista.payment.pad.ak.PadAkDebitRecord;
import com.propertyvista.payment.pad.ak.PadAkFile;

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
                akFile.companyId().setValue(headers[1]);
                akFile.fileCreationNumber().setValue(headers[2]);
                akFile.fileCreationDate().setValue(headers[3]);
                akFile.batcheCount().setValue(headers[4]);
                akFile.recordsCount().setValue(headers[5]);
                akFile.fileAmount().setValue(headers[6]);
                akFile.acknowledgmentStatusCode().setValue(headers[7]);
                EntityValidator.validate(akFile);
                return true;
            }

            @Override
            public void onRow(String[] values) {
                if (values[0].equals("BRJ")) {
                    PadAkBatch batch = EntityFactory.create(PadAkBatch.class);
                    batch.batchId().setValue(values[1]);
                    batch.terminalId().setValue(values[2]);
                    batch.acknowledgmentStatusCode().setValue(values[3]);
                    batch.batchAmount().setValue(values[4]);
                    EntityValidator.validate(batch);
                    akFile.batches().add(batch);
                } else if (values[0].equals("TRJ")) {
                    PadAkDebitRecord record = EntityFactory.create(PadAkDebitRecord.class);
                    record.terminalId().setValue(values[1]);
                    record.clientId().setValue(values[2]);
                    record.transactionId().setValue(values[3]);
                    record.amount().setValue(values[4]);
                    record.acknowledgmentStatusCode().setValue(values[5]);
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
