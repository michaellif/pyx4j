/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 5, 2013
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.crm.client.activity.crud.settings.website.content;

import com.google.gwt.core.client.GWT;

import com.pyx4j.commons.Key;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.crm.client.CrmSite;
import com.propertyvista.crm.client.activity.crud.CrmViewerActivity;
import com.propertyvista.crm.client.ui.crud.settings.website.content.ContentViewer;
import com.propertyvista.crm.client.ui.crud.settings.website.content.pages.PageEditor;
import com.propertyvista.crm.rpc.CrmCrudAppPlace;
import com.propertyvista.crm.rpc.CrmSiteMap.Administration.Website;
import com.propertyvista.crm.rpc.services.PageDescriptorCrudService.PageDescriptorInitializationData;
import com.propertyvista.crm.rpc.services.admin.SiteContentCrudService;
import com.propertyvista.dto.SiteDescriptorDTO;

public class ContentViewerActivity extends CrmViewerActivity<SiteDescriptorDTO> implements ContentViewer.Presenter {

    public ContentViewerActivity(CrudAppPlace place) {
        super(place,  CrmSite.getViewFactory().instantiate(ContentViewer.class), GWT.<SiteContentCrudService> create(SiteContentCrudService.class));
    }

    @Override
    public void viewChild(Key id) {
        AppSite.getPlaceController().goTo(new Website.Content.Page().formViewerPlace(id));
    }

    @Override
    public void viewChild(Key id, Class<? extends CrmCrudAppPlace> openPlaceClass) {
        AppSite.getPlaceController().goTo(AppSite.getHistoryMapper().createPlace(openPlaceClass).formViewerPlace(id));
    }

    @Override
    public void editNew(Key parentid) {
        AppSite.getPlaceController().goTo(
                new Website.Content.Page().formNewItemPlace(parentid).queryArg(PageEditor.Presenter.URL_PARAM_PAGE_PARENT,
                        PageDescriptorInitializationData.PageParent.site.name()));
    }

    @Override
    public void editNew(Key parentId, Class<? extends CrmCrudAppPlace> openPlaceClass) {
        AppSite.getPlaceController().goTo(AppSite.getHistoryMapper().createPlace(openPlaceClass).formNewItemPlace(parentId));
    }
}
