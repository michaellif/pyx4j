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

import com.propertyvista.portal.rpc.portal.PortalSiteMap.LeaseContextSelection;
import com.propertyvista.portal.rpc.portal.PortalSiteMap.Login;
import com.propertyvista.portal.rpc.portal.PortalSiteMap.LoginWithToken;
import com.propertyvista.portal.rpc.portal.PortalSiteMap.Logout;
import com.propertyvista.portal.rpc.portal.PortalSiteMap.NotificationPlace;
import com.propertyvista.portal.rpc.portal.PortalSiteMap.PasswordChange;
import com.propertyvista.portal.rpc.portal.PortalSiteMap.PasswordReset;
import com.propertyvista.portal.rpc.portal.PortalSiteMap.PasswordResetRequest;
import com.propertyvista.portal.rpc.portal.PortalSiteMap.PortalTerms;
import com.propertyvista.portal.rpc.portal.PortalSiteMap.Registration;
import com.propertyvista.portal.rpc.portal.PortalSiteMap.Resident;
import com.propertyvista.portal.web.client.activity.NotificationPageActivity;
import com.propertyvista.portal.web.client.activity.SignUpActivity;
import com.propertyvista.portal.web.client.activity.dashboard.MainDashboardActivity;
import com.propertyvista.portal.web.client.activity.financial.autopay.AutoPayActivity;
import com.propertyvista.portal.web.client.activity.financial.autopay.AutoPayConfirmationActivity;
import com.propertyvista.portal.web.client.activity.financial.autopay.AutoPayWizardActivity;
import com.propertyvista.portal.web.client.activity.financial.dashboard.BillViewActivity;
import com.propertyvista.portal.web.client.activity.financial.dashboard.BillingHistoryViewActivity;
import com.propertyvista.portal.web.client.activity.financial.dashboard.FinancialDashboardActivity;
import com.propertyvista.portal.web.client.activity.financial.dashboard.TransactionHistoryViewActivity;
import com.propertyvista.portal.web.client.activity.financial.payment.PaymentConfirmationActivity;
import com.propertyvista.portal.web.client.activity.financial.payment.PaymentWizardActivity;
import com.propertyvista.portal.web.client.activity.financial.paymentmethod.PaymentMethodConfirmationActivity;
import com.propertyvista.portal.web.client.activity.financial.paymentmethod.PaymentMethodViewActivity;
import com.propertyvista.portal.web.client.activity.financial.paymentmethod.PaymentMethodWizardActivity;
import com.propertyvista.portal.web.client.activity.login.LandingActivity;
import com.propertyvista.portal.web.client.activity.login.LeaseContextSelectionActivity;
import com.propertyvista.portal.web.client.activity.login.LoginWithTokenActivity;
import com.propertyvista.portal.web.client.activity.login.LogoutActivity;
import com.propertyvista.portal.web.client.activity.login.PasswordResetRequestWizardActivity;
import com.propertyvista.portal.web.client.activity.login.VistaTermsActivity;
import com.propertyvista.portal.web.client.activity.maintenance.MaintenanceDashboardActivity;
import com.propertyvista.portal.web.client.activity.maintenance.MaintenanceRequestPageActivity;
import com.propertyvista.portal.web.client.activity.maintenance.MaintenanceRequestWizardActivity;
import com.propertyvista.portal.web.client.activity.profile.AccountPageActivity;
import com.propertyvista.portal.web.client.activity.profile.ProfilePageActivity;
import com.propertyvista.portal.web.client.activity.security.PasswordChangeActivity;
import com.propertyvista.portal.web.client.activity.security.PasswordResetActivity;
import com.propertyvista.portal.web.client.activity.services.dashboard.ServicesDashboardActivity;
import com.propertyvista.portal.web.client.activity.services.insurance.GeneralPolicyPageActivity;
import com.propertyvista.portal.web.client.activity.services.insurance.GeneralPolicyUploadWizardActivity;
import com.propertyvista.portal.web.client.activity.services.insurance.TenantSureAboutActivity;
import com.propertyvista.portal.web.client.activity.services.insurance.TenantSureFaqActivity;
import com.propertyvista.portal.web.client.activity.services.insurance.TenantSureOrderConfirmationPageActivity;
import com.propertyvista.portal.web.client.activity.services.insurance.TenantSureOrderWizardActivity;
import com.propertyvista.portal.web.client.activity.services.insurance.TenantSurePageActivity;
import com.propertyvista.portal.web.client.activity.services.insurance.tenantsurepaymentmethod.TenantSurePaymentMethodUpdateConfirmationActivity;
import com.propertyvista.portal.web.client.activity.services.insurance.tenantsurepaymentmethod.TenantSurePaymentMethodUpdateWizardActivity;

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
                    if (appPlace instanceof Resident.Dashboard) {
                        activity = new MainDashboardActivity(appPlace);
                    } else if (appPlace instanceof Resident.Profile) {
                        activity = new ProfilePageActivity(appPlace);
                    } else if (appPlace instanceof Resident.Account) {
                        activity = new AccountPageActivity(appPlace);

// Financial:
                    } else if (appPlace instanceof Resident.Financial) {
                        activity = new FinancialDashboardActivity(appPlace);
                    } else if (appPlace instanceof Resident.Financial.Payment.PayNow) {
                        activity = new PaymentWizardActivity(appPlace);
                    } else if (appPlace instanceof Resident.Financial.Payment.PaymentSubmitting) {
                        activity = new PaymentConfirmationActivity(appPlace);

                    } else if (appPlace instanceof Resident.Financial.PaymentMethods.PaymentMethod) {
                        activity = new PaymentMethodViewActivity(appPlace);
                    } else if (appPlace instanceof Resident.Financial.PaymentMethods.NewPaymentMethod) {
                        activity = new PaymentMethodWizardActivity(appPlace);
                    } else if (appPlace instanceof Resident.Financial.PaymentMethods.PaymentMethodSubmitted) {
                        activity = new PaymentMethodConfirmationActivity(appPlace);

                    } else if (appPlace instanceof Resident.Financial.PreauthorizedPayments.PreauthorizedPayment) {
                        activity = new AutoPayActivity(appPlace);
                    } else if (appPlace instanceof Resident.Financial.PreauthorizedPayments.NewPreauthorizedPayment) {
                        activity = new AutoPayWizardActivity(appPlace);
                    } else if (appPlace instanceof Resident.Financial.PreauthorizedPayments.PreauthorizedPaymentSubmitted) {
                        activity = new AutoPayConfirmationActivity(appPlace);

                    } else if (appPlace instanceof Resident.Financial.BillingHistory) {
                        activity = new BillingHistoryViewActivity(appPlace);
                    } else if (appPlace instanceof Resident.Financial.TransactionHistory) {
                        activity = new TransactionHistoryViewActivity(appPlace);

                    } else if (appPlace instanceof Resident.Financial.BillingHistory.BillView) {
                        activity = new BillViewActivity(appPlace);

// Services:
                    } else if (appPlace instanceof Resident.ResidentServices) {
                        activity = new ServicesDashboardActivity(appPlace);
// Insurance:
                    } else if (appPlace instanceof Resident.ResidentServices.TenantInsurance.TenantSure.TenantSureWizard) {
                        activity = new TenantSureOrderWizardActivity(appPlace);
                    } else if (appPlace instanceof Resident.ResidentServices.TenantInsurance.TenantSure.TenantSureWizardConfirmation) {
                        activity = new TenantSureOrderConfirmationPageActivity(appPlace);
                    } else if (appPlace instanceof Resident.ResidentServices.TenantInsurance.TenantSure.TenantSurePage) {
                        activity = new TenantSurePageActivity(appPlace);
                    } else if (appPlace instanceof Resident.ResidentServices.TenantInsurance.TenantSure.TenantSurePage.UpdateCreditCard) {
                        activity = new TenantSurePaymentMethodUpdateWizardActivity(appPlace);
                    } else if (appPlace instanceof Resident.ResidentServices.TenantInsurance.TenantSure.TenantSurePage.UpdateCreditCardConfirmation) {
                        activity = new TenantSurePaymentMethodUpdateConfirmationActivity(appPlace);
                    } else if (appPlace instanceof Resident.ResidentServices.TenantInsurance.TenantSure.TenantSurePage.Faq) {
                        activity = new TenantSureFaqActivity(appPlace);
                    } else if (appPlace instanceof Resident.ResidentServices.TenantInsurance.TenantSure.TenantSurePage.About) {
                        activity = new TenantSureAboutActivity(appPlace);

                    } else if (appPlace instanceof Resident.ResidentServices.TenantInsurance.GeneralPolicyWizard) {
                        activity = new GeneralPolicyUploadWizardActivity(appPlace);
                    } else if (appPlace instanceof Resident.ResidentServices.TenantInsurance.GeneralPolicyPage) {
                        activity = new GeneralPolicyPageActivity(appPlace);
// Maintenance:
                    } else if (place instanceof Resident.Maintenance) {
                        activity = new MaintenanceDashboardActivity(appPlace);
                    } else if (place instanceof Resident.Maintenance.MaintenanceRequestWizard) {
                        activity = new MaintenanceRequestWizardActivity(appPlace);
                    } else if (place instanceof Resident.Maintenance.MaintenanceRequestPage) {
                        activity = new MaintenanceRequestPageActivity(appPlace);

// Internals:
                    } else if (place instanceof Login) {
                        activity = new LandingActivity(place);
                    } else if (place instanceof Logout) {
                        activity = new LogoutActivity();
                    } else if (place instanceof PasswordReset) {
                        activity = new PasswordResetActivity(place);
                    } else if (place instanceof LoginWithToken) {
                        activity = new LoginWithTokenActivity(place);
                    } else if (place instanceof PasswordResetRequest) {
                        activity = new PasswordResetRequestWizardActivity(place);
                    } else if (place instanceof PasswordChange) {
                        activity = new PasswordChangeActivity();
                    } else if (place instanceof Registration) {
                        activity = new SignUpActivity(place);
                    } else if (place instanceof LeaseContextSelection) {
                        activity = new LeaseContextSelectionActivity();
                    } else if (appPlace instanceof NotificationPlace) {
                        activity = new NotificationPageActivity((NotificationPlace) place);

// Internals/Terms: @formatter:off
                    } else if (place instanceof PortalTerms.BillingPolicy 
                            || place instanceof PortalTerms.CreditCardPolicy
                            || place instanceof PortalTerms.PadPolicy
                            || place instanceof PortalTerms.TermsAndConditions) {
                        activity = new VistaTermsActivity(place);
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
