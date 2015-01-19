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
 */
package com.propertyvista.biz.communication;

import java.io.Serializable;
import java.util.List;

import org.apache.commons.collections4.set.ListOrderedSet;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.criterion.EntityListCriteria;
import com.pyx4j.entity.rpc.EntitySearchResult;

import com.propertyvista.domain.communication.CommunicationAssociation;
import com.propertyvista.domain.communication.CommunicationEndpoint;
import com.propertyvista.domain.communication.CommunicationThread;
import com.propertyvista.domain.communication.CommunicationThread.ThreadStatus;
import com.propertyvista.domain.communication.DeliveryHandle;
import com.propertyvista.domain.communication.Message;
import com.propertyvista.domain.communication.MessageCategory;
import com.propertyvista.domain.communication.MessageCategory.TicketType;
import com.propertyvista.domain.communication.SystemEndpoint;
import com.propertyvista.domain.communication.SystemEndpoint.SystemEndpointName;
import com.propertyvista.domain.company.Employee;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.dto.communication.CommunicationEndpointDTO;
import com.propertyvista.dto.communication.CommunicationThreadDTO;
import com.propertyvista.dto.communication.MessageDTO;

public interface CommunicationMessageFacade {

    // Communication text builder -----------------------------------------------------------------
    String buildForwardSubject(MessageDTO forwardedMessage);

    String buildForwardText(MessageDTO forwardedMessage, String subject);

    // Communication category management -------------------------------------------------------
    MessageCategory getMessageCategoryFromCache(TicketType mgCategory);

    List<MessageCategory> getDispatchedMessageCategories(Employee employee, AttachLevel attachLevel);

    // Communication endpoint management -------------------------------------------------------
    SystemEndpoint getSystemEndpointFromCache(SystemEndpointName sep);

    String extractEndpointName(CommunicationEndpoint entity);

    DeliveryHandle createDeliveryHandle(CommunicationEndpoint endpoint, boolean generatedFromGroup);

    CommunicationEndpointDTO generateEndpointDTO(CommunicationEndpoint entity);

    String sendersAsStringView(ListOrderedSet<CommunicationEndpoint> senders);

    void buildRecipientList(Message bo, MessageDTO to, CommunicationThread thread);

    void buildRecipientsList4UnitLeaseParticipants(Message message, AptUnit unit, boolean includeGuarantors);

    // Communication entity common management -------------------------------------------------------
    EntitySearchResult<CommunicationThread> query(EntityListCriteria<CommunicationThread> criteria);

    Serializable getCommunicationStatus();

    boolean isDispatchedThread(Key threadKey, boolean includeByRoles, Employee e);

    List<CommunicationThread> getDirectThreads(CommunicationEndpoint e);

    List<CommunicationThread> getDispathcedThreads(Employee e);

    void enhanceThreadDbo(CommunicationThread bo, CommunicationThreadDTO to, boolean isForList, CommunicationEndpoint currentUser);

    Message saveMessage(MessageDTO message, ThreadStatus threadStatus, CommunicationEndpoint currentUser, boolean updateOwner);

    // Communication associated entity management -------------------------------------------------------
    CommunicationThread association2Thread(CommunicationAssociation ca, CommunicationEndpoint currentUser, String messageBody);

    Message association2Message(CommunicationAssociation ca);

    Message associationChange2Message(CommunicationAssociation ca, CommunicationEndpoint currentUser, String messageBody);
}
