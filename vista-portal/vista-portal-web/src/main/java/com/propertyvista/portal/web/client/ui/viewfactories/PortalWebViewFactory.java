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
package com.propertyvista.portal.web.client.ui.viewfactories;

import com.google.gwt.user.client.ui.IsWidget;

import com.propertyvista.common.client.ui.components.login.LoginWithTokenView;
import com.propertyvista.common.client.ui.components.login.LoginWithTokenViewImpl;
import com.propertyvista.common.client.ui.components.login.PasswordResetRequestView;
import com.propertyvista.common.client.ui.components.security.PasswordChangeView;
import com.propertyvista.common.client.ui.components.security.PasswordResetView;
import com.propertyvista.portal.web.client.ui.CommunicationView;
import com.propertyvista.portal.web.client.ui.CommunicationViewImpl;
import com.propertyvista.portal.web.client.ui.ExtraView;
import com.propertyvista.portal.web.client.ui.ExtraViewImpl;
import com.propertyvista.portal.web.client.ui.FooterView;
import com.propertyvista.portal.web.client.ui.FooterViewImpl;
import com.propertyvista.portal.web.client.ui.HeaderView;
import com.propertyvista.portal.web.client.ui.HeaderViewImpl;
import com.propertyvista.portal.web.client.ui.LeaseContextSelectionView;
import com.propertyvista.portal.web.client.ui.LeaseContextSelectionViewImpl;
import com.propertyvista.portal.web.client.ui.MenuView;
import com.propertyvista.portal.web.client.ui.MenuViewImpl;
import com.propertyvista.portal.web.client.ui.NotificationHeaderView;
import com.propertyvista.portal.web.client.ui.NotificationHeaderViewImpl;
import com.propertyvista.portal.web.client.ui.NotificationPageView;
import com.propertyvista.portal.web.client.ui.NotificationPageViewImpl;
import com.propertyvista.portal.web.client.ui.ToolbarView;
import com.propertyvista.portal.web.client.ui.ToolbarViewImpl;
import com.propertyvista.portal.web.client.ui.dashboard.DashboardView;
import com.propertyvista.portal.web.client.ui.dashboard.DashboardViewImpl;
import com.propertyvista.portal.web.client.ui.landing.PasswordResetRequestViewImpl;
import com.propertyvista.portal.web.client.ui.landing.PasswordResetViewImpl;
import com.propertyvista.portal.web.client.ui.profile.ProfileView;
import com.propertyvista.portal.web.client.ui.profile.ProfileViewImpl;
import com.propertyvista.portal.web.client.ui.residents.billing.BillSummaryView;
import com.propertyvista.portal.web.client.ui.residents.billing.BillSummaryViewImpl;
import com.propertyvista.portal.web.client.ui.residents.billing.BillingHistoryView;
import com.propertyvista.portal.web.client.ui.residents.billing.BillingHistoryViewImpl;
import com.propertyvista.portal.web.client.ui.residents.billing.ViewBillView;
import com.propertyvista.portal.web.client.ui.residents.billing.ViewBillViewImpl;
import com.propertyvista.portal.web.client.ui.residents.communicationcenter.CommunicationCenterView;
import com.propertyvista.portal.web.client.ui.residents.communicationcenter.CommunicationCenterViewImpl;
import com.propertyvista.portal.web.client.ui.residents.financial.yardi.FinancialSummaryView;
import com.propertyvista.portal.web.client.ui.residents.financial.yardi.FinancialSummaryViewImpl;
import com.propertyvista.portal.web.client.ui.residents.maintenance.EditMaintenanceRequestView;
import com.propertyvista.portal.web.client.ui.residents.maintenance.EditMaintenanceRequestViewImpl;
import com.propertyvista.portal.web.client.ui.residents.maintenance.MaintenanceView;
import com.propertyvista.portal.web.client.ui.residents.maintenance.MaintenanceViewImpl;
import com.propertyvista.portal.web.client.ui.residents.maintenance.ViewMaintenanceRequestView;
import com.propertyvista.portal.web.client.ui.residents.maintenance.ViewMaintenanceRequestViewImpl;
import com.propertyvista.portal.web.client.ui.residents.payment.PaymentSubmittedView;
import com.propertyvista.portal.web.client.ui.residents.payment.PaymentSubmittedViewImpl;
import com.propertyvista.portal.web.client.ui.residents.payment.PaymentWizardView;
import com.propertyvista.portal.web.client.ui.residents.payment.PaymentWizardViewImpl;
import com.propertyvista.portal.web.client.ui.residents.payment.autopay.PreauthorizedPaymentSubmittedView;
import com.propertyvista.portal.web.client.ui.residents.payment.autopay.PreauthorizedPaymentSubmittedViewImpl;
import com.propertyvista.portal.web.client.ui.residents.payment.autopay.PreauthorizedPaymentWizardView;
import com.propertyvista.portal.web.client.ui.residents.payment.autopay.PreauthorizedPaymentWizardViewImpl;
import com.propertyvista.portal.web.client.ui.residents.payment.autopay.PreauthorizedPaymentsView;
import com.propertyvista.portal.web.client.ui.residents.payment.autopay.PreauthorizedPaymentsViewImpl;
import com.propertyvista.portal.web.client.ui.residents.paymentmethod.EditPaymentMethodView;
import com.propertyvista.portal.web.client.ui.residents.paymentmethod.EditPaymentMethodViewImpl;
import com.propertyvista.portal.web.client.ui.residents.paymentmethod.PaymentMethodSubmittedView;
import com.propertyvista.portal.web.client.ui.residents.paymentmethod.PaymentMethodSubmittedViewImpl;
import com.propertyvista.portal.web.client.ui.residents.paymentmethod.PaymentMethodWizardView;
import com.propertyvista.portal.web.client.ui.residents.paymentmethod.PaymentMethodWizardViewImpl;
import com.propertyvista.portal.web.client.ui.residents.paymentmethod.PaymentMethodsView;
import com.propertyvista.portal.web.client.ui.residents.paymentmethod.PaymentMethodsViewImpl;
import com.propertyvista.portal.web.client.ui.residents.paymentmethod.ViewPaymentMethodView;
import com.propertyvista.portal.web.client.ui.residents.paymentmethod.ViewPaymentMethodViewImpl;
import com.propertyvista.portal.web.client.ui.residents.registration.TenantRegistrationView;
import com.propertyvista.portal.web.client.ui.residents.registration.TenantRegistrationViewImpl;
import com.propertyvista.portal.web.client.ui.residents.tenantinsurance.otherprovider.views.TenantInsuranceByOtherProviderUpdateView;
import com.propertyvista.portal.web.client.ui.residents.tenantinsurance.otherprovider.views.TenantInsuranceByOtherProviderUpdateViewImpl;
import com.propertyvista.portal.web.client.ui.residents.tenantinsurance.tenantsure.views.TenantSureAboutView;
import com.propertyvista.portal.web.client.ui.residents.tenantinsurance.tenantsure.views.TenantSureAboutViewImpl;
import com.propertyvista.portal.web.client.ui.residents.tenantinsurance.tenantsure.views.TenantSureCreditCardUpdateView;
import com.propertyvista.portal.web.client.ui.residents.tenantinsurance.tenantsure.views.TenantSureCreditCardUpdateViewImpl;
import com.propertyvista.portal.web.client.ui.residents.tenantinsurance.tenantsure.views.TenantSureManagementView;
import com.propertyvista.portal.web.client.ui.residents.tenantinsurance.tenantsure.views.TenantSureManagementViewImpl;
import com.propertyvista.portal.web.client.ui.residents.tenantinsurance.tenantsure.views.TenantSurePurchaseView;
import com.propertyvista.portal.web.client.ui.residents.tenantinsurance.tenantsure.views.TenantSurePurchaseViewImpl;
import com.propertyvista.portal.web.client.ui.residents.tenantinsurance.tenantsure.views.TermsView;
import com.propertyvista.portal.web.client.ui.residents.tenantinsurance.tenantsure.views.TermsViewImpl;
import com.propertyvista.portal.web.client.ui.residents.tenantinsurance.views.ProvideTenantInsuranceView;
import com.propertyvista.portal.web.client.ui.residents.tenantinsurance.views.ProvideTenantInsuranceViewImpl;
import com.propertyvista.portal.web.client.ui.residents.tenantinsurance.views.TenantInsuranceCoveredByOtherTenantView;
import com.propertyvista.portal.web.client.ui.residents.tenantinsurance.views.TenantInsuranceCoveredByOtherTenantViewImpl;
import com.propertyvista.portal.web.client.ui.security.PasswordChangeViewImpl;

