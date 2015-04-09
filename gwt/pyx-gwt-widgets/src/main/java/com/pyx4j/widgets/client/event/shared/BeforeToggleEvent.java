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
 * Represents a BeforeToggle event.
 * 
 */
public class BeforeToggleEvent extends GwtEvent<BeforeToggleHandler> {

    /**
     * Handler type.
     */
    private static Type<BeforeToggleHandler> TYPE;

    /**
     * Fires a BeforeToggle event on all registered handlers in the handler manager. If no
     * such handlers exist, this method will do nothing.
     * 
     * @param <I>
     *            the item type
     * @param source
     *            the source of the handlers
     * @return the event so that the caller can check if it was canceled, or null if no
     *         handlers of this event type have been registered
     */
    public static BeforeToggleEvent fire(HasToggleHandlers source, boolean toggleOn) {
        // If no handlers exist, then type can be null.
        if (TYPE != null) {
            BeforeToggleEvent event = new BeforeToggleEvent(toggleOn);
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
    public static Type<BeforeToggleHandler> getType() {
        if (TYPE == null) {
            TYPE = new Type<BeforeToggleHandler>();
        }
        return TYPE;
    }

    private boolean canceled;

    private final boolean toggleOn;

    /**
     * Creates a new before close event.
     */
    protected BeforeToggleEvent(boolean toggleOn) {
        this.toggleOn = toggleOn;
    }

    /**
     * Cancel the before close event.
     * 
     * Classes overriding this method should still call super.cancel().
     */
    public void cancel() {
        canceled = true;
    }

    // The instance knows its BeforeCloseHandler is of type I, but the TYPE
    // field itself does not, so we have to do an unsafe cast here.
    @Override
    public final Type<BeforeToggleHandler> getAssociatedType() {
        return TYPE;
    }

    public boolean isToggleOn() {
        return toggleOn;
    }

    /**
     * Has the selection event already been canceled?
     * 
     * @return is canceled
     */
    public boolean isCanceled() {
        return canceled;
    }

    @Override
    protected void dispatch(BeforeToggleHandler handler) {
        handler.onBeforeToggle(this);
    }

}
