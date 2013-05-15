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

import com.pyx4j.server.contexts.NamespaceManager;

import com.propertyvista.biz.system.OperationsTriggerFacade;
import com.propertyvista.operations.domain.scheduler.PmcProcessType;

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
                SchedulerMock.runProcess(processType);
                return null;
            }
        });
    }
}
