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
import com.google.gwt.place.shared.Place;

import com.pyx4j.commons.Key;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.activity.crud.EditorActivityBase;
import com.pyx4j.site.rpc.services.AbstractCrudService;

import com.propertyvista.crm.client.event.NavigationUpdateEvent;
import com.propertyvista.crm.client.ui.dashboard.DashboardEditor;
import com.propertyvista.crm.client.ui.viewfactories.DashboardViewFactory;
import com.propertyvista.crm.rpc.services.DashboardCrudService;
import com.propertyvista.domain.dashboard.DashboardMetadata;
import com.propertyvista.domain.dashboard.DashboardMetadata.LayoutType;

public class DashboardEditorActivity extends EditorActivityBase<DashboardMetadata> {

    @SuppressWarnings("unchecked")
    public DashboardEditorActivity(Place place) {
        super((DashboardEditor) DashboardViewFactory.instance(DashboardEditor.class), (AbstractCrudService<DashboardMetadata>) GWT
                .create(DashboardCrudService.class), DashboardMetadata.class);
        withPlace(place);
    }

    @Override
    protected void initNewItem(DashboardMetadata entity) {
        if (isNewItem()) {
            entity.type().setValue(((DashboardEditor) view).showSelectTypePopUp());
            entity.layoutType().setValue(LayoutType.Two12);

            // TODO: get current user Key here: 
            entity.user().id().setValue(Key.DORMANT_KEY);
        }
    }

    @Override
    protected void onApplySuccess(DashboardMetadata result) {
        super.onApplySuccess(result);
        AppSite.instance().getEventBus().fireEvent(new NavigationUpdateEvent());
    }

    @Override
    protected void onSaveSuccess(DashboardMetadata result) {
        super.onSaveSuccess(result);
        AppSite.instance().getEventBus().fireEvent(new NavigationUpdateEvent());
    }
}
