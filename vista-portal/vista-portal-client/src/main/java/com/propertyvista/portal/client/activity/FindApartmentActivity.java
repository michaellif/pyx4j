/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 14, 2011
 * @author Dad
 * @version $Id$
 */
package com.propertyvista.portal.client.activity;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;

import com.pyx4j.entity.rpc.GeoCriteria;
import com.pyx4j.site.client.AppSite;

import com.propertyvista.common.domain.ref.City;
import com.propertyvista.common.domain.ref.Province;
import com.propertyvista.portal.client.ui.FindApartmentView;
import com.propertyvista.portal.rpc.portal.PortalSiteMap;

public class FindApartmentActivity extends AbstractActivity implements FindApartmentView.Presenter {

    private final FindApartmentView view;

    @Inject
    public FindApartmentActivity(FindApartmentView view) {
        this.view = view;
        this.view.setPresenter(this);
    }

    @Override
    public void start(AcceptsOneWidget containerWidget, EventBus eventBus) {
        containerWidget.setWidget(view);

    }

    public FindApartmentActivity withPlace(Place place) {
        return this;
    }

    @Override
    public void goToCityMap(Province province) {
        AppSite.getPlaceController().goTo(new PortalSiteMap.FindApartment.CityMap());

    }

    @Override
    public void goToPropertyMap(City city) {
        AppSite.getPlaceController().goTo(new PortalSiteMap.FindApartment.PropertyMap());
    }

    @Override
    public void goToPropertyMap(GeoCriteria geoCriteria) {
        AppSite.getPlaceController().goTo(new PortalSiteMap.FindApartment.PropertyMap());
    }
}
