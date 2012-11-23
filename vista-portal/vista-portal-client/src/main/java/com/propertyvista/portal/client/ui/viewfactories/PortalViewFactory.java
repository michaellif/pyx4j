/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 11, 2011
 * @author Dad
 * @version $Id$
 */
package com.propertyvista.portal.client.ui.viewfactories;

import com.google.gwt.user.client.ui.IsWidget;

import com.propertyvista.common.client.ui.components.login.LoginWithTokenView;
import com.propertyvista.common.client.ui.components.login.LoginWithTokenViewImpl;
import com.propertyvista.common.client.ui.components.login.PasswordResetRequestView;
import com.propertyvista.common.client.ui.components.login.PasswordResetRequestViewImpl;
import com.propertyvista.common.client.ui.components.security.PasswordChangeView;
import com.propertyvista.common.client.ui.components.security.PasswordChangeViewImpl;
import com.propertyvista.common.client.ui.components.security.PasswordResetView;
import com.propertyvista.common.client.ui.components.security.PasswordResetViewImpl;
import com.propertyvista.portal.client.ui.CaptionView;
import com.propertyvista.portal.client.ui.CaptionViewImpl;
import com.propertyvista.portal.client.ui.LeaseContextSelectionView;
import com.propertyvista.portal.client.ui.LeaseContextSelectionViewImpl;
import com.propertyvista.portal.client.ui.NavigView;
import com.propertyvista.portal.client.ui.NavigViewImpl;
import com.propertyvista.portal.client.ui.PotentialTenantView;
import com.propertyvista.portal.client.ui.PotentialTenantViewImpl;
import com.propertyvista.portal.client.ui.residents.billing.BillSummaryView;
import com.propertyvista.portal.client.ui.residents.billing.BillSummaryViewImpl;
import com.propertyvista.portal.client.ui.residents.billing.BillingHistoryView;
import com.propertyvista.portal.client.ui.residents.billing.BillingHistoryViewImpl;
import com.propertyvista.portal.client.ui.residents.billing.ViewBillView;
import com.propertyvista.portal.client.ui.residents.billing.ViewBillViewImpl;
import com.propertyvista.portal.client.ui.residents.dashboard.DashboardView;
import com.propertyvista.portal.client.ui.residents.dashboard.DashboardViewImpl;
import com.propertyvista.portal.client.ui.residents.maintenance.EditMaintenanceRequestView;
import com.propertyvista.portal.client.ui.residents.maintenance.EditMaintenanceRequestViewImpl;
import com.propertyvista.portal.client.ui.residents.maintenance.MaintenanceView;
import com.propertyvista.portal.client.ui.residents.maintenance.MaintenanceViewImpl;
import com.propertyvista.portal.client.ui.residents.payment.PaymentView;
import com.propertyvista.portal.client.ui.residents.payment.PaymentViewImpl;
import com.propertyvista.portal.client.ui.residents.paymentmethod.EditPaymentMethodView;
import com.propertyvista.portal.client.ui.residents.paymentmethod.EditPaymentMethodViewImpl;
import com.propertyvista.portal.client.ui.residents.paymentmethod.PaymentMethodsView;
import com.propertyvista.portal.client.ui.residents.paymentmethod.PaymentMethodsViewImpl;
import com.propertyvista.portal.client.ui.residents.personalinfo.PersonalInfoView;
import com.propertyvista.portal.client.ui.residents.personalinfo.PersonalInfoViewImpl;
import com.propertyvista.portal.client.ui.residents.tenantinsurance.otherprovider.views.TenantInsuranceByOtherProviderUpdateView;
import com.propertyvista.portal.client.ui.residents.tenantinsurance.otherprovider.views.TenantInsuranceByOtherProviderUpdateViewImpl;
import com.propertyvista.portal.client.ui.residents.tenantinsurance.tenantsure.views.TenantSureCreditCardUpdateView;
import com.propertyvista.portal.client.ui.residents.tenantinsurance.tenantsure.views.TenantSureCreditCardUpdateViewImpl;
import com.propertyvista.portal.client.ui.residents.tenantinsurance.tenantsure.views.TenantSureManagementView;
import com.propertyvista.portal.client.ui.residents.tenantinsurance.tenantsure.views.TenantSureManagementViewImpl;
import com.propertyvista.portal.client.ui.residents.tenantinsurance.tenantsure.views.TenantSurePurchaseView;
import com.propertyvista.portal.client.ui.residents.tenantinsurance.tenantsure.views.TenantSurePurchaseViewImpl;
import com.propertyvista.portal.client.ui.residents.tenantinsurance.views.ProvideTenantInsuranceView;
import com.propertyvista.portal.client.ui.residents.tenantinsurance.views.ProvideTenantInsuranceViewImpl;

