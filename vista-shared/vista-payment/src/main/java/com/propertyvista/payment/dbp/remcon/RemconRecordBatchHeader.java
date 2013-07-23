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

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.propertyvista.payment.dbp.remcon.RemconField.RemconFieldType;

/**
 * A batch is created for each incoming source location code (e.g. Royal Bank) and for each unique file date
 * 
 */
public class RemconRecordBatchHeader implements RemconRecord {

    @Override
    public char recordType() {
        return '2';
    };

    //Numeric; Last two are cents
    @RemconField(12)
    public String batchAmount;

    //Numeric;
    @RemconField(5)
    public String sequenceNumberFirst;

    //Numeric;
    @RemconField(5)
    public String sequenceNumberLast;

    //Numeric;
    @RemconField(5)
    public String numberOfItems;

    //Numeric; Location code assigned to each source (e.g. BMO is 1001)
    @RemconField(4)
    public String locationCode;

    @RemconField(1)
    public String mode;

    //Numeric; Box number assigned by Bank
    @RemconField(4)
    public String boxNumber;

    //Unique sequential batch number. Each batch begins at 1 and incrementally increases by 1 for each additional batch until 999 is reached. Once 999 is reached, the batch number is reset to 1.
    @RemconField(3)
    public String batchNumber;

    @RemconField(5)
    public String numberOfStubs;

    @RemconField(5)
    public String numberOfChecks;

    @RemconField(1)
    public String order;

    @RemconField(1)
    public String digitBatchNo;

    @RemconField(1)
    public String adjCode;

    @RemconField(7)
    public String adjAmt;

    @RemconField(2)
    public String machineNumber;

    @RemconField(2)
    public String operatorNumber;

    //Collection Date. Date of payment collection provided by the source location code telebanking operator (MMDDYY)
    @RemconField(value = 6, type = RemconFieldType.DateYYMMDD)
    public String collectionDate;

    @RemconField(5)
    public String microfilmSequence;

    @RemconField(2)
    public String numberOfAdj;

    @RemconField(1)
    public String financialFlag;

    //Identification of source of remittance information. Value of 14 represents telebanking/electronic bill payments.
    @RemconField(2)
    public String sourceCode;

    //If the source location (e.g. TDBANK) sends a trace number for their incoming file, then that trace number is referenced here. Each source location can have a different trace number format.
    @RemconField(value = 30, type = RemconFieldType.Alphanumeric)
    public String traceNumber;

    @RemconField(value = 40, type = RemconFieldType.Filler)
    public String filler;

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}
