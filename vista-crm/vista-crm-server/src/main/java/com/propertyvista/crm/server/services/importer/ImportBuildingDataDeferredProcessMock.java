/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Apr 15, 2014
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.crm.server.services.importer;

import com.pyx4j.gwt.rpc.deferred.DeferredProcessProgressResponse;
import com.pyx4j.gwt.server.deferred.AbstractDeferredProcess;

import com.propertyvista.biz.ExecutionMonitor;
import com.propertyvista.operations.domain.scheduler.CompletionType;

@SuppressWarnings("serial")
public class ImportBuildingDataDeferredProcessMock extends AbstractDeferredProcess {

    private final ExecutionMonitor monitor;

    ImportBuildingDataDeferredProcessMock() {
        monitor = new ExecutionMonitor();
    }

    @Override
    public void cancel() {
        monitor.requestTermination();
        super.cancel();
    }

    @Override
    public DeferredProcessProgressResponse status() {
        DeferredProcessProgressResponse r = super.status();
        if (!r.isCompleted() && !r.isCanceled()) {
            r.setMessage("Errors: " + monitor.getErred());
        } else if (monitor.getErred() > 0) {
            r.setErrorStatusMessage(monitor.getTextMessages(CompletionType.erred, CompletionType.failed));
        } else {
            r.setMessage(monitor.getTextMessages(CompletionType.erred, CompletionType.failed));
        }
        return r;
    }

    @Override
    public void execute() {
        progress.progressMaximum.set(100 * 100);
        for (int b = 1; b <= 100 && !monitor.isTerminationRequested(); b++) {
            for (int u = 1; u <= 100 && !monitor.isTerminationRequested(); u++) {
                monitor.addProcessedEvent("Unit", "bCode" + b + ", " + "uCode" + u);
                if (u % 10 == 0) {
                    monitor.addErredEvent("Unit", "Some error #" + u + "x");
                }
                progress.progress.set(b * u);
            }
            monitor.addProcessedEvent("Building", "bCode" + b);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                break;
            }
        }
        completed = true;
    }

}
