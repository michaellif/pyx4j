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
package com.propertyvista.crm.client.activity.crud.administration.website.content;

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
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.crm.rpc.services.admin.SiteContentCrudService;

public class SiteActivity extends AbstractActivity {

    private final CrudAppPlace place;

    public SiteActivity(Place place) {
        assert (place instanceof CrudAppPlace);
        this.place = (CrudAppPlace) place;
        withPlace(place);
    }

    @Override
    public void start(AcceptsOneWidget container, EventBus eventBus) {
        GWT.<SiteContentCrudService> create(SiteContentCrudService.class).retrieveHomeItem(new AsyncCallback<Key>() {
            @Override
            public void onSuccess(Key result) {
                AppSite.getPlaceController().goTo(AppSite.getHistoryMapper().createPlace(place.getClass()).formViewerPlace(result));
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
