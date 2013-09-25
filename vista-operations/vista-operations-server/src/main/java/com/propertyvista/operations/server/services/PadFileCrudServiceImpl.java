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

import com.pyx4j.entity.server.AbstractCrudServiceDtoImpl;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;

import com.propertyvista.operations.domain.payment.pad.PadDebitRecord;
import com.propertyvista.operations.domain.payment.pad.PadFile;
import com.propertyvista.operations.rpc.dto.PadFileDTO;
import com.propertyvista.operations.rpc.services.PadFileCrudService;

public class PadFileCrudServiceImpl extends AbstractCrudServiceDtoImpl<PadFile, PadFileDTO> implements PadFileCrudService {

    public PadFileCrudServiceImpl() {
        super(PadFile.class, PadFileDTO.class);
    }

    @Override
    protected void bind() {
        bindCompleteObject();
    }

    @Override
    protected void enhanceRetrieved(PadFile bo, PadFileDTO to, RetrieveTarget retrieveTarget) {
        {
            //TODO count only,
            EntityQueryCriteria<PadDebitRecord> criteria = EntityQueryCriteria.create(PadDebitRecord.class);
            criteria.eq(criteria.proto().padBatch().padFile(), bo);
            to.debitRecords().addAll(Persistence.service().query(criteria, AttachLevel.IdOnly));
        }
    }

}
