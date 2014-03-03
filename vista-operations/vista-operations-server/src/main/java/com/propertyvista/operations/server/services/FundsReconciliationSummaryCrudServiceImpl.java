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

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.server.AbstractListServiceDtoImpl;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.rpc.shared.ServiceExecution;

import com.propertyvista.operations.domain.eft.caledoneft.FundsReconciliationRecordRecord;
import com.propertyvista.operations.domain.eft.caledoneft.FundsReconciliationSummary;
import com.propertyvista.operations.rpc.dto.FundsReconciliationSummaryDTO;
import com.propertyvista.operations.rpc.services.FundsReconciliationSummaryCrudService;

public class FundsReconciliationSummaryCrudServiceImpl extends AbstractListServiceDtoImpl<FundsReconciliationSummary, FundsReconciliationSummaryDTO> implements
        FundsReconciliationSummaryCrudService {

    public FundsReconciliationSummaryCrudServiceImpl() {
        super(FundsReconciliationSummary.class, FundsReconciliationSummaryDTO.class);
    }

    @Override
    protected void bind() {
        bindCompleteObject();
    }

    @Override
    public void init(AsyncCallback<FundsReconciliationSummaryDTO> callback, com.pyx4j.entity.rpc.AbstractCrudService.InitializationData initializationData) {
        throw new RuntimeException("Operation not Supported");
    }

    @Override
    public void retrieve(AsyncCallback<FundsReconciliationSummaryDTO> callback, Key entityId,
            com.pyx4j.entity.rpc.AbstractCrudService.RetrieveTarget retrieveTarget) {

        FundsReconciliationSummary summary = Persistence.secureRetrieve(FundsReconciliationSummary.class, entityId);
        FundsReconciliationSummaryDTO summaryDto = EntityFactory.create(FundsReconciliationSummaryDTO.class);
        copyBOtoTO(summary, summaryDto);

        EntityQueryCriteria<FundsReconciliationRecordRecord> recordsCountCriteria = EntityQueryCriteria.create(FundsReconciliationRecordRecord.class);
        recordsCountCriteria.eq(recordsCountCriteria.proto().reconciliationSummary(), entityId);
        summaryDto.recordsCount().setValue(Persistence.service().count(recordsCountCriteria));

        callback.onSuccess(summaryDto);
    }

    @Override
    public void create(AsyncCallback<Key> callback, FundsReconciliationSummaryDTO editableEntity) {
        throw new RuntimeException("Operation not Supported");
    }

    @Override
    @ServiceExecution(waitCaption = "Saving...")
    public void save(AsyncCallback<Key> callback, FundsReconciliationSummaryDTO editableEntity) {
        throw new RuntimeException("Operation not Supported");
    }
}