public class PortalViewFactory extends ViewFactoryBase {

    public static <T extends IsWidget> T instance(Class<T> type) {
        if (!map.containsKey(type)) {
            if (DashboardView.class.equals(type)) {
                map.put(type, new DashboardViewImpl());
            } else if (PersonalInfoView.class.equals(type)) {
                map.put(type, new PersonalInfoViewImpl());
            } else if (PotentialTenantView.class.equals(type)) {
                map.put(type, new PotentialTenantViewImpl());

            } else if (BillSummaryView.class.equals(type)) {
                map.put(type, new BillSummaryViewImpl());
            } else if (BillingHistoryView.class.equals(type)) {
                map.put(type, new BillingHistoryViewImpl());
            } else if (ViewBillView.class.equals(type)) {
                map.put(type, new ViewBillViewImpl());

            } else if (PaymentMethodsView.class.equals(type)) {
                map.put(type, new PaymentMethodsViewImpl());
            } else if (EditPaymentMethodView.class.equals(type)) {
                map.put(type, new EditPaymentMethodViewImpl());
            } else if (PaymentView.class.equals(type)) {
                map.put(type, new PaymentViewImpl());

            } else if (MaintenanceView.class.equals(type)) {
                map.put(type, new MaintenanceViewImpl());
            } else if (EditMaintenanceRequestView.class.equals(type)) {
                map.put(type, new EditMaintenanceRequestViewImpl());

            } else if (NavigView.class.equals(type)) {
                map.put(type, new NavigViewImpl());
            } else if (CaptionView.class.equals(type)) {
                map.put(type, new CaptionViewImpl());
            } else if (PasswordResetRequestView.class.equals(type)) {
                map.put(type, new PasswordResetRequestViewImpl());
            } else if (PasswordResetView.class.equals(type)) {
                map.put(type, new PasswordResetViewImpl());
            } else if (LoginWithTokenView.class.equals(type)) {
                map.put(type, new LoginWithTokenViewImpl());
            } else if (PasswordChangeView.class.equals(type)) {
                map.put(type, new PasswordChangeViewImpl());
            } else if (LeaseContextSelectionView.class.equals(type)) {
                map.put(type, new LeaseContextSelectionViewImpl());
            } else if (TenantSureManagementView.class.equals(type)) {
                map.put(type, new TenantSureManagementViewImpl());
            } else if (TenantSurePurchaseView.class.equals(type)) {
                map.put(type, new TenantSurePurchaseViewImpl());
            } else if (TenantSureCreditCardUpdateView.class.equals(type)) {
                map.put(type, new TenantSureCreditCardUpdateViewImpl());
            } else if (TenantInsuranceByOtherProviderUpdateView.class.equals(type)) {
                map.put(type, new TenantInsuranceByOtherProviderUpdateViewImpl());
            } else if (ProvideTenantInsuranceView.class.equals(type)) {
                map.put(type, new ProvideTenantInsuranceViewImpl());
            }
        }

        @SuppressWarnings("unchecked")
        T impl = (T) map.get(type);
        if (impl == null) {
            throw new Error("implementation of " + type.getName() + " not found");
        }
        return impl;
    }
}
