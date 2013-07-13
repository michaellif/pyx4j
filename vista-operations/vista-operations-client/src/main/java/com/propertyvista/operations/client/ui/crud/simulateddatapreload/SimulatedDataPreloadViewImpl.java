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
package com.propertyvista.operations.client.ui.crud.simulateddatapreload;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.widgets.client.Button;

import com.propertyvista.operations.client.activity.crud.simulateddatapreload.SimulatedDataPreloadActivity;

public class SimulatedDataPreloadViewImpl implements SimulatedDataPreloadView {

    private final TwoColumnFlexFormPanel viewPanel;

    private SimulatedDataPreloadActivity presenter;

    private Button arrearsHistoryGenerationButton;

    private Button maintenanceRequestsGenerationButton;

    public SimulatedDataPreloadViewImpl() {
        viewPanel = new TwoColumnFlexFormPanel();
        viewPanel.setWidth("100%");
        int row = -1;
        viewPanel.setWidget(++row, 0, arrearsHistoryGenerationButton = new Button("Generate arrears snapshots history", new Command() {
            @Override
            public void execute() {
                presenter.generateArrearsSnapshotsHistory();

            }
        }));
        viewPanel.setWidget(++row, 0, maintenanceRequestsGenerationButton = new Button("Generate maintenance requests", new Command() {
            @Override
            public void execute() {
                presenter.generateMaintenanceRequests();

            }
        }));
        arrearsHistoryGenerationButton.getElement().getStyle().setWidth(20, Unit.EM);
        maintenanceRequestsGenerationButton.getElement().getStyle().setWidth(20, Unit.EM);
    }

    @Override
    public Widget asWidget() {
        return viewPanel;
    }

    @Override
    public void setPresenter(SimulatedDataPreloadActivity activity) {
        this.presenter = activity;
    }

    @Override
    public void updateArrearsHistoryGenerationProgress(int current, int total) {
        if (total == 0) {
            arrearsHistoryGenerationButton.setCaption("Generate arrears snapshots history");
        } else {
            arrearsHistoryGenerationButton.setCaption("Generating arrears history: Processed " + current + " out of " + total + " (Click to abort)");
        }

    }

}
