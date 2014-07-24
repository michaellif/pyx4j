/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 31, 2014
 * @author smolka
 * @version $Id$
 */
package com.propertyvista.crm.server.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Vector;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.config.server.SystemDateManager;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IList;
import com.pyx4j.entity.core.Path;
import com.pyx4j.entity.core.criterion.EntityListCriteria;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.server.AbstractCrudServiceDtoImpl;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.essentials.server.upload.FileUploadRegistry;

import com.propertyvista.biz.communication.CommunicationMessageFacade;
import com.propertyvista.crm.rpc.services.MessageCrudService;
import com.propertyvista.crm.server.util.CrmAppContext;
import com.propertyvista.domain.communication.CommunicationEndpoint;
import com.propertyvista.domain.communication.CommunicationEndpoint.ContactType;
import com.propertyvista.domain.communication.CommunicationThread;
import com.propertyvista.domain.communication.CommunicationThread.ThreadStatus;
import com.propertyvista.domain.communication.DeliveryHandle;
import com.propertyvista.domain.communication.Message;
import com.propertyvista.domain.communication.MessageAttachment;
import com.propertyvista.domain.communication.MessageCategory.MessageGroupCategory;
import com.propertyvista.domain.communication.SystemEndpoint.SystemEndpointName;
import com.propertyvista.domain.company.Employee;
import com.propertyvista.domain.company.Portfolio;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.Tenant;
import com.propertyvista.dto.CommunicationEndpointDTO;
import com.propertyvista.dto.MessageDTO;

public class MessageCrudServiceImpl extends AbstractCrudServiceDtoImpl<Message, MessageDTO> implements MessageCrudService {
    public MessageCrudServiceImpl() {
        super(Message.class, MessageDTO.class);
    }

    @Override
    protected Path convertPropertyDTOPathToDBOPath(String path, Message boProto, MessageDTO toProto) {
        if (path.equals(toProto.topic().getPath().toString())) {
            return boProto.thread().topic().getPath();
        }
        if (path.equals(toProto.topic().category().getPath().toString())) {
            return boProto.thread().topic().category().getPath();
        }
        if (path.equals(toProto.content().$().recipients().$().isRead().getPath().toString()) || path.equals(toProto.isRead().getPath().toString())) {
            return boProto.recipients().$().isRead().getPath();
        }
        if (path.equals(toProto.content().$().recipients().$().star().getPath().toString()) || path.equals(toProto.star().getPath().toString())) {
            return boProto.recipients().$().star().getPath();
        }
        if (path.equals(toProto.content().$().recipients().$().recipient().getPath().toString())) {
            return boProto.recipients().$().recipient().getPath();
        }
        if (path.equals(toProto.content().$().date().getPath().toString())) {
            return boProto.date().getPath();
        }
        if (path.equals(toProto.subject().getPath().toString())) {
            return boProto.thread().subject().getPath();
        }
        return super.convertPropertyDTOPathToDBOPath(path, boProto, toProto);
    }

    @Override
    protected void enhanceListCriteria(EntityListCriteria<Message> boCriteria, EntityListCriteria<MessageDTO> toCriteria) {
        List<Sort> sorts = toCriteria.getSorts();
        if (sorts == null || sorts.isEmpty()) {
            toCriteria.desc(toCriteria.proto().date());
        }
        PropertyCriterion recipientCiteria = toCriteria.getCriterion(toCriteria.proto().thread().content().$().recipients().$().recipient());
        if (recipientCiteria != null) {
            toCriteria.getFilters().remove(recipientCiteria);
            boCriteria.eq(boCriteria.proto().recipients().$().recipient(), CrmAppContext.getCurrentUser());
        }
        super.enhanceListCriteria(boCriteria, toCriteria);
    }

    @Override
    protected EntitySearchResult<Message> query(EntityListCriteria<Message> criteria) {
        return ServerSideFactory.create(CommunicationMessageFacade.class).query(criteria);
    }

