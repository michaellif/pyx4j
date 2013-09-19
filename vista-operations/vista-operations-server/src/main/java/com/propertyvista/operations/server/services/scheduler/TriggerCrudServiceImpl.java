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
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.config.server.SystemDateManager;
import com.pyx4j.entity.server.AbstractCrudServiceDtoImpl;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;

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
        bindCompleteDBO();
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
    protected void enhanceRetrieved(Trigger entity, TriggerDTO dto, RetrieveTarget retrieveTarget) {
        if (entity != null) {
            JobUtils.getScheduleDetails(dto);
        }
    }

    @Override
    protected void create(Trigger entity, TriggerDTO dto) {
        super.create(entity, dto);
        JobUtils.createJobDetail(entity);
        JobUtils.updateSchedule(null, entity);
    }

    @Override
    protected void save(Trigger entity, TriggerDTO dto) {
        Trigger origProcess = Persistence.service().retrieve(Trigger.class, entity.getPrimaryKey());
        super.save(entity, dto);
        JobUtils.updateSchedule(origProcess, entity);
    }

    @Override
    public void runImmediately(AsyncCallback<Run> callback, TriggerDTO triggerStub) {
        runForDate(callback, triggerStub, new LogicalDate(SystemDateManager.getDate()));
    }

    @Override
    public void runForDate(AsyncCallback<Run> callback, TriggerDTO triggerDTOStub, LogicalDate executionDate) {
        Run run = ServerSideFactory.create(OperationsTriggerFacade.class).startProcess(triggerDTOStub, null, executionDate);
        callback.onSuccess(run.<Run> createIdentityStub());
    }
}
