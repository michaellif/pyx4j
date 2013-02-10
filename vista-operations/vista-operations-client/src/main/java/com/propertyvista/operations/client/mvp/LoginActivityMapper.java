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
package com.propertyvista.operations.client.mvp;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.place.shared.Place;

import com.propertyvista.operations.client.activity.login.LoginActivity;
import com.propertyvista.operations.client.activity.login.LoginWithTokenActivity;
import com.propertyvista.operations.client.activity.security.PasswordResetActivity;
import com.propertyvista.operations.client.activity.security.PasswordResetRequesetActivity;
import com.propertyvista.operations.rpc.OperationsSiteMap;

public class LoginActivityMapper implements ActivityMapper {

    public LoginActivityMapper() {
    }

    @Override
    public Activity getActivity(Place place) {
        if (place instanceof OperationsSiteMap.Login) {
            return new LoginActivity(place);
        } else if (place instanceof OperationsSiteMap.LoginWithToken) {
            return new LoginWithTokenActivity(place);
        } else if (place instanceof OperationsSiteMap.PasswordResetRequest) {
            return new PasswordResetRequesetActivity(place);
        } else if (place instanceof OperationsSiteMap.PasswordReset) {
            return new PasswordResetActivity(place);
        }

        return null;
    }
}
