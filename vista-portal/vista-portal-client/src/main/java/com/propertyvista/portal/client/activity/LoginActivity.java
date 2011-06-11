/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 15, 2011
 * @author Dad
 * @version $Id$
 */
package com.propertyvista.portal.client.activity;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.propertyvista.portal.client.ui.LoginView;
import com.propertyvista.portal.client.ui.viewfactories.PortalViewFactory;
import com.propertyvista.portal.rpc.portal.PortalSiteMap;

import com.pyx4j.site.client.AppSite;

public class LoginActivity extends AbstractActivity implements LoginView.Presenter {
    LoginView view;

    public LoginActivity(Place place) {
        this.view = (LoginView) PortalViewFactory.instance(LoginView.class);
        this.view.setPresenter(this);
        withPlace(place);
    }

    public LoginActivity withPlace(Place place) {
        return this;
    }

    @Override
    public void start(AcceptsOneWidget containerWidget, EventBus eventBus) {
        containerWidget.setWidget(view);

    }

    @Override
    public void gotoResidentsNavig() {
        AppSite.getPlaceController().goTo(new PortalSiteMap.Residents.Navigator());

    }
}
