/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 13, 2011
 * @author Misha
 * @version $Id$
 */
package com.propertyvista.common.client.site;

import com.pyx4j.commons.IDebugId;

public class Notification {

    public enum NotificationType implements IDebugId {

        INFO, WARN, ERROR, FAILURE, CONFIRM;

        @Override
        public String debugId() {
            return this.name();
        }
    }

    private final String message;

    private final String title;

    private final NotificationType notificationType;

    private String systemInfo;

    public Notification(String message, NotificationType type, String title) {
        this.message = message;
        this.title = title;
        notificationType = type;
    }

    public Notification setDebugMessage(String message) {
        systemInfo = message;
        return this;
    }

    public String getSystemInfo() {
        return systemInfo;
    }

    public NotificationType getNotificationType() {
        return notificationType;
    }

    public String getMessage() {
        return message;
    }

    public String getTitle() {
        return title;
    }

}
