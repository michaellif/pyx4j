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
package com.propertyvista.crm.client.activity;

import com.pyx4j.gwt.commons.ClientEventBus;
import com.pyx4j.rpc.client.RPCManager;
import com.pyx4j.rpc.client.SystemNotificationEvent;
import com.pyx4j.rpc.client.SystemNotificationHandler;

import com.propertyvista.crm.client.event.CommunicationStatusUpdateEvent;
import com.propertyvista.crm.rpc.dto.communication.CrmCommunicationSystemNotification;

public class CrmClientCommunicationManager {

    private static class SingletonHolder {
        public static final CrmClientCommunicationManager INSTANCE = new CrmClientCommunicationManager();
    }

    public static CrmClientCommunicationManager instance() {
        return SingletonHolder.INSTANCE;
    }

    private CrmClientCommunicationManager() {
        RPCManager.addSystemNotificationHandler(new SystemNotificationHandler() {

            @Override
            public void onSystemNotificationReceived(SystemNotificationEvent event) {
                if (event.getSystemNotification() instanceof CrmCommunicationSystemNotification) {
                    CrmCommunicationSystemNotification notification = (CrmCommunicationSystemNotification) event.getSystemNotification();
                    ClientEventBus.fireEvent(new CommunicationStatusUpdateEvent(notification));
                }
            }
        });
    }

}
