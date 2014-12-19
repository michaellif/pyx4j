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
package com.propertyvista.crm.rpc.dto.communication;

import java.io.Serializable;

import com.pyx4j.entity.rpc.EntitySearchResult;

import com.propertyvista.dto.MessageDTO;

@SuppressWarnings("serial")
public class CrmCommunicationSystemNotification implements Serializable {
    public int numberOfNewDirectMessages;

    public int numberOfNewDispatchedMessages;

    public EntitySearchResult<MessageDTO> notifications;

    public CrmCommunicationSystemNotification() {
    }

    public CrmCommunicationSystemNotification(int numberOfNewDirectMessages, int numberOfNewDispatchedMessages, EntitySearchResult<MessageDTO> notifications) {
        this.numberOfNewDirectMessages = numberOfNewDirectMessages;
        this.numberOfNewDispatchedMessages = numberOfNewDispatchedMessages;
        this.notifications = notifications;
    }
}
