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
 * @version $Id$
 */
package com.pyx4j.widgets.client.event.shared;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Represents a before selection event.
 * 
 * @param <I>
 *            the type about to be selected
 */
public class BeforeCloseEvent<I> extends GwtEvent<BeforeCloseHandler<I>> {

    /**
     * Handler type.
     */
    private static Type<BeforeCloseHandler<?>> TYPE;

    /**
     * Fires a before close event on all registered handlers in the handler manager. If no
     * such handlers exist, this method will do nothing.
     * 
     * @param <I>
     *            the item type
     * @param source
     *            the source of the handlers
     * @param item
     *            the item
     * @return the event so that the caller can check if it was canceled, or null if no
     *         handlers of this event type have been registered
     */
    public static <I> BeforeCloseEvent<I> fire(HasBeforeCloseHandlers<I> source, I item) {
        // If no handlers exist, then type can be null.
        if (TYPE != null) {
            BeforeCloseEvent<I> event = new BeforeCloseEvent<I>();
            event.setItem(item);
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
    public static Type<BeforeCloseHandler<?>> getType() {
        if (TYPE == null) {
            TYPE = new Type<BeforeCloseHandler<?>>();
        }
        return TYPE;
    }

    private I item;

    private boolean canceled;

    /**
     * Creates a new before close event.
     */
    protected BeforeCloseEvent() {
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
    @SuppressWarnings("unchecked")
    @Override
    public final Type<BeforeCloseHandler<I>> getAssociatedType() {
        return (Type) TYPE;
    }

    /**
     * Gets the item.
     * 
     * @return the item
     */
    public I getItem() {
        return item;
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
    protected void dispatch(BeforeCloseHandler<I> handler) {
        handler.onBeforeClose(this);
    }

    /**
     * Sets the item.
     * 
     * @param item
     *            the item
     */
    protected final void setItem(I item) {
        this.item = item;
    }
}
