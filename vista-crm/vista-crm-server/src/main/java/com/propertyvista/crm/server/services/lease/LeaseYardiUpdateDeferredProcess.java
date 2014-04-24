/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 30, 2012
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.crm.server.services.lease;

import java.rmi.RemoteException;

import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.Executable;
import com.pyx4j.entity.server.TransactionScopeOption;
import com.pyx4j.entity.server.UnitOfWork;
import com.pyx4j.gwt.rpc.deferred.DeferredProcessProgressResponse;
import com.pyx4j.gwt.server.deferred.AbstractDeferredProcess;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.biz.ExecutionMonitor;
import com.propertyvista.biz.system.YardiARFacade;
import com.propertyvista.biz.system.YardiServiceException;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.operations.domain.scheduler.CompletionType;

class LeaseYardiUpdateDeferredProcess extends AbstractDeferredProcess {
    private static final long serialVersionUID = 1L;

    private final static I18n i18n = I18n.get(LeaseYardiUpdateDeferredProcess.class);

    private final Lease lease;

    private final ExecutionMonitor monitor;

    LeaseYardiUpdateDeferredProcess(Lease lease) {
        this.lease = lease;
        monitor = new ExecutionMonitor();
    }

    @Override
    public void execute() {
        new UnitOfWork(TransactionScopeOption.RequiresNew).execute(new Executable<Void, RuntimeException>() {
            @Override
            public Void execute() {
                try {
                    ServerSideFactory.create(YardiARFacade.class).updateLease(lease, monitor);
                } catch (RemoteException e) {
                    throw new UserRuntimeException(i18n.tr("Yardi connection problem"), e);
                } catch (YardiServiceException e) {
                    throw new UserRuntimeException(i18n.tr("Error updating lease form Yardi"), e);
                }
                return null;
            }
        });
        completed = true;
    }

    @Override
    public void cancel() {
        canceled = true;
    }

    @Override
    public DeferredProcessProgressResponse status() {
        DeferredProcessProgressResponse r = super.status();
        if (!r.isCompleted() && !r.isCanceled()) {
            r.setMessage(i18n.tr("Updating Lease..."));
            r.setProgressMaximum(100);
            r.setProgress((int) (100 * monitor.getProcessed() / monitor.getExpectedTotal()));
        } else if (monitor.getErred() > 0) {
            r.setErrorStatusMessage(monitor.getTextMessages(CompletionType.erred) + monitor.getTextMessages(CompletionType.failed));
        } else {
            r.setMessage(monitor.getTextMessages(CompletionType.erred) + monitor.getTextMessages(CompletionType.failed));
        }
        return r;
    }

}
