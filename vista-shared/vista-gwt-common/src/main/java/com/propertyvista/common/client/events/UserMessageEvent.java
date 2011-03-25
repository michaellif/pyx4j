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

public class UserMessageEvent extends GwtEvent<UserMessageHandler> {

    public enum UserMessageType implements IDebugId {

        INFO, WARN, ERROR, FAILURE;

        @Override
        public String getDebugIdString() {
            return this.name();
        }
    }

    private static Type<UserMessageHandler> TYPE;

    private final String userMessage;

    private final String debugMessage;

    private final UserMessageType messageType;

    public UserMessageEvent(String userMessage, String debugMessage, UserMessageType messageType) {
        this.userMessage = userMessage;
        this.debugMessage = debugMessage;
        this.messageType = messageType;
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

    public String getUserMessage() {
        return userMessage;
    }

    public String getDebugMessage() {
        return debugMessage;
    }

    public UserMessageType getMessageType() {
        return messageType;
    }

}