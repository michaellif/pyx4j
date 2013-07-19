/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-05-15
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.test.mock.schedule;

import java.util.concurrent.Callable;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.server.SystemDateManager;
import com.pyx4j.server.contexts.NamespaceManager;

import com.propertyvista.biz.system.OperationsTriggerFacade;
import com.propertyvista.domain.pmc.Pmc;
import com.propertyvista.operations.domain.scheduler.PmcProcessType;
import com.propertyvista.operations.domain.scheduler.Run;
import com.propertyvista.operations.domain.scheduler.Trigger;

public class OperationsTriggerFacadeMock implements OperationsTriggerFacade {

    @Override
    public void startProcess(final PmcProcessType processType) {
        String namespace = SchedulerMock.requestNamspaceLocal.get();
        if (namespace == null) {
            namespace = NamespaceManager.getNamespace();
        }

        NamespaceManager.runInTargetNamespace(namespace, new Callable<Void>() {
            @Override
            public Void call() {
                SchedulerMock.runProcess(processType, SystemDateManager.getDate());
                return null;
            }
        });
    }

    @Override
    public Run startProcess(PmcProcessType processType, Pmc pmcId, LogicalDate executionDate) {
        throw new Error("Not supported");
    }

    @Override
    public Run startProcess(Trigger triggerId, Pmc pmc, LogicalDate executionDate) {
        throw new Error("Not supported");
    }
}
