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
package com.propertyvista.admin.server.services.proc;

import com.pyx4j.entity.server.AbstractCrudServiceImpl;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.admin.domain.proc.Process;
import com.propertyvista.admin.rpc.services.proc.ProcessCrudService;
import com.propertyvista.admin.server.proc.JobUtils;

public class ProcessCrudServiceImpl extends AbstractCrudServiceImpl<Process> implements ProcessCrudService {

    public ProcessCrudServiceImpl() {
        super(Process.class);
    }

    @Override
    protected void bind() {
        bindCompleateDBO();
    }

    @Override
    protected void enhanceRetrieved(Process entity, Process dto) {
        if (entity != null) {
            JobUtils.getScheduleDetails(dto);
        }
    }

    @Override
    protected void persist(Process entity, Process dto) {
        Process origProcess = null;
        if (entity.getPrimaryKey() != null) {
            origProcess = Persistence.service().retrieve(Process.class, entity.getPrimaryKey());
        }
        super.persist(entity, dto);
        JobUtils.updateSchedule(origProcess, entity);
    }

}
