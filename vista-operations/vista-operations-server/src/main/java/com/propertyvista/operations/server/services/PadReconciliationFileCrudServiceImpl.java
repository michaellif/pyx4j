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

import com.propertyvista.operations.domain.eft.caledoneft.FundsReconciliationFile;
import com.propertyvista.operations.domain.eft.caledoneft.FundsReconciliationRecordRecord;
import com.propertyvista.operations.rpc.dto.FundsReconciliationFileDTO;
import com.propertyvista.operations.rpc.services.PadReconciliationFileCrudService;

public class PadReconciliationFileCrudServiceImpl extends AbstractCrudServiceDtoImpl<FundsReconciliationFile, FundsReconciliationFileDTO> implements
        PadReconciliationFileCrudService {

    public PadReconciliationFileCrudServiceImpl() {
        super(FundsReconciliationFile.class, FundsReconciliationFileDTO.class);
    }

    @Override
    protected void bind() {
        bindCompleteObject();
    }

    @Override
    protected void enhanceRetrieved(FundsReconciliationFile bo, FundsReconciliationFileDTO to, RetrieveTarget retrieveTarget) {
        {
            EntityQueryCriteria<FundsReconciliationRecordRecord> criteria = EntityQueryCriteria.create(FundsReconciliationRecordRecord.class);
            criteria.eq(criteria.proto().reconciliationSummary().reconciliationFile(), bo);
            to.reconciliationRecordsCount().setValue(Persistence.service().count(criteria));
        }
    }
}
