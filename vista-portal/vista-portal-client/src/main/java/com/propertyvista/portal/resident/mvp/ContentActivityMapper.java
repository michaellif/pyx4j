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
 */
package com.propertyvista.portal.resident.mvp;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.site.client.activity.AppActivityMapper;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.portal.resident.activity.LandingActivity;
import com.propertyvista.portal.resident.activity.LeaseContextSelectionActivity;
import com.propertyvista.portal.resident.activity.PortalTermsActivity;
import com.propertyvista.portal.resident.activity.SignUpActivity;
import com.propertyvista.portal.resident.activity.dashboard.MainDashboardActivity;
import com.propertyvista.portal.resident.activity.financial.autopay.AutoPayActivity;
import com.propertyvista.portal.resident.activity.financial.autopay.AutoPayConfirmationActivity;
import com.propertyvista.portal.resident.activity.financial.autopay.AutoPayWizardActivity;
import com.propertyvista.portal.resident.activity.financial.dashboard.BillViewActivity;
import com.propertyvista.portal.resident.activity.financial.dashboard.BillingHistoryViewActivity;
import com.propertyvista.portal.resident.activity.financial.dashboard.FinancialDashboardActivity;
import com.propertyvista.portal.resident.activity.financial.dashboard.TransactionHistoryViewActivity;
import com.propertyvista.portal.resident.activity.financial.payment.PaymentConfirmationActivity;
import com.propertyvista.portal.resident.activity.financial.payment.PaymentWizardActivity;
import com.propertyvista.portal.resident.activity.financial.paymentmethod.PaymentMethodConfirmationActivity;
import com.propertyvista.portal.resident.activity.financial.paymentmethod.PaymentMethodViewActivity;
import com.propertyvista.portal.resident.activity.financial.paymentmethod.PaymentMethodWizardActivity;
import com.propertyvista.portal.resident.activity.leasesigning.LeaseSigningActivity;
import com.propertyvista.portal.resident.activity.leasesigning.LeaseSigningConfirmationActivity;
import com.propertyvista.portal.resident.activity.maintenance.MaintenanceDashboardActivity;
import com.propertyvista.portal.resident.activity.maintenance.MaintenanceRequestPageActivity;
import com.propertyvista.portal.resident.activity.maintenance.MaintenanceRequestWizardActivity;
import com.propertyvista.portal.resident.activity.movein.MoveInWizardActivity;
import com.propertyvista.portal.resident.activity.offers.dashboard.OffersDashboardActivity;
import com.propertyvista.portal.resident.activity.profile.ProfilePageActivity;
import com.propertyvista.portal.resident.activity.services.dashboard.ServicesDashboardActivity;
import com.propertyvista.portal.resident.activity.services.insurance.GeneralPolicyPageActivity;
import com.propertyvista.portal.resident.activity.services.insurance.GeneralPolicyUploadWizardActivity;
import com.propertyvista.portal.resident.activity.services.insurance.TenantSureAboutActivity;
import com.propertyvista.portal.resident.activity.services.insurance.TenantSureFaqActivity;
import com.propertyvista.portal.resident.activity.services.insurance.TenantSureOrderConfirmationPageActivity;
import com.propertyvista.portal.resident.activity.services.insurance.TenantSureOrderWizardActivity;
import com.propertyvista.portal.resident.activity.services.insurance.TenantSurePageActivity;
import com.propertyvista.portal.resident.activity.services.insurance.tenantsurepaymentmethod.TenantSurePaymentMethodUpdateConfirmationActivity;
import com.propertyvista.portal.resident.activity.services.insurance.tenantsurepaymentmethod.TenantSurePaymentMethodUpdateWizardActivity;
import com.propertyvista.portal.rpc.portal.PortalSiteMap;
import com.propertyvista.portal.rpc.portal.PortalSiteMap.Account;
import com.propertyvista.portal.rpc.portal.PortalSiteMap.CommunityEvent;
import com.propertyvista.portal.rpc.portal.PortalSiteMap.Login;
import com.propertyvista.portal.rpc.portal.PortalSiteMap.LoginWithToken;
import com.propertyvista.portal.rpc.portal.PortalSiteMap.Logout;
import com.propertyvista.portal.rpc.portal.PortalSiteMap.NotificationPlace;
import com.propertyvista.portal.rpc.portal.PortalSiteMap.PasswordChange;
import com.propertyvista.portal.rpc.portal.PortalSiteMap.PasswordReset;
import com.propertyvista.portal.rpc.portal.PortalSiteMap.PasswordResetRequest;
import com.propertyvista.portal.rpc.portal.resident.ResidentPortalSiteMap;
import com.propertyvista.portal.rpc.portal.resident.ResidentPortalSiteMap.LeaseContextSelection;
import com.propertyvista.portal.rpc.portal.resident.ResidentPortalSiteMap.Registration;
import com.propertyvista.portal.rpc.portal.resident.ResidentPortalSiteMap.ResidentPortalTerms;
import com.propertyvista.portal.shared.activity.NotificationPageActivity;
import com.propertyvista.portal.shared.activity.account.AccountPageActivity;
import com.propertyvista.portal.shared.activity.communication.CommunicationPageActivity;
import com.propertyvista.portal.shared.activity.communication.CommunicationViewActivity;
import com.propertyvista.portal.shared.activity.communication.CommunicationWizardActivity;
import com.propertyvista.portal.shared.activity.communityevent.CommunityEventPageActivity;
import com.propertyvista.portal.shared.activity.login.LoginWithTokenActivity;
import com.propertyvista.portal.shared.activity.login.LogoutActivity;
import com.propertyvista.portal.shared.activity.login.PasswordResetRequestWizardActivity;
import com.propertyvista.portal.shared.activity.security.PasswordChangeActivity;
import com.propertyvista.portal.shared.activity.security.PasswordResetActivity;

