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

import com.propertyvista.biz.communication.CommunicationMessageFacade;
import com.propertyvista.crm.rpc.services.MessageCrudService;
import com.propertyvista.crm.server.util.CrmAppContext;
import com.propertyvista.domain.communication.CommunicationEndpoint;
import com.propertyvista.domain.communication.CommunicationEndpoint.ContactType;
import com.propertyvista.domain.communication.CommunicationThread;
import com.propertyvista.domain.communication.CommunicationThread.ThreadStatus;
import com.propertyvista.domain.communication.DeliveryHandle;
import com.propertyvista.domain.communication.Message;
import com.propertyvista.domain.communication.MessageCategory.MessageGroupCategory;
import com.propertyvista.domain.communication.SystemEndpoint;
import com.propertyvista.domain.communication.SystemEndpoint.SystemEndpointName;
import com.propertyvista.domain.company.Employee;
import com.propertyvista.domain.security.CrmUser;
import com.propertyvista.domain.security.CustomerUser;
import com.propertyvista.dto.CommunicationEndpointDTO;
import com.propertyvista.dto.MessageDTO;

public class MessageCrudServiceImpl extends AbstractCrudServiceDtoImpl<Message, MessageDTO> implements MessageCrudService {
    public MessageCrudServiceImpl() {
        super(Message.class, MessageDTO.class);
    }

    @Override
    protected void bind() {
        bindCompleteObject();
    }

    @Override
    protected Path convertPropertyDTOPathToDBOPath(String path, Message boProto, MessageDTO toProto) {
        if (path.equals(toProto.topic().getPath().toString())) {
            return boProto.thread().topic().getPath();
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
    public void copyTOtoBO(MessageDTO to, Message bo) {
        super.copyTOtoBO(to, bo);
    }

    @Override
    protected MessageDTO init(InitializationData initializationData) {
        MessageDTO dto = EntityFactory.create(MessageDTO.class);
        dto.date().setValue(SystemDateManager.getDate());
        dto.isRead().setValue(false);
        dto.highImportance().setValue(false);
        dto.allowedReply().setValue(true);
        dto.status().setValue(ThreadStatus.Unassigned);
        dto.sender().set(generateEndpointDTO(CrmAppContext.getCurrentUser()));

        if (initializationData instanceof MessageInitializationData) {
            dto.text().set(((MessageInitializationData) initializationData).initalizedText());
        }

        return dto;

    }

    @Override
    protected boolean persist(Message bo, MessageDTO to) {

        boolean isNew = to.thread() == null || to.thread().isNull() || to.thread().isEmpty();
        if (isNew) {
            bo.date().setValue(SystemDateManager.getDate());
        }

        for (CommunicationEndpointDTO todep : to.to()) {
            bo.recipients().add(ServerSideFactory.create(CommunicationMessageFacade.class).createDeliveryHandle(todep.endpoint()));
        }

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
                to.owner() == null || to.owner().isEmpty() || to.owner().isPrototype() || to.owner().isNull() ? ServerSideFactory.create(
                        CommunicationMessageFacade.class).getSystemEndpointFromCache(SystemEndpointName.Unassigned) : to.owner().endpoint());

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

    private CommunicationEndpointDTO generateEndpointDTO(CommunicationEndpoint entity) {
        if (entity == null) {
            return null;
        }
        CommunicationEndpointDTO rec = EntityFactory.create(CommunicationEndpointDTO.class);
        rec.endpoint().set(entity);

        if (entity.getInstanceValueClass().equals(SystemEndpoint.class)) {
            SystemEndpoint e = entity.cast();
            rec.name().setValue(e.name().getValue().name());
            rec.type().setValue(ContactType.System);
        } else if (entity.getInstanceValueClass().equals(CrmUser.class)) {
            CrmUser e = entity.cast();
            rec.name().set(e.name());
            rec.type().setValue(ContactType.Employee);
        } else if (entity.getInstanceValueClass().equals(CustomerUser.class)) {
            CustomerUser e = entity.cast();
            rec.name().set(e.name());
            rec.type().setValue(ContactType.Tenants);
        }
        return rec;
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
            for (Message m : ms) {
                Persistence.ensureRetrieve(m.recipients(), AttachLevel.Attached);
                Persistence.ensureRetrieve(m.sender(), AttachLevel.Attached);
                if (!isForList) {
                    Persistence.ensureRetrieve(m.attachments(), AttachLevel.Attached);
                }
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
                    copyChildDTO(m, to, isForList);
                }
            }
            if (isHighImportance) {
                to.highImportance().setValue(true);
            }
            if (star) {
                to.star().setValue(true);
            }
            if (!isRead) {
                to.isRead().setValue(false);
            }
        }
    }

    private MessageDTO copyChildDTO(Message m, MessageDTO messageDTO, boolean isForList) {
        boolean star = false;
        boolean isRead = true;

        messageDTO.isInRecipients().setValue(false);
        for (DeliveryHandle dh : m.recipients()) {
            if (!isForList) {
                Persistence.ensureRetrieve(dh.recipient(), AttachLevel.Attached);
                messageDTO.to().add(generateEndpointDTO(dh.recipient()));
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
            messageDTO.owner().set(generateEndpointDTO(m.thread().owner()));
        }
        messageDTO.text().set(m.text());
        messageDTO.date().set(m.date());
        messageDTO.thread().setAttachLevel(AttachLevel.Attached);
        messageDTO.thread().set(m.thread());
        messageDTO.attachments().set(m.attachments());
        messageDTO.highImportance().set(m.highImportance());
        messageDTO.sender().setAttachLevel(AttachLevel.Attached);
        messageDTO.sender().set(generateEndpointDTO(m.sender()));
        messageDTO.isRead().setValue(isRead);
        messageDTO.star().setValue(star);
        messageDTO.topic().set(m.thread().topic());
        messageDTO.header().sender().setValue(ServerSideFactory.create(CommunicationMessageFacade.class).extractEndpointName(m.sender()));
        messageDTO.header().date().set(m.date());

        return messageDTO;
    }

    @Override
    public void saveMessage(AsyncCallback<MessageDTO> callback, MessageDTO message, ThreadStatus threadStatus) {
        if (message.date().isNull()) {
            CommunicationThread thread = Persistence.secureRetrieve(CommunicationThread.class, message.thread().id().getValue());

            Message m = EntityFactory.create(Message.class);
            if (threadStatus != null) {
                message.status().setValue(threadStatus);
                thread.status().setValue(threadStatus);

                Persistence.service().persist(thread);
                m.recipients().add(
                        ServerSideFactory.create(CommunicationMessageFacade.class).createDeliveryHandle(
                                ServerSideFactory.create(CommunicationMessageFacade.class).getSystemEndpointFromCache(SystemEndpointName.Unassigned)));

            } else {
                for (CommunicationEndpointDTO d : message.to()) {
                    m.recipients().add(ServerSideFactory.create(CommunicationMessageFacade.class).createDeliveryHandle(d.endpoint()));
                }
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
        message.owner().set(generateEndpointDTO(employee.user()));
        message.status().set(thread.status());
        callback.onSuccess(message);
    }
}