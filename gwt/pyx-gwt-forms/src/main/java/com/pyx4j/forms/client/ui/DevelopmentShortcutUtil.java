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

import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.forms.client.events.DevShortcutEvent;

/**
 * Helper to avoid "java.lang.IncompatibleClassChangeError: Found interface com.google.gwt.user.client.Event, but class was expected"
 */
class DevelopmentShortcutUtil {

    static final void attachDevelopmentShortcuts(Widget widget, final CComponent<?, ?> component) {

        widget.addAttachHandler(new AttachEvent.Handler() {

            private HandlerRegistration handlerRegistration = null;

            @Override
            public void onAttachOrDetach(AttachEvent event) {
                if (event.isAttached()) {
                    handlerRegistration = Event.addNativePreviewHandler(new NativePreviewHandler() {
                        @Override
                        public void onPreviewNativeEvent(NativePreviewEvent event) {
                            if ((event.getTypeInt() == Event.ONKEYDOWN && event.getNativeEvent().getCtrlKey())) {
                                DevShortcutEvent devShortcutEvent = new DevShortcutEvent(event.getNativeEvent().getKeyCode());
                                component.fireEvent(devShortcutEvent);
                                if (devShortcutEvent.isConsumed()) {
                                    event.getNativeEvent().preventDefault();
                                }
                            }
                        }
                    });
                } else if (handlerRegistration != null) {
                    handlerRegistration.removeHandler();
                }
            }
        });
    }
}
