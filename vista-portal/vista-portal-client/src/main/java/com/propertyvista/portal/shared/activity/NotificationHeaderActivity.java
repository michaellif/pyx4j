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
package com.propertyvista.portal.shared.activity;

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
import com.pyx4j.site.shared.domain.Notification.NotificationType;

import com.propertyvista.portal.shared.PortalSite;
import com.propertyvista.portal.shared.ui.NotificationHeaderView;
import com.propertyvista.portal.shared.ui.NotificationHeaderView.NotificationHeaderPresenter;

public class NotificationHeaderActivity extends AbstractActivity implements NotificationHeaderPresenter, NotificationHandler {

    private final NotificationHeaderView view;

    private static List<Notification> notifications;

    static {
        notifications = new ArrayList<>();
        if (false) {
            notifications.add(new Notification("Error Message goes here", "Error Notification", NotificationType.ERROR));
            notifications.add(new Notification("Info Message goes here", "Info Notification", NotificationType.INFO));
            notifications.add(new Notification("Warn Message goes here", "Warn Notification", NotificationType.WARNING));
            notifications.add(new Notification("Confirm Message goes here", "Confirm Notification", NotificationType.STATUS));
        }

    }

    public NotificationHeaderActivity(Place place) {
        view = PortalSite.getViewFactory().getView(NotificationHeaderView.class);
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
