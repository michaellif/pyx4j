/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 4, 2012
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.operations.server.services.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.commons.TimeUtils;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.config.server.SystemDateManager;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.server.AbstractCrudServiceDtoImpl;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.biz.system.OperationsTriggerFacade;
import com.propertyvista.operations.domain.scheduler.Run;
import com.propertyvista.operations.domain.scheduler.Trigger;
import com.propertyvista.operations.domain.scheduler.TriggerSchedule;
import com.propertyvista.operations.rpc.dto.TriggerDTO;
import com.propertyvista.operations.rpc.services.scheduler.TriggerCrudService;
import com.propertyvista.operations.server.proc.JobUtils;

public class TriggerCrudServiceImpl extends AbstractCrudServiceDtoImpl<Trigger, TriggerDTO> implements TriggerCrudService {

    private static final Logger log = LoggerFactory.getLogger(TriggerCrudServiceImpl.class);

    public TriggerCrudServiceImpl() {
        super(Trigger.class, TriggerDTO.class);
    }

    @Override
    protected void bind() {
        bindCompleteObject();
    }

    @Override
    protected TriggerDTO init(InitializationData initializationData) {
        TriggerDTO process = EntityFactory.create(TriggerDTO.class);

        process.created().setValue(SystemDateManager.getDate());

        return process;
    }

    @Override
    protected void enhanceListRetrieved(Trigger entity, TriggerDTO dto) {
        super.enhanceListRetrieved(entity, dto);

        {
            StringBuilder b = new StringBuilder();
            for (TriggerSchedule triggerSchedule : dto.schedules()) {
                if (b.length() > 0) {
                    b.append("; ");
                }
                b.append(triggerSchedule.repeatType().getStringView()).append(' ').append(triggerSchedule.time().getStringView());
            }
            dto.schedule().setValue(b.toString());
            JobUtils.getScheduleDetails(dto);
        }
    }

    @Override
    protected void enhanceRetrieved(Trigger bo, TriggerDTO to, RetrieveTarget retrieveTarget) {
        if (bo != null) {
            JobUtils.getScheduleDetails(to);
        }

        if (!bo.runTimeout().isNull()) {
            to.timeout().setValue(TimeUtils.durationFormatSeconds(bo.runTimeout().getValue()));
        }
    }

    @Override
    protected void create(Trigger bo, TriggerDTO to) {
        super.create(bo, to);
        JobUtils.createJobDetail(bo);
        JobUtils.updateSchedule(null, bo);
    }

    @Override
    protected boolean save(Trigger bo, TriggerDTO to) {
        Trigger origProcess = Persistence.service().retrieve(Trigger.class, bo.getPrimaryKey());

        if (to.timeout().isNull()) {
            bo.runTimeout().setValue(null);
        } else {
            bo.runTimeout().setValue(TimeUtils.durationParseSeconds(to.timeout().getValue()));
        }

        boolean updated = super.save(bo, to);
        JobUtils.updateSchedule(origProcess, bo);
        return updated;
    }

    @Override
    public void runImmediately(AsyncCallback<Run> callback, TriggerDTO triggerStub) {
        runForDate(callback, triggerStub, SystemDateManager.getLogicalDate());
    }

    @Override
    public void runForDate(AsyncCallback<Run> callback, TriggerDTO triggerDTOStub, LogicalDate executionDate) {
        Run run = ServerSideFactory.create(OperationsTriggerFacade.class).startProcess(triggerDTOStub, null, executionDate);
        callback.onSuccess(run.<Run> createIdentityStub());
    }
}
