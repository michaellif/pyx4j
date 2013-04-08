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

public class RemconRecordFileHeader implements RemconRecord {

    @RemconRecordLenght(1)
    public String recordType = "0";

    //Numeric; File creation date (YYMMDD)
    @RemconRecordLenght(6)
    public String currentDate;

    //Numeric; System assigned file serial #
    @RemconRecordLenght(6)
    public String fileSerialNumber;

    //Numeric; Date of system assigned file serial #
    @RemconRecordLenght(6)
    public String fileSerialDate;

    @RemconRecordLenght(131)
    public String filler;
}
