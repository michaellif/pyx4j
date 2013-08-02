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

public class RemconRecordBatchTrailer implements RemconRecord {

    @Override
    public char recordType() {
        return '8';
    };

    //Cumulative amount of total dollar value of remittances within the batch
    @RemconField(12)
    public String batchAmount;

    @RemconField(5)
    public String sequenceNumberFirst;

    @RemconField(5)
    public String sequenceNumberLast;

    //Initialized as zero, but represents total number of transaction records within the batch.
    @RemconField(5)
    public String numberOfItems;

    //Location code as defined under batch header
    @RemconField(4)
    public String locationCode;

    @RemconField(1)
    public String mode;

    //Box number as defined under batch header
    @RemconField(4)
    public String boxNumber;

    //Batch number as defined under batch header
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

    //Collection date as defined under batch header
    @RemconField(6)
    public String collectionDate;

    @RemconField(5)
    public String microfilmSequence;

    @RemconField(2)
    public String numberOfAdj;

    @RemconField(1)
    public String financialFlag;

    //As defined in the batch header
    @RemconField(2)
    public String sourceCode;

    //Trace number as defined in batch heade.
    @RemconField(value = 30, type = RemconFieldType.Alphanumeric)
    public String traceNumber;

    @RemconField(value = 40, type = RemconFieldType.Filler)
    public String filler;

}
