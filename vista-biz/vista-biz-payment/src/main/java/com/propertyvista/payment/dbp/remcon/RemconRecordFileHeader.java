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
package com.propertyvista.payment.dbp.remcon;

import com.propertyvista.payment.dbp.remcon.RemconField.RemconFieldType;

public class RemconRecordFileHeader implements RemconRecord {

    @Override
    public char recordType() {
        return '0';
    };

    //Numeric; File creation date (YYMMDD)
    @RemconField(value = 6, type = RemconFieldType.DateYYMMDD)
    public String currentDate;

    //Numeric; System assigned file serial #
    @RemconField(6)
    public String fileSerialNumber;

    //Numeric; Date of system assigned file serial #
    @RemconField(value = 6, type = RemconFieldType.DateYYMMDD)
    public String fileSerialDate;

    @RemconField(value = 131, type = RemconFieldType.Filler)
    public String filler;

}
