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
package com.propertyvista.portal.client.ptapp.events;

import com.google.gwt.event.shared.GwtEvent;

public class UserMessageEvent extends GwtEvent<UserMessageHandler> {

    private static Type<UserMessageHandler> TYPE;

    private final String message;

    public UserMessageEvent(String message) {
        this.message = message;
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

    public String getMessage() {
        return message;
    }
}