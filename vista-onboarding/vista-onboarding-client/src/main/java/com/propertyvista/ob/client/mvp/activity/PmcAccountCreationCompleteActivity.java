/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-12-11
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.ob.client.mvp.activity;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.ob.client.views.OnboardingViewFactory;
import com.propertyvista.ob.client.views.PmcAccountCreationCompleteView;

public class PmcAccountCreationCompleteActivity extends AbstractActivity implements PmcAccountCreationCompleteView.Presenter {

    private final PmcAccountCreationCompleteView view;

    public PmcAccountCreationCompleteActivity(AppPlace place) {
        view = OnboardingViewFactory.instance(PmcAccountCreationCompleteView.class);
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        panel.setWidget(view);
        view.setCrmSiteUrl("http://yeshiva.org.il");
    }

    @Override
    public void populate() {
        // TODO Auto-generated method stub

    }

    @Override
    @Deprecated
    public void refresh() {
        // TODO Auto-generated method stub

    }

}
