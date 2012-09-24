/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Aug 13, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.visor.dashboard;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.gwt.commons.UnrecoverableClientError;
import com.pyx4j.site.client.ui.crud.IView;

import com.propertyvista.crm.client.ui.gadgets.commonMk2.dashboard.DashboardPrinterDialog;
import com.propertyvista.crm.rpc.services.dashboard.DashboardMetadataService;
import com.propertyvista.domain.dashboard.DashboardMetadata;
import com.propertyvista.domain.property.asset.building.Building;

public class DashboardVisorController implements IDashboardVisorController {

    private final DashboardVisorView view;

    private final DashboardMetadata dashboardStub;

    private final DashboardMetadataService service;

    private final List<Building> buildings;

    public DashboardVisorController(DashboardMetadata dashboardMetadataStringViewStub, List<Building> buildingStringViewStubs) {
        service = GWT.create(DashboardMetadataService.class);
        dashboardStub = dashboardMetadataStringViewStub;
        buildings = buildingStringViewStubs;
        view = new DashboardVisorView(this);
    }

    @Override
    public IsWidget getView() {
        return view;
    }

    @Override
    public void show(IView parentView) {
        populate(parentView);
    }

    @Override
    public void saveDashboardMetadata() {
        service.saveDashboardMetadata(new AsyncCallback<DashboardMetadata>() {
            @Override
            public void onSuccess(DashboardMetadata result) {
            }

            @Override
            public void onFailure(Throwable caught) {

            }
        }, view.getDashboardMetadata());
    }

    @Override
    public void print() {
        DashboardPrinterDialog.print(view.getDashboardMetadata(), view.getSelectedBuildingsStubs());
    }

    private void populate(final IView parentView) {
        service.retrieveMetadata(new AsyncCallback<DashboardMetadata>() {
            @Override
            public void onSuccess(DashboardMetadata result) {
                view.setBuildings(buildings);
                view.populate(result);

                parentView.showVisor(getView(), dashboardStub.name().getValue());
            }

            @Override
            public void onFailure(Throwable caught) {
                throw new UnrecoverableClientError(caught);
            }
        }, dashboardStub.getPrimaryKey());
    }
}
