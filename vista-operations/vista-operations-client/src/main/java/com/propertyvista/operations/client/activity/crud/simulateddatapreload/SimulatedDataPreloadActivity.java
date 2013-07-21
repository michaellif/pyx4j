/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 25, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.operations.client.activity.crud.simulateddatapreload;

import java.util.Vector;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.rpc.shared.VoidSerializable;
import com.pyx4j.widgets.client.dialog.MessageDialog;

import com.propertyvista.operations.client.ui.crud.simulateddatapreload.SimulatedDataPreloadView;
import com.propertyvista.operations.client.viewfactories.MiscViewFactory;
import com.propertyvista.operations.rpc.services.simulator.SimulatedDataPreloadService;

public class SimulatedDataPreloadActivity extends AbstractActivity {

    private final SimulatedDataPreloadView view;

    private final SimulatedDataPreloadService service;

    public SimulatedDataPreloadActivity() {
        this.view = MiscViewFactory.instance(SimulatedDataPreloadView.class);
        this.view.setPresenter(this);
        this.service = GWT.<SimulatedDataPreloadService> create(SimulatedDataPreloadService.class);
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        panel.setWidget(view);
    }

    public void generateArrearsSnapshotsHistory() {
        service.generateArrearsHistory(new DefaultAsyncCallback<VoidSerializable>() {

            @Override
            public void onSuccess(VoidSerializable result) {
                view.updateArrearsHistoryGenerationProgress(0, 100);
                pollArrearsHishtoryGenerationProgress();
            }
        });
    }

    public void generateMaintenanceRequests() {
        service.generateMaintenanceRequests(new DefaultAsyncCallback<VoidSerializable>() {

            @Override
            public void onSuccess(VoidSerializable result) {
                MessageDialog.info("Finished making maintenance requests");
            }

        });
    }

    protected void pollArrearsHishtoryGenerationProgress() {
        Timer timer = new Timer() {

            @Override
            public void run() {
                service.getArrearsHistoryGenerationProgress(new DefaultAsyncCallback<Vector<Integer>>() {

                    @Override
                    public void onSuccess(Vector<Integer> result) {
                        if (result != null) {
                            view.updateArrearsHistoryGenerationProgress(result.get(0), result.get(1));
                            pollArrearsHishtoryGenerationProgress();
                        } else {
                            view.updateArrearsHistoryGenerationProgress(0, 0);
                        }
                    }
                });
            };
        };
        timer.schedule(1000);
    }

}
