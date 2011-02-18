/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 1, 2011
 * @author Misha
 * @version $Id: VistaTesterDispatcher.java 32 2011-02-02 04:49:39Z vlads $
 */
package com.propertyvista.portal.client.ptapp.activity;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.propertyvista.portal.client.ptapp.SiteMap;
import com.propertyvista.portal.client.ptapp.ui.MainNavigView;

import com.pyx4j.site.client.place.AppPlace;
import com.pyx4j.site.client.place.AppPlaceListing;

public class MainNavigActivity extends AbstractActivity implements MainNavigView.Presenter {

    private final MainNavigView view;

    private final PlaceController placeController;

    private final AppPlaceListing appPlaceListing;

    @Inject
    public MainNavigActivity(MainNavigView view, PlaceController placeController, AppPlaceListing appPlaceListing) {
        this.view = view;
        this.placeController = placeController;
        this.appPlaceListing = appPlaceListing;
        view.setPresenter(this);
    }

    public MainNavigActivity withPlace(Place place) {
        return this;
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        panel.setWidget(view);
    }

    @Override
    public void navigTo(Place place) {
        placeController.goTo(place);
    }

    @Override
    public String getNavigLabel(AppPlace place) {
        return appPlaceListing.getPlaceInfo(place).getNavigLabel();
    }

    @Override
    public List<AppPlace> getTopLevelPlaces() {
        List<AppPlace> places = new ArrayList<AppPlace>();

        // TODO call PtAppWizardManager

        places.add(new SiteMap.Apartment());
        places.add(new SiteMap.Tenants());
        places.add(new SiteMap.Info());
        places.add(new SiteMap.Financial());
        places.add(new SiteMap.Pets());
        places.add(new SiteMap.Charges());
        places.add(new SiteMap.Summary());
        places.add(new SiteMap.Payment());
        return places;
    }

}
