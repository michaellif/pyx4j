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
 * Created on 2010-05-14
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.site.shared.domain;

import com.pyx4j.commons.IDebugId;
import com.pyx4j.i18n.annotations.I18n;
import com.pyx4j.i18n.shared.I18nEnum;

public class Notification {

    @I18n
    public enum NotificationType implements IDebugId {

        INFO, WARNING, ERROR, FAILURE, STATUS;

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
