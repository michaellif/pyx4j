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
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.activity.crud.ViewerActivityBase;
import com.pyx4j.site.rpc.CrudAppPlace;
import com.pyx4j.site.rpc.services.AbstractCrudService;

import com.propertyvista.crm.client.ui.crud.settings.content.SiteViewer;
import com.propertyvista.crm.client.ui.crud.viewfactories.SettingsViewFactory;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.crm.rpc.services.SiteDescriptorCrudService;
import com.propertyvista.dto.SiteDescriptorDTO;

public class SiteViewerActivity extends ViewerActivityBase<SiteDescriptorDTO> implements SiteViewer.Presenter {

    @SuppressWarnings("unchecked")
    public SiteViewerActivity(Place place) {
        super((SiteViewer) SettingsViewFactory.instance(SiteViewer.class), (AbstractCrudService<SiteDescriptorDTO>) GWT.create(SiteDescriptorCrudService.class));
        setPlace(place);
    }

    @Override
    public void viewChild(Key id) {
        CrudAppPlace place = AppSite.getHistoryMapper().createPlace(CrmSiteMap.Settings.Page.class);
        place.formViewerPlace(id);
        AppSite.getPlaceController().goTo(place);
    }

    @Override
    public void editNew(Key parentid) {
        CrudAppPlace place = AppSite.getHistoryMapper().createPlace(CrmSiteMap.Settings.Page.class);
        place.formNewItemPlace(parentid);
        AppSite.getPlaceController().goTo(place);
    }
}
