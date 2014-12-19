/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-12-20
 * @author ArtyomB
 */
package com.propertyvista.ob.client.mvp.activity;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.rpc.AppPlace;
import com.pyx4j.site.rpc.NotificationAppPlace;

import com.propertyvista.ob.client.views.OnboardingViewFactory;
import com.propertyvista.ob.client.views.RuntimeErrorView;

public class RuntimeErrorActivity extends AbstractActivity implements RuntimeErrorView.Presenter {

    private final RuntimeErrorView view;

    private final NotificationAppPlace place;

    public RuntimeErrorActivity(NotificationAppPlace place) {
        this.place = place;
        view = OnboardingViewFactory.instance(RuntimeErrorView.class);
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        view.setPresenter(this);
        view.setErrorMessage(place.getNotification());
        panel.setWidget(view);
    }

    @Override
    public void acknowledgeError() {
        AppSite.getPlaceController().goTo(AppPlace.NOWHERE);
    }

}
