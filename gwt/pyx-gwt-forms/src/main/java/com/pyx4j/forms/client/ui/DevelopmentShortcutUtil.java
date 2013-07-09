/*
 * Pyx4j framework
 * Copyright (C) 2008-2012 pyx4j.com.
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
 * Created on 2012-10-03
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.forms.client.ui;

import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.forms.client.events.DevShortcutEvent;

/**
 * Helper to avoid "java.lang.IncompatibleClassChangeError: Found interface com.google.gwt.user.client.Event, but class was expected"
 */
class DevelopmentShortcutUtil {

    static final void attachDevelopmentShortcuts(Widget widget, final CComponent<?> component) {

        widget.addDomHandler(new KeyDownHandler() {

            @Override
            public void onKeyDown(KeyDownEvent event) {
                if ((event.getNativeEvent().getCtrlKey() && (event.getNativeKeyCode() != KeyCodes.KEY_CTRL))) {
                    DevShortcutEvent devShortcutEvent = new DevShortcutEvent(event.getNativeEvent().getKeyCode());
                    component.fireEvent(devShortcutEvent);
                    if (devShortcutEvent.isConsumed()) {
                        event.preventDefault();
                    }
                }

            }
        }, KeyDownEvent.getType());

    }
}
