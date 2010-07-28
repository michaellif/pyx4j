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
package com.pyx4j.widgets.client.tree.event;

import com.google.gwt.event.logical.shared.HasInitializeHandlers;
import com.google.gwt.event.logical.shared.InitializeEvent;
import com.google.gwt.event.logical.shared.InitializeHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HasHandlers;

public class TreeStructureChangeEvent extends GwtEvent<TreeStructureChangeHandler> {

    /**
     * The event type.
     */
    private static Type<TreeStructureChangeHandler> TYPE;

    /**
     * Fires a initialize event on all registered handlers in the handler source.
     * 
     * @param <S>
     *            The handler source
     * @param source
     *            the source of the handlers
     */
    public static <S extends HasInitializeHandlers & HasHandlers> void fire(S source) {
        if (TYPE != null) {
            TreeStructureChangeEvent event = new TreeStructureChangeEvent();
            source.fireEvent(event);
        }
    }

    /**
     * Ensures the existence of the handler hook and then returns it.
     * 
     * @return returns a handler hook
     */
    public static Type<TreeStructureChangeHandler> getType() {
        if (TYPE == null) {
            TYPE = new Type<TreeStructureChangeHandler>();
        }
        return TYPE;
    }

    /**
     * Construct a new {@link InitializeEvent}.
     * 
     */
    protected TreeStructureChangeEvent() {
    }

    @Override
    public final Type<TreeStructureChangeHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(TreeStructureChangeHandler handler) {
        handler.onBeforeClose(this);
    }
}
