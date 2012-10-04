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
package com.propertyvista.crm.client.activity.dashboard;

import com.google.gwt.core.client.GWT;

import com.pyx4j.security.client.ClientContext;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.crm.client.activity.crud.CrmViewerActivity;
import com.propertyvista.crm.client.ui.dashboard.DashboardManagementViewerView;
import com.propertyvista.crm.client.ui.viewfactories.DashboardViewFactory;
import com.propertyvista.crm.rpc.services.dashboard.DashboardMetadataCrudService;
import com.propertyvista.domain.dashboard.DashboardMetadata;

public class DashboardManagementViewerActivity extends CrmViewerActivity<DashboardMetadata> {

    private boolean canEdit;

    public DashboardManagementViewerActivity(CrudAppPlace place) {
        super(place, DashboardViewFactory.instance(DashboardManagementViewerView.class), GWT
                .<DashboardMetadataCrudService> create(DashboardMetadataCrudService.class));
        canEdit = false;
    }

    @Override
    public boolean canEdit() {
        return canEdit;
    }

    @Override
    protected void onPopulateSuccess(DashboardMetadata result) {
        canEdit = ClientContext.getUserVisit().getPrincipalPrimaryKey().equals(result.ownerUser().getPrimaryKey());
        super.onPopulateSuccess(result);
    }
}
