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
 */
package com.propertyvista.crm.client.visor.dashboard;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.gwt.commons.UnrecoverableClientError;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.client.ClientContext;
import com.pyx4j.site.client.backoffice.activity.AbstractVisorController;
import com.pyx4j.site.client.backoffice.ui.prime.IPrimePaneView;

import com.propertyvista.crm.client.ui.gadgets.commonMk2.dashboard.DashboardPrinterDialog;
import com.propertyvista.crm.rpc.services.dashboard.DashboardMetadataService;
import com.propertyvista.domain.dashboard.DashboardMetadata;
import com.propertyvista.domain.property.asset.building.Building;

public class DashboardVisorController extends AbstractVisorController implements IDashboardVisorController {

    private static final I18n i18n = I18n.get(DashboardVisorController.class);

    private final DashboardVisorView visor;

    private final DashboardMetadata dashboardStub;

    private final DashboardMetadataService service;

    private final List<Building> buildings;

    public DashboardVisorController(IPrimePaneView parentView, DashboardMetadata dashboardMetadataStringViewStub, List<Building> buildingStringViewStubs) {
        super(parentView);
        service = GWT.create(DashboardMetadataService.class);
        dashboardStub = dashboardMetadataStringViewStub;
        buildings = buildingStringViewStubs;
        visor = new DashboardVisorView(this);
    }

    @Override
    public void show() {
        populate(getParentView());
    }

    @Override
    public void saveDashboardMetadata() {
        DashboardMetadata dm = visor.getDashboardMetadata();

        // the if statement is only for sake of defense programming, because a View shouldn't request save() when in ReadOnly mode
        if (ClientContext.getUserVisit().getPrincipalPrimaryKey().equals(dm.ownerUser().getPrimaryKey())) {
            dm.gadgetMetadataList().clear(); // clear gadget metadata list it's only one way transportation
            service.saveDashboardMetadata(new AsyncCallback<DashboardMetadata>() {
                @Override
                public void onSuccess(DashboardMetadata result) {
                }

                @Override
                public void onFailure(Throwable caught) {

                }
            }, dm);
        }
    }

    @Override
    public void print() {
        DashboardPrinterDialog.print(visor.getDashboardMetadata(), visor.getSelectedBuildingsStubs());
    }

    private void populate(final IPrimePaneView parentView) {
        service.retrieveMetadata(new AsyncCallback<DashboardMetadata>() {
            @Override
            public void onSuccess(DashboardMetadata dashboardMetadata) {
                visor.setBuildings(buildings);
                visor.populate(dashboardMetadata);

                boolean isReadOnly = !ClientContext.getUserVisit().getPrincipalPrimaryKey().equals(dashboardMetadata.ownerUser().getPrimaryKey());
                visor.setReadOnly(isReadOnly);
                String readOnlyWarning = isReadOnly ? " " + i18n.tr("(Read-Only)") : "";
                visor.setCaption(dashboardStub.name().getValue() + readOnlyWarning);
                parentView.showVisor(visor);
            }

            @Override
            public void onFailure(Throwable caught) {
                throw new UnrecoverableClientError(caught);
            }
        }, dashboardStub.getPrimaryKey());
    }
}
