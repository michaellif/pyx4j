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

import java.util.List;
import java.util.Vector;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.config.server.SystemDateManager;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.Path;
import com.pyx4j.entity.core.criterion.EntityListCriteria;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.server.AbstractCrudServiceDtoImpl;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.essentials.server.upload.FileUploadRegistry;
import com.pyx4j.server.contexts.ServerContext;

import com.propertyvista.biz.communication.CommunicationMessageFacade;
import com.propertyvista.crm.rpc.services.MessageCrudService;
import com.propertyvista.crm.server.util.CrmAppContext;
import com.propertyvista.domain.communication.CommunicationThread;
import com.propertyvista.domain.communication.CommunicationThread.ThreadStatus;
import com.propertyvista.domain.communication.DeliveryHandle;
import com.propertyvista.domain.communication.Message;
import com.propertyvista.domain.communication.MessageAttachment;
import com.propertyvista.domain.communication.MessageCategory.MessageGroupCategory;
import com.propertyvista.domain.communication.SystemEndpoint.SystemEndpointName;
import com.propertyvista.domain.company.Employee;
import com.propertyvista.domain.security.CrmUser;
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
        if (path.equals(toProto.status().getPath().toString())) {
            return boProto.thread().status().getPath();
        }
        if (path.equals(toProto.owner().getPath().toString()) || path.equals(toProto.ownerForList().getPath().toString())) {
            return boProto.thread().owner().getPath();
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

        List<CommunicationThread> directThreads = communicationFacade.getDirectThreads();
        List<CommunicationThread> dispatchedThreads = communicationFacade.getDispathcedThreads();

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
        ServerContext.getVisit().setAttribute(CommunicationMessageFacade.class.getName(), new Long(0L));

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
        CommunicationMessageFacade communicationFacade = ServerSideFactory.create(CommunicationMessageFacade.class);
        dto.sender().set(communicationFacade.generateEndpointDTO(CrmAppContext.getCurrentUser()));

        if (initializationData instanceof MessageInitializationData) {
            MessageInitializationData data = (MessageInitializationData) initializationData;
            if (data.forwardedMessage() != null && !data.forwardedMessage().isNull()) {
                MessageDTO forwardedMessage = data.forwardedMessage();
                if (forwardedMessage != null) {
                    dto.subject().setValue(communicationFacade.buildForwardSubject(forwardedMessage));
                    dto.text().setValue(communicationFacade.buildForwardText(forwardedMessage));
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
            if (data.messageCategory() != null && !data.messageCategory().isNull()) {
                dto.topic().set(data.messageCategory());
            }
        }
        return dto;

    }

    @Override
    protected boolean persist(Message bo, MessageDTO to) {

        boolean isNew = to.thread() == null || to.thread().isNull() || to.thread().isEmpty();
        if (isNew) {
            bo.date().setValue(SystemDateManager.getDate());
        }

        CommunicationMessageFacade communicationFacade = ServerSideFactory.create(CommunicationMessageFacade.class);
        communicationFacade.buildRecipientList(bo, to);

        bo.attachments().set(to.attachments());
        bo.date().setValue(SystemDateManager.getDate());
        bo.sender().set(CrmAppContext.getCurrentUser());
        bo.text().set(to.text());
        bo.highImportance().set(to.highImportance());

        CommunicationThread t = EntityFactory.create(CommunicationThread.class);
        t.subject().set(to.subject());
        t.allowedReply().set(to.allowedReply());

        t.topic().set(to.topic());
        if (to.topic().isValueDetached()) {
            Persistence.service().retrieve(to.topic());
        }
        t.status().setValue(isNew && !MessageGroupCategory.Custom.equals(to.topic().category().getValue()) ? ThreadStatus.New : ThreadStatus.Unassigned);
        t.content().add(bo);
        t.owner().set(
                to.owner() == null || to.owner().isEmpty() || to.owner().isPrototype() || to.owner().isNull() ? communicationFacade
                        .getSystemEndpointFromCache(SystemEndpointName.Unassigned) : to.owner().endpoint());

        ServerContext.getVisit().setAttribute(CommunicationMessageFacade.class.getName(), new Long(0L));
        return Persistence.secureSave(t);
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
        EntityListCriteria<Message> visibleMessageCriteria = EntityListCriteria.create(Message.class);
        visibleMessageCriteria.eq(visibleMessageCriteria.proto().thread(), bo.thread());
        final Vector<Message> ms = Persistence.secureQuery(visibleMessageCriteria, AttachLevel.Attached);

        Persistence.ensureRetrieve(bo.thread(), AttachLevel.Attached);
        Persistence.ensureRetrieve(bo.thread().topic(), AttachLevel.Attached);
        Persistence.ensureRetrieve(bo.thread().owner(), AttachLevel.Attached);

        Persistence.ensureRetrieve(bo.recipients(), AttachLevel.Attached);
        if (ms != null && ms.size() > 0) {
            boolean star = false;
            boolean isRead = true;
            boolean isHighImportance = false;
            boolean hasAttachment = false;
            Message lastMessage = null;
            CommunicationMessageFacade communicationFacade = ServerSideFactory.create(CommunicationMessageFacade.class);
            for (Message m : ms) {
                Persistence.ensureRetrieve(m.recipients(), AttachLevel.Attached);
                Persistence.ensureRetrieve(m.sender(), AttachLevel.Attached);
                if (!isForList || !hasAttachment) {
                    Persistence.ensureRetrieve(m.attachments(), AttachLevel.Attached);
                }
                hasAttachment = hasAttachment || m.attachments().size() > 0;
                MessageDTO currentDTO = copyChildDTO(bo.thread(), m, EntityFactory.create(MessageDTO.class), isForList, communicationFacade);
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
            copyChildDTO(bo.thread(), lastMessage, to, isForList, communicationFacade);
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

            to.isDirect().setValue(!communicationFacade.isDispatchedThread(bo.thread().getPrimaryKey(), !isForList));

        }
    }

    private MessageDTO copyChildDTO(CommunicationThread thread, Message m, MessageDTO messageDTO, boolean isForList,
            CommunicationMessageFacade communicationFacade) {
        boolean star = false;
        boolean isRead = true;

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
        messageDTO.subject().set(thread.subject());
        messageDTO.allowedReply().set(thread.allowedReply());
        messageDTO.status().set(thread.status());
        Persistence.ensureRetrieve(thread.owner(), AttachLevel.Attached);
        messageDTO.owner().set((communicationFacade.generateEndpointDTO(thread.owner())));
        if (isForList && thread.owner().getInstanceValueClass().equals(CrmUser.class)) {
            messageDTO.ownerForList().set(thread.owner());
        }
        messageDTO.ownerForList();

        messageDTO.text().set(m.text());
        messageDTO.date().set(m.date());
        messageDTO.thread().setAttachLevel(AttachLevel.Attached);
        messageDTO.thread().set(thread);
        messageDTO.attachments().set(m.attachments());
        messageDTO.hasAttachments().setValue(m.attachments().size() > 0);
        messageDTO.highImportance().set(m.highImportance());
        messageDTO.sender().setAttachLevel(AttachLevel.Attached);
        messageDTO.sender().set((communicationFacade.generateEndpointDTO(m.sender())));
        messageDTO.isRead().setValue(isRead);
        messageDTO.star().setValue(star);
        messageDTO.topic().set(thread.topic());
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
                thread.owner().set(CrmAppContext.getCurrentUser());

                Persistence.service().persist(thread);
                m.recipients().add(
                        communicationFacade.createDeliveryHandle(communicationFacade.getSystemEndpointFromCache(SystemEndpointName.Unassigned), true));

            } else {
                communicationFacade.buildRecipientList(m, message);
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
        ServerContext.getVisit().setAttribute(CommunicationMessageFacade.class.getName(), new Long(0L));
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
        ServerContext.getVisit().setAttribute(CommunicationMessageFacade.class.getName(), new Long(0L));
        callback.onSuccess(message);
    }
}