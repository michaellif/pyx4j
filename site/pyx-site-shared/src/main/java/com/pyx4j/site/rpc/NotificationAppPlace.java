/*
 * Pyx4j framework
 * Copyright (C) 2006-2010 pyx4j.com.
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
 * Created on Jun 11, 2011
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.site.rpc;

import com.pyx4j.i18n.annotations.I18n;
import com.pyx4j.i18n.annotations.I18n.I18nStrategy;
import com.pyx4j.site.shared.domain.Notification;

@I18n(strategy = I18nStrategy.IgnoreAll)
public abstract class NotificationAppPlace extends AppPlace {

    private Notification notification;

    private AppPlace continuePlace;

    public NotificationAppPlace() {
        setStable(false);
        // Xui
    }

    public void setNotification(Notification notification) {
        this.notification = notification;
    }

    public Notification getNotification() {
        return notification;
    }

    public AppPlace getContinuePlace() {
        return continuePlace;
    }

    public void setContinuePlace(AppPlace continuePlace) {
        this.continuePlace = continuePlace;
    }
}
