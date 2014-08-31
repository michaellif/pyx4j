/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 5, 2014
 * @author smolka
 * @version $Id$
 */
package com.propertyvista.biz.communication;

import java.io.Serializable;
import java.util.List;

import org.apache.commons.collections4.set.ListOrderedSet;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.criterion.EntityListCriteria;
import com.pyx4j.entity.rpc.EntitySearchResult;

import com.propertyvista.domain.communication.CommunicationEndpoint;
import com.propertyvista.domain.communication.CommunicationThread;
import com.propertyvista.domain.communication.DeliveryHandle;
import com.propertyvista.domain.communication.Message;
import com.propertyvista.domain.communication.MessageCategory;
import com.propertyvista.domain.communication.MessageCategory.TicketType;
import com.propertyvista.domain.communication.SystemEndpoint;
import com.propertyvista.domain.communication.SystemEndpoint.SystemEndpointName;
import com.propertyvista.domain.company.Employee;
import com.propertyvista.dto.CommunicationEndpointDTO;
import com.propertyvista.dto.MessageDTO;

public interface CommunicationMessageFacade {

    public String buildForwardSubject(MessageDTO forwardedMessage);

    public String buildForwardText(MessageDTO forwardedMessage);

    public MessageCategory getMessageCategoryFromCache(TicketType mgCategory);

    public List<MessageCategory> getDispatchedMessageCategories(Employee employee, AttachLevel attachLevel);

    public SystemEndpoint getSystemEndpointFromCache(SystemEndpointName sep);

    public String extractEndpointName(CommunicationEndpoint entity);

    public DeliveryHandle createDeliveryHandle(CommunicationEndpoint endpoint, boolean generatedFromGroup);

    public CommunicationEndpointDTO generateEndpointDTO(CommunicationEndpoint entity);

    public String sendersAsStringView(ListOrderedSet<CommunicationEndpoint> senders);

    public void buildRecipientList(Message bo, MessageDTO to, CommunicationThread thread);

    public EntitySearchResult<Message> query(EntityListCriteria<Message> criteria);

    public Serializable getCommunicationStatus();

    boolean isDispatchedThread(Key threadKey, boolean includeByRoles, Employee e);

    List<CommunicationThread> getDirectThreads(CommunicationEndpoint e);

    List<CommunicationThread> getDispathcedThreads(Employee e);
}
