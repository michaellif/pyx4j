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
package com.propertyvista.portal.web.client;

import com.pyx4j.commons.TimeUtils;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.client.SessionInactiveEvent;
import com.pyx4j.security.client.SessionInactiveHandler;
import com.pyx4j.security.client.SessionMonitor;
import com.pyx4j.site.shared.domain.Notification;
import com.pyx4j.site.shared.domain.Notification.NotificationType;

public class PortalSessionInactiveHandler implements SessionInactiveHandler {

    private static final I18n i18n = I18n.get(PortalSessionInactiveHandler.class);

    public static void register() {
        SessionMonitor.addSessionInactiveHandler(new PortalSessionInactiveHandler());
    }

    @Override
    public void onSessionInactive(SessionInactiveEvent event) {

        String reasonMessage;

        if (event.isTimeout()) {
            reasonMessage = i18n.tr("Your Session Has Timed Out.");
        } else {
            reasonMessage = i18n.tr("Your Session Has Been Terminated." + "\nOnly One Application Session Can Be Active At The Same Time");
        }

        reasonMessage += "\n\n" + i18n.tr("Please Sign In To Continue.") + "\n";

        if (!event.isTimeout()) {
            reasonMessage += "\n" + i18n.tr("Please Open Another Browser Window If You Want To Keep Multiple Sessions Active");
        } else {
            reasonMessage += "\n"
                    + i18n.tr("Session Duration {0}, Inactive For {1}", TimeUtils.minutesSince(SessionMonitor.getSessionStartTime()),
                            TimeUtils.minutesSince(SessionMonitor.getSessionInactiveTime()));
        }

        Notification message = new Notification(reasonMessage, "", NotificationType.INFO);
        PortalWebSite.getPlaceController().showNotification(message);

    }

}
