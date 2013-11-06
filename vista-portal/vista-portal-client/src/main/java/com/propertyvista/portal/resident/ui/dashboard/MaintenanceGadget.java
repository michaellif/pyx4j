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
package com.propertyvista.portal.resident.ui.dashboard;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;

import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.widgets.client.Anchor;

import com.propertyvista.portal.resident.resources.PortalImages;
import com.propertyvista.portal.resident.ui.AbstractGadget;
import com.propertyvista.portal.resident.ui.maintenance.MaintenanceToolbar;
import com.propertyvista.portal.rpc.portal.PortalSiteMap;
import com.propertyvista.portal.rpc.portal.web.dto.maintenance.MaintenanceSummaryDTO;

public class MaintenanceGadget extends AbstractGadget<MainDashboardViewImpl> {

    static final I18n i18n = I18n.get(MaintenanceGadget.class);

    private final HTML message;

    private final NavigationBar navigationBar;

    MaintenanceGadget(MainDashboardViewImpl form) {
        super(form, PortalImages.INSTANCE.maintenanceIcon(), i18n.tr("My Maintenance Requests"), ThemeColor.contrast5, 1);
        setActionsToolbar(new MaintenanceToolbar() {

            @Override
            protected void onNewRequestClicked() {
                getGadgetView().getPresenter().createMaintenanceRequest();
            }

        });

        FlowPanel contentPanel = new FlowPanel();
        message = new HTML();
        contentPanel.add(message);
        setContent(contentPanel);

        setNavigationBar(navigationBar = new NavigationBar());

    }

    class NavigationBar extends FlowPanel {

        private final Anchor viewMaintenanceAnchor;

        public NavigationBar() {
            viewMaintenanceAnchor = new Anchor(i18n.tr("View details in Maintenance Section"), new Command() {

                @Override
                public void execute() {
                    AppSite.getPlaceController().goTo(new PortalSiteMap.Resident.Maintenance());
                }
            });
            add(viewMaintenanceAnchor);
        }

        public void recalculateState(MaintenanceSummaryDTO value) {

            if (value.openMaintenanceRequests().size() == 0) {
                viewMaintenanceAnchor.setVisible(false);
            } else {
                viewMaintenanceAnchor.setVisible(true);
            }
        }
    }

    protected void populate(MaintenanceSummaryDTO value) {
        navigationBar.recalculateState(value);
        if (value.openMaintenanceRequests().size() == 0) {
            message.setHTML(i18n.tr("You don't have any open Maintenance Requests."));
        } else {
            message.setHTML(i18n.tr("You have {0} open Maintenance Requests.", value.openMaintenanceRequests().size()));
        }
    }
}
