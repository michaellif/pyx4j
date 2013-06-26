/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 24, 2013
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.portal.web.client.mvp;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.place.shared.Place;

import com.pyx4j.security.client.ClientContext;

import com.propertyvista.portal.web.client.activity.UserMessageHeaderActivity;

public class UserMessageActivityMapper implements ActivityMapper {

    public UserMessageActivityMapper() {
    }

    @Override
    public Activity getActivity(Place place) {
        if (ClientContext.isAuthenticated()) {
            return new UserMessageHeaderActivity(place);
        } else {
            return null;
        }
    }

}
