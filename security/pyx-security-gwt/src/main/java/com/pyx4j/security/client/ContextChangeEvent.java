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
 * Created on Oct 29, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.security.client;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Fired when UserVisit (user name and other attributes) changed on client e.g. previously changed on server.
 * Also fired at the same time when SecurityControllerEvent is triggered
 */
public class ContextChangeEvent extends GwtEvent<ContextChangeHandler> {

    static Type<ContextChangeHandler> TYPE = new Type<ContextChangeHandler>();

    private final String attributeName;

    private final Object value;

    public static Type<ContextChangeHandler> getType() {
        return TYPE;
    }

    public ContextChangeEvent(String attributeName, Object value) {
        this.attributeName = attributeName;
        this.value = value;
    }

    @Override
    public GwtEvent.Type<ContextChangeHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(ContextChangeHandler handler) {
        handler.onContextChange(this);
    }

    /**
     * ClientContext.USER_VISIT_ATTRIBUTE if UserVisit object has been changed.
     */
    public String getAttributeName() {
        return attributeName;
    }

    public Object getValue() {
        return value;
    }

}
