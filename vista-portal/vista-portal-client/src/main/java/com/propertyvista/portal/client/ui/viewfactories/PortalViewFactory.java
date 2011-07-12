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

import com.propertyvista.portal.client.ui.FooterView;
import com.propertyvista.portal.client.ui.FooterViewImpl;
import com.propertyvista.portal.client.ui.LoginInvitationView;
import com.propertyvista.portal.client.ui.LoginInvitationViewImpl;
import com.propertyvista.portal.client.ui.LoginView;
import com.propertyvista.portal.client.ui.LoginViewImpl;
import com.propertyvista.portal.client.ui.LogoView;
import com.propertyvista.portal.client.ui.LogoViewImpl;
import com.propertyvista.portal.client.ui.MainNavigView;
import com.propertyvista.portal.client.ui.MainNavigViewImpl;
import com.propertyvista.portal.client.ui.PotentialTenantView;
import com.propertyvista.portal.client.ui.PotentialTenantViewImpl;
import com.propertyvista.portal.client.ui.RetrievePasswordView;
import com.propertyvista.portal.client.ui.RetrievePasswordViewImpl;
import com.propertyvista.portal.client.ui.StaticPageView;
import com.propertyvista.portal.client.ui.StaticPageViewImpl;
import com.propertyvista.portal.client.ui.TopRightActionsView;
import com.propertyvista.portal.client.ui.TopRightActionsViewImpl;
import com.propertyvista.portal.client.ui.residents.BillingHistoryView;
import com.propertyvista.portal.client.ui.residents.BillingHistoryViewImpl;
import com.propertyvista.portal.client.ui.residents.CreateAccountView;
import com.propertyvista.portal.client.ui.residents.CreateAccountViewImpl;
import com.propertyvista.portal.client.ui.residents.CurrentBillView;
import com.propertyvista.portal.client.ui.residents.CurrentBillViewImpl;
import com.propertyvista.portal.client.ui.residents.EditPaymentMethodView;
import com.propertyvista.portal.client.ui.residents.EditPaymentMethodViewImpl;
import com.propertyvista.portal.client.ui.residents.MaintenanceListerView;
import com.propertyvista.portal.client.ui.residents.MaintenanceListerViewImpl;
import com.propertyvista.portal.client.ui.residents.MaintenanceView;
import com.propertyvista.portal.client.ui.residents.MaintenanceViewImpl;
import com.propertyvista.portal.client.ui.residents.NewPaymentMethodView;
import com.propertyvista.portal.client.ui.residents.NewPaymentMethodViewImpl;
import com.propertyvista.portal.client.ui.residents.PaymentMethodsView;
import com.propertyvista.portal.client.ui.residents.PaymentMethodsViewImpl;
import com.propertyvista.portal.client.ui.residents.PersonalInfoView;
import com.propertyvista.portal.client.ui.residents.PersonalInfoViewImpl;

public class PortalViewFactory extends ViewFactoryBase {

    public static IsWidget instance(Class<?> type) {
        if (!map.containsKey(type)) {
            if (FooterView.class.equals(type)) {
                map.put(type, new FooterViewImpl());
            } else if (LoginView.class.equals(type)) {
                map.put(type, new LoginViewImpl());
            } else if (LogoView.class.equals(type)) {
                map.put(type, new LogoViewImpl());
            } else if (MaintenanceView.class.equals(type)) {
                map.put(type, new MaintenanceViewImpl());
            } else if (MainNavigView.class.equals(type)) {
                map.put(type, new MainNavigViewImpl());
            } else if (BillingHistoryView.class.equals(type)) {
                map.put(type, new BillingHistoryViewImpl());
            } else if (LoginInvitationView.class.equals(type)) {
                map.put(type, new LoginInvitationViewImpl());
            } else if (StaticPageView.class.equals(type)) {
                map.put(type, new StaticPageViewImpl());
            } else if (PersonalInfoView.class.equals(type)) {
                map.put(type, new PersonalInfoViewImpl());
            } else if (TopRightActionsView.class.equals(type)) {
                map.put(type, new TopRightActionsViewImpl());
            } else if (RetrievePasswordView.class.equals(type)) {
                map.put(type, new RetrievePasswordViewImpl());
            } else if (CreateAccountView.class.equals(type)) {
                map.put(type, new CreateAccountViewImpl());
            } else if (PaymentMethodsView.class.equals(type)) {
                map.put(type, new PaymentMethodsViewImpl());
            } else if (CurrentBillView.class.equals(type)) {
                map.put(type, new CurrentBillViewImpl());
            } else if (PotentialTenantView.class.equals(type)) {
                map.put(type, new PotentialTenantViewImpl());
            } else if (MaintenanceListerView.class.equals(type)) {
                map.put(type, new MaintenanceListerViewImpl());
            } else if (NewPaymentMethodView.class.equals(type)) {
                map.put(type, new NewPaymentMethodViewImpl());
            } else if (EditPaymentMethodView.class.equals(type)) {
                map.put(type, new EditPaymentMethodViewImpl());
            }

        }
        return map.get(type);
    }
}
