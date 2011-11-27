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

import com.propertyvista.portal.client.ui.CaptionView;
import com.propertyvista.portal.client.ui.CaptionViewImpl;
import com.propertyvista.portal.client.ui.NavigView;
import com.propertyvista.portal.client.ui.NavigViewImpl;
import com.propertyvista.portal.client.ui.PotentialTenantView;
import com.propertyvista.portal.client.ui.PotentialTenantViewImpl;
import com.propertyvista.portal.client.ui.residents.BillingHistoryView;
import com.propertyvista.portal.client.ui.residents.BillingHistoryViewImpl;
import com.propertyvista.portal.client.ui.residents.CurrentBillView;
import com.propertyvista.portal.client.ui.residents.CurrentBillViewImpl;
import com.propertyvista.portal.client.ui.residents.DashboardView;
import com.propertyvista.portal.client.ui.residents.DashboardViewImpl;
import com.propertyvista.portal.client.ui.residents.EditPaymentMethodView;
import com.propertyvista.portal.client.ui.residents.EditPaymentMethodViewImpl;
import com.propertyvista.portal.client.ui.residents.MaintenanceDetailsView;
import com.propertyvista.portal.client.ui.residents.MaintenanceDetailsViewImpl;
import com.propertyvista.portal.client.ui.residents.MaintenanceView;
import com.propertyvista.portal.client.ui.residents.MaintenanceViewImpl;
import com.propertyvista.portal.client.ui.residents.NewPaymentMethodView;
import com.propertyvista.portal.client.ui.residents.NewPaymentMethodViewImpl;
import com.propertyvista.portal.client.ui.residents.PaymentMethodsView;
import com.propertyvista.portal.client.ui.residents.PaymentMethodsViewImpl;
import com.propertyvista.portal.client.ui.residents.PersonalInfoView;
import com.propertyvista.portal.client.ui.residents.PersonalInfoViewImpl;

public class PortalViewFactory extends ViewFactoryBase {

    public static <T extends IsWidget> T instance(Class<T> type) {
        if (!map.containsKey(type)) {
            if (DashboardView.class.equals(type)) {
                map.put(type, new DashboardViewImpl());
            } else if (MaintenanceView.class.equals(type)) {
                map.put(type, new MaintenanceViewImpl());
            } else if (NavigView.class.equals(type)) {
                map.put(type, new NavigViewImpl());
            } else if (CaptionView.class.equals(type)) {
                map.put(type, new CaptionViewImpl());
            } else if (BillingHistoryView.class.equals(type)) {
                map.put(type, new BillingHistoryViewImpl());
            } else if (PersonalInfoView.class.equals(type)) {
                map.put(type, new PersonalInfoViewImpl());
            } else if (PaymentMethodsView.class.equals(type)) {
                map.put(type, new PaymentMethodsViewImpl());
            } else if (CurrentBillView.class.equals(type)) {
                map.put(type, new CurrentBillViewImpl());
            } else if (PotentialTenantView.class.equals(type)) {
                map.put(type, new PotentialTenantViewImpl());
            } else if (MaintenanceDetailsView.class.equals(type)) {
                map.put(type, new MaintenanceDetailsViewImpl());
            } else if (NewPaymentMethodView.class.equals(type)) {
                map.put(type, new NewPaymentMethodViewImpl());
            } else if (EditPaymentMethodView.class.equals(type)) {
                map.put(type, new EditPaymentMethodViewImpl());
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