    @Override
    public void listForHeader(AsyncCallback<EntitySearchResult<MessageDTO>> callback) {
        CommunicationMessageFacade communicationFacade = ServerSideFactory.create(CommunicationMessageFacade.class);

        Vector<CommunicationThread> directThreads = communicationFacade.getDirectThreads();
        Vector<CommunicationThread> dispatchedThreads = communicationFacade.getDispathcedThreads();

        if (directThreads != null && directThreads.size() > 0 && dispatchedThreads != null && dispatchedThreads.size() > 0) {
            directThreads.removeAll(dispatchedThreads);
        }

        if (directThreads == null) {
            directThreads = new Vector<CommunicationThread>();
        }

        if (dispatchedThreads != null && dispatchedThreads.size() > 0) {
            directThreads.addAll(dispatchedThreads);
        }

        EntityListCriteria<MessageDTO> messageCriteria = EntityListCriteria.create(MessageDTO.class);
        if (directThreads != null && directThreads.size() > 0) {
            messageCriteria.add(PropertyCriterion.in(messageCriteria.proto().thread(), directThreads));
        } else {
            messageCriteria.notExists(messageCriteria.proto().thread());
        }
        messageCriteria.setPageSize(50);
        messageCriteria.setPageNumber(0);
        list(callback, messageCriteria);
    }

    @Override
    protected MessageDTO init(InitializationData initializationData) {
        MessageDTO dto = EntityFactory.create(MessageDTO.class);
        dto.date().setValue(SystemDateManager.getDate());
        dto.isRead().setValue(false);
        dto.highImportance().setValue(false);
        dto.allowedReply().setValue(true);
        dto.status().setValue(ThreadStatus.Unassigned);
        dto.sender().set((ServerSideFactory.create(CommunicationMessageFacade.class).generateEndpointDTO(CrmAppContext.getCurrentUser())));

        if (initializationData instanceof MessageInitializationData) {
            MessageInitializationData data = (MessageInitializationData) initializationData;
            if (data.forwardedMessage() != null && !data.forwardedMessage().isNull()) {
                MessageDTO forwardedMessage = data.forwardedMessage();
                if (forwardedMessage != null) {
                    dto.subject().setValue(buildForwardSubject(forwardedMessage));
                    dto.text().setValue(buildForwardText(forwardedMessage));
                    dto.hasAttachments().set(forwardedMessage.hasAttachments());

                    if (forwardedMessage.hasAttachments().getValue(false).booleanValue()) {
                        for (MessageAttachment fromAtt : forwardedMessage.attachments()) {
                            MessageAttachment toAtt = EntityFactory.create(MessageAttachment.class);
                            toAtt.description().set(fromAtt.description());
                            toAtt.file().set(fromAtt.file());
                            FileUploadRegistry.register(fromAtt.file());
                            dto.attachments().add(toAtt);
                        }
                    }
                }
            }
        }
        return dto;

    }

    private String buildForwardSubject(MessageDTO currentMessage) {
        if (currentMessage == null || currentMessage.thread() == null || currentMessage.thread().subject() == null) {
            return null;
        }

        String result = currentMessage.thread().subject().getValue() == null ? "" : currentMessage.thread().subject().getValue();
        if (result.startsWith("Fwd: ")) {
            return result;
        }

        if (result.startsWith("Re: ")) {
            result = result.substring(3);
        }

        return "Fwd: " + result;
    }

    private String buildForwardText(MessageDTO currentMessage) {
        if (currentMessage == null) {
            return null;
        }
        StringBuffer bodyText = new StringBuffer();
        StringBuffer buffer = null;
        new StringBuffer();
        for (CommunicationEndpointDTO recipient : currentMessage.to()) {
            if (buffer == null) {
                buffer = new StringBuffer();
            } else {
                buffer.append(", ");
            }
            buffer.append(recipient.name().getValue());
        }

        bodyText.append("\n---------- Forwarded message ----------");
        bodyText.append("\nFrom: ");
        bodyText.append(currentMessage.sender().name().getValue());
        bodyText.append("\nDate: ");
        bodyText.append(currentMessage.date().getStringView());
        bodyText.append("\nSubject: ");
        bodyText.append(currentMessage.subject().getValue());
        bodyText.append("\nTo: ");
        bodyText.append(buffer.toString());
        bodyText.append("\n\nFwd:\n");
        bodyText.append(currentMessage.text().getValue());

        return bodyText.toString();
    }

