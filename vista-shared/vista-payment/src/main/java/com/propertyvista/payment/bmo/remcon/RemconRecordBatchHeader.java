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

/**
 * A batch is created for each incoming source location code (e.g. Royal Bank) and for each unique file date
 * 
 */
public class RemconRecordBatchHeader implements RemconRecord {

    @RemconRecordLenght(1)
    public String recordType = "2";

    //Numeric; Last two are cents
    @RemconRecordLenght(12)
    public String batchAmount;

    //Numeric;
    @RemconRecordLenght(5)
    public String sequenceNunberFirst;

    //Numeric;
    @RemconRecordLenght(5)
    public String sequenceNunberLast;

    //Numeric;
    @RemconRecordLenght(5)
    public String numberOfItems;

    @RemconRecordLenght(40)
    public String filler;
}
