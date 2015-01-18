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
 */
package com.propertyvista.portal.rpc.shared.dto.communication;

import java.io.Serializable;

import com.pyx4j.entity.rpc.EntitySearchResult;

import com.propertyvista.dto.communication.MessageDTO;

@SuppressWarnings("serial")
public class PortalCommunicationSystemNotification implements Serializable {
    public int numberOfNewDirectMessages;

    public EntitySearchResult<MessageDTO> notifications;

    public PortalCommunicationSystemNotification() {
    }

    public PortalCommunicationSystemNotification(int numberOfNewDirectMessages, EntitySearchResult<MessageDTO> notifications) {
        this.numberOfNewDirectMessages = numberOfNewDirectMessages;
        this.notifications = notifications;
    }
}
