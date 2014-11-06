/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 23, 2014
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.shared.activity;

import com.pyx4j.rpc.client.RPCManager;
import com.pyx4j.rpc.client.SystemNotificationEvent;
import com.pyx4j.rpc.client.SystemNotificationHandler;

import com.propertyvista.portal.rpc.shared.dto.communication.PortalCommunicationSystemNotification;

public class PortalClientCommunicationManager {

    private PortalCommunicationSystemNotification latestNotification = null;

    private static class SingletonHolder {
        public static final PortalClientCommunicationManager INSTANCE = new PortalClientCommunicationManager();
    }

    public static PortalClientCommunicationManager instance() {
        return SingletonHolder.INSTANCE;
    }

    private PortalClientCommunicationManager() {
        RPCManager.addSystemNotificationHandler(new SystemNotificationHandler() {

            @Override
            public void onSystemNotificationReceived(SystemNotificationEvent event) {
                if (event.getSystemNotification() instanceof PortalCommunicationSystemNotification) {
                    latestNotification = (PortalCommunicationSystemNotification) event.getSystemNotification();
                }
            }
        });
    }

    public PortalCommunicationSystemNotification getLatestCommunicationNotification() {
        return latestNotification;
    }
}
