/*
 * Pyx4j framework
 * Copyright (C) 2008-2010 pyx4j.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * Created on 2010-05-14
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.site.client.events;

import com.google.gwt.event.shared.GwtEvent;

import com.pyx4j.site.shared.domain.Notification;
import com.pyx4j.site.shared.domain.Notification.NotificationType;

public class NotificationEvent extends GwtEvent<NotificationHandler> {

    private static Type<NotificationHandler> TYPE;

    private final Notification notification;

    public NotificationEvent(String message, String title, String systemInfo, NotificationType messageType) {
        notification = new Notification(message, title, messageType);
        notification.setSystemInfo(systemInfo);
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
        handler.onNotification(this);
    }

    public Notification getNotification() {
        return notification;
    }

    public String getMessage() {
        return notification.getMessage();
    }

    public String getSystemInfo() {
        return notification.getSystemInfo();
    }

    public NotificationType getNotificationType() {
        return notification.getNotificationType();
    }

}