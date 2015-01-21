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
 */
package com.propertyvista.portal.shared.activity;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.gwt.commons.layout.LayoutChangeRequestEvent;
import com.pyx4j.gwt.commons.layout.LayoutChangeRequestEvent.ChangeType;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.events.NotificationEvent;
import com.pyx4j.site.client.events.NotificationHandler;
import com.pyx4j.site.shared.domain.Notification;
import com.pyx4j.site.shared.domain.Notification.NotificationType;

import com.propertyvista.domain.communication.NotificationDelivery;
import com.propertyvista.dto.communication.MessageDTO;
import com.propertyvista.portal.rpc.portal.resident.services.CommunicationPortalCrudService;
import com.propertyvista.portal.rpc.shared.dto.communication.PortalCommunicationSystemNotification;
import com.propertyvista.portal.shared.CommunicationStatusUpdateEvent;
import com.propertyvista.portal.shared.CommunicationStatusUpdateHandler;
import com.propertyvista.portal.shared.PortalSite;
import com.propertyvista.portal.shared.ui.NotificationHeaderView;
import com.propertyvista.portal.shared.ui.NotificationHeaderView.NotificationHeaderPresenter;

public class NotificationHeaderActivity extends AbstractActivity implements NotificationHeaderPresenter, NotificationHandler {

    private final NotificationHeaderView view;

    private final int MAX_SIZE = 3;

    private final Map<Notification, MessageDTO> notifications = new HashMap<>();

    public NotificationHeaderActivity(Place place) {
        view = PortalSite.getViewFactory().getView(NotificationHeaderView.class);
        view.setPresenter(this);
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        eventBus.addHandler(NotificationEvent.getType(), this);
        eventBus.addHandler(CommunicationStatusUpdateEvent.getType(), new CommunicationStatusUpdateHandler() {

            @Override
            public void onStatusUpdate(CommunicationStatusUpdateEvent event) {
                PortalCommunicationSystemNotification data = event.getCommunicationSystemNotification();
                updateAuthenticatedView(data == null ? null : data.notifications);
                view.showNotifications(notifications.keySet());
            }
        });

        panel.setWidget(view);
        AppSite.getEventBus().fireEvent(new LayoutChangeRequestEvent(ChangeType.resizeComponents));
        view.showNotifications(notifications.keySet());
    }

    @Override
    public void acceptMessage(Notification notification) {
        MessageDTO m = notifications.get(notification);
        if (m != null) {
            m.isRead().setValue(true);
            GWT.<CommunicationPortalCrudService> create(CommunicationPortalCrudService.class).saveChildMessage(new AsyncCallback<MessageDTO>() {

                @Override
                public void onSuccess(MessageDTO result) {
                }

                @Override
                public void onFailure(Throwable caught) {
                }
            }, m);
        }
        notifications.remove(notification);
        view.showNotifications(notifications.keySet());
    }

    @Override
    public void addMessage(Notification notification) {
        addMessage(notification, null);
    }

    private void addMessage(Notification notification, MessageDTO message) {

        if (message != null && !message.isNull()) {
            for (Notification n : notifications.keySet()) {
                MessageDTO m = notifications.get(n);
                if (m != null && m.id().getValue().equals(message.id().getValue())) {
                    return;
                }
            }
        }
        if (notifications.size() == MAX_SIZE) {
            Notification notificationToRemove = null;
            for (Notification n : notifications.keySet()) {
                // TODO: Add a logic to remove according a weight
                notificationToRemove = n;
                break;
            }
            notifications.remove(notificationToRemove);
        }
        notifications.put(notification, message);
    }

    private void updateAuthenticatedView(EntitySearchResult<MessageDTO> notificationMessages) {
        if (notificationMessages != null && notificationMessages.getData() != null) {
            for (MessageDTO m : notificationMessages.getData()) {
                NotificationType nt = NotificationType.INFO;
                switch (m.notificationType().getValue(NotificationDelivery.NotificationType.Information)) {
                case Information:
                    nt = NotificationType.INFO;
                    break;
                case Warning:
                    nt = NotificationType.WARNING;
                    break;
                case Note:
                    nt = NotificationType.STATUS;
                    break;
                }
                addMessage(new Notification(m.content().getValue(), m.thread().subject().getValue(), nt), m);
            }
        }
    }

    @Override
    public void onNotification(NotificationEvent event) {
        if (event != null && event.getNotification() != null) {
            addMessage(event.getNotification());
        }
    }
}
