/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 9, 2011
 * @author vadims
 * @version $Id$
 */
package com.propertyvista.admin.client.ui.crud.maintenance;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

import com.pyx4j.essentials.rpc.admin.SystemMaintenanceState;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.Button;

import com.propertyvista.admin.client.activity.crud.maintenance.MaintenanceViewerActivity;
import com.propertyvista.admin.client.ui.crud.AdminViewerViewImplBase;
import com.propertyvista.admin.rpc.AdminSiteMap;

public class MaintenanceViewerViewImpl extends AdminViewerViewImplBase<SystemMaintenanceState> implements MaintenanceViewerView {

    private final static I18n i18n = I18n.get(MaintenanceViewerViewImpl.class);

    private final Button btnResetCache;

    public MaintenanceViewerViewImpl() {
        super(AdminSiteMap.Administration.Maintenance.class, new MaintenanceEditorForm(true));

        // Add actions:
        btnResetCache = new Button(i18n.tr("Reset Global Cache"), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                ((MaintenanceViewerActivity) getPresenter()).resetGlobalCache();
            }
        });
        addHeaderToolbarTwoItem(btnResetCache.asWidget());
    }
}
