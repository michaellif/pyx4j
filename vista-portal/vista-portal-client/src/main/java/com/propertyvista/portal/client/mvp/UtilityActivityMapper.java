/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-01-17
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.client.mvp;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.site.client.activity.AppActivityMapper;

import com.propertyvista.portal.client.activity.TenantRegistrationActivity;
import com.propertyvista.portal.client.activity.UserMessageActivity;
import com.propertyvista.portal.client.activity.login.LandingActivity;
import com.propertyvista.portal.client.activity.login.LeaseContextSelectionActivity;
import com.propertyvista.portal.client.activity.login.LoginWithTokenActivity;
import com.propertyvista.portal.client.activity.login.LogoutActivity;
import com.propertyvista.portal.client.activity.login.PasswordResetRequestActivity;
import com.propertyvista.portal.client.activity.login.VistaTermsActivity;
import com.propertyvista.portal.client.activity.security.PasswordChangeActivity;
import com.propertyvista.portal.client.activity.security.PasswordResetActivity;
import com.propertyvista.portal.rpc.portal.PortalSiteMap;

public class UtilityActivityMapper implements AppActivityMapper {

    @Override
    public void obtainActivity(Place place, AsyncCallback<Activity> callback) {
        Activity activity = null;
        if (place instanceof PortalSiteMap.Login) {
            activity = new LandingActivity(place);
        } else if (place instanceof PortalSiteMap.LogOut) {
            activity = new LogoutActivity();
        } else if (place instanceof PortalSiteMap.PasswordReset) {
            activity = new PasswordResetActivity(place);
        } else if (place instanceof PortalSiteMap.LoginWithToken) {
            activity = new LoginWithTokenActivity(place);
        } else if (place instanceof PortalSiteMap.PasswordResetRequest) {
            activity = new PasswordResetRequestActivity(place);
        } else if (place instanceof PortalSiteMap.PasswordChange) {
            activity = new PasswordChangeActivity();
        } else if (place instanceof PortalSiteMap.Registration) {
            activity = new TenantRegistrationActivity(place);
        } else if (place instanceof PortalSiteMap.LeaseContextSelection) {
            activity = new LeaseContextSelectionActivity();
        } else if (place instanceof PortalSiteMap.PortalTermsAndConditions) {
            activity = new VistaTermsActivity();
        } else if (place instanceof PortalSiteMap.NotificationPlace) {
            activity = new UserMessageActivity(place);
        }
        callback.onSuccess(activity);
    }

}
