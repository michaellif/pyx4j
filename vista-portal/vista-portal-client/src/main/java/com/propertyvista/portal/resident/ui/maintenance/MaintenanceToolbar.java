/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 26, 2013
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.portal.resident.ui.maintenance;

import com.google.gwt.user.client.Command;

import com.pyx4j.commons.css.StyleManager;
import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.Button;

import com.propertyvista.portal.rpc.portal.resident.dto.maintenance.MaintenanceSummaryDTO;
import com.propertyvista.portal.shared.ui.GadgetToolbar;

public abstract class MaintenanceToolbar extends GadgetToolbar {

    static final I18n i18n = I18n.get(MaintenanceToolbar.class);

    private final Button createButton;

    public MaintenanceToolbar() {

        createButton = new Button(i18n.tr("New Maintenance Request"), new Command() {

            @Override
            public void execute() {
                onNewRequestClicked();
            }
        });
        createButton.getElement().getStyle().setProperty("background", StyleManager.getPalette().getThemeColor(ThemeColor.contrast5, 1));
        addItem(createButton);

        recalculateState(null);
    }

    protected abstract void onNewRequestClicked();

    public void recalculateState(MaintenanceSummaryDTO insuranceStatus) {
    }
}