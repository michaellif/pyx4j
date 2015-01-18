/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 9, 2011
 * @author vadims
 */
package com.propertyvista.crm.client.activity;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.client.BehaviorChangeEvent;
import com.pyx4j.security.client.BehaviorChangeHandler;
import com.pyx4j.security.client.ContextChangeEvent;
import com.pyx4j.security.client.ContextChangeHandler;
import com.pyx4j.site.shared.domain.Notification;
import com.pyx4j.site.shared.domain.Notification.NotificationType;

import com.propertyvista.crm.client.CrmSite;
import com.propertyvista.crm.client.event.CommunicationStatusUpdateEvent;
import com.propertyvista.crm.client.event.CommunicationStatusUpdateHandler;
import com.propertyvista.crm.client.ui.NotificationsView;
import com.propertyvista.crm.rpc.dto.communication.CrmCommunicationSystemNotification;
import com.propertyvista.crm.rpc.services.CommunicationCrudService;
import com.propertyvista.domain.communication.NotificationDelivery;
import com.propertyvista.dto.communication.CommunicationThreadDTO;
import com.propertyvista.dto.communication.MessageDTO;

public class NotificationsActivity extends AbstractActivity implements NotificationsView.NotificationsPresenter {

    private static final I18n i18n = I18n.get(NotificationsActivity.class);

    private final NotificationsView view;

    private final int MAX_SIZE = 3;

    private final Map<Notification, MessageDTO> notifications = new HashMap<>();

    public NotificationsActivity() {
        view = CrmSite.getViewFactory().getView(NotificationsView.class);
    }

    @Override
    public void start(AcceptsOneWidget container, EventBus eventBus) {
        view.setPresenter(this);
        container.setWidget(view);

        updateAuthenticatedView(null);
        view.showNotifications(notifications.keySet());

        eventBus.addHandler(BehaviorChangeEvent.getType(), new BehaviorChangeHandler() {
            @Override
            public void onBehaviorChange(BehaviorChangeEvent event) {
                view.showNotifications(notifications.keySet());
            }
        });

        eventBus.addHandler(ContextChangeEvent.getType(), new ContextChangeHandler() {

            @Override
            public void onContextChange(ContextChangeEvent event) {
                view.showNotifications(notifications.keySet());
            }
        });

        eventBus.addHandler(CommunicationStatusUpdateEvent.getType(), new CommunicationStatusUpdateHandler() {

            @Override
            public void onStatusUpdate(CommunicationStatusUpdateEvent event) {
                CrmCommunicationSystemNotification data = event.getCommunicationSystemNotification();
                updateAuthenticatedView(data == null ? null : data.notifications);
                view.showNotifications(notifications.keySet());
            }
        });

    }

    public NotificationsActivity withPlace(Place place) {
        return this;
    }

    @Override
    public void acceptMessage(Notification notification) {
        MessageDTO m = notifications.get(notification);
        if (m != null) {
            m.isRead().setValue(true);
            GWT.<CommunicationCrudService> create(CommunicationCrudService.class).saveMessage(new AsyncCallback<CommunicationThreadDTO>() {

                @Override
                public void onSuccess(CommunicationThreadDTO result) {
                }

                @Override
                public void onFailure(Throwable caught) {
                }
            }, m, null);
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
                addMessage(new Notification(m.text().getValue(), m.thread().subject().getValue(), nt), m);
            }
        }
    }
}
