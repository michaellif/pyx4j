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
package com.propertyvista.admin.client.ui.crud.simulateddatapreload;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.widgets.client.Button;

import com.propertyvista.admin.client.activity.simulateddatapreload.SimulatedDataPreloadActivity;

public class SimulatedDataPreloadViewImpl implements SimulatedDataPreloadView {

    private final VerticalPanel viewPanel;

    private SimulatedDataPreloadActivity presenter;

    private Button arrearsHistoryGenerationButton;

    public SimulatedDataPreloadViewImpl() {
        viewPanel = new VerticalPanel();
        viewPanel.setSize("100%", "100%");
        viewPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
        viewPanel.add(arrearsHistoryGenerationButton = new Button("Generate arrears snapshots history", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                presenter.generateArrearsSnapshotsHistory();

            }
        }));
        arrearsHistoryGenerationButton.getElement().getStyle().setPadding(1, Unit.EM);
        arrearsHistoryGenerationButton.getElement().getStyle().setMargin(2, Unit.EM);

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
