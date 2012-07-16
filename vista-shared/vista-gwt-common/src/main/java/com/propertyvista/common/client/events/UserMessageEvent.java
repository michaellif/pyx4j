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

import com.pyx4j.commons.IDebugId;

import com.propertyvista.common.client.UserMessage;

public class UserMessageEvent extends GwtEvent<UserMessageHandler> {

    public enum UserMessageType implements IDebugId {

        INFO, WARN, ERROR, FAILURE;

        @Override
        public String debugId() {
            return this.name();
        }
    }

    private static Type<UserMessageHandler> TYPE;

    private final UserMessage userMessage;

    public UserMessageEvent(String message, String debugMessage, UserMessageType messageType) {
        userMessage = new UserMessage(message, messageType, null, null, null);
        userMessage.setDebugMessage(debugMessage);
    }

    public static Type<UserMessageHandler> getType() {
        if (TYPE == null) {
            TYPE = new Type<UserMessageHandler>();
        }
        return TYPE;
    }

    @Override
    public final Type<UserMessageHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(UserMessageHandler handler) {
        handler.onUserMessage(this);
    }

    public UserMessage getUserMessage() {
        return userMessage;
    }

    public String getMessage() {
        return userMessage.getMessage();
    }

    public String getDebugMessage() {
        return userMessage.getDebugMessage();
    }

    public UserMessageType getMessageType() {
        return userMessage.getMessageType();
    }

}