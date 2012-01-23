/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 23, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.activity.security;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.site.client.AppSite;

import com.propertyvista.crm.client.ui.security.PasswordResetRequestSuccessView;
import com.propertyvista.crm.client.ui.viewfactories.SecurityViewFactory;
import com.propertyvista.crm.rpc.CrmSiteMap;

public class PasswordResetRequestSuccessActivity extends AbstractActivity implements PasswordResetRequestSuccessView.Presenter {

    private final PasswordResetRequestSuccessView view;

    public PasswordResetRequestSuccessActivity(Place place) {
        view = SecurityViewFactory.instance(PasswordResetRequestSuccessView.class);
        view.setPresetner(this);
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        panel.setWidget(view);
    }

    @Override
    public void goToLoginScreen() {
        AppSite.getPlaceController().goTo(new CrmSiteMap.Login());
    }

}
