/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 19, 2013
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.operations.rpc.dto;

import com.pyx4j.entity.annotations.ExtendsBO;
import com.pyx4j.entity.annotations.Transient;

import com.propertyvista.operations.domain.payment.pad.PadDebitRecord;

@Transient
@ExtendsBO(PadDebitRecord.class)
public interface PadDebitRecordDTO extends PadDebitRecord {

    PadReconciliationDebitRecordDTO reconciliationRecordPaidOrRejected();

    PadReconciliationDebitRecordDTO reconciliationRecordReturn();

}
