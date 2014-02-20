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
package com.propertyvista.portal.shared.activity;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.rpc.AppPlace;
import com.pyx4j.site.rpc.NotificationAppPlace;

import com.propertyvista.portal.shared.PortalSite;
import com.propertyvista.portal.shared.ui.NotificationPageView;

public class NotificationPageActivity extends AbstractActivity implements NotificationPageView.NotificationPagePresenter {

    private final NotificationPageView view;

    private final NotificationAppPlace place;

    public NotificationPageActivity(NotificationAppPlace place) {
        this.place = place;
        view = PortalSite.getViewFactory().getView(NotificationPageView.class);
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        view.setPresenter(this);
        view.populate(place.getNotification());
        panel.setWidget(view);
    }

    @Override
    public void acceptMessage() {
        if (place.getContinuePlace() == null) {
            AppSite.getPlaceController().goTo(AppPlace.NOWHERE);
        } else {
            AppSite.getPlaceController().goTo(place.getContinuePlace());
        }
    }

    @Override
    public String mayStop() {
        return null;
    }

}
