/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 15, 2011
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.common.client.events;

import com.google.gwt.event.shared.GwtEvent;

import com.propertyvista.common.client.site.Notification;
import com.propertyvista.common.client.site.Notification.NotificationType;

public class NotificationEvent extends GwtEvent<NotificationHandler> {

    private static Type<NotificationHandler> TYPE;

    private final Notification notification;

    public NotificationEvent(String message, String debugMessage, NotificationType messageType) {
        notification = new Notification(message, messageType, null);
        notification.setDebugMessage(debugMessage);
    }

    public static Type<NotificationHandler> getType() {
        if (TYPE == null) {
            TYPE = new Type<NotificationHandler>();
        }
        return TYPE;
    }

    @Override
    public final Type<NotificationHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(NotificationHandler handler) {
        handler.onUserMessage(this);
    }

    public Notification getNotification() {
        return notification;
    }

    public String getMessage() {
        return notification.getMessage();
    }

    public String getDebugMessage() {
        return notification.getSystemInfo();
    }

    public NotificationType getMessageType() {
        return notification.getNotificationType();
    }

}