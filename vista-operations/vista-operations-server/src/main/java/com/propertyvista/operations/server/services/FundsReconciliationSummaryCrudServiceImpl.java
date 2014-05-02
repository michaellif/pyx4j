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
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.server.AbstractCrudServiceDtoImpl;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.rpc.shared.ServiceExecution;

import com.propertyvista.operations.domain.eft.caledoneft.FundsReconciliationRecordRecord;
import com.propertyvista.operations.domain.eft.caledoneft.FundsReconciliationSummary;
import com.propertyvista.operations.rpc.dto.FundsReconciliationSummaryDTO;
import com.propertyvista.operations.rpc.services.FundsReconciliationSummaryCrudService;

public class FundsReconciliationSummaryCrudServiceImpl extends AbstractCrudServiceDtoImpl<FundsReconciliationSummary, FundsReconciliationSummaryDTO> implements
        FundsReconciliationSummaryCrudService {

    public FundsReconciliationSummaryCrudServiceImpl() {
        super(FundsReconciliationSummary.class, FundsReconciliationSummaryDTO.class);
    }

    @Override
    protected void bind() {
        bindCompleteObject();
    }

    @Override
    protected FundsReconciliationSummaryDTO init(InitializationData initializationData) {
        throw new RuntimeException("Operation not Supported");
    }

    @Override
    protected void retrievedForList(FundsReconciliationSummary bo) {
        Persistence.ensureRetrieve(bo.merchantAccount().pmc(), AttachLevel.Attached);
    }

    @Override
    protected void enhanceRetrieved(FundsReconciliationSummary bo, FundsReconciliationSummaryDTO to, RetrieveTarget retrieveTarget) {
        Persistence.ensureRetrieve(to.merchantAccount().pmc(), AttachLevel.Attached);

        {
            EntityQueryCriteria<FundsReconciliationRecordRecord> criteria = EntityQueryCriteria.create(FundsReconciliationRecordRecord.class);
            criteria.eq(criteria.proto().reconciliationSummary(), bo);
            to.recordsCount().setValue(Persistence.service().count(criteria));
        }
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
