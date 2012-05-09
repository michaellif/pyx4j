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
package com.propertyvista.admin.server.services.scheduler;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Consts;
import com.pyx4j.entity.server.AbstractCrudServiceImpl;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.rpc.shared.UserRuntimeException;

import com.propertyvista.admin.domain.scheduler.Run;
import com.propertyvista.admin.domain.scheduler.RunStatus;
import com.propertyvista.admin.domain.scheduler.Trigger;
import com.propertyvista.admin.rpc.services.scheduler.TriggerCrudService;
import com.propertyvista.admin.server.proc.JobUtils;

public class TriggerCrudServiceImpl extends AbstractCrudServiceImpl<Trigger> implements TriggerCrudService {

    private static final Logger log = LoggerFactory.getLogger(TriggerCrudServiceImpl.class);

    public TriggerCrudServiceImpl() {
        super(Trigger.class);
    }

    @Override
    protected void bind() {
        bindCompleateDBO();
    }

    @Override
    protected void enhanceRetrieved(Trigger entity, Trigger dto) {
        if (entity != null) {
            JobUtils.getScheduleDetails(dto);
        }
    }

    @Override
    protected void create(Trigger entity, Trigger dto) {
        super.create(entity, dto);
        JobUtils.createJobDetail(entity);
        JobUtils.updateSchedule(null, entity);
    }

    @Override
    protected void save(Trigger entity, Trigger dto) {
        Trigger origProcess = Persistence.service().retrieve(Trigger.class, entity.getPrimaryKey());
        super.save(entity, dto);
        JobUtils.updateSchedule(origProcess, entity);
    }

    @Override
    public void runImmediately(AsyncCallback<Run> callback, Trigger triggerStub) {
        EntityQueryCriteria<Run> criteria = EntityQueryCriteria.create(Run.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().trigger(), triggerStub));
        criteria.add(PropertyCriterion.eq(criteria.proto().status(), RunStatus.Running));
        Run run = Persistence.service().retrieve(criteria);
        if (run != null) {
            throw new UserRuntimeException("The process is already running");
        }

        Date startDate = new Date();
        JobUtils.runNow(triggerStub);
        // Find running Run
        long start = System.currentTimeMillis();
        Run runStub = null;
        do {
            criteria = EntityQueryCriteria.create(Run.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().trigger(), triggerStub));
            criteria.add(PropertyCriterion.in(criteria.proto().status(), RunStatus.Sleeping, RunStatus.Running));
            run = Persistence.service().retrieve(criteria);
            if (run != null) {
                runStub = run.createIdentityStub();
                break;
            }
        } while ((System.currentTimeMillis() - start) < 10 * Consts.SEC2MSEC);

        if (runStub == null) {
            criteria = EntityQueryCriteria.create(Run.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().trigger(), triggerStub));
            criteria.add(PropertyCriterion.ge(criteria.proto().updated(), startDate));
            run = Persistence.service().retrieve(criteria);
            if (run != null) {
                runStub = run.createIdentityStub();
            }
        }

        if (runStub == null) {
            throw new UserRuntimeException("Can't find started run");
        }
        callback.onSuccess(runStub);
    }

}
