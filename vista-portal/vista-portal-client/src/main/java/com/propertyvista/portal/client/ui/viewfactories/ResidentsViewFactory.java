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
import com.propertyvista.portal.client.ui.TopRightActionsView;
import com.propertyvista.portal.client.ui.TopRightActionsViewImpl;
import com.propertyvista.portal.client.ui.residents.billing.BillSummaryView;
import com.propertyvista.portal.client.ui.residents.billing.BillSummaryViewImpl;
import com.propertyvista.portal.client.ui.residents.billing.BillingHistoryView;
import com.propertyvista.portal.client.ui.residents.billing.BillingHistoryViewImpl;
import com.propertyvista.portal.client.ui.residents.billing.ViewBillView;
import com.propertyvista.portal.client.ui.residents.billing.ViewBillViewImpl;
import com.propertyvista.portal.client.ui.residents.dashboard.DashboardView;
import com.propertyvista.portal.client.ui.residents.dashboard.DashboardViewImpl;
import com.propertyvista.portal.client.ui.residents.financial.yardi.FinancialSummaryView;
import com.propertyvista.portal.client.ui.residents.financial.yardi.FinancialSummaryViewImpl;
import com.propertyvista.portal.client.ui.residents.maintenance.EditMaintenanceRequestView;
import com.propertyvista.portal.client.ui.residents.maintenance.EditMaintenanceRequestViewImpl;
import com.propertyvista.portal.client.ui.residents.maintenance.MaintenanceView;
import com.propertyvista.portal.client.ui.residents.maintenance.MaintenanceViewImpl;
import com.propertyvista.portal.client.ui.residents.maintenance.ViewMaintenanceRequestView;
import com.propertyvista.portal.client.ui.residents.maintenance.ViewMaintenanceRequestViewImpl;
import com.propertyvista.portal.client.ui.residents.payment.PaymentSubmittedView;
import com.propertyvista.portal.client.ui.residents.payment.PaymentSubmittedViewImpl;
import com.propertyvista.portal.client.ui.residents.payment.PaymentWizardView;
import com.propertyvista.portal.client.ui.residents.payment.PaymentWizardViewImpl;
import com.propertyvista.portal.client.ui.residents.payment.autopay.PreauthorizedPaymentSubmittedView;
import com.propertyvista.portal.client.ui.residents.payment.autopay.PreauthorizedPaymentSubmittedViewImpl;
import com.propertyvista.portal.client.ui.residents.payment.autopay.PreauthorizedPaymentWizardView;
import com.propertyvista.portal.client.ui.residents.payment.autopay.PreauthorizedPaymentWizardViewImpl;
import com.propertyvista.portal.client.ui.residents.payment.autopay.PreauthorizedPaymentsView;
import com.propertyvista.portal.client.ui.residents.payment.autopay.PreauthorizedPaymentsViewImpl;
import com.propertyvista.portal.client.ui.residents.paymentmethod.EditPaymentMethodView;
import com.propertyvista.portal.client.ui.residents.paymentmethod.EditPaymentMethodViewImpl;
import com.propertyvista.portal.client.ui.residents.paymentmethod.PaymentMethodSubmittedView;
import com.propertyvista.portal.client.ui.residents.paymentmethod.PaymentMethodSubmittedViewImpl;
import com.propertyvista.portal.client.ui.residents.paymentmethod.PaymentMethodWizardView;
import com.propertyvista.portal.client.ui.residents.paymentmethod.PaymentMethodWizardViewImpl;
import com.propertyvista.portal.client.ui.residents.paymentmethod.PaymentMethodsView;
import com.propertyvista.portal.client.ui.residents.paymentmethod.PaymentMethodsViewImpl;
import com.propertyvista.portal.client.ui.residents.paymentmethod.ViewPaymentMethodView;
import com.propertyvista.portal.client.ui.residents.paymentmethod.ViewPaymentMethodViewImpl;
import com.propertyvista.portal.client.ui.residents.personalinfo.PersonalInfoEdit;
import com.propertyvista.portal.client.ui.residents.personalinfo.PersonalInfoEditImpl;
import com.propertyvista.portal.client.ui.residents.personalinfo.PersonalInfoView;
import com.propertyvista.portal.client.ui.residents.personalinfo.PersonalInfoViewImpl;
import com.propertyvista.portal.client.ui.residents.registration.TenantRegistrationView;
import com.propertyvista.portal.client.ui.residents.registration.TenantRegistrationViewImpl;
import com.propertyvista.portal.client.ui.residents.tenantinsurance.otherprovider.views.TenantInsuranceByOtherProviderUpdateView;
import com.propertyvista.portal.client.ui.residents.tenantinsurance.otherprovider.views.TenantInsuranceByOtherProviderUpdateViewImpl;
import com.propertyvista.portal.client.ui.residents.tenantinsurance.tenantsure.views.TenantSureAboutView;
import com.propertyvista.portal.client.ui.residents.tenantinsurance.tenantsure.views.TenantSureAboutViewImpl;
import com.propertyvista.portal.client.ui.residents.tenantinsurance.tenantsure.views.TenantSureCreditCardUpdateView;
import com.propertyvista.portal.client.ui.residents.tenantinsurance.tenantsure.views.TenantSureCreditCardUpdateViewImpl;
import com.propertyvista.portal.client.ui.residents.tenantinsurance.tenantsure.views.TenantSureManagementView;
import com.propertyvista.portal.client.ui.residents.tenantinsurance.tenantsure.views.TenantSureManagementViewImpl;
import com.propertyvista.portal.client.ui.residents.tenantinsurance.tenantsure.views.TenantSurePurchaseView;
import com.propertyvista.portal.client.ui.residents.tenantinsurance.tenantsure.views.TenantSurePurchaseViewImpl;
import com.propertyvista.portal.client.ui.residents.tenantinsurance.tenantsure.views.TermsView;
import com.propertyvista.portal.client.ui.residents.tenantinsurance.tenantsure.views.TermsViewImpl;
import com.propertyvista.portal.client.ui.residents.tenantinsurance.views.ProvideTenantInsuranceView;
import com.propertyvista.portal.client.ui.residents.tenantinsurance.views.ProvideTenantInsuranceViewImpl;
import com.propertyvista.portal.client.ui.residents.tenantinsurance.views.TenantInsuranceCoveredByOtherTenantView;
import com.propertyvista.portal.client.ui.residents.tenantinsurance.views.TenantInsuranceCoveredByOtherTenantViewImpl;
import com.propertyvista.portal.client.ui.residents.usermessage.UserMessageView;
import com.propertyvista.portal.client.ui.residents.usermessage.UserMessageViewImpl;