    @Override
    protected boolean persist(Message bo, MessageDTO to) {

        boolean isNew = to.thread() == null || to.thread().isNull() || to.thread().isEmpty();
        if (isNew) {
            bo.date().setValue(SystemDateManager.getDate());
        }

        CommunicationMessageFacade communicationFacade = ServerSideFactory.create(CommunicationMessageFacade.class);
        buildRecipientList(bo, to, communicationFacade);

        bo.attachments().set(to.attachments());
        bo.date().setValue(SystemDateManager.getDate());
        bo.sender().set(CrmAppContext.getCurrentUser());
        bo.text().set(to.text());
        bo.highImportance().set(to.highImportance());

        CommunicationThread t = EntityFactory.create(CommunicationThread.class);
        t.subject().set(to.subject());
        t.allowedReply().set(to.allowedReply());

        t.topic().set(to.topic());
        Persistence.service().retrieveMember(to.topic());
        t.status().setValue(isNew && !MessageGroupCategory.Custom.equals(to.topic().category().getValue()) ? ThreadStatus.New : ThreadStatus.Unassigned);
        t.content().add(bo);
        t.owner().set(
                to.owner() == null || to.owner().isEmpty() || to.owner().isPrototype() || to.owner().isNull() ? communicationFacade
                        .getSystemEndpointFromCache(SystemEndpointName.Unassigned) : to.owner().endpoint());

        return Persistence.secureSave(t);
    }

    private void buildRecipientList(Message bo, MessageDTO to, CommunicationMessageFacade communicationFacade) {
        HashMap<CommunicationEndpoint, Boolean> visited = new HashMap<CommunicationEndpoint, Boolean>();
        for (CommunicationEndpointDTO todep : to.to()) {
            if (!Tenant.class.equals(todep.endpoint().getInstanceValueClass())) {
                if (visited.containsKey(todep.endpoint())) {
                    Boolean currentValue = visited.get(todep.endpoint());
                    visited.put(todep.endpoint(), visited.get(todep.endpoint()).booleanValue() && (currentValue == null ? false : currentValue.booleanValue()));
                } else {
                    visited.put(todep.endpoint(), false);
                }

            }
            expandCommunicationEndpoint(visited, communicationFacade, todep);
        }
        for (Entry<CommunicationEndpoint, Boolean> todep : visited.entrySet()) {
            bo.recipients().add(communicationFacade.createDeliveryHandle(todep.getKey(), todep.getValue()));
        }
    }

    private void expandCommunicationEndpoint(HashMap<CommunicationEndpoint, Boolean> visited, CommunicationMessageFacade communicationFacade,
            CommunicationEndpointDTO ep) {
        EntityListCriteria<Tenant> criteria = createActiveLeaseCriteria();

        switch (ep.type().getValue()) {
        case Building: {
            criteria.eq(criteria.proto().lease().unit().building(), ep.endpoint());
            break;
        }
        case Unit: {
            criteria.eq(criteria.proto().lease().unit(), ep.endpoint());
            break;
        }
        case Portfolio: {
            EntityListCriteria<Portfolio> buildingCriteria = EntityListCriteria.create(Portfolio.class);
            buildingCriteria.in(buildingCriteria.proto().id(), ep.endpoint());
            Vector<Portfolio> ps = Persistence.secureQuery(buildingCriteria, AttachLevel.Attached);
            ArrayList<Building> bs = new ArrayList<Building>();
            if (ps == null || ps.isEmpty()) {
                return;
            }
            for (Portfolio p : ps) {
                bs.addAll(p.buildings());
            }

            if (bs.isEmpty()) {
                return;
            }
            criteria.in(criteria.proto().lease().unit().building(), bs);
            break;
        }
        case Tenant: {
            criteria.eq(criteria.proto().id(), ep.endpoint().id());
            break;
        }
        case Employee:
        default: {
            return;
        }
        }

        Vector<Tenant> tenants = Persistence.secureQuery(criteria, AttachLevel.IdOnly);
        if (tenants != null) {
            for (Tenant t : tenants) {
                Persistence.ensureRetrieve(t.customer(), AttachLevel.Attached);
                if (visited.containsKey(t.customer().user())) {
                    Boolean currentValue = visited.get(t.customer().user()).booleanValue();

                    visited.put(t.customer().user(), currentValue && !ContactType.Tenant.equals(ep.type().getValue()));
                } else {
                    visited.put(t.customer().user(), !ContactType.Tenant.equals(ep.type().getValue()));
                }
            }
        }
    }

