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

import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.IList;

import com.propertyvista.operations.domain.eft.caledoneft.FundsReconciliationFile;
import com.propertyvista.operations.domain.eft.caledoneft.FundsReconciliationRecordRecord;

@Transient
public interface FundsReconciliationFileDTO extends FundsReconciliationFile {

    //TODO count only, Should be DTO
    @Detached(level = AttachLevel.Detached)
    IList<FundsReconciliationRecordRecord> reconciliationRecords();
}
