/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Apr 7, 2014
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.crm.server.services.building;

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
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.operations.domain.scheduler.CompletionType;

class BuildingYardiUpdateDeferredProcess extends AbstractDeferredProcess {
    private static final long serialVersionUID = 1L;

    private final static I18n i18n = I18n.get(BuildingYardiUpdateDeferredProcess.class);

    private final Building building;

    private final ExecutionMonitor monitor;

    BuildingYardiUpdateDeferredProcess(Building building) {
        this.building = building;
        monitor = new ExecutionMonitor();
    }

    @Override
    public void execute() {
        new UnitOfWork(TransactionScopeOption.RequiresNew).execute(new Executable<Void, RuntimeException>() {
            @Override
            public Void execute() {

                try {
                    ServerSideFactory.create(YardiARFacade.class).updateBuilding(building, monitor);
                } catch (RemoteException e) {
                    throw new UserRuntimeException(i18n.tr("Yardi connection problem"), e);
                } catch (YardiServiceException e) {
                    throw new UserRuntimeException(i18n.tr("Error updating building form Yardi"), e);
                }

                return null;
            }
        });
        completed = true;
    }

    @Override
    public DeferredProcessProgressResponse status() {
        DeferredProcessProgressResponse r = super.status();
        String message = null;
        if (!r.isCompleted() && !r.isCanceled()) {
            int progress = 0;
            // unit progress
            Long currentCounter = monitor.getTotalCounter("Unit");
            Long expectedTotal = monitor.getExpectedTotal("Unit");
            if (currentCounter != null && expectedTotal != null && expectedTotal > 0) {
                progress += (int) (40 * currentCounter / expectedTotal);
                message = "Updating Transactions";
            }
            // lease progress
            currentCounter = monitor.getTotalCounter("Lease");
            expectedTotal = monitor.getExpectedTotal("Lease");
            if (currentCounter != null && expectedTotal != null && expectedTotal > 0) {
                progress += (int) (40 * currentCounter / expectedTotal);
                message = "Updating Lease Charges";
            }
            // availability progress
            currentCounter = monitor.getTotalCounter("Availability");
            expectedTotal = monitor.getExpectedTotal("Availability");
            if (currentCounter != null && expectedTotal != null && expectedTotal > 0) {
                progress += (int) (20 * currentCounter / expectedTotal);
                message = "Updating Unit Availability";
            }
            r.setProgressMaximum(100);
            r.setProgress(progress);
            if (message != null) {
                r.setMessage(message + " - Errors: " + monitor.getErred());
            }
        } else if (monitor.getErred() > 0) {
            r.setErrorStatusMessage(monitor.getTextMessages(CompletionType.erred, CompletionType.failed));
        } else {
            r.setMessage(monitor.getTextMessages(CompletionType.erred, CompletionType.failed));
        }
        return r;
    }

}
