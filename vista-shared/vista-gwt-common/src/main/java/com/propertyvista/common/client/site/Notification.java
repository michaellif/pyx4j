/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 13, 2011
 * @author Misha
 * @version $Id$
 */
package com.propertyvista.common.client.site;

import com.pyx4j.commons.IDebugId;
import com.pyx4j.i18n.annotations.I18n;
import com.pyx4j.i18n.shared.I18nEnum;

public class Notification {

    @I18n
    public enum NotificationType implements IDebugId {

        INFO, WARNING, ERROR, FAILURE, CONFIRM;

        @Override
        public String debugId() {
            return this.name();
        }

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }
    }

    private final String message;

    private final String title;

    private final NotificationType notificationType;

    private String systemInfo;

    public Notification(String message, NotificationType type, String title) {
        this.message = message;
        this.title = title;
        notificationType = type;
    }

    public void setSystemInfo(String message) {
        systemInfo = message;
    }

    public String getSystemInfo() {
        return systemInfo;
    }

    public NotificationType getNotificationType() {
        return notificationType;
    }

    public String getMessage() {
        return message;
    }

    public String getTitle() {
        return title;
    }

}
