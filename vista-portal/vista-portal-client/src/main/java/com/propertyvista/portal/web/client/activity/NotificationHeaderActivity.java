/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 24, 2013
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.portal.web.client.activity;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.events.NotificationEvent;
import com.pyx4j.site.client.events.NotificationHandler;
import com.pyx4j.site.client.ui.layout.responsive.LayoutChangeRequestEvent;
import com.pyx4j.site.client.ui.layout.responsive.LayoutChangeRequestEvent.ChangeType;
import com.pyx4j.site.shared.domain.Notification;

import com.propertyvista.portal.web.client.PortalWebSite;
import com.propertyvista.portal.web.client.ui.NotificationHeaderView;
import com.propertyvista.portal.web.client.ui.NotificationHeaderView.NotificationHeaderPresenter;

public class NotificationHeaderActivity extends AbstractActivity implements NotificationHeaderPresenter, NotificationHandler {

    private final NotificationHeaderView view;

    private static List<Notification> notifications;

    static {
        notifications = new ArrayList<Notification>();
//        notifications.add(new Notification("Error Message goes here", NotificationType.ERROR, "Error Notification"));
//        notifications.add(new Notification("Info Message goes here", NotificationType.INFO, "Info Notification"));
//        notifications.add(new Notification("Warn Message goes here", NotificationType.WARN, "Warn Notification"));
//        notifications.add(new Notification("Confirm Message goes here", NotificationType.CONFIRM, "Confirm Notification"));

    }

    public NotificationHeaderActivity(Place place) {
        view = PortalWebSite.getViewFactory().instantiate(NotificationHeaderView.class);
        view.setPresenter(this);

    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        eventBus.addHandler(NotificationEvent.getType(), this);

        panel.setWidget(view);
        AppSite.getEventBus().fireEvent(new LayoutChangeRequestEvent(ChangeType.resizeComponents));
        view.populate(notifications);
    }

    @Override
    public void acceptMessage(Notification notification) {
        notifications.remove(notification);
        view.populate(notifications);
    }

    @Override
    public void onNotification(NotificationEvent event) {
        // TODO Auto-generated method stub

    }
}
