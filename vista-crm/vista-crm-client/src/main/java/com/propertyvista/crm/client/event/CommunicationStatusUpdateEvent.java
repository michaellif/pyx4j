/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 15, 2011
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.crm.client.event;

import com.google.gwt.event.shared.GwtEvent;

import com.propertyvista.crm.rpc.dto.communication.CrmCommunicationSystemNotification;

public class CommunicationStatusUpdateEvent extends GwtEvent<CommunicationStatusUpdateHandler> {

    private static Type<CommunicationStatusUpdateHandler> TYPE = new Type<CommunicationStatusUpdateHandler>();

    private final CrmCommunicationSystemNotification notification;

    public static Type<CommunicationStatusUpdateHandler> getType() {
        return TYPE;
    }

    public CommunicationStatusUpdateEvent(CrmCommunicationSystemNotification notification) {
        this.notification = notification;
    }

    @Override
    public final Type<CommunicationStatusUpdateHandler> getAssociatedType() {
        return TYPE;
    }

    public CrmCommunicationSystemNotification getCommunicationSystemNotification() {
        return notification;
    }

    @Override
    protected void dispatch(CommunicationStatusUpdateHandler handler) {
        handler.onStatusUpdate(this);
    }
}