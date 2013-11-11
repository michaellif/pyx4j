/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-12-11
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.crm.client.activity.crud.administration.website.content;

import com.google.gwt.core.client.GWT;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.crm.client.CrmSite;
import com.propertyvista.crm.client.activity.crud.CrmViewerActivity;
import com.propertyvista.crm.client.ui.crud.administration.website.content.cityintro.CityIntroPageViewer;
import com.propertyvista.crm.rpc.services.CityIntroPageCrudService;
import com.propertyvista.domain.site.CityIntroPage;

public class CityIntroPageViewerActivity extends CrmViewerActivity<CityIntroPage> implements CityIntroPageViewer.Presenter {

    @SuppressWarnings("unchecked")
    public CityIntroPageViewerActivity(CrudAppPlace place) {
        super(place,  CrmSite.getViewFactory().getView(CityIntroPageViewer.class), (AbstractCrudService<CityIntroPage>) GWT.create(CityIntroPageCrudService.class));
    }

    @Override
    public void viewPage(Key id) {
        AppSite.getPlaceController().goTo(AppSite.getHistoryMapper().createPlace(getPlace().getClass()).formViewerPlace(id));
    }

    @Override
    public void editNew(Key parentid) {
        AppSite.getPlaceController().goTo(AppSite.getHistoryMapper().createPlace(getPlace().getClass()).formNewItemPlace(parentid));
    }
}
