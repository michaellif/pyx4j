/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Oct 25, 2011
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.complex;

import java.util.Iterator;

import com.google.gwt.user.client.Command;

import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.Button.ButtonMenuBar;

import com.propertyvista.crm.client.ui.crud.CrmViewerViewImplBase;
import com.propertyvista.domain.dashboard.DashboardMetadata;
import com.propertyvista.dto.ComplexDTO;

public class ComplexViewerViewImpl extends CrmViewerViewImplBase<ComplexDTO> implements ComplexViewerView {

    private static final I18n i18n = I18n.get(ComplexViewerViewImpl.class);

    private final ButtonMenuBar dashboardsMenu;

    public ComplexViewerViewImpl() {
        Button dashboardButton = new Button(i18n.tr("Dashboard"));
        dashboardsMenu = new ButtonMenuBar();
        dashboardButton.setMenu(dashboardsMenu);
        addHeaderToolbarItem(dashboardButton);

        setForm(new ComplexForm(this));
    }

    @Override
    public void populate(ComplexDTO value) {
        super.populate(value);
        populateDashboardsMenu(value.dashboards().iterator());
    }

    private void populateDashboardsMenu(Iterator<DashboardMetadata> dashboardsIterator) {
        dashboardsMenu.clearItems();
        while (dashboardsIterator.hasNext()) {
            final DashboardMetadata dashboard = dashboardsIterator.next();
            dashboardsMenu.addItem(dashboard.name().getValue(), new Command() {
                @Override
                public void execute() {
                    if (!isVisorShown()) {
                        ((ComplexViewerView.Presenter) getPresenter()).getDashboardController(dashboard, getForm().getValue().buildings()).show();
                    }
                }
            });
        }
    }
}
