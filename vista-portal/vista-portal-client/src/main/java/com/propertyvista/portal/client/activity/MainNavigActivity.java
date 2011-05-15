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
 * @version $Id$
 */
package com.propertyvista.portal.client.activity;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.portal.client.ui.MainNavigView;
import com.propertyvista.portal.domain.site.MainNavig;
import com.propertyvista.portal.domain.site.NavigItem;
import com.propertyvista.portal.rpc.portal.PortalSiteMap.FindApartment;
import com.propertyvista.portal.rpc.portal.PortalSiteMap.Residents;

public class MainNavigActivity extends AbstractActivity implements MainNavigView.MainNavigPresenter {

    private final MainNavigView view;

    @Inject
    public MainNavigActivity(MainNavigView view) {
        this.view = view;
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
        AppSite.getPlaceController().goTo(place);
    }

    @Override
    public String getNavigLabel(AppPlace place) {
        return AppSite.getHistoryMapper().getPlaceInfo(place).getNavigLabel();
    }

    @Override
    public Place getWhere() {
        return AppSite.getPlaceController().getWhere();
    }

    @Override
    public MainNavig getMainNavig() {
        MainNavig navig = EntityFactory.create(MainNavig.class);

        NavigItem findApt = EntityFactory.create(NavigItem.class);
        findApt.placeId().setValue(AppSite.getHistoryMapper().getPlaceId(new FindApartment()));
        findApt.title().setValue("Find Apartment");
        navig.items().add(findApt);

        NavigItem residents = EntityFactory.create(NavigItem.class);
        residents.placeId().setValue(AppSite.getHistoryMapper().getPlaceId(new Residents()));
        residents.title().setValue("Residents");
        navig.items().add(residents);

        return navig;

    }

    @Override
    public String getCaption(AppPlace place) {
        return AppSite.getHistoryMapper().getPlaceInfo(place).getCaption();
    }

}
