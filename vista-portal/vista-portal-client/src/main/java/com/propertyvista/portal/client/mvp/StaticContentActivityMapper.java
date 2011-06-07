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

import com.propertyvista.portal.client.activity.StaticPageActivity;
import com.propertyvista.portal.rpc.portal.PortalSiteMap;

public class StaticContentActivityMapper implements ActivityMapper {

    Provider<StaticPageActivity> staticPageActivityProvider;

    @Inject
    public StaticContentActivityMapper(

    Provider<StaticPageActivity> staticPageActivityProvider) {
        super();
        this.staticPageActivityProvider = staticPageActivityProvider;
    }

    @Override
    public Activity getActivity(Place place) {
        if (place instanceof PortalSiteMap.Page || place instanceof PortalSiteMap.Landing) {
            return staticPageActivityProvider.get().withPlace(place);
        }
        return null;
    }

}
