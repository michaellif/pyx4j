/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-02-05
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.portal.web.client.activity;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.portal.web.client.PortalWebSite;
import com.propertyvista.portal.web.client.ui.NotificationPageView;

public class NotificationPageActivity extends AbstractActivity implements NotificationPageView.NotificationPagePresenter {

    private final NotificationPageView view;

    public NotificationPageActivity(Place place) {
        view = PortalWebSite.getViewFactory().instantiate(NotificationPageView.class);
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        view.setPresenter(this);
        view.populate(PortalWebSite.instance().getNotification());
        panel.setWidget(view);

    }

    @Override
    public void acceptMessage() {
        AppSite.getPlaceController().goTo(AppPlace.NOWHERE);
    }

}
