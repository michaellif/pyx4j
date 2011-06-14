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
import com.propertyvista.portal.client.ui.LoginView;
import com.propertyvista.portal.client.ui.LoginViewImpl;
import com.propertyvista.portal.client.ui.LogoView;
import com.propertyvista.portal.client.ui.LogoViewImpl;
import com.propertyvista.portal.client.ui.MainNavigView;
import com.propertyvista.portal.client.ui.MainNavigViewImpl;
import com.propertyvista.portal.client.ui.MaintenanceView;
import com.propertyvista.portal.client.ui.MaintenanceViewImpl;
import com.propertyvista.portal.client.ui.PaymentView;
import com.propertyvista.portal.client.ui.PaymentViewImpl;
import com.propertyvista.portal.client.ui.ResidentsView;
import com.propertyvista.portal.client.ui.ResidentsViewImpl;
import com.propertyvista.portal.client.ui.StaticPageView;
import com.propertyvista.portal.client.ui.StaticPageViewImpl;
import com.propertyvista.portal.client.ui.TenantProfileView;
import com.propertyvista.portal.client.ui.TenantProfileViewImpl;
import com.propertyvista.portal.client.ui.TopRightActionsView;
import com.propertyvista.portal.client.ui.TopRightActionsViewImpl;

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
            } else if (PaymentView.class.equals(type)) {
                map.put(type, new PaymentViewImpl());
            } else if (ResidentsView.class.equals(type)) {
                map.put(type, new ResidentsViewImpl());
            } else if (StaticPageView.class.equals(type)) {
                map.put(type, new StaticPageViewImpl());
            } else if (TenantProfileView.class.equals(type)) {
                map.put(type, new TenantProfileViewImpl());
            } else if (TopRightActionsView.class.equals(type)) {
                map.put(type, new TopRightActionsViewImpl());
            }

        }
        return map.get(type);
    }
}
