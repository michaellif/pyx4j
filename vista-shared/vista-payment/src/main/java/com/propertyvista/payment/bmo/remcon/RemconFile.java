/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-04-08
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.payment.bmo.remcon;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.Validate;

public class RemconFile {

    public List<RemconRecord> records = new ArrayList<RemconRecord>();

    public List<RemconRecordDetailRecord> getDetailRecords() {
        List<RemconRecordDetailRecord> detailRecords = new ArrayList<RemconRecordDetailRecord>();
        for (RemconRecord record : records) {
            if (record instanceof RemconRecordDetailRecord) {
                detailRecords.add((RemconRecordDetailRecord) record);
            }
        }
        return detailRecords;
    }

    public void validateStructure() {
        int totalFileAmount = 0;
        {
            RemconRecord headerRecord = records.get(0);
            Validate.isTrue((headerRecord instanceof RemconRecordFileHeader), "Unexpected file header record type " + headerRecord.getClass().getSimpleName());
            RemconRecordFileHeader header = (RemconRecordFileHeader) headerRecord;

            RemconRecord trailerRecord = records.get(records.size() - 1);
            Validate.isTrue((headerRecord instanceof RemconRecordFileTrailer), "Unexpected file trailer record type "
                    + trailerRecord.getClass().getSimpleName());
            RemconRecordFileTrailer trailer = (RemconRecordFileTrailer) trailerRecord;

            Validate.isTrue(header.fileSerialNumber.equals(trailer.fileSerialNumber), "header/trailer fileSerialNumber mismatch");
            Validate.isTrue(header.fileSerialDate.equals(trailer.fileSerialDate), "header/trailer fileSerialNumber mismatch");
            Validate.isTrue(Integer.valueOf(trailer.recordCount) == records.size(), "records count mismatch");
            totalFileAmount = Integer.valueOf(trailer.totalAmount);
        }
        int fileamount = 0;
        for (RemconRecord record : records) {
            if (record instanceof RemconRecordDetailRecord) {
                int itemAmount = Integer.valueOf(((RemconRecordDetailRecord) record).itemAmount);

                fileamount += itemAmount;
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append("{\n");
        for (RemconRecord record : records) {
            buf.append(record);
            buf.append("\n");
        }
        buf.append("}");
        return buf.toString();
    }
}
