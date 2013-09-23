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
package com.propertyvista.portal.web.client.ui.maintenance;

import com.google.gwt.user.client.ui.FlowPanel;

import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.portal.rpc.portal.web.dto.insurance.status.InsuranceStatusDTO;
import com.propertyvista.portal.rpc.portal.web.dto.maintenance.MaintenanceSummaryDTO;
import com.propertyvista.portal.web.client.themes.DashboardTheme;

public class MaintenanceDashboardViewImpl extends FlowPanel implements MaintenanceDashboardView {

    @SuppressWarnings("unused")
    private static final I18n i18n = I18n.get(MaintenanceDashboardViewImpl.class);

    private MaintenanceDashboardPresenter presenter;

    private final OpenMaintenanceRequestsGadget openMaintenanceRequestsGadget;

    public MaintenanceDashboardViewImpl() {

        setStyleName(DashboardTheme.StyleName.Dashboard.name());

        openMaintenanceRequestsGadget = new OpenMaintenanceRequestsGadget(this);
        openMaintenanceRequestsGadget.asWidget().setWidth("100%");

        add(openMaintenanceRequestsGadget);

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
    }

}