    private EntityListCriteria<Tenant> createActiveLeaseCriteria() {
        EntityListCriteria<Tenant> criteria = EntityListCriteria.create(Tenant.class);
        criteria.eq(criteria.proto().lease().status(), Lease.Status.Active);
        return criteria;
    }

    @Override
    protected void enhanceRetrieved(Message bo, MessageDTO to, RetrieveTarget retrieveTarget) {
        enhanceDbo(bo, to, false);
    }

    @Override
    protected void enhanceListRetrieved(Message entity, MessageDTO dto) {
        enhanceDbo(entity, dto, true);
    }

    protected void enhanceDbo(Message bo, MessageDTO to, boolean isForList) {
        Persistence.ensureRetrieve(bo.thread(), AttachLevel.Attached);
        Persistence.ensureRetrieve(bo.thread().content(), AttachLevel.Attached);
        Persistence.ensureRetrieve(bo.thread().topic(), AttachLevel.Attached);
        if (!isForList) {
            Persistence.ensureRetrieve(bo.thread().owner(), AttachLevel.Attached);
        }
        Persistence.ensureRetrieve(bo.recipients(), AttachLevel.Attached);
        IList<Message> ms = bo.thread().content();
        if (ms != null && !ms.isNull()) {
            boolean star = false;
            boolean isRead = true;
            boolean isHighImportance = false;
            boolean hasAttachment = false;
            Message lastMessage = null;
            for (Message m : ms) {
                Persistence.ensureRetrieve(m.recipients(), AttachLevel.Attached);
                Persistence.ensureRetrieve(m.sender(), AttachLevel.Attached);
                if (!isForList || !hasAttachment) {
                    Persistence.ensureRetrieve(m.attachments(), AttachLevel.Attached);
                }
                hasAttachment = hasAttachment || m.attachments().size() > 0;
                MessageDTO currentDTO = copyChildDTO(m, EntityFactory.create(MessageDTO.class), isForList);
                to.content().add(currentDTO);
                if (currentDTO.star().getValue(false)) {
                    star = true;
                }
                if (!currentDTO.isRead().getValue(false)) {
                    isRead = false;
                }
                if (!currentDTO.highImportance().getValue(false)) {
                    isHighImportance = false;
                }
                if (to.id().equals(currentDTO.id())) {
                    lastMessage = m;
                } else if (isForList) {
                    lastMessage = m;
                }
            }
            copyChildDTO(lastMessage, to, isForList);
            if (isHighImportance) {
                to.highImportance().setValue(true);
            }
            if (star) {
                to.star().setValue(true);
            }
            if (!isRead) {
                to.isRead().setValue(false);
            }
            to.hasAttachments().setValue(hasAttachment);
        }
    }

