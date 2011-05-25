/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 18, 2011
 * @author vadims
 * @version $Id$
 */
package com.propertyvista.portal.client.mvp;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.place.shared.Place;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.propertyvista.portal.client.activity.ResidentsNavigActivity;
import com.propertyvista.portal.rpc.portal.PortalSiteMap;

public class SecondaryNavigActivityMapper implements ActivityMapper {

    Provider<ResidentsNavigActivity> residentsNavigActivity;

    @Inject
    public SecondaryNavigActivityMapper(

    Provider<ResidentsNavigActivity> residentsNavigActivity

    ) {

        this.residentsNavigActivity = residentsNavigActivity;
    }

    @Override
    public Activity getActivity(Place place) {

        if (place instanceof PortalSiteMap.Residents.Navigator ||

        place instanceof PortalSiteMap.Residents.Navigator.TenantProfile ||

        place instanceof PortalSiteMap.Residents.Navigator.LeaseApplication ||

        place instanceof PortalSiteMap.Residents.Navigator.Maintenance ||

        place instanceof PortalSiteMap.Residents.Navigator.Payment) {

            return residentsNavigActivity.get().withPlace(place);

        }

        return null;

    }
}
