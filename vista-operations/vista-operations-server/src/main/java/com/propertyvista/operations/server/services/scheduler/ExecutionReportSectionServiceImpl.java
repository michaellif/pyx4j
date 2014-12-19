/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-02-25
 * @author stanp
 */
package com.propertyvista.operations.server.services.scheduler;

import com.pyx4j.entity.server.AbstractCrudServiceImpl;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.operations.domain.scheduler.ExecutionReportSection;
import com.propertyvista.operations.rpc.services.scheduler.ExecutionReportSectionService;

public class ExecutionReportSectionServiceImpl extends AbstractCrudServiceImpl<ExecutionReportSection> implements ExecutionReportSectionService {

    public ExecutionReportSectionServiceImpl() {
        super(ExecutionReportSection.class);
    }

    @Override
    protected void bind() {
        bindCompleteObject();
    }

    @Override
    protected void enhanceRetrieved(ExecutionReportSection bo, ExecutionReportSection to, com.pyx4j.entity.rpc.AbstractCrudService.RetrieveTarget retrieveTarget) {
        super.enhanceRetrieved(bo, to, retrieveTarget);
        if (to.messages().size() > 0) {
            Persistence.service().retrieve(to.messages().get(0));
        }
    }

    @Override
    protected void enhanceListRetrieved(ExecutionReportSection bo, ExecutionReportSection dto) {
        super.enhanceListRetrieved(bo, dto);
        if (dto.messages().size() > 0) {
            Persistence.service().retrieve(dto.messages().get(0));
        }
    }
}