public class ContentActivityMapper implements AppActivityMapper {

    public ContentActivityMapper() {
    }

    @Override
    public void obtainActivity(final AppPlace place, final AsyncCallback<Activity> callback) {
        GWT.runAsync(new RunAsyncCallback() {

            @Override
            public void onSuccess() {
                if (place instanceof AppPlace) {
                    AppPlace appPlace = place;

                    Activity activity = null;
                    if (appPlace instanceof ResidentPortalSiteMap.Dashboard) {
                        activity = new MainDashboardActivity(appPlace);
                    } else if (appPlace instanceof ResidentPortalSiteMap.Profile) {
                        activity = new ProfilePageActivity(appPlace);
                    } else if (appPlace instanceof Account) {
                        activity = new AccountPageActivity(appPlace);
                    } else if (appPlace instanceof CommunityEvent) {
                        activity = new CommunityEventPageActivity(appPlace);

// Financial
                    } else if (appPlace instanceof ResidentPortalSiteMap.Financial) {
                        activity = new FinancialDashboardActivity(appPlace);
                    } else if (appPlace instanceof ResidentPortalSiteMap.Financial.Payment.PayNow) {
                        activity = new PaymentWizardActivity(appPlace);
                    } else if (appPlace instanceof ResidentPortalSiteMap.Financial.Payment.PaymentSubmitting) {
                        activity = new PaymentConfirmationActivity(appPlace);

                    } else if (appPlace instanceof ResidentPortalSiteMap.Financial.PaymentMethods.PaymentMethod) {
                        activity = new PaymentMethodViewActivity(appPlace);
                    } else if (appPlace instanceof ResidentPortalSiteMap.Financial.PaymentMethods.NewPaymentMethod) {
                        activity = new PaymentMethodWizardActivity(appPlace);
                    } else if (appPlace instanceof ResidentPortalSiteMap.Financial.PaymentMethods.PaymentMethodSubmitted) {
                        activity = new PaymentMethodConfirmationActivity(appPlace);

                    } else if (appPlace instanceof ResidentPortalSiteMap.Financial.PreauthorizedPayments.AutoPay) {
                        activity = new AutoPayActivity(appPlace);
                    } else if (appPlace instanceof ResidentPortalSiteMap.Financial.PreauthorizedPayments.AutoPayWizard) {
                        activity = new AutoPayWizardActivity(appPlace);
                    } else if (appPlace instanceof ResidentPortalSiteMap.Financial.PreauthorizedPayments.AutoPayConfirmation) {
                        activity = new AutoPayConfirmationActivity(appPlace);

                    } else if (appPlace instanceof ResidentPortalSiteMap.Financial.BillingHistory) {
                        activity = new BillingHistoryViewActivity(appPlace);
                    } else if (appPlace instanceof ResidentPortalSiteMap.Financial.TransactionHistory) {
                        activity = new TransactionHistoryViewActivity(appPlace);

                    } else if (appPlace instanceof ResidentPortalSiteMap.Financial.BillingHistory.BillView) {
                        activity = new BillViewActivity(appPlace);

// Services
                    } else if (appPlace instanceof ResidentPortalSiteMap.ResidentServices) {
                        activity = new ServicesDashboardActivity(appPlace);
// Insurance
                    } else if (appPlace instanceof ResidentPortalSiteMap.ResidentServices.TenantInsurance.TenantSure.TenantSureWizard) {
                        activity = new TenantSureOrderWizardActivity(appPlace);
                    } else if (appPlace instanceof ResidentPortalSiteMap.ResidentServices.TenantInsurance.TenantSure.TenantSureWizardConfirmation) {
                        activity = new TenantSureOrderConfirmationPageActivity(appPlace);
                    } else if (appPlace instanceof ResidentPortalSiteMap.ResidentServices.TenantInsurance.TenantSure.TenantSurePage) {
                        activity = new TenantSurePageActivity(appPlace);
                    } else if (appPlace instanceof ResidentPortalSiteMap.ResidentServices.TenantInsurance.TenantSure.TenantSurePage.UpdateCreditCard) {
                        activity = new TenantSurePaymentMethodUpdateWizardActivity(appPlace);
                    } else if (appPlace instanceof ResidentPortalSiteMap.ResidentServices.TenantInsurance.TenantSure.TenantSurePage.UpdateCreditCardConfirmation) {
                        activity = new TenantSurePaymentMethodUpdateConfirmationActivity(appPlace);
                    } else if (appPlace instanceof ResidentPortalSiteMap.ResidentServices.TenantInsurance.TenantSure.TenantSurePage.Faq) {
                        activity = new TenantSureFaqActivity(appPlace);
                    } else if (appPlace instanceof ResidentPortalSiteMap.ResidentServices.TenantInsurance.TenantSure.TenantSurePage.About) {
                        activity = new TenantSureAboutActivity(appPlace);

                    } else if (appPlace instanceof ResidentPortalSiteMap.ResidentServices.TenantInsurance.GeneralPolicyWizard) {
                        activity = new GeneralPolicyUploadWizardActivity(appPlace);
                    } else if (appPlace instanceof ResidentPortalSiteMap.ResidentServices.TenantInsurance.GeneralPolicyPage) {
                        activity = new GeneralPolicyPageActivity(appPlace);
// Maintenance
                    } else if (place instanceof ResidentPortalSiteMap.Maintenance) {
                        activity = new MaintenanceDashboardActivity(appPlace);
                    } else if (place instanceof ResidentPortalSiteMap.Maintenance.MaintenanceRequestWizard) {
                        activity = new MaintenanceRequestWizardActivity(appPlace);
                    } else if (place instanceof ResidentPortalSiteMap.Maintenance.MaintenanceRequestPage) {
                        activity = new MaintenanceRequestPageActivity(appPlace);

// Move-in
                    } else if (place instanceof ResidentPortalSiteMap.MoveIn.MoveInWizard) {
                        activity = new MoveInWizardActivity(appPlace);

// LeaseSigning
                    } else if (place instanceof ResidentPortalSiteMap.LeaseSigning.LeaseSigningWizard) {
//                        activity = new LeaseSigningWizardActivity(appPlace);
                        activity = new LeaseSigningActivity(appPlace);
                    } else if (place instanceof ResidentPortalSiteMap.LeaseSigning.LeaseSigningWizardConfirmation) {
                        activity = new LeaseSigningConfirmationActivity(appPlace);
// Communication
                    } else if (place instanceof PortalSiteMap.Message.MessageWizard) {
                        activity = new CommunicationWizardActivity(appPlace);
                    } else if (place instanceof PortalSiteMap.Message.MessagePage) {
                        activity = new CommunicationPageActivity(appPlace);
                    } else if (place instanceof PortalSiteMap.Message.MessageView) {
                        activity = new CommunicationViewActivity();
// Internals
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
                    } else if (place instanceof ResidentPortalTerms.PreauthorizedPaymentECheckTerms
                            || place instanceof ResidentPortalTerms.TenantSurePreAuthorizedPaymentTerms
                            || place instanceof ResidentPortalTerms.PreauthorizedPaymentCardTerms

                            || place instanceof PortalSiteMap.PortalTerms.BillingTerms
                            || place instanceof PortalSiteMap.PortalTerms.WebPaymentFeeTerms
                            || place instanceof PortalSiteMap.PortalTerms.DirectBankingInstruction

                            || place instanceof PortalSiteMap.PortalTerms.VistaTermsAndConditions
                            || place instanceof PortalSiteMap.PortalTerms.VistaPrivacyPolicy

                            || place instanceof PortalSiteMap.PortalTerms.PmcTermsAndConditions
                            || place instanceof PortalSiteMap.PortalTerms.PmcPrivacyPolicy) {
                        activity = new PortalTermsActivity(place);
                 // @formatter:on

                    } else if (place instanceof ResidentPortalSiteMap.Offers) {
                        activity = new OffersDashboardActivity(place);
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
