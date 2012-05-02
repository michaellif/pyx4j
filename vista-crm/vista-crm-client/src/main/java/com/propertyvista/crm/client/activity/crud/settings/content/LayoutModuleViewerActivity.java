/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-04-26
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.crm.client.activity.crud.settings.content;

import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.Place;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.site.client.AppSite;

import com.propertyvista.crm.client.activity.crud.CrmViewerActivity;
import com.propertyvista.crm.client.ui.crud.settings.content.layoutmodule.LayoutModuleViewer;
import com.propertyvista.crm.client.ui.crud.viewfactories.SettingsViewFactory;
import com.propertyvista.crm.rpc.services.LayoutModuleCrudService;
import com.propertyvista.domain.site.HomePageGadget;

public class LayoutModuleViewerActivity extends CrmViewerActivity<HomePageGadget> implements LayoutModuleViewer.Presenter {

    @SuppressWarnings("unchecked")
    public LayoutModuleViewerActivity(Place place) {
        super(place, SettingsViewFactory.instance(LayoutModuleViewer.class), (AbstractCrudService<HomePageGadget>) GWT.create(LayoutModuleCrudService.class));
    }

    @Override
    public void viewModule(Key id) {
        AppSite.getPlaceController().goTo(AppSite.getHistoryMapper().createPlace(placeClass).formViewerPlace(id));
    }

    @Override
    public void editNew(Key parentid) {
        AppSite.getPlaceController().goTo(AppSite.getHistoryMapper().createPlace(placeClass).formNewItemPlace(parentid));
    }
}
