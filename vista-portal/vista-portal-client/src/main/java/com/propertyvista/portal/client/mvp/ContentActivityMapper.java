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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.site.client.activity.AppActivityMapper;

import com.propertyvista.portal.client.activity.PotentialTenantActivity;
import com.propertyvista.portal.client.activity.login.LeaseContextSelectionActivity;
import com.propertyvista.portal.client.activity.login.LoginWithTokenActivity;
import com.propertyvista.portal.client.activity.login.PasswordResetRequestActivity;
import com.propertyvista.portal.client.activity.login.RedirectToLoginPageActivity;
import com.propertyvista.portal.client.activity.residents.DashboardActivity;
import com.propertyvista.portal.client.activity.residents.PaymentActivity;
import com.propertyvista.portal.client.activity.residents.PersonalInfoActivity;
import com.propertyvista.portal.client.activity.residents.billing.BillSummaryActivity;
import com.propertyvista.portal.client.activity.residents.billing.BillingHistoryActivity;
import com.propertyvista.portal.client.activity.residents.billing.ViewBillActivity;
import com.propertyvista.portal.client.activity.residents.communicationcenter.CommunicationCenterActivity;
import com.propertyvista.portal.client.activity.residents.maintenance.EditMaintenanceRequestActivity;
import com.propertyvista.portal.client.activity.residents.maintenance.MaintenanceAcitvity;
import com.propertyvista.portal.client.activity.residents.maintenance.NewMaintenanceRequestActivity;
import com.propertyvista.portal.client.activity.residents.paymentmethod.EditPaymentMethodActivity;
import com.propertyvista.portal.client.activity.residents.paymentmethod.NewPaymentMethodActivity;
import com.propertyvista.portal.client.activity.residents.paymentmethod.PaymentMethodsActivity;
import com.propertyvista.portal.client.activity.security.PasswordResetActivity;
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

    private static Logger log = LoggerFactory.getLogger(ContentActivityMapper.class);

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

                } else if (place instanceof Residents.PaymentMethods) {
                    activity = new PaymentMethodsActivity(place);
                } else if (place instanceof Residents.PaymentMethods.NewPaymentMethod) {
                    activity = new NewPaymentMethodActivity(place);
                } else if (place instanceof Residents.PaymentMethods.EditPaymentMethod) {
                    activity = new EditPaymentMethodActivity(place);

                } else if (place instanceof Residents.Maintenance) {
                    activity = new MaintenanceAcitvity(place);
                } else if (place instanceof Residents.Maintenance.NewMaintenanceRequest) {
                    activity = new NewMaintenanceRequestActivity(place);
                } else if (place instanceof Residents.Maintenance.EditMaintenanceRequest) {
                    activity = new EditMaintenanceRequestActivity(place);

                } else if (place instanceof Residents.BillSummary) {
                    activity = new BillSummaryActivity(place);
                } else if (place instanceof Residents.BillSummary.PayNow) {
                    activity = new PaymentActivity(place);

                } else if (place instanceof Residents.BillingHistory) {
                    activity = new BillingHistoryActivity(place);
                } else if (place instanceof Residents.BillingHistory.ViewBill) {
                    activity = new ViewBillActivity(place);

                } else if (place instanceof PortalSiteMap.Residents.TenantInsurance) {
                    activity = new TenantInsuranceActivity();
                } else if (place instanceof PortalSiteMap.Residents.TenantInsurance.ProvideTenantInsurance) {
                    activity = new ProvideTenantInsuranceActivity();

                } else if (place instanceof PortalSiteMap.Residents.TenantInsurance.TenantSure.TenantSurePurchase) {
                    activity = new TenantSurePurchaseActivity();
                } else if (place instanceof PortalSiteMap.Residents.TenantInsurance.TenantSure.Management) {
                    activity = new TenantSureManagementActivity();
                } else if (place instanceof PortalSiteMap.Residents.TenantInsurance.TenantSure.Faq) {
                    activity = new TenantSureFaqActivity();
                } else if (place instanceof PortalSiteMap.Residents.TenantInsurance.TenantSure.About) {
                    activity = new TenantSureAboutActivity();
                } else if (place instanceof PortalSiteMap.Residents.TenantInsurance.TenantSure.Management.UpdateCreditCard) {
                    activity = new TenantSureCreditCardUpdateActivity();

                } else if (place instanceof PortalSiteMap.Residents.TenantInsurance.Other.UploadCertificate) {
                    activity = new TenantInsuranceByOtherProvdierUpdateActivity(place);

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
                } else if (place instanceof PortalSiteMap.Residents.CommunicationCenter) {
                    activity = new CommunicationCenterActivity(place);
                } else {
                    log.debug("Couldn't find the place class, which is :{} please add to mapping!", place.getClass().getName());
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
