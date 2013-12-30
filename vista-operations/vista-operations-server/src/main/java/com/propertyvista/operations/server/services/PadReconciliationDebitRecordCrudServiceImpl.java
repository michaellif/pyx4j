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
import com.propertyvista.operations.rpc.services.PadReconciliationDebitRecordCrudService;

public class PadReconciliationDebitRecordCrudServiceImpl extends AbstractCrudServiceDtoImpl<FundsReconciliationRecordRecord, FundsReconciliationRecordRecordDTO>
        implements PadReconciliationDebitRecordCrudService {

    public PadReconciliationDebitRecordCrudServiceImpl() {
        super(FundsReconciliationRecordRecord.class, FundsReconciliationRecordRecordDTO.class);
    }

    @Override
    protected void bind() {
        bindCompleteObject();
    }

    @Override
    protected void enhanceRetrieved(FundsReconciliationRecordRecord bo, FundsReconciliationRecordRecordDTO to,
            com.pyx4j.entity.rpc.AbstractCrudService.RetrieveTarget retrieveTarget) {
        Persistence.service().retrieve(to.reconciliationSummary().merchantAccount().pmc());

        {
            EntityQueryCriteria<FundsTransferRecord> criteria = EntityQueryCriteria.create(FundsTransferRecord.class);
            criteria.eq(criteria.proto().transactionId(), bo.transactionId());
            FundsTransferRecord padDebitRecord = Persistence.service().retrieve(criteria);
            if (padDebitRecord != null) {
                to.debitRecord().set(padDebitRecord.duplicate(FundsTransferRecordDTO.class));
            }
        }
    }

    @Override
    protected void enhanceListRetrieved(FundsReconciliationRecordRecord entity, FundsReconciliationRecordRecordDTO dto) {
        Persistence.service().retrieve(dto.reconciliationSummary().merchantAccount().pmc());
    }

}
