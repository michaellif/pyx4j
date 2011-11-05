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
package com.propertyvista.crm.client.activity.crud.settings;

import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.Place;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.activity.crud.ViewerActivityBase;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.crm.client.ui.crud.settings.content.page.PageViewer;
import com.propertyvista.crm.client.ui.crud.viewfactories.SettingsViewFactory;
import com.propertyvista.crm.rpc.services.PageDescriptorCrudService;
import com.propertyvista.domain.site.PageDescriptor;

public class PageViewerActivity extends ViewerActivityBase<PageDescriptor> implements PageViewer.Presenter {

    @SuppressWarnings("unchecked")
    public PageViewerActivity(Place place) {
        super((PageViewer) SettingsViewFactory.instance(PageViewer.class), (AbstractCrudService<PageDescriptor>) GWT.create(PageDescriptorCrudService.class));
        setPlace(place);
    }

    @Override
    public void viewChild(Key id) {
        CrudAppPlace place = AppSite.getHistoryMapper().createPlace(placeClass);
        place.formViewerPlace(id);
        AppSite.getPlaceController().goTo(place);
    }

    @Override
    public void editNew(Key parentid) {
        CrudAppPlace place = AppSite.getHistoryMapper().createPlace(placeClass);
        place.formNewItemPlace(parentid);
        AppSite.getPlaceController().goTo(place);
    }
}
