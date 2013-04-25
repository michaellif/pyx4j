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

import com.propertyvista.portal.client.activity.PotentialTenantActivity;
import com.propertyvista.portal.client.activity.residents.DashboardActivity;
import com.propertyvista.portal.client.activity.residents.billing.BillSummaryActivity;
import com.propertyvista.portal.client.activity.residents.billing.BillingHistoryActivity;
import com.propertyvista.portal.client.activity.residents.billing.ViewBillActivity;
import com.propertyvista.portal.client.activity.residents.communicationcenter.CommunicationCenterActivity;
import com.propertyvista.portal.client.activity.residents.financial.FinancialSummaryActivity;
import com.propertyvista.portal.client.activity.residents.maintenance.EditMaintenanceRequestActivity;
import com.propertyvista.portal.client.activity.residents.maintenance.MaintenanceAcitvity;
import com.propertyvista.portal.client.activity.residents.maintenance.NewMaintenanceRequestActivity;
import com.propertyvista.portal.client.activity.residents.maintenance.ViewMaintenanceRequestActivity;
import com.propertyvista.portal.client.activity.residents.payment.PaymentSubmittedActivity;
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
import com.propertyvista.portal.client.activity.residents.yardimaintenance.EditYardiMaintenanceRequestActivity;
import com.propertyvista.portal.client.activity.residents.yardimaintenance.NewYardiMaintenanceRequestActivity;
import com.propertyvista.portal.client.activity.residents.yardimaintenance.ViewYardiMaintenanceRequestActivity;
import com.propertyvista.portal.client.activity.residents.yardimaintenance.YardiMaintenanceActivity;
import com.propertyvista.portal.client.activity.tenantinsurance.ProvideTenantInsuranceActivity;
import com.propertyvista.portal.client.activity.tenantinsurance.TenantInsuranceActivity;
import com.propertyvista.portal.client.activity.tenantinsurance.otherprovider.TenantInsuranceByOtherProvdierUpdateActivity;
import com.propertyvista.portal.client.activity.tenantinsurance.tenantsure.TenantSureAboutActivity;
import com.propertyvista.portal.client.activity.tenantinsurance.tenantsure.TenantSureCreditCardUpdateActivity;
import com.propertyvista.portal.client.activity.tenantinsurance.tenantsure.TenantSureFaqActivity;
import com.propertyvista.portal.client.activity.tenantinsurance.tenantsure.TenantSureManagementActivity;
import com.propertyvista.portal.client.activity.tenantinsurance.tenantsure.TenantSurePurchaseActivity;
import com.propertyvista.portal.rpc.portal.PortalSiteMap;
import com.propertyvista.portal.rpc.portal.PortalSiteMap.Residents;
import com.propertyvista.shared.config.VistaFeatures;

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
                    if (appPlace instanceof Residents) {
                        activity = new DashboardActivity(appPlace);
                    } else if (appPlace instanceof Residents.ViewPersonalInformation) {
                        activity = new PersonalInfoViewActivity(appPlace);
                    } else if (appPlace instanceof Residents.EditPersonalInformation) {
                        activity = new PersonalInfoEditActivity(appPlace);
                    } else if (appPlace instanceof PortalSiteMap.PotentialTenants) {
                        activity = new PotentialTenantActivity(appPlace);

                    } else if (appPlace instanceof Residents.PaymentMethods) {
                        activity = new PaymentMethodsActivity(appPlace);
                    } else if (appPlace instanceof Residents.PaymentMethods.ViewPaymentMethod) {
                        activity = new ViewPaymentMethodActivity(appPlace);
                    } else if (appPlace instanceof Residents.PaymentMethods.EditPaymentMethod) {
                        activity = new EditPaymentMethodActivity(appPlace);
                    } else if (appPlace instanceof Residents.PaymentMethods.NewPaymentMethod) {
                        activity = new PaymentMethodWizardActivity(appPlace);
                    } else if (appPlace instanceof Residents.PaymentMethods.PaymentMethodSubmitted) {
                        activity = new PaymentMethodSubmittedActivity(appPlace);

                    } else if (appPlace instanceof Residents.Financial.BillSummary) {
                        activity = new BillSummaryActivity(appPlace);
                    } else if (appPlace instanceof Residents.Financial.BillingHistory) {
                        activity = new BillingHistoryActivity(appPlace);
                    } else if (appPlace instanceof Residents.Financial.BillingHistory.ViewBill) {
                        activity = new ViewBillActivity(appPlace);
                    } else if (appPlace instanceof Residents.Financial.FinancialSummary) {
                        activity = new FinancialSummaryActivity(appPlace);

                    } else if (appPlace instanceof Residents.Financial.PayNow) {
                        activity = new PaymentWizardActivity(appPlace);
                    } else if (appPlace instanceof Residents.Financial.PaymentSubmitted) {
                        activity = new PaymentSubmittedActivity(appPlace);

                    } else if (appPlace instanceof Residents.Financial.PreauthorizedPayments) {
                        activity = new PreauthorizedPaymentsActivity(appPlace);
                    } else if (appPlace instanceof Residents.Financial.PreauthorizedPayments.NewPreauthorizedPayment) {
                        activity = new PreauthorizedPaymentWizardActivity(appPlace);
                    } else if (appPlace instanceof Residents.Financial.PreauthorizedPayments.PreauthorizedPaymentSubmitted) {
                        activity = new PreauthorizedPaymentSubmittedActivity(appPlace);

                    } else if (appPlace instanceof Residents.Maintenance) {
                        activity = VistaFeatures.instance().yardiIntegration() ? new YardiMaintenanceActivity(appPlace) : new MaintenanceAcitvity(appPlace);
                    } else if (appPlace instanceof Residents.Maintenance.NewMaintenanceRequest) {
                        activity = VistaFeatures.instance().yardiIntegration() ? new NewYardiMaintenanceRequestActivity(appPlace)
                                : new NewMaintenanceRequestActivity(appPlace);
                    } else if (appPlace instanceof Residents.Maintenance.ViewMaintenanceRequest) {
                        activity = VistaFeatures.instance().yardiIntegration() ? new ViewYardiMaintenanceRequestActivity(appPlace)
                                : new ViewMaintenanceRequestActivity(appPlace);
                    } else if (appPlace instanceof Residents.Maintenance.EditMaintenanceRequest) {
                        activity = VistaFeatures.instance().yardiIntegration() ? new EditYardiMaintenanceRequestActivity(appPlace)
                                : new EditMaintenanceRequestActivity(appPlace);

                    } else if (appPlace instanceof Residents.TenantInsurance) {
                        activity = new TenantInsuranceActivity();
                    } else if (appPlace instanceof Residents.TenantInsurance.ProvideTenantInsurance) {
                        activity = new ProvideTenantInsuranceActivity();

                    } else if (appPlace instanceof Residents.TenantInsurance.TenantSure.TenantSurePurchase) {
                        activity = new TenantSurePurchaseActivity();
                    } else if (appPlace instanceof Residents.TenantInsurance.TenantSure.Management) {
                        activity = new TenantSureManagementActivity();
                    } else if (appPlace instanceof Residents.TenantInsurance.TenantSure.Faq) {
                        activity = new TenantSureFaqActivity();
                    } else if (appPlace instanceof Residents.TenantInsurance.TenantSure.About) {
                        activity = new TenantSureAboutActivity();
                    } else if (appPlace instanceof Residents.TenantInsurance.TenantSure.Management.UpdateCreditCard) {
                        activity = new TenantSureCreditCardUpdateActivity();

                    } else if (appPlace instanceof Residents.TenantInsurance.Other.UploadCertificate) {
                        activity = new TenantInsuranceByOtherProvdierUpdateActivity(appPlace);

                        // TODO not sure if these activities belong here
                    } else if (appPlace instanceof Residents.CommunicationCenter) {
                        activity = new CommunicationCenterActivity(appPlace);
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
