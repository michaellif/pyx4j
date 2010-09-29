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
 * Created on 2010-09-28
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.rpc.shared;

import java.io.Serializable;
import java.util.Vector;

public class SystemNotificationsWrapper implements Serializable {

    private static final long serialVersionUID = -1613144537811931076L;

    private Serializable serviceResult;

    private Vector<Serializable> systemNotifications;

    public SystemNotificationsWrapper() {

    }

    public SystemNotificationsWrapper(Serializable serviceResult) {
        this.serviceResult = serviceResult;
    }

    public Vector<Serializable> getSystemNotifications() {
        return systemNotifications;
    }

    public void addSystemNotification(Serializable systemNotification) {
        if (this.systemNotifications == null) {
            this.systemNotifications = new Vector<Serializable>();
        }
        this.systemNotifications.add(systemNotification);
    }

    public void addSystemNotifications(Vector<Serializable> systemNotifications) {
        if (this.systemNotifications == null) {
            this.systemNotifications = systemNotifications;
        } else {
            this.systemNotifications.addAll(systemNotifications);
        }
    }

    public Serializable getServiceResult() {
        return serviceResult;
    }

}
