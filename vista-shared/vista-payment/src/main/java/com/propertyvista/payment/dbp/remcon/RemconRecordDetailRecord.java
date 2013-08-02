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

public class RemconRecordDetailRecord implements RemconRecord {

    @Override
    public char recordType() {
        return '5';
    };

    //Same batch number as in the Batch Header
    @RemconField(3)
    public String batchNumber;

    //Transaction sequence number for each client remittance within each batch. The sequence number starts at 00001.
    @RemconField(5)
    public String sequenceNumber;

    //Amount of payment remitted, Last two are cents
    @RemconField(10)
    public String itemAmount;

    @RemconField(1)
    public String code;

    //Payer's account number
    //This 12 digit field will contain the first 12 digits of the payer's account number with any extra digits running off to the next field (USER-1).
    @RemconField(value = 12, type = RemconFieldType.Alphanumeric)
    public String accountNumber;

    //Used when client account number is greater than 12 digits
    @RemconField(value = 12, type = RemconFieldType.Alphanumeric)
    public String user1;

    @RemconField(value = 12, type = RemconFieldType.Alphanumeric)
    public String user2;

    @RemconField(value = 12, type = RemconFieldType.Alphanumeric)
    public String user3;

    @RemconField(value = 12, type = RemconFieldType.Alphanumeric)
    public String user4;

    //Trace number unique to the payer's transaction provided by the incoming source location code telebanking operator
    @RemconField(value = 30, type = RemconFieldType.Alphanumeric)
    public String paymentReferenceNumber;

    //Name of the payer if provided by the source location code telebanking operator
    @RemconField(value = 35, type = RemconFieldType.Alphanumeric)
    public String customerName;

    @RemconField(value = 5, type = RemconFieldType.Filler)
    public String filler;

}
