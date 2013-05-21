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
}
