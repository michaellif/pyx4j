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
 * Created on 2010-09-28
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.rpc.client;

import java.io.Serializable;

import com.google.gwt.event.shared.GwtEvent;

public class SystemNotificationEvent extends GwtEvent<SystemNotificationHandler> {

    static Type<SystemNotificationHandler> TYPE = new Type<SystemNotificationHandler>();

    private final Serializable systemNotification;

    /**
     * Creates the event.
     * 
     * @param value
     *            the value
     */
    SystemNotificationEvent(Serializable value) {
        this.systemNotification = value;
    }

    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<SystemNotificationHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(SystemNotificationHandler handler) {
        handler.onSystemNotificationReceived(this);
    }

    public Serializable getSystemNotification() {
        return systemNotification;
    }

}
