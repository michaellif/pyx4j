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
package com.propertyvista.crm.client.activity.crud.settings.content;

import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.Place;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.site.client.AppSite;

import com.propertyvista.crm.client.activity.crud.CrmViewerActivity;
import com.propertyvista.crm.client.ui.crud.settings.content.page.PageEditor;
import com.propertyvista.crm.client.ui.crud.settings.content.page.PageViewer;
import com.propertyvista.crm.client.ui.crud.viewfactories.SettingsViewFactory;
import com.propertyvista.crm.rpc.services.PageDescriptorCrudService;
import com.propertyvista.domain.site.AvailableLocale;
import com.propertyvista.domain.site.PageDescriptor;

public class PageViewerActivity extends CrmViewerActivity<PageDescriptor> implements PageViewer.Presenter {

    @SuppressWarnings("unchecked")
    public PageViewerActivity(Place place) {
        super(place, SettingsViewFactory.instance(PageViewer.class), (AbstractCrudService<PageDescriptor>) GWT.create(PageDescriptorCrudService.class));
    }

    @Override
    public void viewChild(Key id) {
        AppSite.getPlaceController().goTo(AppSite.getHistoryMapper().createPlace(placeClass).formViewerPlace(id));
    }

    @Override
    public void editNew(Key parentid, AvailableLocale locale) {
        AppSite.getPlaceController().goTo(
                AppSite.getHistoryMapper().createPlace(placeClass).formNewItemPlace(parentid)
                        .arg(PageEditor.Presenter.URL_PARAM_PAGE_PARENT, PageEditor.Presenter.PageParent.page.name())
                        .arg(PageEditor.Presenter.URL_PARAM_PAGE_LOCALE, locale.lang().getValue().name()));
    }
}
