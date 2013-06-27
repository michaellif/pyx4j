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

import com.google.gwt.user.client.Command;

import com.pyx4j.commons.IDebugId;

public class Notification {

    public enum NotificationType implements IDebugId {

        INFO, WARN, ERROR, FAILURE;

        @Override
        public String debugId() {
            return this.name();
        }
    }

    private final String message;

    private final String title;

    private final String buttonText;

    private final Command command;

    private final NotificationType notificationType;

    private String debugMessage;

    public Notification(String message, NotificationType type, String title, String buttonText, Command command) {
        this.message = message;
        this.title = title;
        this.buttonText = buttonText;
        this.command = command;
        notificationType = type;
    }

    public Notification setDebugMessage(String message) {
        debugMessage = message;
        return this;
    }

    public String getDebugMessage() {
        return debugMessage;
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

    public String getButtonText() {
        return buttonText;
    }

    public Command getCommand() {
        return command;
    }
}
