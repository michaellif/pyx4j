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

import com.propertyvista.operations.domain.payment.pad.PadDebitRecord;
import com.propertyvista.operations.domain.payment.pad.PadReconciliationDebitRecord;
import com.propertyvista.operations.rpc.dto.PadDebitRecordDTO;
import com.propertyvista.operations.rpc.dto.PadReconciliationDebitRecordDTO;
import com.propertyvista.operations.rpc.services.PadDebitRecordCrudService;

public class PadDebitRecordCrudServiceImpl extends AbstractCrudServiceDtoImpl<PadDebitRecord, PadDebitRecordDTO> implements PadDebitRecordCrudService {

    public PadDebitRecordCrudServiceImpl() {
        super(PadDebitRecord.class, PadDebitRecordDTO.class);
    }

    @Override
    protected void bind() {
        bindCompleteObject();
    }

    @Override
    protected void enhanceRetrieved(PadDebitRecord bo, PadDebitRecordDTO to, RetrieveTarget retrieveTarget) {
        Persistence.service().retrieve(to.padBatch().pmc());

        {
            EntityQueryCriteria<PadReconciliationDebitRecord> criteria = EntityQueryCriteria.create(PadReconciliationDebitRecord.class);
            criteria.eq(criteria.proto().transactionId(), bo.transactionId());
            criteria.asc(criteria.proto().id());
            PadReconciliationDebitRecord padDebitRecord = Persistence.service().retrieve(criteria);
            if (padDebitRecord != null) {
                to.reconciliationRecordPaidOrRejected().set(padDebitRecord.duplicate(PadReconciliationDebitRecordDTO.class));
            }
        }

        if (!to.reconciliationRecordPaidOrRejected().isEmpty()) {
            EntityQueryCriteria<PadReconciliationDebitRecord> criteria = EntityQueryCriteria.create(PadReconciliationDebitRecord.class);
            criteria.eq(criteria.proto().transactionId(), bo.transactionId());
            criteria.ne(criteria.proto().id(), to.reconciliationRecordPaidOrRejected());
            PadReconciliationDebitRecord padDebitRecord = Persistence.service().retrieve(criteria);
            if (padDebitRecord != null) {
                to.reconciliationRecordReturn().set(padDebitRecord.duplicate(PadReconciliationDebitRecordDTO.class));
            }
        }
    }

    @Override
    protected void enhanceListRetrieved(PadDebitRecord entity, PadDebitRecordDTO dto) {
        Persistence.service().retrieve(dto.padBatch().pmc());
    }

}
