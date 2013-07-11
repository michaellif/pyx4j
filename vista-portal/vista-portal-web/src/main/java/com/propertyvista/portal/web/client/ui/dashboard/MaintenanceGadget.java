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

import com.pyx4j.commons.css.StyleManager;
import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.actionbar.Toolbar;

import com.propertyvista.portal.rpc.portal.dto.TenantMaintenanceDTO;
import com.propertyvista.portal.web.client.resources.PortalImages;
import com.propertyvista.portal.web.client.ui.AbstractGadget;

public class MaintenanceGadget extends AbstractGadget<DashboardViewImpl> {

    MaintenanceGadget(DashboardViewImpl form) {
        super(form, PortalImages.INSTANCE.maintenanceIcon(), "My Maintenance Requests", ThemeColor.contrast5);
        setActionsToolbar(new MaintenanceToolbar());

        FlowPanel contentPanel = new FlowPanel();
        contentPanel
                .add(new HTML(
                        "Lorem ipsum dolor sit amet, consectetuer adipiscing elit, sed diam nonummy nibh euismod tincidunt ut laoreet dolore magna aliquam erat volutpat."));
        setContent(contentPanel);
    }

    protected void populate(TenantMaintenanceDTO value) {

    }

    class MaintenanceToolbar extends Toolbar {
        public MaintenanceToolbar() {

            Button createButton = new Button("Create New Request");
            createButton.getElement().getStyle().setProperty("background", StyleManager.getPalette().getThemeColor(ThemeColor.contrast5, 1));
            add(createButton);

        }
    }

}