    private MessageDTO copyChildDTO(Message m, MessageDTO messageDTO, boolean isForList) {
        boolean star = false;
        boolean isRead = true;

        CommunicationMessageFacade communicationFacade = ServerSideFactory.create(CommunicationMessageFacade.class);
        messageDTO.isInRecipients().setValue(false);
        for (DeliveryHandle dh : m.recipients()) {
            if (!isForList && !dh.generatedFromGroup().getValue(false)) {
                Persistence.ensureRetrieve(dh.recipient(), AttachLevel.Attached);
                messageDTO.to().add((communicationFacade.generateEndpointDTO(dh.recipient())));
            }
            if (!CrmAppContext.getCurrentUser().equals(dh.recipient())) {
                continue;
            }
            if (dh.star().getValue(false)) {
                star = true;
            }
            if (!dh.isRead().getValue(false)) {
                isRead = false;
            }
            messageDTO.isInRecipients().setValue(true);
        }

        messageDTO.id().set(m.id());
        messageDTO.subject().set(m.thread().subject());
        messageDTO.allowedReply().set(m.thread().allowedReply());
        messageDTO.status().set(m.thread().status());
        Persistence.ensureRetrieve(m.thread().owner(), AttachLevel.Attached);
        if (!isForList) {
            messageDTO.owner().set((communicationFacade.generateEndpointDTO(m.thread().owner())));
        }
        messageDTO.text().set(m.text());
        messageDTO.date().set(m.date());
        messageDTO.thread().setAttachLevel(AttachLevel.Attached);
        messageDTO.thread().set(m.thread());
        messageDTO.attachments().set(m.attachments());
        messageDTO.hasAttachments().setValue(m.attachments().size() > 0);
        messageDTO.highImportance().set(m.highImportance());
        messageDTO.sender().setAttachLevel(AttachLevel.Attached);
        messageDTO.sender().set((communicationFacade.generateEndpointDTO(m.sender())));
        messageDTO.isRead().setValue(isRead);
        messageDTO.star().setValue(star);
        messageDTO.topic().set(m.thread().topic());
        messageDTO.header().sender().setValue(communicationFacade.extractEndpointName(m.sender()));
        messageDTO.header().date().set(m.date());

        return messageDTO;
    }

    @Override
    public void saveMessage(AsyncCallback<MessageDTO> callback, MessageDTO message, ThreadStatus threadStatus) {
        if (message.date().isNull()) {
            CommunicationThread thread = Persistence.secureRetrieve(CommunicationThread.class, message.thread().id().getValue());
            CommunicationMessageFacade communicationFacade = ServerSideFactory.create(CommunicationMessageFacade.class);
            Message m = EntityFactory.create(Message.class);
            if (threadStatus != null) {
                message.status().setValue(threadStatus);
                thread.status().setValue(threadStatus);

                Persistence.service().persist(thread);
                m.recipients().add(
                        communicationFacade.createDeliveryHandle(communicationFacade.getSystemEndpointFromCache(SystemEndpointName.Unassigned), true));

            } else {
                buildRecipientList(m, message, communicationFacade);
            }
            m.thread().set(thread);
            m.attachments().set(message.attachments());
            m.date().setValue(SystemDateManager.getDate());
            m.sender().set(CrmAppContext.getCurrentUser());
            m.text().set(message.text());
            m.highImportance().setValue(false);
            Persistence.service().persist(m);
            Persistence.service().commit();
            retrieve(callback, m.getPrimaryKey(), RetrieveTarget.View);
        } else {
            EntityQueryCriteria<DeliveryHandle> dhCriteria = EntityQueryCriteria.create(DeliveryHandle.class);
            dhCriteria.eq(dhCriteria.proto().recipient(), CrmAppContext.getCurrentUser());
            dhCriteria.eq(dhCriteria.proto().message(), message);
            DeliveryHandle dh = Persistence.retrieveUnique(dhCriteria, AttachLevel.Attached);
            dh.isRead().set(message.isRead());
            dh.star().set(message.star());
            Persistence.service().persist(dh);
            Persistence.service().commit();
            callback.onSuccess(message);
        }

    }

    @Override
    public void assignOwnership(AsyncCallback<MessageDTO> callback, MessageDTO message, Employee employee) {
        CommunicationThread thread = Persistence.secureRetrieve(CommunicationThread.class, message.thread().id().getValue());

        if (ThreadStatus.New.equals(thread.status().getValue())) {
            thread.status().setValue(ThreadStatus.Open);
        }
        thread.owner().set(employee.user());

        Persistence.service().persist(thread);
        Persistence.service().commit();
        Persistence.ensureRetrieve(employee.user(), AttachLevel.Attached);
        message.owner().set((ServerSideFactory.create(CommunicationMessageFacade.class).generateEndpointDTO(employee.user())));
        message.status().set(thread.status());
        callback.onSuccess(message);
    }
}