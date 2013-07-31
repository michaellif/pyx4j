/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 28, 2013
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.payment.dbp.simulator;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.propertyvista.operations.domain.payment.dbp.simulator.DirectDebitSimFile;
import com.propertyvista.operations.domain.payment.dbp.simulator.DirectDebitSimRecord;
import com.propertyvista.payment.dbp.remcon.RemconFile;
import com.propertyvista.payment.dbp.remcon.RemconRecordBatchHeader;
import com.propertyvista.payment.dbp.remcon.RemconRecordBatchTrailer;
import com.propertyvista.payment.dbp.remcon.RemconRecordBoxHeader;
import com.propertyvista.payment.dbp.remcon.RemconRecordDetailRecord;
import com.propertyvista.payment.dbp.remcon.RemconRecordFileHeader;
import com.propertyvista.payment.dbp.remcon.RemconRecordFileTrailer;
import com.propertyvista.payment.pad.CaledonPadUtils;

public class RemconFileConvertor {

    static RemconFile createRemconFile(DirectDebitSimFile debitFile) {
        RemconFile remconFile = new RemconFile();

        RemconRecordFileHeader header = new RemconRecordFileHeader();
        remconFile.records.add(header);
        RemconRecordFileTrailer trailer = new RemconRecordFileTrailer();

        header.fileSerialNumber = "1";
        header.fileSerialDate = new SimpleDateFormat("yyMMdd").format(debitFile.creatationDate().getValue());
        header.currentDate = new SimpleDateFormat("yyMMdd").format(new Date());
        trailer.fileSerialNumber = header.fileSerialNumber;
        trailer.fileSerialDate = header.fileSerialDate;

        int recordCount = 0;
        BigDecimal totalAmount = BigDecimal.ZERO;
        int batchNumber = 1;
        for (DirectDebitSimRecord debitRecord : debitFile.records()) {
            RemconRecordBoxHeader boxHeader = new RemconRecordBoxHeader();
            boxHeader.boxNumber = "1222";
            remconFile.records.add(boxHeader);

            RemconRecordBatchHeader batchHeader = new RemconRecordBatchHeader();
            RemconRecordDetailRecord detailRecord = new RemconRecordDetailRecord();
            RemconRecordBatchTrailer batchTrailer = new RemconRecordBatchTrailer();

            batchHeader.boxNumber = boxHeader.boxNumber;
            batchHeader.batchNumber = String.valueOf(batchNumber++);
            batchHeader.batchAmount = "0";
            batchHeader.mode = "1";
            batchTrailer.boxNumber = batchHeader.boxNumber;

            detailRecord.batchNumber = batchHeader.batchNumber;
            detailRecord.sequenceNumber = "00001";
            detailRecord.code = "0";
            detailRecord.itemAmount = CaledonPadUtils.formatAmount(debitRecord.amount().getValue());
            detailRecord.customerName = debitRecord.customerName().getValue();

            int len = debitRecord.accountNumber().getValue().length();
            if (len >= 12) {
                detailRecord.accountNumber = debitRecord.accountNumber().getValue().substring(0, 12);
                detailRecord.user1 = debitRecord.accountNumber().getValue().substring(12);
            } else {
                detailRecord.accountNumber = debitRecord.accountNumber().getValue();
            }

            batchTrailer.batchAmount = batchHeader.batchAmount;
            batchTrailer.numberOfItems = "1";

            remconFile.records.add(batchHeader);
            remconFile.records.add(detailRecord);
            remconFile.records.add(batchTrailer);

            recordCount++;
            totalAmount = totalAmount.add(debitRecord.amount().getValue());
        }

        trailer.totalAmount = CaledonPadUtils.formatAmount(totalAmount);
        trailer.recordCount = String.valueOf(recordCount);

        remconFile.records.add(trailer);
        return remconFile;
    }
}
