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
import com.pyx4j.site.rpc.AppPlace;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.portal.client.activity.PotentialTenantActivity;
import com.propertyvista.portal.client.activity.residents.DashboardActivity;
import com.propertyvista.portal.client.activity.residents.billing.BillSummaryActivity;
import com.propertyvista.portal.client.activity.residents.billing.BillingHistoryActivity;
import com.propertyvista.portal.client.activity.residents.billing.ViewBillActivity;
import com.propertyvista.portal.client.activity.residents.financial.FinancialSummaryActivity;
import com.propertyvista.portal.client.activity.residents.maintenance.EditMaintenanceRequestActivity;
import com.propertyvista.portal.client.activity.residents.maintenance.MaintenanceAcitvity;
import com.propertyvista.portal.client.activity.residents.maintenance.NewMaintenanceRequestActivity;
import com.propertyvista.portal.client.activity.residents.maintenance.ViewMaintenanceRequestActivity;
import com.propertyvista.portal.client.activity.residents.payment.PaymentSubmittingActivity;
import com.propertyvista.portal.client.activity.residents.payment.PaymentWizardActivity;
import com.propertyvista.portal.client.activity.residents.payment.PreauthorizedPaymentSubmittedActivity;
import com.propertyvista.portal.client.activity.residents.payment.PreauthorizedPaymentWizardActivity;
import com.propertyvista.portal.client.activity.residents.payment.PreauthorizedPaymentsActivity;
import com.propertyvista.portal.client.activity.residents.paymentmethod.EditPaymentMethodActivity;
import com.propertyvista.portal.client.activity.residents.paymentmethod.PaymentMethodSubmittedActivity;
import com.propertyvista.portal.client.activity.residents.paymentmethod.PaymentMethodWizardActivity;
import com.propertyvista.portal.client.activity.residents.paymentmethod.PaymentMethodsActivity;
import com.propertyvista.portal.client.activity.residents.paymentmethod.ViewPaymentMethodActivity;
import com.propertyvista.portal.client.activity.residents.personalinfo.PersonalInfoEditActivity;
import com.propertyvista.portal.client.activity.residents.personalinfo.PersonalInfoViewActivity;
import com.propertyvista.portal.client.activity.tenantinsurance.ProvideTenantInsuranceActivity;
import com.propertyvista.portal.client.activity.tenantinsurance.TenantInsuranceActivity;
import com.propertyvista.portal.client.activity.tenantinsurance.otherprovider.TenantInsuranceByOtherProvdierUpdateActivity;
import com.propertyvista.portal.client.activity.tenantinsurance.tenantsure.TenantSureAboutActivity;
import com.propertyvista.portal.client.activity.tenantinsurance.tenantsure.TenantSureCreditCardUpdateActivity;
import com.propertyvista.portal.client.activity.tenantinsurance.tenantsure.TenantSureFaqActivity;
import com.propertyvista.portal.client.activity.tenantinsurance.tenantsure.TenantSureManagementActivity;
import com.propertyvista.portal.client.activity.tenantinsurance.tenantsure.TenantSurePurchaseActivity;
import com.propertyvista.portal.rpc.portal.PortalSiteMap;
import com.propertyvista.portal.rpc.portal.PortalSiteMap.Resident;
import com.propertyvista.portal.rpc.portal.PortalSiteMap.Resident.Financial;

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
                        activity = new DashboardActivity(appPlace);
                    } else if (appPlace instanceof Resident.ProfileViewer) {
                        activity = new PersonalInfoViewActivity(appPlace);
                    } else if (appPlace instanceof Resident.ProfileEditor) {
                        activity = new PersonalInfoEditActivity(appPlace);
                    } else if (appPlace instanceof PortalSiteMap.PotentialTenants) {
                        activity = new PotentialTenantActivity(appPlace);

                    } else if (appPlace instanceof Financial.PaymentMethods) {
                        activity = new PaymentMethodsActivity(appPlace);
                    } else if (appPlace instanceof Financial.PaymentMethods.ViewPaymentMethod) {
                        activity = new ViewPaymentMethodActivity(appPlace);
                    } else if (appPlace instanceof Financial.PaymentMethods.EditPaymentMethod) {
                        activity = new EditPaymentMethodActivity(appPlace);
                    } else if (appPlace instanceof Financial.PaymentMethods.NewPaymentMethod) {
                        activity = new PaymentMethodWizardActivity((CrudAppPlace) appPlace);
                    } else if (appPlace instanceof Financial.PaymentMethods.PaymentMethodSubmitted) {
                        activity = new PaymentMethodSubmittedActivity(appPlace);

                    } else if (appPlace instanceof Resident.Financial.BillSummary) {
                        activity = new BillSummaryActivity(appPlace);
                    } else if (appPlace instanceof Resident.Financial.BillingHistory) {
                        activity = new BillingHistoryActivity(appPlace);
                    } else if (appPlace instanceof Resident.Financial.BillingHistory.ViewBill) {
                        activity = new ViewBillActivity(appPlace);
                    } else if (appPlace instanceof Resident.Financial.FinancialSummary) {
                        activity = new FinancialSummaryActivity(appPlace);

                    } else if (appPlace instanceof Resident.Financial.PayNow) {
                        activity = new PaymentWizardActivity((CrudAppPlace) appPlace);
                    } else if (appPlace instanceof Resident.Financial.PaymentSubmitting) {
                        activity = new PaymentSubmittingActivity(appPlace);

                    } else if (appPlace instanceof Resident.Financial.PreauthorizedPayments) {
                        activity = new PreauthorizedPaymentsActivity(appPlace);
                    } else if (appPlace instanceof Resident.Financial.PreauthorizedPayments.NewPreauthorizedPayment) {
                        activity = new PreauthorizedPaymentWizardActivity((CrudAppPlace) appPlace);
                    } else if (appPlace instanceof Resident.Financial.PreauthorizedPayments.PreauthorizedPaymentSubmitted) {
                        activity = new PreauthorizedPaymentSubmittedActivity(appPlace);

                    } else if (appPlace instanceof Resident.Maintenance) {
                        activity = new MaintenanceAcitvity(appPlace);
                    } else if (appPlace instanceof Resident.Maintenance.MaintenanceRequestWizard) {
                        activity = new NewMaintenanceRequestActivity(appPlace);
                    } else if (appPlace instanceof Resident.Maintenance.ViewMaintenanceRequest) {
                        activity = new ViewMaintenanceRequestActivity(appPlace);
                    } else if (appPlace instanceof Resident.Maintenance.EditMaintenanceRequest) {
                        activity = new EditMaintenanceRequestActivity(appPlace);

                    } else if (appPlace instanceof Resident.ResidentServices.TenantInsurance) {
                        activity = new TenantInsuranceActivity();
                    } else if (appPlace instanceof Resident.ResidentServices.TenantInsurance.ProvideTenantInsurance) {
                        activity = new ProvideTenantInsuranceActivity();

                    } else if (appPlace instanceof Resident.ResidentServices.TenantInsurance.TenantSure.TenantSureOrderWizard) {
                        activity = new TenantSurePurchaseActivity();
                    } else if (appPlace instanceof Resident.ResidentServices.TenantInsurance.TenantSure.TenantSurePage) {
                        activity = new TenantSureManagementActivity();
                    } else if (appPlace instanceof Resident.ResidentServices.TenantInsurance.TenantSure.Faq) {
                        activity = new TenantSureFaqActivity();
                    } else if (appPlace instanceof Resident.ResidentServices.TenantInsurance.TenantSure.About) {
                        activity = new TenantSureAboutActivity();
                    } else if (appPlace instanceof Resident.ResidentServices.TenantInsurance.TenantSure.TenantSurePage.UpdateCreditCard) {
                        activity = new TenantSureCreditCardUpdateActivity();

                    } else if (appPlace instanceof Resident.ResidentServices.TenantInsurance.GeneralCertificateUploadWizard) {
                        activity = new TenantInsuranceByOtherProvdierUpdateActivity(appPlace);
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
