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
package com.propertyvista.admin.client.mvp;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.place.shared.Place;

import com.propertyvista.admin.client.activity.login.LoginActivity;
import com.propertyvista.admin.client.activity.login.LoginWithTokenActivity;
import com.propertyvista.admin.rpc.AdminSiteMap;

public class LoginActivityMapper implements ActivityMapper {

    public LoginActivityMapper() {
    }

    @Override
    public Activity getActivity(Place place) {
        if (place instanceof AdminSiteMap.Login) {
            return new LoginActivity(place);
        } else if (place instanceof AdminSiteMap.LoginWithToken) {
            return new LoginWithTokenActivity(place);
        }
        return null;
    }
}
