/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-24
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.operations.client.activity.crud.scheduler;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.rpc.shared.VoidSerializable;
import com.pyx4j.site.client.activity.ListerController;
import com.pyx4j.site.client.ui.prime.lister.ILister.Presenter;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.operations.client.OperationsSite;
import com.propertyvista.operations.client.activity.crud.AdminViewerActivity;
import com.propertyvista.operations.client.ui.crud.scheduler.run.RunViewerView;
import com.propertyvista.operations.domain.scheduler.Run;
import com.propertyvista.operations.domain.scheduler.RunData;
import com.propertyvista.operations.domain.scheduler.RunStatus;
import com.propertyvista.operations.rpc.dto.ExecutionStatusUpdateDTO;
import com.propertyvista.operations.rpc.services.scheduler.RunCrudService;
import com.propertyvista.operations.rpc.services.scheduler.RunDataCrudService;

public class RunViewerActivity extends AdminViewerActivity<Run> implements RunViewerView.Presenter {

    private final Presenter<RunData> runDataLister;

    private final AsyncCallback<ExecutionStatusUpdateDTO> actionsCallback;

    private Timer updateViewTimer;

    public RunViewerActivity(CrudAppPlace place) {
        super(place, OperationsSite.getViewFactory().instantiate(RunViewerView.class), GWT.<RunCrudService> create(RunCrudService.class));

        runDataLister = new ListerController<RunData>(((RunViewerView) getView()).getRunDataListerView(),
                GWT.<RunDataCrudService> create(RunDataCrudService.class), RunData.class);

        actionsCallback = new DefaultAsyncCallback<ExecutionStatusUpdateDTO>() {
            @Override
            public void onSuccess(ExecutionStatusUpdateDTO result) {
                updateState(result);
            }
        };
    }

    @Override
    protected void onPopulateSuccess(Run result) {
        super.onPopulateSuccess(result);

        runDataLister.setParent(result.getPrimaryKey());
        runDataLister.populate();

        updateState(result.status().getValue());
    }

    private void cancelTimer() {
        if (updateViewTimer != null) {
            updateViewTimer.cancel();
            updateViewTimer = null;
        }
    }

    @Override
    public void onDiscard() {
        cancelTimer();
        super.onDiscard();
    }

    @Override
    public void stopRun() {
        ((RunCrudService) getService()).stopRun(new DefaultAsyncCallback<VoidSerializable>() {
            @Override
            public void onSuccess(VoidSerializable result) {
                // TODO Auto-generated method stub
            }
        }, EntityFactory.createIdentityStub(Run.class, getEntityId()));
    }

    private void updateState(ExecutionStatusUpdateDTO result) {
        ((RunViewerView) getView()).populateExecutionState(result);
        updateState(result.status().getValue());
    }

    private void updateState(RunStatus runStatus) {
        switch (runStatus) {
        case Running:
            if (updateViewTimer == null) {
                updateViewTimer = new Timer() {
                    @Override
                    public void run() {
                        ((RunCrudService) getService()).retrieveExecutionState(actionsCallback, EntityFactory.createIdentityStub(Run.class, getEntityId()));
                    }
                };
                updateViewTimer.scheduleRepeating(2000);
            }
            break;
        default:
            if (updateViewTimer != null) {
                updateViewTimer.cancel();
                updateViewTimer = null;
                refresh();
            }
        }
    }
}
