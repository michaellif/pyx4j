/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Apr 19, 2011
 * @author vadims
 * @version $Id$
 */
package com.propertyvista.portal.resident.mvp;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.place.shared.Place;

import com.pyx4j.security.client.ClientContext;

import com.propertyvista.portal.resident.activity.ExtraActivity;
import com.propertyvista.portal.rpc.portal.PortalSiteMap.Logout;
import com.propertyvista.portal.rpc.portal.resident.ResidentPortalSiteMap;

public class ExtraActivityMapper implements ActivityMapper {

    public ExtraActivityMapper() {
    }

    @Override
    public Activity getActivity(Place place) {
        if (ClientContext.isAuthenticated() && !(place instanceof Logout) && !(place instanceof ResidentPortalSiteMap.LeaseContextSelection)) {
            return new ExtraActivity(place);
        } else {
            return null;
        }
    }

}
