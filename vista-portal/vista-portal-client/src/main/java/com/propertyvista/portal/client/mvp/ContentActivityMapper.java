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
package com.propertyvista.portal.client.mvp;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.site.client.activity.AppActivityMapper;

import com.propertyvista.misc.VistaTODO;
import com.propertyvista.portal.client.activity.BillingHistoryActivity;
import com.propertyvista.portal.client.activity.CurrentBillActivity;
import com.propertyvista.portal.client.activity.DashboardActivity;
import com.propertyvista.portal.client.activity.PersonalInfoActivity;
import com.propertyvista.portal.client.activity.PotentialTenantActivity;
import com.propertyvista.portal.client.activity.insurancemockup.InsuranceActivity;
import com.propertyvista.portal.client.activity.login.LeaseContextSelectionActivity;
import com.propertyvista.portal.client.activity.login.LoginWithTokenActivity;
import com.propertyvista.portal.client.activity.login.PasswordResetRequestActivity;
import com.propertyvista.portal.client.activity.login.RedirectToLoginPageActivity;
import com.propertyvista.portal.client.activity.maintenance.MaintenanceAcitvity;
import com.propertyvista.portal.client.activity.maintenance.NewMaintenanceRequestActivity;
import com.propertyvista.portal.client.activity.paymentmethod.EditPaymentMethodActivity;
import com.propertyvista.portal.client.activity.paymentmethod.NewPaymentMethodActivity;
import com.propertyvista.portal.client.activity.paymentmethod.PaymentMethodsActivity;
import com.propertyvista.portal.client.activity.security.PasswordResetActivity;
import com.propertyvista.portal.rpc.portal.PortalSiteMap;
import com.propertyvista.portal.rpc.portal.PortalSiteMap.Residents;

public class ContentActivityMapper implements AppActivityMapper {

    public ContentActivityMapper() {

    }

    @Override
    public void obtainActivity(final Place place, final AsyncCallback<Activity> callback) {
        GWT.runAsync(new RunAsyncCallback() {

            @Override
            public void onSuccess() {
                Activity activity = null;
                if (place instanceof Residents) {
                    activity = new DashboardActivity(place);
                } else if (place instanceof Residents.PersonalInformation) {
                    activity = new PersonalInfoActivity(place);
                } else if (place instanceof PortalSiteMap.PotentialTenants) {
                    activity = new PotentialTenantActivity(place);
                } else if (place instanceof Residents.CurrentBill) {
                    activity = new CurrentBillActivity(place);
                } else if (place instanceof Residents.PaymentMethods) {
                    activity = new PaymentMethodsActivity(place);
                } else if (place instanceof Residents.PaymentMethods.NewPaymentMethod) {
                    activity = new NewPaymentMethodActivity(place);
                } else if (place instanceof Residents.PaymentMethods.EditPaymentMethod) {
                    activity = new EditPaymentMethodActivity(place);
                } else if (place instanceof Residents.Maintenance) {
                    activity = new MaintenanceAcitvity(place);
                } else if (place instanceof Residents.Maintenance.NewTicket) {
                    activity = new NewMaintenanceRequestActivity(place);
                } else if (place instanceof Residents.BillingHistory) {
                    activity = new BillingHistoryActivity(place);
                } else if (VistaTODO.enableWelcomeWizardDemoMode & (place instanceof PortalSiteMap.Residents.Insurance)) {
                    activity = new InsuranceActivity();

                    // TODO not sure if these activities belong here 
                } else if (place instanceof PortalSiteMap.Login) {
                    activity = new RedirectToLoginPageActivity(place);
                } else if (place instanceof PortalSiteMap.LoginWithToken) {
                    activity = new LoginWithTokenActivity(place);
                } else if (place instanceof PortalSiteMap.LeaseContextSelection) {
                    activity = new LeaseContextSelectionActivity();
                } else if (place instanceof PortalSiteMap.PasswordResetRequest) {
                    activity = new PasswordResetRequestActivity(place);
                } else if (place instanceof PortalSiteMap.PasswordReset) {
                    activity = new PasswordResetActivity(place);
                } else if (place instanceof PortalSiteMap.PasswordChange) {
                    // TODO portal password change activity
                }
                callback.onSuccess(activity);
            }

            @Override
            public void onFailure(Throwable reason) {
                callback.onFailure(reason);
            }
        });

    }
}
