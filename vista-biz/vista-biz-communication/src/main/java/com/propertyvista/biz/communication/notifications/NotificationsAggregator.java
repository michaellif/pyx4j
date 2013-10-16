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

import java.util.HashMap;
import java.util.Map;

public class NotificationsAggregator {

    Map<Class<? extends AbstractNotification>, AbstractNotification> aggregations = new HashMap<Class<? extends AbstractNotification>, AbstractNotification>();

    public void aggregate(AbstractNotification notification) {
        AbstractNotification current = aggregations.get(notification.getClass());
        if (current == null) {
            aggregations.put(notification.getClass(), notification);
        } else {
            if (!current.aggregate(notification)) {
                // Start new aggregation 
                current.send();
                aggregations.put(notification.getClass(), notification);
            }
        }
    }

    public void send() {
        try {
            for (AbstractNotification notification : aggregations.values()) {
                notification.send();
            }
        } finally {
            aggregations.clear();
        }
    }

}