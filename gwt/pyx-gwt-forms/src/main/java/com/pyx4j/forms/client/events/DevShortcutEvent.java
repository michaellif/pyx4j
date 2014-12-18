/*
 * Pyx4j framework
 * Copyright (C) 2008-2011 pyx4j.com.
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
 * Created on 2011-01-19
 * @author vlads
 */
package com.pyx4j.forms.client.events;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Development Shortcut e.g. Ctrl+Q
 */
public class DevShortcutEvent extends GwtEvent<DevShortcutHandler> {

    private static Type<DevShortcutHandler> TYPE;

    public static Type<DevShortcutHandler> getType() {
        if (TYPE == null) {
            TYPE = new Type<DevShortcutHandler>();
        }
        return TYPE;
    }

    private final char keyCode;

    /**
     * A boolean indicating whether or not canceling the native event should be
     * prevented.
     */
    private boolean isConsumed = false;

    public DevShortcutEvent(int keyCode) {
        this.keyCode = (char) keyCode;
    }

    public char getKeyCode() {
        return keyCode;
    }

    @Override
    protected void dispatch(DevShortcutHandler handler) {
        if (!isConsumed) {
            handler.onDevShortcut(this);
        }
    }

    public void consume() {
        isConsumed = true;
    }

    public boolean isConsumed() {
        return isConsumed;
    }

    @Override
    public Type<DevShortcutHandler> getAssociatedType() {
        return TYPE;
    }

}
