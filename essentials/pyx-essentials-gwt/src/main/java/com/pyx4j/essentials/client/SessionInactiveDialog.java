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
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.client.SessionInactiveHandler;
import com.pyx4j.security.client.SessionMonitor;
import com.pyx4j.widgets.client.dialog.Dialog;
import com.pyx4j.widgets.client.dialog.MessageDialog;
import com.pyx4j.widgets.client.dialog.OkOption;

public class SessionInactiveDialog implements SessionInactiveHandler {

    private static I18n i18n = I18n.get(SessionInactiveDialog.class);

    private static boolean shown;

    public static void register() {
        SessionMonitor.setSessionInactiveHandler(new SessionInactiveDialog());
    }

    private static class ShowOnceDialogOptions implements OkOption, CloseHandler<PopupPanel> {

        @Override
        public boolean onClickOk() {
            return true;
        }

        @Override
        public void onClose(CloseEvent<PopupPanel> event) {
            shown = false;
        }

    };

    @Override
    public void onSessionInactive(final boolean timeout) {
        if (shown) {
            return;
        }
        shown = true;

        String title = timeout ? i18n.tr("Session Inactive") : i18n.tr("Your Session Has Been Terminated");
        String reasonMessage;

        if (timeout) {
            reasonMessage = i18n.tr("Your Session Has Timed Out. Please Sign In To Continue");
        } else {
            reasonMessage = i18n.tr("Your Session Has Been Terminated." + "\nOnly One Application Session Can Be Active At The Same Time");
        }

        reasonMessage += "\n\n" + i18n.tr("Please login again.") + "\n";

        if (!timeout) {
            reasonMessage += "\n" + i18n.tr("Please Open Another Browser Window If You Want To Keep Multiple Sessions Active");
        } else {
            reasonMessage += "\n"
                    + i18n.tr("Session Duration {0}, Inactive For {1}", TimeUtils.minutesSince(SessionMonitor.getSessionStartTime()),
                            TimeUtils.minutesSince(SessionMonitor.getSessionInactiveTime()));
        }

        MessageDialog.show(title, reasonMessage, Dialog.Type.Info, new ShowOnceDialogOptions());
    }
}
