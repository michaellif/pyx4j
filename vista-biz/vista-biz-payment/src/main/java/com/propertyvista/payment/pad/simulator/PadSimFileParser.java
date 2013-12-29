/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-05-23
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.payment.pad.simulator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.essentials.server.csv.CSVLoad;
import com.pyx4j.essentials.server.csv.CSVParser;
import com.pyx4j.essentials.server.csv.CSVReciver;

import com.propertyvista.domain.financial.FundsTransferType;
import com.propertyvista.operations.domain.payment.pad.simulator.PadSimBatch;
import com.propertyvista.operations.domain.payment.pad.simulator.PadSimDebitRecord;
import com.propertyvista.operations.domain.payment.pad.simulator.PadSimFile;

public class PadSimFileParser {

    private static final Map<String, FundsTransferType> fundsTransferTypeCodes = buildFundsTransferTypeCodes();

    private static Map<String, FundsTransferType> buildFundsTransferTypeCodes() {
        Map<String, FundsTransferType> codes = new HashMap<String, FundsTransferType>();
        for (FundsTransferType fundsTransferType : FundsTransferType.values()) {
            codes.put(fundsTransferType.getCode(), fundsTransferType);
        }
        return codes;
    }

    public static FundsTransferType getFundsTransferTypeByCode(String code) {
        FundsTransferType fundsTransferType = fundsTransferTypeCodes.get(code);
        if (fundsTransferType == null) {
            throw new Error("Unknown FundsTransferType code " + code);
        }
        return fundsTransferType;
    }

    public PadSimFile parsReport(File file) {
        final PadSimFile padFile = EntityFactory.create(PadSimFile.class);

        InputStream is;
        try {
            is = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            throw new RuntimeException("File not found");
        }
        CSVParser parser = new CSVParser();

        CSVLoad.loadFile(is, parser, new CSVReciver() {

            PadSimBatch batch;

            boolean eof = false;

            @Override
            public boolean onHeader(String[] headers) {
                if (!headers[0].equals("A")) {
                    throw new Error("Wrong file header  '" + headers[0] + "' format");
                }
                padFile.companyId().setValue(headers[1]);
                padFile.fileCreationNumber().setValue(headers[2]);
                padFile.fileCreationDate().setValue(headers[3]);
                padFile.fileType().setValue(headers[4]);
                padFile.fileVersion().setValue(headers[5]);
                padFile.fundsTransferType().setValue(getFundsTransferTypeByCode(headers[6]));
                return true;
            }

            @Override
            public void onRow(String[] values) {
                if (values[0].equals("Z")) {
                    padFile.recordsCount().setValue(Integer.valueOf(values[1]));
                    padFile.fileAmount().setValue(values[2]);
                    eof = true;
                } else if (values[0].equals("X")) {
                    batch = EntityFactory.create(PadSimBatch.class);
                    batch.batchNumber().setValue(values[1]);
                    batch.terminalId().setValue(values[4]);
                    batch.chargeDescription().setValue(values[5]);
                    batch.bankId().setValue(values[6]);
                    batch.branchTransitNumber().setValue(values[7]);
                    batch.accountNumber().setValue(values[8]);
                } else if (values[0].equals("Y")) {
                    batch.recordsCount().setValue(Integer.valueOf(values[2]));
                    batch.batchAmount().setValue(values[3]);
                    padFile.batches().add(batch);
                    batch = null;
                } else if (values[0].equals("D")) {
                    PadSimDebitRecord record = EntityFactory.create(PadSimDebitRecord.class);
                    record.clientId().setValue(values[1]);
                    record.amount().setValue(values[2]);
                    record.bankId().setValue(values[3]);
                    record.branchTransitNumber().setValue(values[4]);
                    record.accountNumber().setValue(values[5]);
                    record.transactionId().setValue(values[6]);
                    batch.records().add(record);
                } else {
                    throw new Error("Wrong file record type  '" + values[0] + "'");
                }
            }

            @Override
            public boolean canContuneLoad() {
                return !eof;
            }

        });

        return padFile;
    }
}
