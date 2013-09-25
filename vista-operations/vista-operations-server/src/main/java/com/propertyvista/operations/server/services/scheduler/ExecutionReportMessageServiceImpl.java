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
 * @version $Id$
 */
package com.propertyvista.operations.server.services.scheduler;

import com.pyx4j.entity.server.AbstractCrudServiceImpl;

import com.propertyvista.operations.domain.scheduler.ExecutionReportMessage;
import com.propertyvista.operations.rpc.services.scheduler.ExecutionReportMessageService;

public class ExecutionReportMessageServiceImpl extends AbstractCrudServiceImpl<ExecutionReportMessage> implements ExecutionReportMessageService {

    public ExecutionReportMessageServiceImpl() {
        super(ExecutionReportMessage.class);
    }

    @Override
    protected void bind() {
        bindCompleteObject();
    }

}
