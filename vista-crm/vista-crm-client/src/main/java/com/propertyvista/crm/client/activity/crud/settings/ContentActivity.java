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

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.activity.shared.Activity;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.commons.Key;
import com.pyx4j.gwt.commons.UnrecoverableClientError;
import com.pyx4j.site.client.AppSite;

import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.crm.rpc.CrudAppPlace;
import com.propertyvista.crm.rpc.services.PageDescriptorCrudService;

public class ContentActivity extends AbstractActivity {

    public ContentActivity(Place place) {
        withPlace(place);
    }

    @Override
    public void start(AcceptsOneWidget container, EventBus eventBus) {

        PageDescriptorCrudService pds = GWT.create(PageDescriptorCrudService.class);
        pds.retrieveLandingPage(new AsyncCallback<Key>() {
            @Override
            public void onSuccess(Key result) {
                CrudAppPlace place = new CrmSiteMap.Settings.Content();
                place.formEditorPlace(result);
                AppSite.getPlaceController().goTo(place);
            }

            @Override
            public void onFailure(Throwable caught) {
                throw new UnrecoverableClientError(caught);
            }
        });
    }

    public Activity withPlace(Place place) {
        return this;
    }
}
