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

import com.propertyvista.payment.bmo.remcon.RemconField.RemconFieldType;

public class RemconRecordFileTrailer implements RemconRecord {

    @Override
    public String recordType() {
        return "9";
    };

    //Consists of File Serial date followed by File Serial number as defined under File Header
    @RemconField(12)
    public String fileSerial;

    //Total number of transaction records for the file, including trailers, headers and details
    @RemconField(10)
    public String recordCount;

    //Total dollar value of detail record transactions
    @RemconField(14)
    public String totalAmount;

    @RemconField(value = 113, type = RemconFieldType.Alphanumeric)
    public String filler;
}
