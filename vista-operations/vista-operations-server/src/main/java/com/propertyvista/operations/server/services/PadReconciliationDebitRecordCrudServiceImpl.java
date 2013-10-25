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
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;

import com.propertyvista.operations.domain.payment.pad.PadDebitRecord;
import com.propertyvista.operations.domain.payment.pad.PadReconciliationDebitRecord;
import com.propertyvista.operations.rpc.dto.PadDebitRecordDTO;
import com.propertyvista.operations.rpc.dto.PadReconciliationDebitRecordDTO;
import com.propertyvista.operations.rpc.services.PadReconciliationDebitRecordCrudService;

public class PadReconciliationDebitRecordCrudServiceImpl extends AbstractCrudServiceDtoImpl<PadReconciliationDebitRecord, PadReconciliationDebitRecordDTO>
        implements PadReconciliationDebitRecordCrudService {

    public PadReconciliationDebitRecordCrudServiceImpl() {
        super(PadReconciliationDebitRecord.class, PadReconciliationDebitRecordDTO.class);
    }

    @Override
    protected void bind() {
        bindCompleteObject();
    }

    @Override
    protected void enhanceRetrieved(PadReconciliationDebitRecord bo, PadReconciliationDebitRecordDTO to,
            com.pyx4j.entity.rpc.AbstractCrudService.RetrieveTarget retrieveTarget) {
        Persistence.service().retrieve(to.reconciliationSummary().merchantAccount().pmc());

        {
            EntityQueryCriteria<PadDebitRecord> criteria = EntityQueryCriteria.create(PadDebitRecord.class);
            criteria.eq(criteria.proto().transactionId(), bo.transactionId());
            PadDebitRecord padDebitRecord = Persistence.service().retrieve(criteria);
            if (padDebitRecord != null) {
                to.debitRecord().set(padDebitRecord.duplicate(PadDebitRecordDTO.class));
            }
        }
    }

    @Override
    protected void enhanceListRetrieved(PadReconciliationDebitRecord entity, PadReconciliationDebitRecordDTO dto) {
        Persistence.service().retrieve(dto.reconciliationSummary().merchantAccount().pmc());
    }

}
