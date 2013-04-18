/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 1, 2011
 * @author Misha
 * @version $Id$
 */
package com.propertyvista.crm.client.activity;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.site.client.AppSite;

import com.propertyvista.crm.client.CrmSite;
import com.propertyvista.crm.client.ui.LogoView;
import com.propertyvista.crm.client.ui.viewfactories.CrmVeiwFactory;

public class LogoActivity extends AbstractActivity implements LogoView.Presenter {

    private final LogoView view;

    public LogoActivity(Place place) {
        view = CrmVeiwFactory.instance(LogoView.class);
        view.setPresenter(this);
        withPlace(place);
    }

    public LogoActivity withPlace(Place place) {
        return this;
    }

    @Override
    public void start(AcceptsOneWidget containerWidget, EventBus eventBus) {
        containerWidget.setWidget(view);
    }

    @Override
    public void navigToLanding() {
        AppSite.getPlaceController().goTo(CrmSite.getSystemDashboardPlace());
    }
}