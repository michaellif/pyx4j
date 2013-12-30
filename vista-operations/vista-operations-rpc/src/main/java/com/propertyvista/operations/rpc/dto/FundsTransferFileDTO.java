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

import com.propertyvista.operations.domain.eft.caledoneft.FundsTransferBatch;
import com.propertyvista.operations.domain.eft.caledoneft.FundsTransferFile;
import com.propertyvista.operations.domain.eft.caledoneft.FundsTransferRecord;

@Transient
public interface FundsTransferFileDTO extends FundsTransferFile {

    @Detached(level = AttachLevel.CollectionSizeOnly)
    IList<FundsTransferRecord> debitRecords();

    @Override
    @Detached(level = AttachLevel.CollectionSizeOnly)
    IList<FundsTransferBatch> batches();

}
