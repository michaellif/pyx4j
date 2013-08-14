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
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.rpc.shared.VoidSerializable;

import com.propertyvista.operations.domain.scheduler.RunData;
import com.propertyvista.operations.domain.scheduler.RunDataStatus;
import com.propertyvista.operations.rpc.services.scheduler.RunDataCrudService;

public class RunDataCrudServiceImpl extends AbstractCrudServiceImpl<RunData> implements RunDataCrudService {

    public RunDataCrudServiceImpl() {
        super(RunData.class);
    }

    @Override
    protected void bind() {
        bindCompleteDBO();
    }

    @Override
    protected void enhanceRetrieved(RunData entity, RunData dto, RetrieveTarget retrieveTarget) {
        super.enhanceRetrieved(entity, dto, retrieveTarget);

        Persistence.ensureRetrieve(dto.execution(), AttachLevel.Attached);
    }

    @Override
    protected void enhanceListRetrieved(RunData entity, RunData dto) {
        super.enhanceListRetrieved(entity, dto);

        Persistence.ensureRetrieve(dto.execution(), AttachLevel.Attached);
        Persistence.ensureRetrieve(dto.execution().trigger(), AttachLevel.ToStringMembers);
    }

    @Override
    public void cancelDataRun(AsyncCallback<VoidSerializable> callback, RunData runDataStub) {
        RunData runData = Persistence.service().retrieve(RunData.class, runDataStub.getPrimaryKey());
        if (runData.status().getValue() == RunDataStatus.NeverRan) {
            runData.status().setValue(RunDataStatus.Canceled);
            Persistence.service().persist(runData);
            Persistence.service().commit();
        }
        callback.onSuccess(null);
    }
}
