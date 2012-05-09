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
package com.propertyvista.admin.client.activity.crud.scheduler;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.client.activity.crud.ListerActivityBase;
import com.pyx4j.site.client.ui.crud.lister.IListerView.Presenter;

import com.propertyvista.admin.client.activity.crud.AdminViewerActivity;
import com.propertyvista.admin.client.ui.crud.scheduler.run.RunViewerView;
import com.propertyvista.admin.client.viewfactories.crud.ManagementVeiwFactory;
import com.propertyvista.admin.domain.scheduler.Run;
import com.propertyvista.admin.domain.scheduler.RunData;
import com.propertyvista.admin.domain.scheduler.RunStatus;
import com.propertyvista.admin.rpc.ExecutionStatusUpdateDTO;
import com.propertyvista.admin.rpc.services.scheduler.RunCrudService;
import com.propertyvista.admin.rpc.services.scheduler.RunDataCrudService;

public class RunViewerActivity extends AdminViewerActivity<Run> implements RunViewerView.Presenter {

    private final Presenter<RunData> runDataLister;

    final RunCrudService runCrudService;

    final AsyncCallback<ExecutionStatusUpdateDTO> actionsCallback;

    private Timer updateViewTimer;

    @SuppressWarnings("unchecked")
    public RunViewerActivity(Place place) {
        super(place, ManagementVeiwFactory.instance(RunViewerView.class), (AbstractCrudService<Run>) GWT.create(RunCrudService.class));

        runDataLister = new ListerActivityBase<RunData>(place, ((RunViewerView) view).getRunDataListerView(),
                (AbstractCrudService<RunData>) GWT.create(RunDataCrudService.class), RunData.class);

        runCrudService = GWT.create(RunCrudService.class);
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

    @Override
    public void onStop() {
        if (updateViewTimer != null) {
            updateViewTimer.cancel();
            updateViewTimer = null;
        }
        ((AbstractActivity) runDataLister).onStop();
        super.onStop();
    }

    private void updateState(ExecutionStatusUpdateDTO result) {
        ((RunViewerView) view).populateExecutionState(result);
        updateState(result.status().getValue());
    }

    private void updateState(RunStatus runStatus) {
        switch (runStatus) {
        case Running:
            if (updateViewTimer == null) {
                updateViewTimer = new Timer() {
                    @Override
                    public void run() {
                        runCrudService.retrieveExecutionState(actionsCallback, EntityFactory.createIdentityStub(Run.class, entityId));
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
