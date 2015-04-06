/*
 * Pyx4j framework
 * Copyright (C) 2008-2013 pyx4j.com.
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
 * Created on Sep 6, 2014
 * @author michaellif
 */
package com.pyx4j.widgets.client.event.shared;

import com.google.gwt.event.shared.GwtEvent;

public class PasteEvent extends GwtEvent<PasteHandler> {

    /**
     * Handler type.
     */
    private static Type<PasteHandler> TYPE;

    public static PasteEvent fire(HasPasteHandlers source) {
        // If no handlers exist, then type can be null.
        if (TYPE != null) {
            PasteEvent event = new PasteEvent();
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
    public static Type<PasteHandler> getType() {
        if (TYPE == null) {
            TYPE = new Type<PasteHandler>();
        }
        return TYPE;
    }

    /**
     * Creates a new close event.
     */
    protected PasteEvent() {
    }

    // The instance knows its CloseHandler is of type I, but the TYPE
    // field itself does not, so we have to do an unsafe cast here.
    @Override
    public final Type<PasteHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(PasteHandler handler) {
        handler.onPaste(this);
    }

}
