/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 29, 2013
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.payment.dbp;

import java.math.BigDecimal;
import java.util.Collections;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.operations.domain.payment.dbp.DirectDebitFile;
import com.propertyvista.operations.domain.payment.dbp.DirectDebitRecord;
import com.propertyvista.payment.dbp.remcon.RemconFile;
import com.propertyvista.payment.dbp.remcon.RemconRecord;
import com.propertyvista.payment.dbp.remcon.RemconRecordBatchHeader;
import com.propertyvista.payment.dbp.remcon.RemconRecordBatchTrailer;
import com.propertyvista.payment.dbp.remcon.RemconRecordDetailRecord;
import com.propertyvista.payment.dbp.remcon.RemconRecordFileHeader;
import com.propertyvista.server.sftp.SftpFile;

public class RemconFileInterpreter {

    public static DirectDebitFile interpreter(SftpFile sftpFile, RemconFile remconFile) {
        DirectDebitFile directDebitFile = EntityFactory.create(DirectDebitFile.class);
        directDebitFile.fileName().setValue(sftpFile.remoteName);

        RemconRecordFileHeader headerRecord = (RemconRecordFileHeader) remconFile.records.get(0);
        directDebitFile.fileSerialDate().setValue(headerRecord.fileSerialDate);
        directDebitFile.fileSerialNumber().setValue(headerRecord.fileSerialNumber);

        directDebitFile.records().addAll(Collections.<DirectDebitRecord> emptyList());

        RemconRecordBatchHeader currentBatchHeader = null;
        for (RemconRecord record : remconFile.records) {
            if (record instanceof RemconRecordBatchHeader) {
                currentBatchHeader = (RemconRecordBatchHeader) record;
            } else if (record instanceof RemconRecordDetailRecord) {
                RemconRecordDetailRecord detailRecord = (RemconRecordDetailRecord) record;

                DirectDebitRecord directDebitRecord = EntityFactory.create(DirectDebitRecord.class);
                directDebitRecord.amount().setValue(parsAmount(detailRecord.itemAmount));
                directDebitRecord.paymentReferenceNumber().setValue(detailRecord.paymentReferenceNumber);
                directDebitRecord.customerName().setValue(detailRecord.customerName);
                directDebitRecord.accountNumber().setValue(detailRecord.accountNumber + detailRecord.user1);

                directDebitRecord.trace().locationCode().setValue(currentBatchHeader.locationCode);
                directDebitRecord.trace().collectionDate().setValue(currentBatchHeader.collectionDate);
                directDebitRecord.trace().sourceCode().setValue(currentBatchHeader.sourceCode);
                directDebitRecord.trace().traceNumber().setValue(currentBatchHeader.traceNumber);

                directDebitFile.records().add(directDebitRecord);
            } else if (record instanceof RemconRecordBatchTrailer) {
                currentBatchHeader = null;
            }
        }

        return directDebitFile;
    }

    public static BigDecimal parsAmount(String value) {
        if (CommonsStringUtils.isEmpty(value)) {
            return null;
        } else {
            String valueCents;
            String valueDollars;
            int len = value.length();
            if (len == 1) {
                valueCents = "0" + value;
                valueDollars = "0";
            } else if (len == 2) {
                valueCents = value;
                valueDollars = "0";
            } else {
                valueCents = value.substring(len - 2, len);
                valueDollars = value.substring(0, len - 2);
            }

            BigDecimal money = new BigDecimal(valueDollars + "." + valueCents);
            return money.setScale(2);
        }
    }
}
