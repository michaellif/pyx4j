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
 * Created on May 15, 2009
 * @author michaellif
 */
package com.pyx4j.widgets.client.event.shared;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Represents a Toggle event.
 * 
 */
public class ToggleEvent extends GwtEvent<ToggleHandler> {

    /**
     * Handler type.
     */
    private static Type<ToggleHandler> TYPE;

    /**
     * Fires a Toggle event on all registered handlers in the handler manager. If no
     * such handlers exist, this method will do nothing.
     * 
     * @param <I>
     *            the item type
     * @param source
     *            the source of the handlers
     * @return the event so that the caller can check if it was canceled, or null if no
     *         handlers of this event type have been registered
     */
    public static ToggleEvent fire(HasToggleHandlers source, boolean toggleOn) {
        // If no handlers exist, then type can be null.
        if (TYPE != null) {
            ToggleEvent event = new ToggleEvent(toggleOn);
            source.fireEvent(event);
            return event;
        }
        return null;
    }

    /**
     * Gets the type associated with this event.
     * 
     * @return returns the handler type
     */
    public static Type<ToggleHandler> getType() {
        if (TYPE == null) {
            TYPE = new Type<ToggleHandler>();
        }
        return TYPE;
    }

    private final boolean toggleOn;

    /**
     * Creates a new close event.
     */
    protected ToggleEvent(boolean toggleOn) {
        this.toggleOn = toggleOn;
    }

    // The instance knows its CloseHandler is of type I, but the TYPE
    // field itself does not, so we have to do an unsafe cast here.
    @Override
    public final Type<ToggleHandler> getAssociatedType() {
        return TYPE;
    }

    public boolean isToggleOn() {
        return toggleOn;
    }

    @Override
    protected void dispatch(ToggleHandler handler) {
        handler.onToggle(this);
    }

}
