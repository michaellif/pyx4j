/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 24, 2011
 * @author Dad
 * @version $Id$
 */
package com.propertyvista.portal.client.mvp;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.place.shared.Place;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.propertyvista.portal.client.activity.AboutUsActivity;
import com.propertyvista.portal.client.activity.HomeActivity;
import com.propertyvista.portal.rpc.portal.PortalSiteMap;

public class StaticContentActivityMapper implements ActivityMapper {

    Provider<AboutUsActivity> aboutActivity;

    Provider<HomeActivity> homeActivity;

    @Inject
    public StaticContentActivityMapper(

    Provider<AboutUsActivity> aboutActivity,

    Provider<HomeActivity> homeActivty) {
        super();
        this.aboutActivity = aboutActivity;
        this.homeActivity = homeActivty;

    }

    @Override
    public Activity getActivity(Place place) {
        if (place instanceof PortalSiteMap.AboutUs) {
            return aboutActivity.get().withPlace(place);
        } else if (place instanceof PortalSiteMap.Home) {
            return homeActivity.get().withPlace(place);
        }
        return null;
    }

}
