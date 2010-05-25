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
 * Created on May 25, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.essentials.client;

import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.ui.PopupPanel;

import com.pyx4j.commons.TimeUtils;
import com.pyx4j.widgets.client.dialog.MessageDialog;

public class SessionInactiveDialog {

    private static boolean shown;

    public static void showSessionInactive(boolean timeout) {
        if (shown) {
            return;
        }
        shown = true;
        String title = timeout ? "Session inactive" : "Your session has been terminated";
        String reasonMessage;
        if (timeout) {
            reasonMessage = "You have been logged out due to inactivity.";
        } else {
            reasonMessage = "Your session has been terminated.";
            reasonMessage += "\nOnly one Application session can be active in a browser.";
        }

        reasonMessage += "\n\nPlease login again.\n";

        if (!timeout) {
            reasonMessage += "\nOpen another browser instance to keep multiple active sessions.";
        } else {
            reasonMessage += "\nSession duration " + TimeUtils.minutesSince(SessionMonitor.getSessionStartTime()) + ", inactive for "
                    + TimeUtils.minutesSince(SessionMonitor.getSessionInactiveTime());
        }

        MessageDialog.info(title, reasonMessage).addCloseHandler(new CloseHandler<PopupPanel>() {
            @Override
            public void onClose(CloseEvent<PopupPanel> event) {
                shown = false;
            }
        });
    }
}
