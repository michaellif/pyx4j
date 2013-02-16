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

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Consts;
import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.entity.server.AbstractCrudServiceDtoImpl;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.operations.domain.scheduler.Run;
import com.propertyvista.operations.domain.scheduler.RunStatus;
import com.propertyvista.operations.domain.scheduler.Trigger;
import com.propertyvista.operations.domain.scheduler.TriggerSchedule;
import com.propertyvista.operations.rpc.TriggerDTO;
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
    protected void enhanceRetrieved(Trigger entity, TriggerDTO dto, RetrieveTraget retrieveTraget) {
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
        runForDate(callback, triggerStub, Persistence.service().getTransactionSystemTime());
    }

    @Override
    public void runForDate(AsyncCallback<Run> callback, TriggerDTO triggerDTOStub, Date executionDate) {
        Trigger triggerStub = EntityFactory.createIdentityStub(Trigger.class, triggerDTOStub.getPrimaryKey());
        {
            EntityQueryCriteria<Run> criteria = EntityQueryCriteria.create(Run.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().trigger(), triggerStub));
            criteria.add(PropertyCriterion.eq(criteria.proto().status(), RunStatus.Running));
            Run existingRun = Persistence.service().retrieve(criteria);
            if (existingRun != null) {
                throw new UserRuntimeException("The process is already running");
            }
        }
        Date startDate = new Date();
        JobUtils.runNow(triggerStub, executionDate);
        // Find running Run
        long start = System.currentTimeMillis();
        Run run = null;
        do {
            EntityQueryCriteria<Run> criteria = EntityQueryCriteria.create(Run.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().trigger(), triggerStub));
            criteria.add(PropertyCriterion.ge(criteria.proto().updated(), startDate));
            criteria.add(PropertyCriterion.in(criteria.proto().status(), RunStatus.Sleeping, RunStatus.Running));
            run = Persistence.service().retrieve(criteria);
            if (run != null) {
                break;
            }
        } while ((System.currentTimeMillis() - start) < 10 * Consts.SEC2MSEC);

        if (run == null) {
            EntityQueryCriteria<Run> criteria = EntityQueryCriteria.create(Run.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().trigger(), triggerStub));
            criteria.add(PropertyCriterion.ge(criteria.proto().updated(), startDate));
            run = Persistence.service().retrieve(criteria);
        }

        if (run == null) {
            throw new UserRuntimeException("Can't find started run");
        }
        Run runStub = run.createIdentityStub();
        callback.onSuccess(runStub);
    }
}
