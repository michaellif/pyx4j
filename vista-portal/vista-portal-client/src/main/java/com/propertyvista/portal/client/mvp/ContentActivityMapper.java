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
package com.propertyvista.portal.client.mvp;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.place.shared.Place;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.propertyvista.portal.client.activity.CityMapActivity;
import com.propertyvista.portal.client.activity.FindApartmentActivity;
import com.propertyvista.portal.client.activity.LoginActivity;
import com.propertyvista.portal.client.activity.PropertyMapActivity;
import com.propertyvista.portal.client.activity.ResidentsActivity;
import com.propertyvista.portal.rpc.portal.PortalSiteMap;

public class ContentActivityMapper implements ActivityMapper {

    Provider<FindApartmentActivity> findApartmentActivityProvider;

    Provider<ResidentsActivity> residentsActivityProvider;

    Provider<LoginActivity> loginActivityProvider;

    Provider<CityMapActivity> cityMapActivity;

    Provider<PropertyMapActivity> propertyMapActivity;

    @Inject
    public ContentActivityMapper(Provider<FindApartmentActivity> findApartmentActivityProvider,

    Provider<ResidentsActivity> residentsActivityProvider,

    Provider<LoginActivity> loginActivityProvider,

    Provider<CityMapActivity> cityMapActivity,

    Provider<PropertyMapActivity> propertyMapActivity) {
        super();
        this.findApartmentActivityProvider = findApartmentActivityProvider;
        this.residentsActivityProvider = residentsActivityProvider;
        this.loginActivityProvider = loginActivityProvider;
        this.cityMapActivity = cityMapActivity;
        this.propertyMapActivity = propertyMapActivity;

    }

    @Override
    public Activity getActivity(Place place) {
        if (place instanceof PortalSiteMap.FindApartment)
            return findApartmentActivityProvider.get().withPlace(place);
        else if (place instanceof PortalSiteMap.Residents)
            return residentsActivityProvider.get().withPlace(place);
        else if (place instanceof PortalSiteMap.Residents.Login)
            return loginActivityProvider.get().withPlace(place);
        else if (place instanceof PortalSiteMap.FindApartment.CityMap)
            return cityMapActivity.get().withPlace(place);
        else if (place instanceof PortalSiteMap.FindApartment.PropertyMap)
            return propertyMapActivity.get().withPlace(place);

        return null;

    }
}
