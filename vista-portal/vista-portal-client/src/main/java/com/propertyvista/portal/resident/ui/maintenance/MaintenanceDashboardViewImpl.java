/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 18, 2011
 * @author Dad
 * @version $Id$
 */
package com.propertyvista.portal.resident.ui.maintenance;

import com.google.gwt.user.client.ui.FlowPanel;

import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.portal.resident.themes.DashboardTheme;
import com.propertyvista.portal.rpc.portal.web.dto.maintenance.MaintenanceSummaryDTO;

public class MaintenanceDashboardViewImpl extends FlowPanel implements MaintenanceDashboardView {

    @SuppressWarnings("unused")
    private static final I18n i18n = I18n.get(MaintenanceDashboardViewImpl.class);

    private MaintenanceDashboardPresenter presenter;

    private final OpenMaintenanceRequestsGadget openMaintenanceRequestsGadget;

    private final MaintenanceHistoryGadget maintenanceHistoryGadget;

    public MaintenanceDashboardViewImpl() {

        setStyleName(DashboardTheme.StyleName.Dashboard.name());

        openMaintenanceRequestsGadget = new OpenMaintenanceRequestsGadget(this);
        openMaintenanceRequestsGadget.asWidget().setWidth("100%");
        add(openMaintenanceRequestsGadget);

        maintenanceHistoryGadget = new MaintenanceHistoryGadget(this);
        maintenanceHistoryGadget.asWidget().setWidth("100%");
        add(maintenanceHistoryGadget);

    }

    @Override
    public void setPresenter(MaintenanceDashboardPresenter presenter) {
        this.presenter = presenter;
    }

    protected MaintenanceDashboardPresenter getPresenter() {
        return presenter;
    }

    @Override
    public void populateOpenMaintenanceRequests(MaintenanceSummaryDTO maintenanceSummary) {
        openMaintenanceRequestsGadget.populate(maintenanceSummary);
        maintenanceHistoryGadget.populate(maintenanceSummary);
    }

}
