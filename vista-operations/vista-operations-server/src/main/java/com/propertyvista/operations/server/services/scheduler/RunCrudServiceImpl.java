/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-05-07
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.operations.server.services.scheduler;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.server.AbstractCrudServiceImpl;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.rpc.shared.VoidSerializable;

import com.propertyvista.operations.domain.scheduler.Run;
import com.propertyvista.operations.domain.scheduler.RunStatus;
import com.propertyvista.operations.rpc.dto.ExecutionStatusUpdateDTO;
import com.propertyvista.operations.rpc.services.scheduler.RunCrudService;
import com.propertyvista.operations.server.proc.PmcProcessMonitor;

public class RunCrudServiceImpl extends AbstractCrudServiceImpl<Run> implements RunCrudService {

    public RunCrudServiceImpl() {
        super(Run.class);
    }

    @Override
    protected void bind() {
        bindCompleteDBO();
    }

    @Override
    public void retrieveExecutionState(AsyncCallback<ExecutionStatusUpdateDTO> callback, Run runStub) {
        Run run = Persistence.service().retrieve(Run.class, runStub.getPrimaryKey());
        ExecutionStatusUpdateDTO dto = EntityFactory.create(ExecutionStatusUpdateDTO.class);
        dto.status().setValue(run.status().getValue());
        dto.stats().set(run.executionReport());
        callback.onSuccess(dto);
    }

    @Override
    public void stopRun(AsyncCallback<VoidSerializable> callback, Run runStub) {
        if (PmcProcessMonitor.isRunning(runStub)) {
            PmcProcessMonitor.requestExecutionTermination(runStub);
        } else {
            Run run = Persistence.service().retrieve(Run.class, runStub.getPrimaryKey());
            run.status().setValue(RunStatus.Terminated);
            Persistence.service().persist(run);
            Persistence.service().commit();
        }
        callback.onSuccess(null);
    }
}
