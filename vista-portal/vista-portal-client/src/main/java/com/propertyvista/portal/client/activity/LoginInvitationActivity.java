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
 * @author Dad
 * @version $Id$
 */
package com.propertyvista.portal.client.activity;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.site.client.AppSite;

import com.propertyvista.portal.client.ui.LoginInvitationView;
import com.propertyvista.portal.client.ui.viewfactories.PortalViewFactory;
import com.propertyvista.portal.rpc.portal.PortalSiteMap;

public class LoginInvitationActivity extends AbstractActivity implements LoginInvitationView.Presenter {
    private final LoginInvitationView view;

    public LoginInvitationActivity(Place place) {
        this.view = (LoginInvitationView) PortalViewFactory.instance(LoginInvitationView.class);
        this.view.setPresenter(this);
        withPlace(place);
    }

    @Override
    public void start(AcceptsOneWidget containerWidget, EventBus eventBus) {
        containerWidget.setWidget(view);

    }

    public LoginInvitationActivity withPlace(Place place) {
        return this;
    }

    @Override
    public void gotoLoginForm() {
        AppSite.getPlaceController().goTo(new PortalSiteMap.Residents.Login());

    }

}
