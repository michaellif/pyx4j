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
 * Created on Jan 11, 2010
 * @author Michael
 * @version $Id$
 */
package com.pyx4j.forms.client.events;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Represents a options change event.
 * 
 * @param <I>
 *            the type of options
 */
public class OptionsChangeEvent<I> extends GwtEvent<OptionsChangeHandler<I>> {

    /**
     * Handler type.
     */
    private static Type<OptionsChangeHandler<?>> TYPE;

    /**
     * Fires a options event on all registered handlers in the handler manager.If no such
     * handlers exist, this method will do nothing.
     * 
     * @param <I>
     *            the options type
     * @param source
     *            the source of the handlers
     * @param options
     *            the new options
     */
    public static <I> void fire(HasOptionsChangeHandlers<I> source, I options) {
        if (TYPE != null) {
            OptionsChangeEvent<I> event = new OptionsChangeEvent<I>(options);
            source.fireEvent(event);
        }
    }

    /**
     * Gets the type associated with this event.
     * 
     * @return returns the handler type
     */
    public static Type<OptionsChangeHandler<?>> getType() {
        if (TYPE == null) {
            TYPE = new Type<OptionsChangeHandler<?>>();
        }
        return TYPE;
    }

    private final I options;

    /**
     * Creates a new selection event.
     * 
     * @param options
     *            selected items
     */
    protected OptionsChangeEvent(I options) {
        this.options = options;
    }

    // The instance knows its BeforeSelectionHandler is of type I, but the TYPE
    // field itself does not, so we have to do an unsafe cast here.
    @SuppressWarnings("unchecked")
    @Override
    public final Type<OptionsChangeHandler<I>> getAssociatedType() {
        return (Type) TYPE;
    }

    /**
     * Gets the options.
     * 
     * @return the options
     */
    public I getOptions() {
        return options;
    }

    @Override
    protected void dispatch(OptionsChangeHandler<I> handler) {
        handler.onOptionsChange(this);
    }
}
