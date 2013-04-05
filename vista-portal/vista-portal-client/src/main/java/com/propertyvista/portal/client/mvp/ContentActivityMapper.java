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
import com.propertyvista.portal.client.activity.residents.PaymentWizardActivity;
import com.propertyvista.portal.client.activity.residents.PersonalInfoActivity;
import com.propertyvista.portal.client.activity.residents.billing.BillSummaryActivity;
import com.propertyvista.portal.client.activity.residents.billing.BillingHistoryActivity;
import com.propertyvista.portal.client.activity.residents.billing.ViewBillActivity;
import com.propertyvista.portal.client.activity.residents.communicationcenter.CommunicationCenterActivity;
import com.propertyvista.portal.client.activity.residents.financial.FinancialSummaryActivity;
import com.propertyvista.portal.client.activity.residents.maintenance.EditMaintenanceRequestActivity;
import com.propertyvista.portal.client.activity.residents.maintenance.MaintenanceAcitvity;
import com.propertyvista.portal.client.activity.residents.maintenance.NewMaintenanceRequestActivity;
import com.propertyvista.portal.client.activity.residents.paymentmethod.EditPaymentMethodActivity;
import com.propertyvista.portal.client.activity.residents.paymentmethod.NewPaymentMethodActivity;
import com.propertyvista.portal.client.activity.residents.paymentmethod.PaymentMethodsActivity;
import com.propertyvista.portal.client.activity.residents.yardimaintenance.EditYardiMaintenanceRequestActivity;
import com.propertyvista.portal.client.activity.residents.yardimaintenance.NewYardiMaintenanceRequestActivity;
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
                    } else if (appPlace instanceof Residents.PersonalInformation) {
                        activity = new PersonalInfoActivity(appPlace);
                    } else if (appPlace instanceof PortalSiteMap.PotentialTenants) {
                        activity = new PotentialTenantActivity(appPlace);

                    } else if (appPlace instanceof Residents.PaymentMethods) {
                        activity = new PaymentMethodsActivity(appPlace);
                    } else if (appPlace instanceof Residents.PaymentMethods.NewPaymentMethod) {
                        activity = new NewPaymentMethodActivity(appPlace);
                    } else if (appPlace instanceof Residents.PaymentMethods.EditPaymentMethod) {
                        activity = new EditPaymentMethodActivity(appPlace);

                    } else if (appPlace instanceof Residents.Maintenance) {
                        activity = new MaintenanceAcitvity(appPlace);
                    } else if (appPlace instanceof Residents.Maintenance.NewMaintenanceRequest) {
                        activity = new NewMaintenanceRequestActivity(appPlace);
                    } else if (appPlace instanceof Residents.Maintenance.EditMaintenanceRequest) {
                        activity = new EditMaintenanceRequestActivity(appPlace);

                    } else if (appPlace instanceof Residents.YardiMaintenance) {
                        activity = new YardiMaintenanceActivity(appPlace);
                    } else if (appPlace instanceof Residents.YardiMaintenance.NewYardiMaintenanceRequest) {
                        activity = new NewYardiMaintenanceRequestActivity(appPlace);
                    } else if (appPlace instanceof Residents.YardiMaintenance.EditYardiMaintenanceRequest) {
                        activity = new EditYardiMaintenanceRequestActivity(appPlace);

                    } else if (appPlace instanceof Residents.Financial.BillSummary) {
                        activity = new BillSummaryActivity(appPlace);
                    } else if (appPlace instanceof Residents.Financial.BillSummary.PayNow) {
                        activity = new PaymentWizardActivity(appPlace);
//                        activity = new PaymentActivity(appPlace);

                    } else if (appPlace instanceof Residents.Financial.BillingHistory) {
                        activity = new BillingHistoryActivity(appPlace);
                    } else if (appPlace instanceof Residents.Financial.BillingHistory.ViewBill) {
                        activity = new ViewBillActivity(appPlace);

                    } else if (appPlace instanceof Residents.Financial.FinancialSummary) {
                        activity = new FinancialSummaryActivity(appPlace);

                    } else if (appPlace instanceof PortalSiteMap.Residents.TenantInsurance) {
                        activity = new TenantInsuranceActivity();
                    } else if (appPlace instanceof PortalSiteMap.Residents.TenantInsurance.ProvideTenantInsurance) {
                        activity = new ProvideTenantInsuranceActivity();

                    } else if (appPlace instanceof PortalSiteMap.Residents.TenantInsurance.TenantSure.TenantSurePurchase) {
                        activity = new TenantSurePurchaseActivity();
                    } else if (appPlace instanceof PortalSiteMap.Residents.TenantInsurance.TenantSure.Management) {
                        activity = new TenantSureManagementActivity();
                    } else if (appPlace instanceof PortalSiteMap.Residents.TenantInsurance.TenantSure.Faq) {
                        activity = new TenantSureFaqActivity();
                    } else if (appPlace instanceof PortalSiteMap.Residents.TenantInsurance.TenantSure.About) {
                        activity = new TenantSureAboutActivity();
                    } else if (appPlace instanceof PortalSiteMap.Residents.TenantInsurance.TenantSure.Management.UpdateCreditCard) {
                        activity = new TenantSureCreditCardUpdateActivity();

                    } else if (appPlace instanceof PortalSiteMap.Residents.TenantInsurance.Other.UploadCertificate) {
                        activity = new TenantInsuranceByOtherProvdierUpdateActivity(appPlace);

                        // TODO not sure if these activities belong here
                    } else if (appPlace instanceof PortalSiteMap.Residents.CommunicationCenter) {
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
