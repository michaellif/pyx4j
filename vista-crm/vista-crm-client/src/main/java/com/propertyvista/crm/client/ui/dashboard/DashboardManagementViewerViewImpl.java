/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-10-04
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.dashboard;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.Button;

import com.propertyvista.crm.client.ui.crud.CrmViewerViewImplBase;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.domain.dashboard.DashboardMetadata;

public class DashboardManagementViewerViewImpl extends CrmViewerViewImplBase<DashboardMetadata> implements DashboardManagementViewerView {

    private static final I18n i18n = I18n.get(DashboardManagementViewerViewImpl.class);

    private Button takeOwnershipButton;

    public DashboardManagementViewerViewImpl() {
        super(CrmSiteMap.Dashboard.Manage.class);
        setForm(new DashboardManagementForm(true));

        addHeaderToolbarItem(takeOwnershipButton = new Button(i18n.tr("Take Ownership"), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                ((DashboardManagementViewerView.Presenter) getPresenter()).takeOwnership(getForm().getValue().<DashboardMetadata> createIdentityStub());
            }
        }));
    }

    @Override
    public void setTakeOwnershipEnabled(boolean isEnabled) {
        takeOwnershipButton.setVisible(isEnabled);
    }

}
