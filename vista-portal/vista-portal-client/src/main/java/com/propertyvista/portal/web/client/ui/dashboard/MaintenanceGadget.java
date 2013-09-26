/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 3, 2013
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.portal.web.client.ui.dashboard;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;

import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.portal.rpc.portal.web.dto.maintenance.MaintenanceSummaryDTO;
import com.propertyvista.portal.web.client.resources.PortalImages;
import com.propertyvista.portal.web.client.ui.AbstractGadget;
import com.propertyvista.portal.web.client.ui.maintenance.MaintenanceToolbar;

public class MaintenanceGadget extends AbstractGadget<MainDashboardViewImpl> {

    static final I18n i18n = I18n.get(MaintenanceGadget.class);

    MaintenanceGadget(MainDashboardViewImpl form) {
        super(form, PortalImages.INSTANCE.maintenanceIcon(), i18n.tr("My Maintenance Requests"), ThemeColor.contrast5);
        setActionsToolbar(new MaintenanceToolbar() {

            @Override
            protected void onNewRequestClicked() {
                getGadgetView().getPresenter().createMaintenanceRequest();
            }

        });

        FlowPanel contentPanel = new FlowPanel();
        contentPanel.add(new HTML("You don't have any pending Maintenance Requests."));
        setContent(contentPanel);
    }

    protected void populate(MaintenanceSummaryDTO maintenanceSummary) {

    }

}
