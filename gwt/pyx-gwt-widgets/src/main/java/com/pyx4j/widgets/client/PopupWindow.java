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
 * Created on 2010-09-20
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.widgets.client;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.user.client.Window;

import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.dialog.MessageDialog;

public class PopupWindow {

    private static final I18n i18n = I18n.get(PopupWindow.class);

    private static EventBus eventBus;

    public static class PopupWindowHandle extends JavaScriptObject {

        protected PopupWindowHandle() {

        }

        public final native boolean isClosed() /*-{
			return this.closed;
        }-*/;

        public final native String getName() /*-{
			return this.name;
        }-*/;

    }

    static {
        registerCallbacks();
    }

    public static native int windowScreenLeft() /*-{
		return $wnd.screenLeft != undefined ? $wnd.screenLeft : $wnd.screenX;
    }-*/;

    public static native int windowScreenTop() /*-{
		return $wnd.screenTop != undefined ? $wnd.screenTop : $wnd.screenY;
    }-*/;

    public static native String windowName() /*-{
		return $wnd.name;
    }-*/;

    public static native PopupWindowHandle openPopupWindow(String url, String name, String features) /*-{
		return $wnd.open(url, name, features);
    }-*/;

    /**
     * Opens Popup in the center of parent browser
     */
    public static PopupWindowHandle open(String url, String name, int width, int height) {
        int left = windowScreenLeft() + (int) Math.max(0, Math.floor((Window.getClientWidth() - width) / 2));
        int top = windowScreenTop() + (int) Math.max(0, Math.floor((Window.getClientHeight() - height) / 2));
        return open(url, name, left, top, width, height);
    }

    public static PopupWindowHandle open(String url, String name, int left, int top, int width, int height) {
        StringBuilder features = new StringBuilder();

        features.append("width=").append(width).append(",height=").append(height);
        features.append(",status=1,location=1,resizable=yes");
        features.append(",left=").append(left).append(",top=").append(top);

        PopupWindowHandle windowHandle = openPopupWindow(url, "", features.toString());
        if ((windowHandle == null) || (windowHandle.isClosed())) {
            // Detect blocked popup not working in Chrome
            MessageDialog.error(i18n.tr("Popup window blocked"), i18n.tr("Your Browser Prevented This Application From Opening A Popup Window\n"
                    + "Please Disable Your Popup Blocker For The Application To Function Properly"));
            return null;
        } else {
            return windowHandle;
        }
    }

    public native static String getUserAgent() /*-{
		return $wnd.navigator.userAgent;
    }-*/;

    private static native void registerCallbacks() /*-{
		$wnd.popupWindowSelectionMade = function(sel) {
			@com.pyx4j.widgets.client.PopupWindow::popupWindowSelectionMade(Ljava/lang/String;)(sel);
		}
    }-*/;

    private static void popupWindowSelectionMade(String selectionValue) {
        if (eventBus != null) {
            eventBus.fireEvent(new SelectionEvent<String>(selectionValue) {
            });
        }
    }

    /**
     * Allows the Child window to send some value to parent application.
     */
    public static HandlerRegistration addSelectionHandler(SelectionHandler<String> handler) {
        if (eventBus == null) {
            eventBus = new SimpleEventBus();
        }
        return eventBus.addHandler(SelectionEvent.getType(), handler);

    }
}
