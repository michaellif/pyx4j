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
package com.propertyvista.operations.rpc.services.scheduler;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.rpc.shared.ServiceExecution;
import com.pyx4j.rpc.shared.VoidSerializable;

import com.propertyvista.operations.domain.scheduler.Run;
import com.propertyvista.operations.rpc.dto.ExecutionStatusUpdateDTO;

public interface RunCrudService extends AbstractCrudService<Run> {

    // This is fallback for WebSocket
    @ServiceExecution(operationType = ServiceExecution.OperationType.NonBlocking)
    void retrieveExecutionState(AsyncCallback<ExecutionStatusUpdateDTO> callback, Run runStub);

    void stopRun(AsyncCallback<VoidSerializable> callback, Run runStub);
}
