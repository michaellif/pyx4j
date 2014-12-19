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
 */
package com.propertyvista.portal.resident.activity;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.gwt.commons.layout.LayoutChangeRequestEvent;
import com.pyx4j.gwt.commons.layout.LayoutChangeRequestEvent.ChangeType;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.client.AppSite;

import com.propertyvista.portal.resident.ResidentPortalSite;
import com.propertyvista.portal.resident.ui.extra.WeatherView;
import com.propertyvista.portal.resident.ui.extra.WeatherView.WeatherPresenter;
import com.propertyvista.portal.rpc.portal.resident.dto.WeatherGadgetDTO;
import com.propertyvista.portal.rpc.portal.resident.services.ExtraActivityPortalService;

public class WeatherActivity extends AbstractActivity implements WeatherPresenter {

    private final WeatherView view;

    private final ExtraActivityPortalService extraActivityService = (ExtraActivityPortalService) GWT.create(ExtraActivityPortalService.class);

    public WeatherActivity(Place place) {
        view = ResidentPortalSite.getViewFactory().getView(WeatherView.class);
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        panel.setWidget(view);
        extraActivityService.retreiveWheather(new DefaultAsyncCallback<WeatherGadgetDTO>() {

            @Override
            public void onSuccess(WeatherGadgetDTO result) {
                view.populateWeather(result);
                AppSite.getEventBus().fireEvent(new LayoutChangeRequestEvent(ChangeType.resizeComponents));
            }
        });

    }

}
