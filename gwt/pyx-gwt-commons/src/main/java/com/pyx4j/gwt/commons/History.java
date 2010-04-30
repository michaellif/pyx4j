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
 * Created on Apr 30, 2010
 * @author Misha
 * @version $Id$
 */
package com.pyx4j.gwt.commons;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.impl.HistoryImpl;

public class History {
    private static HistoryImpl impl;

    static {
        impl = GWT.create(HistoryImpl.class);
        if (!impl.init()) {
            // Set impl to null as a flag to no-op future calls.
            impl = null;

            // Tell the user.
            GWT.log("Unable to initialize the history subsystem; did you " + "include the history frame in your host page? Try "
                    + "<iframe src=\"javascript:''\" id='__gwt_historyFrame' " + "style='position:absolute;width:0;height:0;border:0'>" + "</iframe>");
        }
    }

    /**
     * Adds a {@link com.google.gwt.event.logical.shared.ValueChangeEvent} handler to be
     * informed of changes to the browser's history stack.
     * 
     * @param handler
     *            the handler
     * @return the registration used to remove this value change handler
     */
    public static HandlerRegistration addValueChangeHandler(ValueChangeHandler<String> handler) {
        return impl != null ? impl.addValueChangeHandler(handler) : null;
    }

    public static void removeValueChangeHandler(ValueChangeHandler<String> handler) {
        if (impl != null) {
            impl.getHandlers().removeHandler(ValueChangeEvent.getType(), handler);
        }
    }

    /**
     * Programmatic equivalent to the user pressing the browser's 'back' button.
     * 
     * Note that this does not work correctly on Safari 2.
     */
    public static native void back() /*-{
        $wnd.history.back();
    }-*/;

    /**
     * Fire
     * {@link ValueChangeHandler#onValueChange(com.google.gwt.event.logical.shared.ValueChangeEvent)}
     * events with the current history state. This is most often called at the end of an
     * application's {@link com.google.gwt.core.client.EntryPoint#onModuleLoad()} to
     * inform history handlers of the initial application state.
     */
    public static void fireCurrentHistoryState() {
        if (impl != null) {
            String token = getToken();
            impl.fireHistoryChangedImpl(token);
        }
    }

    /**
     * Programmatic equivalent to the user pressing the browser's 'forward' button.
     */
    public static native void forward() /*-{
        $wnd.history.forward();
    }-*/;

    /**
     * Gets the current history token. The handler will not receive a
     * {@link ValueChangeHandler#onValueChange(com.google.gwt.event.logical.shared.ValueChangeEvent)}
     * event for the initial token; requiring that an application request the token
     * explicitly on startup gives it an opportunity to run different initialization code
     * in the presence or absence of an initial token.
     * 
     * @return the initial token, or the empty string if none is present.
     */
    public static String getToken() {
        return impl != null ? HistoryImpl.getToken() : "";
    }

    /**
     * Adds a new browser history entry. In hosted mode, the 'back' and 'forward' actions
     * are accessible via the standard Alt-Left and Alt-Right keystrokes. Calling this
     * method will cause
     * {@link ValueChangeHandler#onValueChange(com.google.gwt.event.logical.shared.ValueChangeEvent)}
     * to be called as well.
     * 
     * @param historyToken
     *            the token to associate with the new history item
     */
    public static void newItem(String historyToken) {
        newItem(historyToken, true);
    }

    /**
     * Adds a new browser history entry. In hosted mode, the 'back' and 'forward' actions
     * are accessible via the standard Alt-Left and Alt-Right keystrokes. Calling this
     * method will cause
     * {@link ValueChangeHandler#onValueChange(com.google.gwt.event.logical.shared.ValueChangeEvent)}
     * to be called as well if and only if issueEvent is true.
     * 
     * @param historyToken
     *            the token to associate with the new history item
     * @param issueEvent
     *            true if a
     *            {@link ValueChangeHandler#onValueChange(com.google.gwt.event.logical.shared.ValueChangeEvent)}
     *            event should be issued
     */
    public static void newItem(String historyToken, boolean issueEvent) {
        if (impl != null) {
            impl.newItem(historyToken, issueEvent);
        }
    }

    /**
     * Call all history handlers with the specified token. Note that this does not change
     * the history system's idea of the current state and is only kept for backward
     * compatibility. To fire history events for the initial state of the application,
     * instead call {@link #fireCurrentHistoryState()} from the application
     * {@link com.google.gwt.core.client.EntryPoint#onModuleLoad()} method.
     * 
     * @param historyToken
     *            history token to fire events for
     * @deprecated Use {@link #fireCurrentHistoryState()} instead.
     */
    @Deprecated
    public static void onHistoryChanged(String historyToken) {
        if (impl != null) {
            impl.fireHistoryChangedImpl(historyToken);
        }
    }

}
