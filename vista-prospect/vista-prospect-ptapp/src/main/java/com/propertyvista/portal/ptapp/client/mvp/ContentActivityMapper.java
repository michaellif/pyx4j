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
package com.propertyvista.portal.ptapp.client.mvp;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.place.shared.Place;

import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.misc.VistaTODO;
import com.propertyvista.portal.ptapp.client.activity.ApplicationSelectionActivity;
import com.propertyvista.portal.ptapp.client.activity.ApplicationStatusActivity;
import com.propertyvista.portal.ptapp.client.activity.LoginActivity;
import com.propertyvista.portal.ptapp.client.activity.LoginWithTokenActivity;
import com.propertyvista.portal.ptapp.client.activity.NotificationActivity;
import com.propertyvista.portal.ptapp.client.activity.PasswordChangeActivity;
import com.propertyvista.portal.ptapp.client.activity.PasswordResetActivity;
import com.propertyvista.portal.ptapp.client.activity.PasswordResetRequestActivity;
import com.propertyvista.portal.ptapp.client.activity.steps.ApartmentActivity;
import com.propertyvista.portal.ptapp.client.activity.steps.ChargesActivity;
import com.propertyvista.portal.ptapp.client.activity.steps.CompletionActivity;
import com.propertyvista.portal.ptapp.client.activity.steps.FinancialActivity;
import com.propertyvista.portal.ptapp.client.activity.steps.InfoActivity;
import com.propertyvista.portal.ptapp.client.activity.steps.PaymentActivity;
import com.propertyvista.portal.ptapp.client.activity.steps.SummaryActivity;
import com.propertyvista.portal.ptapp.client.activity.steps.TenantsActivity;
import com.propertyvista.portal.ptapp.client.activity.steps.welcomewizardmockup.InsuranceActivity;
import com.propertyvista.portal.ptapp.client.activity.steps.welcomewizardmockup.LeaseReviewActivity;
import com.propertyvista.portal.ptapp.client.activity.steps.welcomewizardmockup.MoveInScheduleActivity;
import com.propertyvista.portal.ptapp.client.activity.steps.welcomewizardmockup.WelcomeActivity;
import com.propertyvista.portal.rpc.ptapp.PtSiteMap;

public class ContentActivityMapper implements ActivityMapper {

    @Override
    public Activity getActivity(Place place) {

        if (place instanceof PtSiteMap.Login) {
            return new LoginActivity(place);
        } else if (place instanceof PtSiteMap.LoginWithToken) {
            return new LoginWithTokenActivity(place);
        } else if (place instanceof PtSiteMap.PasswordResetRequest) {
            return new PasswordResetRequestActivity(place);
        } else if (place instanceof PtSiteMap.PasswordChange) {
            return new PasswordChangeActivity(place);
        } else if (place instanceof PtSiteMap.PasswordReset) {
            return new PasswordResetActivity(place);
        } else if (place instanceof PtSiteMap.Notification) {
            return new NotificationActivity((PtSiteMap.Notification) place);
// WizardSteps:
        } else if (place instanceof PtSiteMap.Apartment) {
            return new ApartmentActivity((AppPlace) place);
        } else if (place instanceof PtSiteMap.Tenants) {
            return new TenantsActivity((AppPlace) place);
        } else if (place instanceof PtSiteMap.Info) {
            return new InfoActivity((AppPlace) place);
        } else if (place instanceof PtSiteMap.Financial) {
            return new FinancialActivity((AppPlace) place);
        } else if (place instanceof PtSiteMap.Charges) {
            return new ChargesActivity((AppPlace) place);
        } else if (place instanceof PtSiteMap.Summary) {
            return new SummaryActivity((AppPlace) place);
        } else if (place instanceof PtSiteMap.Payment) {
            return new PaymentActivity((AppPlace) place);
        } else if (place instanceof PtSiteMap.Completion) {
            return new CompletionActivity((AppPlace) place);

            // other places:
        } else if (place instanceof PtSiteMap.ApplicationStatus) {
            return new ApplicationStatusActivity((AppPlace) place);
        } else if (place instanceof PtSiteMap.ApplicationSelectionRequired) {
            return new ApplicationSelectionActivity();
        }

        if (VistaTODO.enableWelcomeWizardDemoMode) {
            // FIXME: mock up wizard steps for approved clients
            AppPlace appPlace = (AppPlace) place;
            if (place instanceof PtSiteMap.WelcomeWizard.ReviewLease) {
                return new LeaseReviewActivity(appPlace);
            } else if (place instanceof PtSiteMap.WelcomeWizard.MoveInSchedule) {
                return new MoveInScheduleActivity(appPlace);
            } else if (place instanceof PtSiteMap.WelcomeWizard.Insurance) {
                return new InsuranceActivity(appPlace);
            } else if (place instanceof PtSiteMap.WelcomeWizard.Completion) {
                return new com.propertyvista.portal.ptapp.client.activity.steps.welcomewizardmockup.CompletionActivity(appPlace);
            } else if (place instanceof PtSiteMap.WelcomeWizard.Welcome) {
                return new WelcomeActivity();
            }
        }

        //TODO what to do on other place
        return null;
    }
}
