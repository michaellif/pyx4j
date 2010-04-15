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
 * Created on Apr 14, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.widgets.client.event.shared;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Fired just before the browser window closes or navigates to a different site @see
 * Window.ClosingEvent or when application navigates to diferent page.
 */
public class PageLeavingEvent extends GwtEvent<PageLeavingHandler> {

    public static final Type<PageLeavingHandler> TYPE = new Type<PageLeavingHandler>();

    /**
     * The message to display to the user to see whether they really want to leave the
     * page.
     */
    private StringBuilder messageBuilder = null;

    private final boolean windowClosing;

    public PageLeavingEvent(boolean windowClosing) {
        this.windowClosing = windowClosing;
    }

    @Override
    public GwtEvent.Type<PageLeavingHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(PageLeavingHandler handler) {
        handler.onPageLeaving(this);
    }

    /**
     * 
     * @return true when browser window closes or navigates to a different site.
     */
    public boolean isWindowClosing() {
        return windowClosing;
    }

    /**
     * Get the message that will be presented to the user in a confirmation dialog that
     * asks the user whether or not she wishes to navigate away from the page.
     * 
     * @return the message to display to the user, or null
     */
    public String getMessage() {
        if ((messageBuilder == null) || (messageBuilder.length() == 0)) {
            return null;
        }
        return messageBuilder.toString();
    }

    public boolean hasMessage() {
        return (messageBuilder != null) && (messageBuilder.length() != 0);
    }

    /**
     * Set the message to a <code>non-null</code> value to present a confirmation dialog
     * that asks the user whether or not she wishes to navigate away from the page. If
     * multiple handlers set the message, the last message will be displayed; all others
     * will be ignored.
     * 
     * @param message
     *            the message to display to the user, or null
     */
    public void addMessage(String message) {
        if ((message == null) || (message.length() == 0)) {
            return;
        }
        if (this.messageBuilder == null) {
            this.messageBuilder = new StringBuilder();
        } else {
            this.messageBuilder.append('\n');
        }
        this.messageBuilder.append(message);
    }

}
