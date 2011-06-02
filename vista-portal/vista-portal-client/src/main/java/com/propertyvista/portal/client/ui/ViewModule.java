/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 14, 2011
 * @author vadims
 * @version $Id$
 */
package com.propertyvista.portal.client.ui;

import com.google.gwt.inject.client.AbstractGinModule;
import com.google.inject.Singleton;

import com.pyx4j.widgets.client.style.Theme;

import com.propertyvista.portal.client.themes.GainsboroTheme;

public class ViewModule extends AbstractGinModule {

    @Override
    protected void configure() {
        bind(PortalView.class);
        bind(Theme.class).to(GainsboroTheme.class).in(Singleton.class);

        bind(LogoView.class).to(LogoViewImpl.class).in(Singleton.class);
        bind(FooterView.class).to(FooterViewImpl.class).in(Singleton.class);
        bind(MainNavigView.class).to(MainNavigViewImpl.class).in(Singleton.class);
        bind(TopRightActionsView.class).to(TopRightActionsViewImpl.class).in(Singleton.class);
        bind(ResidentsView.class).to(ResidentsViewImpl.class).in(Singleton.class);
        bind(LoginView.class).to(LoginViewImpl.class).in(Singleton.class);
        bind(PropertyMapView.class).to(PropertyMapViewImpl.class).in(Singleton.class);
        bind(TenantProfileView.class).to(TenantProfileViewImpl.class).in(Singleton.class);
        bind(MaintenanceView.class).to(MaintenanceViewImpl.class).in(Singleton.class);
        bind(PaymentView.class).to(PaymentViewImpl.class).in(Singleton.class);
        bind(ResidentsNavigView.class).to(ResidentsNavigViewImpl.class).in(Singleton.class);

        bind(ApartmentDetailsView.class).to(ApartmentDetailsViewImpl.class).in(Singleton.class);
        bind(UnitDetailsView.class).to(UnitDetailsViewImpl.class).in(Singleton.class);
        bind(SearchApartmentView.class).to(SearchApartmentViewImpl.class).in(Singleton.class);
        bind(StaticPageView.class).to(StaticPageViewImpl.class).in(Singleton.class);

    }

}
