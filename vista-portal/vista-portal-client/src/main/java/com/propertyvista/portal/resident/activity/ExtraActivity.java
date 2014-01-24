/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 23, 2013
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.portal.resident.activity;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.ui.layout.responsive.LayoutChangeRequestEvent;
import com.pyx4j.site.client.ui.layout.responsive.LayoutChangeRequestEvent.ChangeType;

import com.propertyvista.portal.resident.ResidentPortalSite;
import com.propertyvista.portal.resident.ui.extra.ExtraView;
import com.propertyvista.portal.resident.ui.extra.ExtraView.ExtraPresenter;
import com.propertyvista.portal.rpc.portal.resident.dto.CommunityEventsGadgetDTO;
import com.propertyvista.portal.rpc.portal.resident.dto.WeatherGadgetDTO;
import com.propertyvista.portal.rpc.portal.resident.services.CommunityEventPortalCrudService;
import com.propertyvista.portal.rpc.portal.resident.services.ExtraActivityPortalService;

public class ExtraActivity extends AbstractActivity implements ExtraPresenter {

    private final ExtraView view;

    private final ExtraActivityPortalService extraActivityService = (ExtraActivityPortalService) GWT.create(ExtraActivityPortalService.class);

    private final CommunityEventPortalCrudService communityEventService = (CommunityEventPortalCrudService) GWT.create(CommunityEventPortalCrudService.class);

    public ExtraActivity(Place place) {
        view = ResidentPortalSite.getViewFactory().getView(ExtraView.class);
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        panel.setWidget(view);
        AppSite.getEventBus().fireEvent(new LayoutChangeRequestEvent(ChangeType.resizeComponents));

        retreiveWheather(new DefaultAsyncCallback<WeatherGadgetDTO>() {

            @Override
            public void onSuccess(WeatherGadgetDTO result) {
                view.populateWeather(result);

            }
        });
        retreiveCommunityEvents(new DefaultAsyncCallback<CommunityEventsGadgetDTO>() {

            @Override
            public void onSuccess(CommunityEventsGadgetDTO result) {
                view.populateCommunityEvents(result);

            }
        });

    }

    public void retreiveWheather(final AsyncCallback<WeatherGadgetDTO> callback) {
        extraActivityService.retreiveWheather(new DefaultAsyncCallback<WeatherGadgetDTO>() {
            @Override
            public void onSuccess(WeatherGadgetDTO result) {
                callback.onSuccess(result);
            }
        });
    }

    public void retreiveCommunityEvents(final AsyncCallback<CommunityEventsGadgetDTO> callback) {
        communityEventService.retreiveCommunityEvents(new DefaultAsyncCallback<CommunityEventsGadgetDTO>() {
            @Override
            public void onSuccess(CommunityEventsGadgetDTO result) {
                callback.onSuccess(result);
            }
        });
    }
}
