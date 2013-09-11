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
import com.pyx4j.forms.client.ui.CEntityContainer;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.actionbar.Toolbar;

import com.propertyvista.portal.rpc.portal.web.dto.MaintenanceDTO;
import com.propertyvista.portal.web.client.resources.PortalImages;
import com.propertyvista.portal.web.client.ui.AbstractGadget;

public class MaintenanceGadget extends AbstractGadget<MainDashboardViewImpl> {

    static final I18n i18n = I18n.get(MaintenanceGadget.class);

    MaintenanceGadget(MainDashboardViewImpl form) {
        super(form, PortalImages.INSTANCE.maintenanceIcon(), i18n.tr("My Maintenance Requests"), ThemeColor.contrast5);
        setActionsToolbar(new MaintenanceToolbar());

        FlowPanel contentPanel = new FlowPanel();
        contentPanel.add(new HTML("You don't have any pending Maintenance Requests."));
        setContent(contentPanel);
    }

    protected void populate(MaintenanceDTO value) {

    }

    class MaintenanceToolbar extends Toolbar {
        public MaintenanceToolbar() {

            Button createButton = new Button("Create New Request");
            createButton.getElement().getStyle().setProperty("background", StyleManager.getPalette().getThemeColor(ThemeColor.contrast5, 1));
            add(createButton);

        }
    }

}
