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
package com.propertyvista.payment.pad;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Locale;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.validator.EntityValidator;
import com.pyx4j.essentials.server.csv.CSVLoad;
import com.pyx4j.essentials.server.csv.CSVParser;
import com.pyx4j.essentials.server.csv.CSVReciver;

import com.propertyvista.operations.domain.payment.pad.PadReconciliationDebitRecord;
import com.propertyvista.operations.domain.payment.pad.PadReconciliationFile;
import com.propertyvista.operations.domain.payment.pad.PadReconciliationSummary;

class CaledonPadReconciliationParser {

    PadReconciliationFile parsReport(File file) {
        final PadReconciliationFile reconciliationFile = EntityFactory.create(PadReconciliationFile.class);
        reconciliationFile.fileName().setValue(file.getName());

        InputStream is;
        try {
            is = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            throw new RuntimeException("File not found");
        }
        CSVParser parser = new CSVParser();

        CSVLoad.loadFile(is, parser, new CSVReciver() {

            PadReconciliationSummary summary;

            @Override
            public boolean onHeader(String[] headers) {
                if (!headers[0].equals("SUMM")) {
                    throw new Error("Wrong file header  '" + headers[0] + "' format");
                }
                parsSummary(headers);
                return true;
            }

            @Override
            public void onRow(String[] values) {
                if (values[0].equals("SUMM")) {
                    parsSummary(values);
                } else if (values[0].equals("TDTL")) {
                    PadReconciliationDebitRecord record = EntityFactory.create(PadReconciliationDebitRecord.class);
                    record.processingStatus().setValue(Boolean.FALSE);
                    int v = 1;
                    record.paymentDate().setValue(CaledonPadUtils.parsDateReconciliation(values[v++]));
                    record.merchantTerminalId().setValue(values[v++]);
                    record.clientId().setValue(values[v++]);
                    record.transactionId().setValue(values[v++]);
                    record.amount().setValue(CaledonPadUtils.parsAmount(values[v++]));
                    record.reconciliationStatus().setValue(record.reconciliationStatus().parse(values[v++].toUpperCase(Locale.ENGLISH)));
                    if (values.length > v) {
                        record.reasonCode().setValue(values[v++]);
                        if (values.length > v) {
                            record.reasonText().setValue(values[v++]);
                            if (values.length > v) {
                                record.fee().setValue(CaledonPadUtils.parsAmount(values[v++]));
                            }
                        }
                    }

                    EntityValidator.validate(record);
                    summary.records().add(record);
                } else {
                    throw new Error("Wrong file record type  '" + values[0] + "'");
                }
            }

            private void parsSummary(String[] values) {
                summary = EntityFactory.create(PadReconciliationSummary.class);
                summary.processingStatus().setValue(Boolean.FALSE);
                int v = 1;
                summary.paymentDate().setValue(CaledonPadUtils.parsDateReconciliation(values[v++]));
                summary.merchantTerminalId().setValue(values[v++]);
                summary.grossPaymentAmount().setValue(CaledonPadUtils.parsAmount(values[v++]));
                summary.grossPaymentCount().setValue(Integer.valueOf(values[v++]));
                summary.grossPaymentFee().setValue(CaledonPadUtils.parsAmount(values[v++]));
                summary.rejectItemsAmount().setValue(CaledonPadUtils.parsAmount(values[v++]));
                summary.rejectItemsCount().setValue(Integer.valueOf(values[v++]));
                summary.rejectItemsFee().setValue(CaledonPadUtils.parsAmount(values[v++]));
                summary.returnItemsAmount().setValue(CaledonPadUtils.parsAmount(values[v++]));
                summary.returnItemsCount().setValue(Integer.valueOf(values[v++]));
                summary.returnItemsFee().setValue(CaledonPadUtils.parsAmount(values[v++]));
                summary.netAmount().setValue(CaledonPadUtils.parsAmount(values[v++]));
                summary.adjustments().setValue(CaledonPadUtils.parsAmount(values[v++]));
                summary.previousBalance().setValue(CaledonPadUtils.parsAmount(values[v++]));
                summary.merchantBalance().setValue(CaledonPadUtils.parsAmount(values[v++]));
                summary.fundsReleased().setValue(CaledonPadUtils.parsAmount(values[v++]));
                summary.reconciliationStatus().setValue(summary.reconciliationStatus().parse(values[v++]));

                EntityValidator.validate(summary);
                reconciliationFile.batches().add(summary);
            }

            @Override
            public boolean canContuneLoad() {
                return true;
            }

        });

        return reconciliationFile;
    }
}
