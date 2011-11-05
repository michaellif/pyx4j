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
 * Created on Dec 22, 2009
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.tester.client.ui.event;

import com.google.gwt.event.shared.GwtEvent;

import com.pyx4j.forms.client.ui.CComponent;

public class CComponentBrowserEvent extends GwtEvent<CComponentBrowserHandler> {

    private static Type<CComponentBrowserHandler> TYPE;

    private final CComponent<?, ?> component;

    public CComponentBrowserEvent(CComponent<?, ?> component) {
        this.component = component;
    }

    public static Type<CComponentBrowserHandler> getType() {
        if (TYPE == null) {
            TYPE = new Type<CComponentBrowserHandler>();
        }
        return TYPE;
    }

    @Override
    public final Type<CComponentBrowserHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(CComponentBrowserHandler handler) {
        handler.onBrowseEntity(this);
    }

    public CComponent<?, ?> getComponent() {
        return component;
    }

}