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

import java.util.HashMap;
import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.config.server.SystemDateManager;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.Path;
import com.pyx4j.entity.core.criterion.EntityListCriteria;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.core.criterion.OrCriterion;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.server.AbstractCrudServiceDtoImpl;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.server.contexts.Context;

import com.propertyvista.biz.communication.CommunicationMessageFacade;
import com.propertyvista.crm.rpc.services.CommunicationMessageCrudService;
import com.propertyvista.crm.server.util.CrmAppContext;
import com.propertyvista.domain.communication.CommunicationEndpoint;
import com.propertyvista.domain.communication.CommunicationEndpoint.ContactType;
import com.propertyvista.domain.communication.Message;
import com.propertyvista.domain.communication.MessageCategory;
import com.propertyvista.domain.communication.SystemEndpoint;
import com.propertyvista.domain.communication.SystemEndpoint.SystemEndpointName;
import com.propertyvista.domain.security.CrmUser;
import com.propertyvista.domain.security.CustomerUser;
import com.propertyvista.dto.CommunicationEndpointDTO;
import com.propertyvista.dto.CommunicationMessageDTO;
import com.propertyvista.dto.MessagesDTO;

public class CommunicationMessageCrudServiceImpl extends AbstractCrudServiceDtoImpl<Message, CommunicationMessageDTO> implements
        CommunicationMessageCrudService {
    public CommunicationMessageCrudServiceImpl() {
        super(Message.class, CommunicationMessageDTO.class);
    }

    @Override
    protected void bind() {
        bind(toProto.id(), boProto.id());
        //bind(toProto.isRead(), boProto.isRead());
    }

    @Override
    protected Path convertPropertyDTOPathToDBOPath(String path, Message boProto, CommunicationMessageDTO toProto) {
        if (path.equals(toProto.date().getPath().toString())) {
            return boProto.date().getPath();
        }
        if (path.equals(toProto.sender().getPath().toString())) {
            return boProto.sender().getPath();
        }
        if (path.equals(toProto.subject().getPath().toString())) {
            return boProto.thread().subject().getPath();
        }
        if (path.equals(toProto.text().getPath().toString())) {
            return boProto.text().getPath();
        }
        if (path.equals(toProto.isHighImportance().getPath().toString())) {
            return boProto.isHighImportance().getPath();
        }
        if (path.equals(toProto.attachments().getPath().toString())) {
            return boProto.attachments().getPath();
        }
        return super.convertPropertyDTOPathToDBOPath(path, boProto, toProto);
    }

    @Override
    public void copyTOtoBO(CommunicationMessageDTO to, Message bo) {
        super.copyTOtoBO(to, bo);
    }

    @Override
    protected CommunicationMessageDTO init(InitializationData initializationData) {
        CommunicationMessageDTO dto = EntityFactory.create(CommunicationMessageDTO.class);
        dto.date().setValue(SystemDateManager.getDate());
        dto.isRead().setValue(false);
        dto.sender().set(CrmAppContext.getCurrentUser());

        return dto;

    }

    @Override
    protected boolean persist(Message bo, CommunicationMessageDTO to) {
/*-        CommunicationMessage c = null;
 if (bo.id().isNull()) {
 c = EntityFactory.create(CommunicationMessage.class);
 } else {
 c = bo.data();
 }

 c.attachments().set(to.attachments());
 c.date().setValue(SystemDateManager.getDate());
 c.sender().set(CrmAppContext.getCurrentUser());
 c.text().set(to.text());
 c.isHighImportance().set(to.isHighImportance());
 Persistence.service().persist(c);

 CommunicationThread thread = null;
 boolean isNew = to.threadDTO().created().isNull() || to.threadDTO().created().isPrototype();
 if (isNew) {
 thread = EntityFactory.create(CommunicationThread.class);
 thread.subject().set(to.subject());
 // TODO; Smolka
 thread.topic()
 .set(ServerSideFactory.create(CommunicationMessageFacade.class).getCommunicationGroupFromCache(MessageGroupCategory.TenantOriginated));
 thread.created().setValue(SystemDateManager.getDate());
 thread.owner().set(ServerSideFactory.create(CommunicationMessageFacade.class).getSystemEndpointFromCache(SystemEndpointName.Unassigned));
 Persistence.service().persist(thread);
 Persistence.service().commit();
 bo.thread().set(thread);

 for (CommunicationEndpointDTO mto : to.to()) {
 DeliveryHandle m = EntityFactory.create(DeliveryHandle.class);
 m.thread().set(thread);
 m.isRead().setValue(isNew ? false : to.isRead().getValue());
 m.recipient().set(mto.endpoint());
 m.data().set(c);

 thread.content().add(m);
 }
 Persistence.service().persist(thread);
 Persistence.service().commit();
 bo.thread().set(thread);
 }
 bo.data().set(c);
 bo.isRead().setValue(false);
 //bo.thread().set(to.thread());-*/
        return super.persist(bo, to);
    }

    @Override
    protected void enhanceRetrieved(Message bo, CommunicationMessageDTO to, RetrieveTarget retrieveTarget) {
        enhanceAll(bo, to);
        to.subject().set(to.threadDTO().subject());
        to.attachments().setAttachLevel(AttachLevel.Attached);
        to.attachments().set(bo.attachments());

    }

    @Override
    public void list(AsyncCallback<EntitySearchResult<CommunicationMessageDTO>> callback, EntityListCriteria<CommunicationMessageDTO> dtoCriteria) {
        List<Message> ms = filterRelevantMessages(false, AttachLevel.IdOnly);

        if (ms == null || ms.isEmpty()) {
            callback.onSuccess(new EntitySearchResult<CommunicationMessageDTO>());
            return;
        }

        HashMap<Key, Key> visitedThreads = new HashMap<Key, Key>();
        for (Message m : ms) {
            Persistence.ensureRetrieve(m.thread(), AttachLevel.Attached);
            if (visitedThreads.containsKey(m.thread().id().getValue())) {
                continue;
            }
            visitedThreads.put(m.thread().id().getValue(), m.id().getValue());
        }

        dtoCriteria.in(dtoCriteria.proto().id(), visitedThreads.values());

        super.list(callback, dtoCriteria);
    }

    @Override
    protected void enhanceListRetrieved(Message entity, CommunicationMessageDTO dto) {
        enhanceAll(entity, dto);
        dto.subject().set(entity.thread().subject());

        dto.setAttachLevel(AttachLevel.Attached);
        dto.sender().setAttachLevel(AttachLevel.Attached);
        dto.sender().set(entity.sender());
        dto.date().set(entity.date());
        dto.to().setAttachLevel(AttachLevel.Attached);
        //dto.to().add(generateEndpointDTO(entity.recipient()));
        dto.attachments().setAttachLevel(AttachLevel.Attached);
        dto.attachments().set(entity.attachments());

        //if (belongsToUser(CrmAppContext.getCurrentUser(), dto.recipient())) {
        //    dto.isRead().set(dto.isRead());
        //} else {
        //    dto.isRead().setValue(true);
        //}
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

    protected void enhanceAll(Message entity, CommunicationMessageDTO to) {
        enhanceDbo(entity, to);
    }

    protected void enhanceDbo(Message dbo, CommunicationMessageDTO to) {
        /*-       Persistence.ensureRetrieve(dbo.thread(), AttachLevel.Attached);
               Persistence.ensureRetrieve(dbo.sender(), AttachLevel.Attached);
               //Persistence.ensureRetrieve(dbo.recipient(), AttachLevel.Attached);
               Persistence.ensureRetrieve(dbo.data().attachments(), AttachLevel.Attached);
               CommunicationThread thread = dbo.thread();
               Persistence.ensureRetrieve(dbo.thread().topic(), AttachLevel.Attached);
               Persistence.ensureRetrieve(dbo.thread().owner(), AttachLevel.Attached);
               CommunicationThreadDTO toThread = EntityFactory.create(CommunicationThreadDTO.class);
               IList<DeliveryHandle> ms = thread.content();
               if (ms != null && !ms.isNull()) {
                   HashMap<Key, CommunicationMessageDTO> visitedMessages = new HashMap<Key, CommunicationMessageDTO>();
                   for (DeliveryHandle m : ms) {
                       Persistence.ensureRetrieve(m.recipient(), AttachLevel.Attached);

                       if (visitedMessages.containsKey(m.data().getPrimaryKey())) {
                           visitedMessages.get(m.data().getPrimaryKey()).to().add(generateEndpointDTO(m.recipient()));
                           continue;
                       }
                       CommunicationMessageDTO message = EntityFactory.create(CommunicationMessageDTO.class);
                       Persistence.ensureRetrieve(m.data(), AttachLevel.Attached);
                       Persistence.ensureRetrieve(m.data().attachments(), AttachLevel.Attached);
                       Persistence.ensureRetrieve(m.data().sender(), AttachLevel.Attached);
                       message.id().set(m.id());
                       message.subject().set(m.thread().subject());
                       message.isHighImportance().set(m.data().isHighImportance());
                       message.text().set(m.data().text());
                       message.date().set(m.data().date());
                       message.threadDTO().setAttachLevel(AttachLevel.Attached);
                       toThread.id().set(m.thread().id());
                       toThread.subject().set(m.thread().subject());
                       toThread.created().set(m.thread().created());
                       toThread.responsible().set(m.thread().owner());
                       message.threadDTO().set(toThread);
                       message.sender().setAttachLevel(AttachLevel.Attached);
                       message.sender().set(m.data().sender());
                       message.to().setAttachLevel(AttachLevel.Attached);
                       message.to().add(generateEndpointDTO(m.recipient()));

                       toThread.content().add(message);
                       //if (belongsToUser(CrmAppContext.getCurrentUser(), m.recipient())) {
                       //    m.isRead().set(m.isRead());
                       //} else {
                       //    m.isRead().setValue(true);
                       // }

                       visitedMessages.put(m.data().getPrimaryKey(), message);
                   }
               }

               to.threadDTO().setAttachLevel(AttachLevel.Attached);
               to.threadDTO().set(toThread);
        -*/
    }

    @Override
    public void retreiveCommunicationMessages(AsyncCallback<MessagesDTO> callback, boolean newOnly) {
        if (!Context.isUserLoggedIn()) {
            callback.onSuccess(EntityFactory.create(MessagesDTO.class));
            return;
        }
        CrmUser currentUser = CrmAppContext.getCurrentUser();
        if (currentUser == null || currentUser.isEmpty()) {
            callback.onSuccess(EntityFactory.create(MessagesDTO.class));
            return;
        }

        MessagesDTO data = prefilterMessages(newOnly);
        callback.onSuccess(data);

    }

    private MessagesDTO prefilterMessages(boolean newOnly) {
/*-        List<DeliveryHandle> ms = filterRelevantMessages(newOnly, AttachLevel.Attached);

 if (ms == null || ms.isEmpty()) {
 return EntityFactory.create(MessagesDTO.class);
 }

 HashMap<Key, CommunicationMessageDTO> visitedThreads = new HashMap<Key, CommunicationMessageDTO>();
 MessagesDTO data = EntityFactory.create(MessagesDTO.class);
 for (DeliveryHandle m : ms) {
 Persistence.ensureRetrieve(m.thread(), AttachLevel.Attached);
 if (visitedThreads.containsKey(m.thread().id().getValue())) {
 CommunicationMessageDTO thread = visitedThreads.get(m.thread().id().getValue());
 if (CrmAppContext.getCurrentUser().equals(m.data().sender())) {
 continue;
 }
 if (!m.isRead().getValue(false)) {
 thread.isRead().set(m.isRead());
 }
 continue;
 }
 Persistence.ensureRetrieve(m.data(), AttachLevel.Attached);
 Persistence.ensureRetrieve(m.data().sender(), AttachLevel.Attached);
 Persistence.ensureRetrieve(m.recipient(), AttachLevel.Attached);

 Persistence.ensureRetrieve(m.thread().topic(), AttachLevel.Attached);
 Persistence.ensureRetrieve(m.thread().owner(), AttachLevel.Attached);

 CommunicationMessageDTO message = EntityFactory.create(CommunicationMessageDTO.class);
 message.id().set(m.id());
 message.subject().set(m.thread().subject());
 message.isHighImportance().set(m.data().isHighImportance());
 message.text().set(m.data().text());
 message.date().set(m.data().date());
 message.threadDTO().setAttachLevel(AttachLevel.Attached);
 CommunicationThreadDTO toThread = EntityFactory.create(CommunicationThreadDTO.class);
 toThread.subject().set(m.thread().subject());
 toThread.created().set(m.thread().created());
 toThread.id().set(m.thread().id());
 toThread.responsible().set(m.thread().owner());
 message.threadDTO().set(toThread);
 message.sender().setAttachLevel(AttachLevel.Attached);
 message.sender().set(m.data().sender());
 message.to().setAttachLevel(AttachLevel.Attached);
 message.to().add(generateEndpointDTO(m.recipient()));
 if (belongsToUser(CrmAppContext.getCurrentUser(), m.recipient())) {
 message.isRead().set(m.isRead());
 } else {
 message.isRead().setValue(true);
 }

 visitedThreads.put(m.thread().id().getValue(), message);
 data.messages().add(message);

 }
 return data;-*/
        return null;
    }

/*--
 Messages for CRM user:
 1) When requesting for new messages only - returns:
 a) messages sent directly to the current user
 + if current user is commandant, includes also
 b) messages sent by tenant (attentionRequiried== true) without responsible person or responsible== current user

 2) When requesting all messages - returns
 a) messages sent directly to the current user
 b) messages sent by the current user
 c) messages sent to the group current user belongs to
 -*/

    private List<Message> filterRelevantMessages(boolean newOnly, AttachLevel attachLevel) {
        EntityQueryCriteria<Message> messageCriteria = EntityQueryCriteria.create(Message.class);
        messageCriteria.desc(messageCriteria.proto().date());

        if (newOnly) {
            List<MessageCategory> groups = ServerSideFactory.create(CommunicationMessageFacade.class).getDispatchedGroups(CrmAppContext.getCurrentUserEmployee(),
                    AttachLevel.IdOnly);
            OrCriterion or = messageCriteria.or();
            or.left().eq(messageCriteria.proto().recipients().$().isRead(), false);
            or.left().eq(messageCriteria.proto().recipients().$().recipient(), CrmAppContext.getCurrentUser());
            if (groups != null && !groups.isEmpty()) {

                OrCriterion or2 = or.right().or();
                or2.left().eq(messageCriteria.proto().thread().owner(), CrmAppContext.getCurrentUser());
                or2.left().eq(messageCriteria.proto().recipients().$().isRead(), false);
                or2.left().eq(messageCriteria.proto().recipients().$().recipient(),
                        ServerSideFactory.create(CommunicationMessageFacade.class).getSystemEndpointFromCache(SystemEndpointName.Unassigned));

                or2.right().isNull(messageCriteria.proto().thread().owner());
                or2.right().in(messageCriteria.proto().thread().topic(), groups);
            } else {
                or.right().eq(messageCriteria.proto().thread().owner(), CrmAppContext.getCurrentUser());
                or.right().eq(messageCriteria.proto().recipients().$().isRead(), false);
                or.right().eq(messageCriteria.proto().recipients().$().recipient(),
                        ServerSideFactory.create(CommunicationMessageFacade.class).getSystemEndpointFromCache(SystemEndpointName.Unassigned));
            }
        }

        List<Message> ms = Persistence.secureQuery(messageCriteria, attachLevel);
        return ms;
    }

    @Override
    public void saveMessage(AsyncCallback<CommunicationMessageDTO> callback, CommunicationMessageDTO source) {
        if (source.threadDTO().created().isNull() || source.threadDTO().created().isPrototype()) {
            Persistence.service().persist(source.threadDTO());
        }

        if (source.date().isNull()) {
            source.date().setValue(SystemDateManager.getDate());
        }
        //if (source.to().isNull() || source.to().isEmpty()) {
        //    source.recipient().set(ServerSideFactory.create(CommunicationMessageFacade.class).getCommunicationGroupFromCache(EndpointGroup.Commandant));
        // }
        if (source.sender().isNull()) {
            source.sender().set(CrmAppContext.getCurrentUser());
        }

        Persistence.service().persist(source);
        Persistence.service().commit();
        callback.onSuccess(source);
    }

    @Override
    public void takeOwnership(AsyncCallback<CommunicationMessageDTO> callback, CommunicationMessageDTO source) {
        source.threadDTO().responsible().set(CrmAppContext.getCurrentUser());

        Persistence.service().persist(source.threadDTO());
        Persistence.service().commit();
        callback.onSuccess(source);
    }

    private static boolean belongsToUser(CommunicationEndpoint user, CommunicationEndpoint recipient) {
        //if (user.equals(recipient)
        //         || ServerSideFactory.create(CommunicationMessageFacade.class).getCommunicationGroupFromCache(EndpointGroup.Commandant).equals(recipient)) {
        //     return true;
        // }

        return false;
    }
}