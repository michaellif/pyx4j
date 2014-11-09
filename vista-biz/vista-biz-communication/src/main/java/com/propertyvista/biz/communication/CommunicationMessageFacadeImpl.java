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
import com.propertyvista.dto.CommunicationEndpointDTO;
import com.propertyvista.dto.MessageDTO;

public class CommunicationMessageFacadeImpl implements CommunicationMessageFacade {

    // Communication text builder -----------------------------------------------------------------
    @Override
    public String buildForwardSubject(MessageDTO forwardedMessage) {
        return MessageTextBuilder.buildForwardSubject(forwardedMessage);
    }

    @Override
    public String buildForwardText(MessageDTO forwardedMessage) {
        return MessageTextBuilder.buildForwardText(forwardedMessage);
    }

    // Communication category management -------------------------------------------------------
    @Override
    public MessageCategory getMessageCategoryFromCache(TicketType mgCategory) {
        return MessageCategoryManager.instance().getMessageCategoryFromCache(mgCategory);
    }

    @Override
    public List<MessageCategory> getDispatchedMessageCategories(Employee employee, AttachLevel attachLevel) {
        return MessageCategoryManager.instance().getDispatchedMessageCategegories(employee, attachLevel);
    }

    // Communication endpoint management -------------------------------------------------------
    @Override
    public SystemEndpoint getSystemEndpointFromCache(SystemEndpointName sep) {
        return CommunicationEndpointManager.instance().getSystemEndpointFromCache(sep);
    }

    @Override
    public String extractEndpointName(CommunicationEndpoint entity) {
        return CommunicationEndpointManager.instance().extractEndpointName(entity);
    }

    @Override
    public DeliveryHandle createDeliveryHandle(CommunicationEndpoint endpoint, boolean generatedFromGroup) {
        return CommunicationEndpointManager.instance().createDeliveryHandle(endpoint, generatedFromGroup);
    }

    @Override
    public CommunicationEndpointDTO generateEndpointDTO(CommunicationEndpoint entity) {
        return CommunicationEndpointManager.instance().generateEndpointDTO(entity);
    }

    @Override
    public String sendersAsStringView(ListOrderedSet<CommunicationEndpoint> senders) {
        return CommunicationEndpointManager.instance().sendersAsStringView(senders);
    }

    @Override
    public void buildRecipientList(Message bo, MessageDTO to, CommunicationThread thread) {
        CommunicationEndpointManager.instance().buildRecipientList(bo, to, thread);
    }

    @Override
    public void buildRecipientsList4UnitLeaseParticipants(Message message, AptUnit unit, boolean includeGuarantors) {
        CommunicationEndpointManager.instance().buildRecipientsList4UnitLeaseParticipants(message, unit, includeGuarantors);
    }

    // Communication entity common management -------------------------------------------------------
    @Override
    public EntitySearchResult<Message> query(EntityListCriteria<Message> criteria) {
        return CommunicationManager.instance().query(criteria);
    }

    @Override
    public Serializable getCommunicationStatus() {
        return CommunicationManager.instance().getCommunicationStatus();
    }

    @Override
    public List<CommunicationThread> getDispathcedThreads(Employee e) {
        return CommunicationManager.instance().getDispatchedThreads(e);
    }

    @Override
    public List<CommunicationThread> getDirectThreads(CommunicationEndpoint ep) {
        return CommunicationManager.instance().getDirectThreads(ep);
    }

    @Override
    public boolean isDispatchedThread(Key threadKey, boolean includeByRoles, Employee e) {
        return CommunicationManager.instance().isDispatchedThread(threadKey, includeByRoles, e);
    }

    @Override
    public void enhanceMessageDbo(Message bo, MessageDTO to, boolean isForList, CommunicationEndpoint currentUser) {
        CommunicationManager.instance().enhanceMessageDbo(bo, to, isForList, currentUser);
    }

    @Override
    public Message saveMessage(MessageDTO message, ThreadStatus threadStatus, CommunicationEndpoint currentUser, boolean updateOwner) {
        return CommunicationManager.instance().saveMessage(message, threadStatus, currentUser, updateOwner);
    }

    // Communication associated entity management -------------------------------------------------------
    @Override
    public CommunicationThread association2Thread(CommunicationAssociation ca, CommunicationEndpoint currentUser, String messageBody) {
        return CommunicationAssociationManager.association2Thread(ca, currentUser, messageBody);
    }

    @Override
    public Message association2Message(CommunicationAssociation ca) {
        return CommunicationAssociationManager.association2Message(ca);
    }

    @Override
    public Message associationChange2Message(CommunicationAssociation ca, CommunicationEndpoint currentUser, String messageBody) {
        return CommunicationAssociationManager.associationChange2Message(ca, currentUser, messageBody);
    }
}
