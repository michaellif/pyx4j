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
package com.propertyvista.operations.server.services;

import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.server.AbstractCrudServiceDtoImpl;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.operations.domain.eft.caledoneft.FundsTransferBatch;
import com.propertyvista.operations.domain.eft.caledoneft.FundsTransferFile;
import com.propertyvista.operations.domain.eft.caledoneft.FundsTransferRecord;
import com.propertyvista.operations.rpc.dto.FundsTransferFileDTO;
import com.propertyvista.operations.rpc.services.PadFileCrudService;

public class PadFileCrudServiceImpl extends AbstractCrudServiceDtoImpl<FundsTransferFile, FundsTransferFileDTO> implements PadFileCrudService {

    public PadFileCrudServiceImpl() {
        super(FundsTransferFile.class, FundsTransferFileDTO.class);
    }

    @Override
    protected void bind() {
        bindCompleteObject();
    }

    @Override
    protected void enhanceRetrieved(FundsTransferFile bo, FundsTransferFileDTO to, RetrieveTarget retrieveTarget) {
        {
            EntityQueryCriteria<FundsTransferRecord> criteria = EntityQueryCriteria.create(FundsTransferRecord.class);
            criteria.eq(criteria.proto().padBatch().padFile(), bo);
            to.debitRecords().setCollectionSizeOnly(Persistence.service().count(criteria));
        }
        {
            EntityQueryCriteria<FundsTransferBatch> criteria = EntityQueryCriteria.create(FundsTransferBatch.class);
            criteria.eq(criteria.proto().padFile(), bo);
            to.batches().setCollectionSizeOnly(Persistence.service().count(criteria));
        }
    }

}