public class ResidentsViewFactory extends ViewFactoryBase {

    public static <T extends IsWidget> T instance(Class<T> type) {
        if (!map.containsKey(type)) {
            if (TenantRegistrationView.class.equals(type)) {
                map.put(type, new TenantRegistrationViewImpl());
            } else if (DashboardView.class.equals(type)) {
                map.put(type, new DashboardViewImpl());
            } else if (PersonalInfoView.class.equals(type)) {
                map.put(type, new PersonalInfoViewImpl());
            } else if (PersonalInfoEdit.class.equals(type)) {
                map.put(type, new PersonalInfoEditImpl());

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

            } else if (NavigView.class.equals(type)) {
                map.put(type, new NavigViewImpl());
            } else if (TopRightActionsView.class.equals(type)) {
                map.put(type, new TopRightActionsViewImpl());
            } else if (CaptionView.class.equals(type)) {
                map.put(type, new CaptionViewImpl());
            } else if (PasswordResetRequestView.class.equals(type)) {
                map.put(type, new PasswordResetRequestViewImpl());
            } else if (PasswordResetView.class.equals(type)) {
                map.put(type, new PasswordResetViewImpl());
            } else if (PasswordChangeView.class.equals(type)) {
                map.put(type, new PasswordChangeViewImpl());
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

            } else if (UserMessageView.class.equals(type)) {
                map.put(type, new UserMessageViewImpl());
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
