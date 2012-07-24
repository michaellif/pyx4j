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
package com.propertyvista.crm.client.activity.report;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.activity.crud.EditorActivityBase;
import com.pyx4j.site.rpc.CrudAppPlace;
import com.pyx4j.site.rpc.CrudAppPlace.Type;

import com.propertyvista.crm.client.event.BoardUpdateEvent;
import com.propertyvista.crm.client.ui.report.ReportEditor;
import com.propertyvista.crm.client.ui.viewfactories.DashboardViewFactory;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.crm.rpc.services.dashboard.ReportMetadataCrudService;
import com.propertyvista.domain.dashboard.DashboardMetadata;
import com.propertyvista.domain.dashboard.DashboardMetadata.DashboardType;
import com.propertyvista.domain.dashboard.DashboardMetadata.LayoutType;

public class ReportEditorActivity extends EditorActivityBase<DashboardMetadata> {

    @SuppressWarnings("unchecked")
    public ReportEditorActivity(CrudAppPlace place) {
        super(place, DashboardViewFactory.instance(ReportEditor.class), (AbstractCrudService<DashboardMetadata>) GWT.create(ReportMetadataCrudService.class),
                DashboardMetadata.class);

    }

    @Override
    protected void createNewEntity(AsyncCallback<DashboardMetadata> callback) {
        DashboardMetadata entity = EntityFactory.create(getEntityClass());
        entity.type().setValue(DashboardType.system);
        entity.layoutType().setValue(LayoutType.Report);
        callback.onSuccess(entity);
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
        CrudAppPlace place = new CrmSiteMap.Report.Management();
        place.setType(Type.lister);
        AppSite.getPlaceController().goTo(place);
    }
}
