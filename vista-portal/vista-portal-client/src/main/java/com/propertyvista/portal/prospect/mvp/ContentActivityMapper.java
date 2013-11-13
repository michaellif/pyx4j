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

import com.propertyvista.portal.prospect.activity.LandingActivity;
import com.propertyvista.portal.prospect.activity.SignUpActivity;
import com.propertyvista.portal.prospect.activity.ApplicationStatusPageActivity;
import com.propertyvista.portal.prospect.activity.steps.ContactsStepActivity;
import com.propertyvista.portal.prospect.activity.steps.FinancialStepActivity;
import com.propertyvista.portal.prospect.activity.steps.OptionsStepActivity;
import com.propertyvista.portal.prospect.activity.steps.PaymentStepActivity;
import com.propertyvista.portal.prospect.activity.steps.PeopleStepActivity;
import com.propertyvista.portal.prospect.activity.steps.PersonalInfoAStepActivity;
import com.propertyvista.portal.prospect.activity.steps.PersonalInfoBStepActivity;
import com.propertyvista.portal.prospect.activity.steps.PmcCustomStepActivity;
import com.propertyvista.portal.prospect.activity.steps.SummaryStepActivity;
import com.propertyvista.portal.prospect.activity.steps.UnitStepActivity;
import com.propertyvista.portal.rpc.portal.PortalSiteMap;
import com.propertyvista.portal.rpc.portal.ProspectPortalSiteMap;
import com.propertyvista.portal.rpc.portal.ProspectPortalSiteMap.Registration;
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
                    } else if (appPlace instanceof PortalSiteMap.NotificationPlace) {
                        activity = new NotificationPageActivity((PortalSiteMap.NotificationPlace) appPlace);

// Steps:
                    } else if (appPlace instanceof ProspectPortalSiteMap.Application.UnitStep) {
                        activity = new UnitStepActivity(appPlace);
                    } else if (appPlace instanceof ProspectPortalSiteMap.Application.OptionsStep) {
                        activity = new OptionsStepActivity(appPlace);
                    } else if (appPlace instanceof ProspectPortalSiteMap.Application.PersonalInfoAStep) {
                        activity = new PersonalInfoAStepActivity(appPlace);
                    } else if (appPlace instanceof ProspectPortalSiteMap.Application.PersonalInfoBStep) {
                        activity = new PersonalInfoBStepActivity(appPlace);
                    } else if (appPlace instanceof ProspectPortalSiteMap.Application.FinancialStep) {
                        activity = new FinancialStepActivity(appPlace);
                    } else if (appPlace instanceof ProspectPortalSiteMap.Application.PeopleStep) {
                        activity = new PeopleStepActivity(appPlace);
                    } else if (appPlace instanceof ProspectPortalSiteMap.Application.ContactsStep) {
                        activity = new ContactsStepActivity(appPlace);
                    } else if (appPlace instanceof ProspectPortalSiteMap.Application.PmcCustomStep) {
                        activity = new PmcCustomStepActivity(appPlace);
                    } else if (appPlace instanceof ProspectPortalSiteMap.Application.SummaryStep) {
                        activity = new SummaryStepActivity(appPlace);
                    } else if (appPlace instanceof ProspectPortalSiteMap.Application.PaymentStep) {
                        activity = new PaymentStepActivity(appPlace);

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