public class PortalWebViewFactory extends ViewFactoryBase {

    public static <T extends IsWidget> T instance(Class<T> type) {
        if (!map.containsKey(type)) {
            if (TenantRegistrationView.class.equals(type)) {
                map.put(type, new TenantRegistrationViewImpl());
            } else if (DashboardView.class.equals(type)) {
                map.put(type, new DashboardViewImpl());
            }

            else if (ProfileView.class.equals(type)) {
                map.put(type, new ProfileViewImpl());

            } else if (BillSummaryView.class.equals(type)) {
                map.put(type, new BillSummaryViewImpl());
            } else if (BillingHistoryView.class.equals(type)) {
                map.put(type, new BillingHistoryViewImpl());
            } else if (ViewBillView.class.equals(type)) {
                map.put(type, new ViewBillViewImpl());
            } else if (FinancialSummaryView.class.equals(type)) {
                map.put(type, new FinancialSummaryViewImpl());

            } else if (PaymentWizardView.class.equals(type)) {
                map.put(type, new PaymentWizardViewImpl());
            } else if (PaymentSubmittedView.class.equals(type)) {
                map.put(type, new PaymentSubmittedViewImpl());

            } else if (PreauthorizedPaymentsView.class.equals(type)) {
                map.put(type, new PreauthorizedPaymentsViewImpl());
            } else if (PreauthorizedPaymentWizardView.class.equals(type)) {
                map.put(type, new PreauthorizedPaymentWizardViewImpl());
            } else if (PreauthorizedPaymentSubmittedView.class.equals(type)) {
                map.put(type, new PreauthorizedPaymentSubmittedViewImpl());

            } else if (PaymentMethodsView.class.equals(type)) {
                map.put(type, new PaymentMethodsViewImpl());
            } else if (ViewPaymentMethodView.class.equals(type)) {
                map.put(type, new ViewPaymentMethodViewImpl());
            } else if (EditPaymentMethodView.class.equals(type)) {
                map.put(type, new EditPaymentMethodViewImpl());
            } else if (PaymentMethodWizardView.class.equals(type)) {
                map.put(type, new PaymentMethodWizardViewImpl());
            } else if (PaymentMethodSubmittedView.class.equals(type)) {
                map.put(type, new PaymentMethodSubmittedViewImpl());

            } else if (MaintenanceView.class.equals(type)) {
                map.put(type, new MaintenanceViewImpl());
            } else if (ViewMaintenanceRequestView.class.equals(type)) {
                map.put(type, new ViewMaintenanceRequestViewImpl());
            } else if (EditMaintenanceRequestView.class.equals(type)) {
                map.put(type, new EditMaintenanceRequestViewImpl());

            } else if (CommunicationCenterView.class.equals(type)) {
                map.put(type, new CommunicationCenterViewImpl());

            } else if (HeaderView.class.equals(type)) {
                map.put(type, new HeaderViewImpl());
            } else if (FooterView.class.equals(type)) {
                map.put(type, new FooterViewImpl());
            } else if (MenuView.class.equals(type)) {
                map.put(type, new MenuViewImpl());
            } else if (CommunicationView.class.equals(type)) {
                map.put(type, new CommunicationViewImpl());
            } else if (ExtraView.class.equals(type)) {
                map.put(type, new ExtraViewImpl());
            } else if (NotificationHeaderView.class.equals(type)) {
                map.put(type, new NotificationHeaderViewImpl());
            } else if (ToolbarView.class.equals(type)) {
                map.put(type, new ToolbarViewImpl());

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
            } else if (TermsView.class.equals(type)) {
                map.put(type, new TermsViewImpl());
            } else if (TenantSureAboutView.class.equals(type)) {
                map.put(type, new TenantSureAboutViewImpl());
            } else if (TenantInsuranceCoveredByOtherTenantView.class.equals(type)) {
                map.put(type, new TenantInsuranceCoveredByOtherTenantViewImpl());

            } else if (NotificationPageView.class.equals(type)) {
                map.put(type, new NotificationPageViewImpl());
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
