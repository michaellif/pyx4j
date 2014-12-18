/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-25
 * @author Vlad
 */
package com.propertyvista.crm.client.ui.dashboard;

import com.propertyvista.crm.client.ui.crud.CrmEditorViewImplBase;
import com.propertyvista.domain.dashboard.DashboardMetadata;

public class DashboardManagementEditorViewImpl extends CrmEditorViewImplBase<DashboardMetadata> implements DashboardManagementEditorView {

    public DashboardManagementEditorViewImpl() {
        setForm(new DashboardManagementForm(this));
    }

    @Override
    public void setNewDashboardMode(boolean isNewDashboard) {
        ((DashboardManagementForm) getForm()).setNewDashboardMode(isNewDashboard);
    }

}
