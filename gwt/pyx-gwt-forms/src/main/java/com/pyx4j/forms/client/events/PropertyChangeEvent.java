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

public class PropertyChangeEvent extends GwtEvent<PropertyChangeHandler> {

    public static enum PropertyName {

        tooltip,

        title,

        enabled,

        visible,

        mandatory,

        editable,

        modifiable, //Relates to property of component representing collection. If true collection's add/remove/up/down actions are enabled

        orderable,

        collapsed,

        collapsible,

        valid

    };

    private final PropertyName propertyName;

    /**
     * Handler type.
     */
    private static Type<PropertyChangeHandler> TYPE;

    /**
     * Fires a property change event on all registered handlers in the handler manager. If
     * no such handlers exist, this method will do nothing.
     * 
     * @param propertyName
     *            the new value
     * @param source
     *            the source of the handlers
     */

    public static void fire(HasPropertyChangeHandlers source, PropertyName propertyName) {
        if (TYPE != null) {
            PropertyChangeEvent event = new PropertyChangeEvent(propertyName);
            source.fireEvent(event);
        }
    }

    /**
     * Gets the type associated with this event.
     * 
     * @return returns the handler type
     */
    public static Type<PropertyChangeHandler> getType() {
        if (TYPE == null) {
            TYPE = new Type<PropertyChangeHandler>();
        }
        return TYPE;
    }

    public PropertyChangeEvent(PropertyName propertyName) {
        this.propertyName = propertyName;
    }

    @Override
    public final Type<PropertyChangeHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(PropertyChangeHandler handler) {
        handler.onPropertyChange(this);
    }

    public PropertyName getPropertyName() {
        return propertyName;
    }

}
