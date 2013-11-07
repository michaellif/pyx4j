/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-01-25
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.portal.prospect.activity;

import java.util.Arrays;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.Window;

import com.pyx4j.security.rpc.AuthenticationService;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.NavigationUri;
import com.pyx4j.site.rpc.AppPlaceInfo;

import com.propertyvista.common.client.ui.components.login.AbstractLoginActivty;
import com.propertyvista.common.client.ui.components.login.LoginView.DevLoginCredentials;
import com.propertyvista.domain.DemoData.UserType;
import com.propertyvista.portal.prospect.ui.landing.LandingView;
import com.propertyvista.portal.rpc.portal.PortalSiteMap;
import com.propertyvista.portal.rpc.portal.ProspectPortalSiteMap;
import com.propertyvista.portal.rpc.portal.web.services.PortalAuthenticationService;
import com.propertyvista.portal.shared.PortalSite;

public class LandingActivity extends AbstractLoginActivty implements LandingView.LandingPresenter {

    public LandingActivity(Place place) {
        super(place, PortalSite.getViewFactory().instantiate(LandingView.class), GWT.<AuthenticationService> create(PortalAuthenticationService.class),
                new PortalSiteMap.PasswordResetRequest());
    }

    @Override
    public void signUp() {
        AppSite.getPlaceController().goTo(new ProspectPortalSiteMap.Registration());
    }

    @Override
    public void showProspectTerms() {
        Window.open(AppPlaceInfo.absoluteUrl(NavigationUri.getHostPageURL(), false, getPortalTermsPlace()), "_blank", null);
    }

    @Override
    protected List<? extends DevLoginCredentials> getDevLoginCredentials() {
        return Arrays.asList(//@formatter:off
                new DevLoginCredentialsImpl(UserType.TENANT, 'T')
        );//@formatter:on
    }

    @Override
    public final Class<? extends Place> getPortalTermsPlace() {
        return ProspectPortalSiteMap.TermsAndConditions.class;
    }
}
