/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Oct 16, 2013
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.biz.communication.notifications;

import com.propertyvista.domain.company.Notification.NotificationType;

public abstract class AbstractNotification {

    protected NotificationType type;

    public AbstractNotification(NotificationType type) {
        this.type = type;
    }

    public abstract void send();

    public abstract boolean aggregate(AbstractNotification other);

}