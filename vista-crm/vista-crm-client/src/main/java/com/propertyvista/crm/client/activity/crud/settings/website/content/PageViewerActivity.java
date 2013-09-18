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
package com.propertyvista.crm.client.activity.crud.settings.website.content;

import com.google.gwt.core.client.GWT;

import com.pyx4j.commons.Key;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.crm.client.CrmSite;
import com.propertyvista.crm.client.activity.crud.CrmViewerActivity;
import com.propertyvista.crm.client.ui.crud.settings.website.content.pages.PageEditor;
import com.propertyvista.crm.client.ui.crud.settings.website.content.pages.PageViewer;
import com.propertyvista.crm.rpc.services.PageDescriptorCrudService;
import com.propertyvista.crm.rpc.services.PageDescriptorCrudService.PageDescriptorInitializationData;
import com.propertyvista.domain.site.PageDescriptor;

public class PageViewerActivity extends CrmViewerActivity<PageDescriptor> implements PageViewer.Presenter {

    public PageViewerActivity(CrudAppPlace place) {
        super(place,  CrmSite.getViewFactory().instantiate(PageViewer.class), GWT.<PageDescriptorCrudService> create(PageDescriptorCrudService.class));
    }

    @Override
    public void viewChild(Key id) {
        AppSite.getPlaceController().goTo(AppSite.getHistoryMapper().createPlace(getPlace().getClass()).formViewerPlace(id));
    }

    @Override
    public void editNew(Key parentid) {
        AppSite.getPlaceController().goTo(
                AppSite.getHistoryMapper().createPlace(getPlace().getClass()).formNewItemPlace(parentid)
                        .queryArg(PageEditor.Presenter.URL_PARAM_PAGE_PARENT, PageDescriptorInitializationData.PageParent.page.name()));
    }
}
