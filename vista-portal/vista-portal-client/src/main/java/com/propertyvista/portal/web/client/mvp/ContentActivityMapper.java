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
                    }

                    else if (appPlace instanceof Financial) {
                        activity = new FinancialDashboardActivity(appPlace);
                    }

                    else if (appPlace instanceof Resident.Profile) {
                        activity = new ProfilePageActivity(appPlace);
                    } else if (appPlace instanceof Resident.Account) {
                        activity = new AccountPageActivity(appPlace);
                    }

                    else if (appPlace instanceof PortalSiteMap.Resident.ResidentServices) {
                        activity = new ServicesDashboardActivity(appPlace);

// Financial:
                    } else if (appPlace instanceof Financial.Payment.PayNow) {
                        activity = new PaymentWizardActivity(appPlace);
                    } else if (appPlace instanceof Financial.Payment.PaymentSubmitting) {
                        activity = new PaymentConfirmationActivity(appPlace);

                    } else if (appPlace instanceof Financial.PaymentMethods.PaymentMethod) {
                        activity = new PaymentMethodViewActivity(appPlace);
                    } else if (appPlace instanceof Financial.PaymentMethods.NewPaymentMethod) {
                        activity = new PaymentMethodWizardActivity(appPlace);
                    } else if (appPlace instanceof Financial.PaymentMethods.PaymentMethodSubmitted) {
                        activity = new PaymentMethodConfirmationActivity(appPlace);

                    } else if (appPlace instanceof Resident.Financial.PreauthorizedPayments.PreauthorizedPayment) {
                        activity = new AutoPayActivity(appPlace);
                    } else if (appPlace instanceof Resident.Financial.PreauthorizedPayments.NewPreauthorizedPayment) {
                        activity = new AutoPayWizardActivity(appPlace);
                    } else if (appPlace instanceof Resident.Financial.PreauthorizedPayments.PreauthorizedPaymentSubmitted) {
                        activity = new AutoPayConfirmationActivity(appPlace);

                    } else if (appPlace instanceof Financial.BillingHistory) {
                        activity = new BillingHistoryViewActivity(appPlace);
                    } else if (appPlace instanceof Financial.TransactionHistory) {
                        activity = new TransactionHistoryViewActivity(appPlace);

                    } else if (appPlace instanceof Financial.BillingHistory.BillView) {
                        activity = new BillViewActivity(appPlace);

// Insurance:
                    } else if (appPlace instanceof PortalSiteMap.Resident.ResidentServices.TenantInsurance.TenantSure.TenantSureWizard) {
                        activity = new TenantSureOrderWizardActivity(appPlace);
                    } else if (appPlace instanceof PortalSiteMap.Resident.ResidentServices.TenantInsurance.TenantSure.TenantSureWizardConfirmation) {
                        activity = new TenantSureOrderConfirmationPageActivity(appPlace);
                    } else if (appPlace instanceof PortalSiteMap.Resident.ResidentServices.TenantInsurance.TenantSure.TenantSurePage) {
                        activity = new TenantSurePageActivity(appPlace);
                    } else if (appPlace instanceof PortalSiteMap.Resident.ResidentServices.TenantInsurance.TenantSure.TenantSurePage.UpdateCreditCard) {
                        activity = new TenantSurePaymentMethodUpdateWizardActivity(appPlace);
                    } else if (appPlace instanceof PortalSiteMap.Resident.ResidentServices.TenantInsurance.TenantSure.TenantSurePage.UpdateCreditCardConfirmation) {
                        activity = new TenantSurePaymentMethodUpdateConfirmationActivity(appPlace);
                    } else if (appPlace instanceof PortalSiteMap.Resident.ResidentServices.TenantInsurance.TenantSure.TenantSurePage.Faq) {
                        activity = new TenantSureFaqActivity(appPlace);
                    } else if (appPlace instanceof PortalSiteMap.Resident.ResidentServices.TenantInsurance.TenantSure.TenantSurePage.About) {
                        activity = new TenantSureAboutActivity(appPlace);

                    } else if (appPlace instanceof PortalSiteMap.Resident.ResidentServices.TenantInsurance.GeneralPolicyWizard) {
                        activity = new GeneralPolicyUploadWizardActivity(appPlace);
                    } else if (appPlace instanceof PortalSiteMap.Resident.ResidentServices.TenantInsurance.GeneralPolicyPage) {
                        activity = new GeneralPolicyPageActivity(appPlace);

// Maintenance:
                    } else if (place instanceof PortalSiteMap.Resident.Maintenance) {
                        activity = new MaintenanceDashboardActivity(appPlace);
                    } else if (place instanceof PortalSiteMap.Resident.Maintenance.MaintenanceRequestWizard) {
                        activity = new MaintenanceRequestWizardActivity(appPlace);
                    } else if (place instanceof PortalSiteMap.Resident.Maintenance.MaintenanceRequestPage) {
                        activity = new MaintenanceRequestPageActivity(appPlace);

// Internals:
                    } else if (place instanceof PortalSiteMap.Login) {
                        activity = new LandingActivity(place);
                    } else if (place instanceof PortalSiteMap.PasswordReset) {
                        activity = new PasswordResetActivity(place);
                    } else if (place instanceof PortalSiteMap.LoginWithToken) {
                        activity = new LoginWithTokenActivity(place);
                    } else if (place instanceof PortalSiteMap.PasswordResetRequest) {
                        activity = new PasswordResetRequestWizardActivity(place);
                    } else if (place instanceof PortalSiteMap.PasswordChange) {
                        activity = new PasswordChangeActivity();
                    } else if (place instanceof PortalSiteMap.Registration) {
                        activity = new SignUpActivity(place);
                    } else if (place instanceof PortalSiteMap.LeaseContextSelection) {
                        activity = new LeaseContextSelectionActivity();
                    } else if (appPlace instanceof PortalSiteMap.NotificationPlace) {
                        activity = new NotificationPageActivity((PortalSiteMap.NotificationPlace) place);

// Internals/Terms: @formatter:off
                    } else if (place instanceof PortalSiteMap.PortalTerms.BillingPolicy 
                            || place instanceof PortalSiteMap.PortalTerms.CreditCardPolicy
                            || place instanceof PortalSiteMap.PortalTerms.PadPolicy
                            || place instanceof PortalSiteMap.PortalTerms.TermsAndConditions) {
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
