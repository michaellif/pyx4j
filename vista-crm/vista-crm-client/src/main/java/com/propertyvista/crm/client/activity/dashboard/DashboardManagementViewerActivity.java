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

import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.rpc.shared.VoidSerializable;
import com.pyx4j.security.client.ClientContext;
import com.pyx4j.security.shared.SecurityController;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.crm.client.CrmSite;
import com.propertyvista.crm.client.activity.crud.CrmViewerActivity;
import com.propertyvista.crm.client.ui.dashboard.DashboardManagementViewerView;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.crm.rpc.services.dashboard.DashboardMetadataCrudService;
import com.propertyvista.crm.rpc.services.dashboard.DashboardMetadataService;
import com.propertyvista.domain.dashboard.DashboardMetadata;
import com.propertyvista.domain.security.CrmUser;
import com.propertyvista.domain.security.VistaCrmBehavior;

public class DashboardManagementViewerActivity extends CrmViewerActivity<DashboardMetadata> implements DashboardManagementViewerView.Presenter {

    private boolean canEdit;

    public DashboardManagementViewerActivity(CrudAppPlace place) {
        super(place,  CrmSite.getViewFactory().getView(DashboardManagementViewerView.class), GWT
                .<DashboardMetadataCrudService> create(DashboardMetadataCrudService.class));
        canEdit = false;
    }

    @Override
    public boolean canEdit() {
        return canEdit;
    }

    @Override
    protected void onPopulateSuccess(DashboardMetadata result) {
        boolean isAccessedByOwner = ClientContext.getUserVisit().getPrincipalPrimaryKey().equals(result.ownerUser().getPrimaryKey());
        canEdit = isAccessedByOwner;
        ((DashboardManagementViewerView) getView()).setTakeOwnershipEnabled(SecurityController.checkBehavior(VistaCrmBehavior.DashboardManager)
                & !isAccessedByOwner);
        ((DashboardManagementViewerView) getView()).setChangeOwnershipEnabled(isAccessedByOwner);
        super.onPopulateSuccess(result);
    }

    @Override
    public void takeOwnership(DashboardMetadata dashboardMetadataStub) {
        GWT.<DashboardMetadataService> create(DashboardMetadataService.class).takeOwnership(new DefaultAsyncCallback<VoidSerializable>() {
            @Override
            public void onSuccess(VoidSerializable result) {
                populate();
            }
        }, dashboardMetadataStub);
    }

    @Override
    public void changeOwnership(DashboardMetadata dashboardMetadataStub, CrmUser newOwnerStub) {
        GWT.<DashboardMetadataService> create(DashboardMetadataService.class).changeOwnership(new DefaultAsyncCallback<VoidSerializable>() {
            @Override
            public void onSuccess(VoidSerializable result) {
                AppSite.getPlaceController().goTo(new CrmSiteMap.Dashboard.Manage().formListerPlace());
            }
        }, dashboardMetadataStub, newOwnerStub);
    }
}
