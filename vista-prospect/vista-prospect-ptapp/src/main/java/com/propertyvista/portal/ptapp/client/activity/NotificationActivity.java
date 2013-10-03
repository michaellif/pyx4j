/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-02-20
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.portal.ptapp.client.activity;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.site.rpc.NotificationAppPlace;

import com.propertyvista.portal.ptapp.client.ui.NotificationView;
import com.propertyvista.portal.ptapp.client.ui.viewfactories.PtAppViewFactory;

/**
 * 
 * Shows dialog style message on full screen
 * 
 */
public class NotificationActivity extends AbstractActivity implements NotificationView.Presenter {

    enum Params {
        TITLE, MESSAGE, BUTTON_LABEL
    }

    private final NotificationView view;

    private final NotificationAppPlace place;

    public NotificationActivity(NotificationAppPlace place) {
        this.place = place;
        view = PtAppViewFactory.instance(NotificationView.class);
        assert (view != null);
        view.setPresenter(this);
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        panel.setWidget(view);
        view.setNotification(place.getNotification());
    }

    @Override
    public void action() {

    }
}
