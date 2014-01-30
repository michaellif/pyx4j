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
package com.propertyvista.portal.prospect.mvp;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.site.client.activity.AppActivityMapper;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.portal.prospect.activity.ApplicationContextSelectionActivity;
import com.propertyvista.portal.prospect.activity.LandingActivity;
import com.propertyvista.portal.prospect.activity.PortalTermsActivity;
import com.propertyvista.portal.prospect.activity.SignUpActivity;
import com.propertyvista.portal.prospect.activity.application.ApplicationConfirmationActivity;
import com.propertyvista.portal.prospect.activity.application.ApplicationStatusPageActivity;
import com.propertyvista.portal.prospect.activity.application.ApplicationWizardActivity;
import com.propertyvista.portal.rpc.portal.PortalSiteMap;
import com.propertyvista.portal.rpc.portal.prospect.ProspectPortalSiteMap;
import com.propertyvista.portal.rpc.portal.prospect.ProspectPortalSiteMap.ApplicationContextSelection;
import com.propertyvista.portal.rpc.portal.prospect.ProspectPortalSiteMap.Registration;
import com.propertyvista.portal.shared.activity.NotificationPageActivity;
import com.propertyvista.portal.shared.activity.login.LoginWithTokenActivity;
import com.propertyvista.portal.shared.activity.login.LogoutActivity;
import com.propertyvista.portal.shared.activity.login.PasswordResetRequestWizardActivity;
import com.propertyvista.portal.shared.activity.security.PasswordChangeActivity;
import com.propertyvista.portal.shared.activity.security.PasswordResetActivity;

public class ContentActivityMapper implements AppActivityMapper {

    public ContentActivityMapper() {
    }

    @Override
    public void obtainActivity(final Place place, final AsyncCallback<Activity> callback) {
        GWT.runAsync(new RunAsyncCallback() {

            @Override
            public void onSuccess() {
                if (place instanceof AppPlace) {
                    AppPlace appPlace = (AppPlace) place;

                    Activity activity = null;
                    if (appPlace instanceof ProspectPortalSiteMap.Status) {
                        activity = new ApplicationStatusPageActivity(appPlace);

// Internals:
                    } else if (appPlace instanceof PortalSiteMap.Login) {
                        activity = new LandingActivity(appPlace);
                    } else if (appPlace instanceof PortalSiteMap.Logout) {
                        activity = new LogoutActivity();
                    } else if (appPlace instanceof PortalSiteMap.PasswordReset) {
                        activity = new PasswordResetActivity(appPlace);
                    } else if (appPlace instanceof PortalSiteMap.LoginWithToken) {
                        activity = new LoginWithTokenActivity(appPlace);
                    } else if (appPlace instanceof PortalSiteMap.PasswordResetRequest) {
                        activity = new PasswordResetRequestWizardActivity(appPlace);
                    } else if (appPlace instanceof PortalSiteMap.PasswordChange) {
                        activity = new PasswordChangeActivity();
                    } else if (appPlace instanceof Registration) {
                        activity = new SignUpActivity(appPlace);
                    } else if (place instanceof ApplicationContextSelection) {
                        activity = new ApplicationContextSelectionActivity();
                    } else if (appPlace instanceof PortalSiteMap.NotificationPlace) {
                        activity = new NotificationPageActivity((PortalSiteMap.NotificationPlace) appPlace);

                    } else if (appPlace instanceof ProspectPortalSiteMap.Application) {
                        activity = new ApplicationWizardActivity(appPlace);
                    } else if (appPlace instanceof ProspectPortalSiteMap.ApplicationConfirmation) {
                        activity = new ApplicationConfirmationActivity(appPlace);

// Internals/Terms: @formatter:off
                    } else if (place instanceof PortalSiteMap.PortalTerms.BillingTerms 
                            || place instanceof PortalSiteMap.PortalTerms.PortalTermsAndConditions
                            || place instanceof PortalSiteMap.PortalTerms.PortalPrivacyPolicy
                            || place instanceof PortalSiteMap.PortalTerms.PMCPrivacyPolicy
                            || place instanceof PortalSiteMap.PortalTerms.PMCPrivacyPolicy) {
                        activity = new PortalTermsActivity(place);
                 // @formatter:on
                    }

                    callback.onSuccess(activity);
                }
            }

            @Override
            public void onFailure(Throwable reason) {
                callback.onFailure(reason);
            }
        });
    }
}
