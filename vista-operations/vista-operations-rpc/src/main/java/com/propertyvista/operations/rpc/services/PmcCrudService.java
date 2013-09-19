/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-06-13
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.operations.rpc.services;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.rpc.shared.VoidSerializable;

import com.propertyvista.operations.domain.scheduler.PmcProcessType;
import com.propertyvista.operations.domain.scheduler.Run;
import com.propertyvista.operations.rpc.dto.PmcDTO;

public interface PmcCrudService extends AbstractCrudService<PmcDTO> {

    public void resetCache(AsyncCallback<VoidSerializable> callback, Key entityId);

    public void activate(AsyncCallback<String> callback, Key entityId);

    public void suspend(AsyncCallback<VoidSerializable> callback, Key entityId);

    public void cancelPmc(AsyncCallback<VoidSerializable> callback, Key entityId);

    public void runPmcProcess(AsyncCallback<Run> callback, Key entityId, PmcProcessType processType, LogicalDate executionDate);
}
