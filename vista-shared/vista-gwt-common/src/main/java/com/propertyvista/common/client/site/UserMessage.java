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

public class UserMessage extends Message {

    public enum UserMessageType implements IDebugId {

        INFO, WARN, ERROR, FAILURE;

        @Override
        public String debugId() {
            return this.name();
        }
    }

    private final UserMessageType messageType;

    private String debugMessage;

    public UserMessage(Message message) {
        this(message.getMessage(), UserMessageType.ERROR, message.getTitle(), message.getButtonText(), message.getCommand());
    }

    public UserMessage(String message, UserMessageType type, String title, String buttonText, Command command) {
        super(message, title, buttonText, command);
        messageType = type;
    }

    public UserMessage setDebugMessage(String message) {
        debugMessage = message;
        return this;
    }

    public String getDebugMessage() {
        return debugMessage;
    }

    public UserMessageType getMessageType() {
        return messageType;
    }

}
