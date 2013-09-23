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
package com.propertyvista.portal.web.client.mvp;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.site.client.activity.AppActivityMapper;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.portal.rpc.portal.PortalSiteMap;
import com.propertyvista.portal.rpc.portal.PortalSiteMap.Resident;
import com.propertyvista.portal.rpc.portal.PortalSiteMap.Resident.Financial;
import com.propertyvista.portal.rpc.portal.PortalSiteMap.Resident.Financial.BillingDashboard;
import com.propertyvista.portal.web.client.activity.NotificationPageActivity;
import com.propertyvista.portal.web.client.activity.SignUpActivity;
import com.propertyvista.portal.web.client.activity.dashboard.MainDashboardActivity;
import com.propertyvista.portal.web.client.activity.financial.autopay.AutoPayActivity;
import com.propertyvista.portal.web.client.activity.financial.autopay.AutoPayConfirmationActivity;
import com.propertyvista.portal.web.client.activity.financial.autopay.AutoPayWizardActivity;
import com.propertyvista.portal.web.client.activity.financial.dashboard.FinancialDashboardActivity;
import com.propertyvista.portal.web.client.activity.financial.payment.PaymentConfirmationActivity;
import com.propertyvista.portal.web.client.activity.financial.payment.PaymentWizardActivity;
import com.propertyvista.portal.web.client.activity.financial.paymentmethod.PaymentMethodConfirmationActivity;
import com.propertyvista.portal.web.client.activity.financial.paymentmethod.PaymentMethodEditorActivity;
import com.propertyvista.portal.web.client.activity.financial.paymentmethod.PaymentMethodWizardActivity;
import com.propertyvista.portal.web.client.activity.login.LandingActivity;
import com.propertyvista.portal.web.client.activity.login.LeaseContextSelectionActivity;
import com.propertyvista.portal.web.client.activity.login.LoginWithTokenActivity;
import com.propertyvista.portal.web.client.activity.login.PasswordResetRequestActivity;
import com.propertyvista.portal.web.client.activity.login.VistaTermsActivity;
import com.propertyvista.portal.web.client.activity.maintenance.MaintenanceDashboardActivity;
import com.propertyvista.portal.web.client.activity.profile.ProfilePageActivity;
import com.propertyvista.portal.web.client.activity.security.PasswordChangeActivity;
import com.propertyvista.portal.web.client.activity.security.PasswordResetActivity;
import com.propertyvista.portal.web.client.activity.services.dashboard.ServicesDashboardActivity;
import com.propertyvista.portal.web.client.activity.services.insurance.TenantSureOrderWizardActivity;

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
                    if (appPlace instanceof Resident) {
                        activity = new MainDashboardActivity(appPlace);
                    }

                    else if (appPlace instanceof BillingDashboard) {
                        activity = new FinancialDashboardActivity(appPlace);
                    }

                    else if (appPlace instanceof Resident.ProfileViewer) {
                        activity = new ProfilePageActivity(appPlace);
                    }

                    else if (appPlace instanceof PortalSiteMap.Resident.ResidentServices) {
                        activity = new ServicesDashboardActivity(appPlace);

                    } else if (appPlace instanceof Financial.PaymentMethods.EditPaymentMethod) {
                        activity = new PaymentMethodEditorActivity(appPlace);
                    } else if (appPlace instanceof Financial.PaymentMethods.NewPaymentMethod) {
                        activity = new PaymentMethodWizardActivity(appPlace);
                    } else if (appPlace instanceof Financial.PaymentMethods.PaymentMethodSubmitted) {
                        activity = new PaymentMethodConfirmationActivity(appPlace);

                    } else if (appPlace instanceof Resident.Financial.PayNow) {
                        activity = new PaymentWizardActivity(appPlace);
                    } else if (appPlace instanceof Resident.Financial.PaymentSubmitting) {
                        activity = new PaymentConfirmationActivity(appPlace);

                    } else if (appPlace instanceof Resident.Financial.PreauthorizedPayments.PreauthorizedPayment) {
                        activity = new AutoPayActivity(appPlace);
                    } else if (appPlace instanceof Resident.Financial.PreauthorizedPayments.NewPreauthorizedPayment) {
                        activity = new AutoPayWizardActivity(appPlace);
                    } else if (appPlace instanceof Resident.Financial.PreauthorizedPayments.PreauthorizedPaymentSubmitted) {
                        activity = new AutoPayConfirmationActivity(appPlace);

                    } else if (appPlace instanceof PortalSiteMap.Resident.ResidentServices.TenantInsurance.TenantSure.TenantSureOrderWizard) {
                        activity = new TenantSureOrderWizardActivity(appPlace);
                    } else if (appPlace instanceof PortalSiteMap.Resident.ResidentServices.TenantInsurance.GeneralCertificateUploadWizard) {
                        activity = new TenantSureOrderWizardActivity(appPlace);

                    } else if (place instanceof PortalSiteMap.Resident.Maintenance) {
                        activity = new MaintenanceDashboardActivity(place);

                    } else if (place instanceof PortalSiteMap.Login) {
                        activity = new LandingActivity(place);
                    } else if (place instanceof PortalSiteMap.PasswordReset) {
                        activity = new PasswordResetActivity(place);
                    } else if (place instanceof PortalSiteMap.LoginWithToken) {
                        activity = new LoginWithTokenActivity(place);
                    } else if (place instanceof PortalSiteMap.PasswordResetRequest) {
                        activity = new PasswordResetRequestActivity(place);
                    } else if (place instanceof PortalSiteMap.PasswordChange) {
                        activity = new PasswordChangeActivity();
                    } else if (place instanceof PortalSiteMap.Registration) {
                        activity = new SignUpActivity(place);
                    } else if (place instanceof PortalSiteMap.LeaseContextSelection) {
                        activity = new LeaseContextSelectionActivity();
                    } else if (place instanceof PortalSiteMap.PortalTermsAndConditions) {
                        activity = new VistaTermsActivity();
                    } else if (place instanceof PortalSiteMap.NotificationPlace) {
                        activity = new NotificationPageActivity(place);
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
