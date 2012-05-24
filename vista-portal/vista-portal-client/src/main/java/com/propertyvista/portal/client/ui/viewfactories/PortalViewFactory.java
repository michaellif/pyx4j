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
import com.propertyvista.misc.VistaTODO;
import com.propertyvista.portal.client.ui.CaptionView;
import com.propertyvista.portal.client.ui.CaptionViewImpl;
import com.propertyvista.portal.client.ui.LeaseContextSelectionView;
import com.propertyvista.portal.client.ui.LeaseContextSelectionViewImpl;
import com.propertyvista.portal.client.ui.NavigView;
import com.propertyvista.portal.client.ui.NavigViewImpl;
import com.propertyvista.portal.client.ui.PotentialTenantView;
import com.propertyvista.portal.client.ui.PotentialTenantViewImpl;
import com.propertyvista.portal.client.ui.residents.billing.BillingHistoryView;
import com.propertyvista.portal.client.ui.residents.billing.BillingHistoryViewImpl;
import com.propertyvista.portal.client.ui.residents.billing.CurrentBillView;
import com.propertyvista.portal.client.ui.residents.billing.CurrentBillViewImpl;
import com.propertyvista.portal.client.ui.residents.dashboard.DashboardView;
import com.propertyvista.portal.client.ui.residents.dashboard.DashboardViewImpl;
import com.propertyvista.portal.client.ui.residents.insurancemockup.InsuranceView;
import com.propertyvista.portal.client.ui.residents.insurancemockup.InsuranceViewImpl;
import com.propertyvista.portal.client.ui.residents.maintenance.EditMaintenanceRequestView;
import com.propertyvista.portal.client.ui.residents.maintenance.EditMaintenanceRequestViewImpl;
import com.propertyvista.portal.client.ui.residents.maintenance.MaintenanceView;
import com.propertyvista.portal.client.ui.residents.maintenance.MaintenanceViewImpl;
import com.propertyvista.portal.client.ui.residents.maintenance.NewMaintenanceRequestView;
import com.propertyvista.portal.client.ui.residents.maintenance.NewMaintenanceRequestViewImpl;
import com.propertyvista.portal.client.ui.residents.paymentmethod.EditPaymentMethodView;
import com.propertyvista.portal.client.ui.residents.paymentmethod.EditPaymentMethodViewImpl;
import com.propertyvista.portal.client.ui.residents.paymentmethod.NewPaymentMethodView;
import com.propertyvista.portal.client.ui.residents.paymentmethod.NewPaymentMethodViewImpl;
import com.propertyvista.portal.client.ui.residents.paymentmethod.PaymentMethodsView;
import com.propertyvista.portal.client.ui.residents.paymentmethod.PaymentMethodsViewImpl;
import com.propertyvista.portal.client.ui.residents.personalinfo.PersonalInfoView;
import com.propertyvista.portal.client.ui.residents.personalinfo.PersonalInfoViewImpl;

public class PortalViewFactory extends ViewFactoryBase {

    public static <T extends IsWidget> T instance(Class<T> type) {
        if (!map.containsKey(type)) {
            if (DashboardView.class.equals(type)) {
                map.put(type, new DashboardViewImpl());
            } else if (PersonalInfoView.class.equals(type)) {
                map.put(type, new PersonalInfoViewImpl());
            } else if (PotentialTenantView.class.equals(type)) {
                map.put(type, new PotentialTenantViewImpl());
            } else if (BillingHistoryView.class.equals(type)) {
                map.put(type, new BillingHistoryViewImpl());
            } else if (CurrentBillView.class.equals(type)) {
                map.put(type, new CurrentBillViewImpl());

            } else if (PaymentMethodsView.class.equals(type)) {
                map.put(type, new PaymentMethodsViewImpl());
            } else if (NewPaymentMethodView.class.equals(type)) {
                map.put(type, new NewPaymentMethodViewImpl());
            } else if (EditPaymentMethodView.class.equals(type)) {
                map.put(type, new EditPaymentMethodViewImpl());

            } else if (MaintenanceView.class.equals(type)) {
                map.put(type, new MaintenanceViewImpl());
            } else if (NewMaintenanceRequestView.class.equals(type)) {
                map.put(type, new NewMaintenanceRequestViewImpl());
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
            } else if (VistaTODO.enableWelcomeWizardDemoMode & InsuranceView.class.equals(type)) {
                map.put(type, new InsuranceViewImpl());
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
