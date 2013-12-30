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

import com.propertyvista.operations.domain.payment.pad.FundsTransferRecord;
import com.propertyvista.operations.domain.payment.pad.FundsReconciliationRecordRecord;
import com.propertyvista.operations.rpc.dto.FundsTransferRecordDTO;
import com.propertyvista.operations.rpc.dto.FundsReconciliationRecordRecordDTO;
import com.propertyvista.operations.rpc.services.PadDebitRecordCrudService;

public class PadDebitRecordCrudServiceImpl extends AbstractCrudServiceDtoImpl<FundsTransferRecord, FundsTransferRecordDTO> implements PadDebitRecordCrudService {

    public PadDebitRecordCrudServiceImpl() {
        super(FundsTransferRecord.class, FundsTransferRecordDTO.class);
    }

    @Override
    protected void bind() {
        bindCompleteObject();
    }

    @Override
    protected void enhanceRetrieved(FundsTransferRecord bo, FundsTransferRecordDTO to, RetrieveTarget retrieveTarget) {
        Persistence.service().retrieve(to.padBatch().pmc());

        {
            EntityQueryCriteria<FundsReconciliationRecordRecord> criteria = EntityQueryCriteria.create(FundsReconciliationRecordRecord.class);
            criteria.eq(criteria.proto().transactionId(), bo.transactionId());
            criteria.asc(criteria.proto().id());
            FundsReconciliationRecordRecord padDebitRecord = Persistence.service().retrieve(criteria);
            if (padDebitRecord != null) {
                to.reconciliationRecordPaidOrRejected().set(padDebitRecord.duplicate(FundsReconciliationRecordRecordDTO.class));
            }
        }

        if (!to.reconciliationRecordPaidOrRejected().isEmpty()) {
            EntityQueryCriteria<FundsReconciliationRecordRecord> criteria = EntityQueryCriteria.create(FundsReconciliationRecordRecord.class);
            criteria.eq(criteria.proto().transactionId(), bo.transactionId());
            criteria.ne(criteria.proto().id(), to.reconciliationRecordPaidOrRejected());
            FundsReconciliationRecordRecord padDebitRecord = Persistence.service().retrieve(criteria);
            if (padDebitRecord != null) {
                to.reconciliationRecordReturn().set(padDebitRecord.duplicate(FundsReconciliationRecordRecordDTO.class));
            }
        }
    }

    @Override
    protected void enhanceListRetrieved(FundsTransferRecord entity, FundsTransferRecordDTO dto) {
        Persistence.service().retrieve(dto.padBatch().pmc());
    }

}
