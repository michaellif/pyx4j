/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-24
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.activity.dashboard;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.activity.AbstractEditorActivity;
import com.pyx4j.site.rpc.CrudAppPlace;
import com.pyx4j.site.rpc.CrudAppPlace.Type;

import com.propertyvista.crm.client.CrmSite;
import com.propertyvista.crm.client.event.BoardUpdateEvent;
import com.propertyvista.crm.client.ui.dashboard.DashboardManagementEditorView;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.crm.rpc.dto.dashboard.DashboardColumnLayoutFormat;
import com.propertyvista.crm.rpc.services.dashboard.DashboardMetadataCrudService;
import com.propertyvista.domain.dashboard.DashboardMetadata.LayoutType;
import com.propertyvista.domain.dashboard.DashboardMetadata;

public class DashboardManagementEditorActivity extends AbstractEditorActivity<DashboardMetadata> {

    @SuppressWarnings("unchecked")
    public DashboardManagementEditorActivity(CrudAppPlace place) {
        super(place,  CrmSite.getViewFactory().instantiate(DashboardManagementEditorView.class), (AbstractCrudService<DashboardMetadata>) GWT
                .create(DashboardMetadataCrudService.class), DashboardMetadata.class);

    }

    @Override
    protected void createNewEntity(final AsyncCallback<DashboardMetadata> callback) {
        DashboardMetadata entity = EntityFactory.create(getEntityClass());

        entity.encodedLayout().setValue(new DashboardColumnLayoutFormat.Builder(LayoutType.Two11).build().getSerializedForm());

        callback.onSuccess(entity);
    }

    @Override
    public void onPopulateSuccess(DashboardMetadata result) {
        super.onPopulateSuccess(result);
        ((DashboardManagementEditorView) getView()).setNewDashboardMode(result.getPrimaryKey() == null);
    }

    @Override
    protected void onApplySuccess(Key result) {
        super.onApplySuccess(result);
        AppSite.instance();
        AppSite.getEventBus().fireEvent(new BoardUpdateEvent());
    }

    @Override
    protected void onSaveSuccess(Key result) {
        super.onSaveSuccess(result);
        AppSite.instance();
        AppSite.getEventBus().fireEvent(new BoardUpdateEvent());
    }

    @Override
    protected void goToViewer(Key entityID) {
        CrudAppPlace place = new CrmSiteMap.Dashboard.Manage();
        place.setType(Type.lister);
        AppSite.getPlaceController().goTo(place);
    }
}
