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
import com.pyx4j.entity.core.IList;
import com.pyx4j.entity.core.Path;
import com.pyx4j.entity.core.criterion.EntityListCriteria;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.core.criterion.OrCriterion;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.server.AbstractCrudServiceDtoImpl;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.security.shared.SecurityController;
import com.pyx4j.server.contexts.Context;

import com.propertyvista.biz.communication.CommunicationMessageFacade;
import com.propertyvista.crm.rpc.services.CommunicationMessageCrudService;
import com.propertyvista.crm.server.util.CrmAppContext;
import com.propertyvista.domain.communication.CommunicationEndpoint;
import com.propertyvista.domain.communication.CommunicationGroup;
import com.propertyvista.domain.communication.CommunicationGroup.ContactType;
import com.propertyvista.domain.communication.CommunicationGroup.EndpointGroup;
import com.propertyvista.domain.communication.CommunicationMessage;
import com.propertyvista.domain.communication.CommunicationMessageData;
import com.propertyvista.domain.communication.CommunicationThread;
import com.propertyvista.domain.security.CrmUser;
import com.propertyvista.domain.security.CustomerUser;
import com.propertyvista.domain.security.VistaCrmBehavior;
import com.propertyvista.dto.CommunicationEndpointDTO;
import com.propertyvista.dto.CommunicationMessageDTO;
import com.propertyvista.dto.CommunicationThreadDTO;
import com.propertyvista.dto.MessagesDTO;

public class CommunicationMessageCrudServiceImpl extends AbstractCrudServiceDtoImpl<CommunicationMessage, CommunicationMessageDTO> implements
        CommunicationMessageCrudService {
    public CommunicationMessageCrudServiceImpl() {
        super(CommunicationMessage.class, CommunicationMessageDTO.class);
    }

    @Override
    protected void bind() {
        bind(toProto.id(), boProto.id());
        bind(toProto.isRead(), boProto.isRead());
    }

    @Override
    protected Path convertPropertyDTOPathToDBOPath(String path, CommunicationMessage boProto, CommunicationMessageDTO toProto) {
        if (path.equals(toProto.date().getPath().toString())) {
            return boProto.data().date().getPath();
        }
        if (path.equals(toProto.sender().getPath().toString())) {
            return boProto.data().sender().getPath();
        }
        if (path.equals(toProto.subject().getPath().toString())) {
            return boProto.thread().subject().getPath();
        }
        if (path.equals(toProto.text().getPath().toString())) {
            return boProto.data().text().getPath();
        }
        if (path.equals(toProto.isHighImportance().getPath().toString())) {
            return boProto.data().isHighImportance().getPath();
        }
        if (path.equals(toProto.attachments().getPath().toString())) {
            return boProto.data().attachments().getPath();
        }
        return super.convertPropertyDTOPathToDBOPath(path, boProto, toProto);
    }

    @Override
    public void copyTOtoBO(CommunicationMessageDTO to, CommunicationMessage bo) {
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
    protected boolean persist(CommunicationMessage bo, CommunicationMessageDTO to) {
        CommunicationMessageData c = null;
        if (bo.id().isNull()) {
            c = EntityFactory.create(CommunicationMessageData.class);
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
            thread.created().setValue(SystemDateManager.getDate());
            Persistence.service().persist(thread);
            Persistence.service().commit();
            bo.thread().set(thread);

            for (CommunicationEndpointDTO mto : to.to()) {
                CommunicationMessage m = EntityFactory.create(CommunicationMessage.class);
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
        //bo.thread().set(to.thread());
        return super.persist(bo, to);
    }

    @Override
    protected void enhanceRetrieved(CommunicationMessage bo, CommunicationMessageDTO to, RetrieveTarget retrieveTarget) {
        enhanceAll(bo, to);
        to.subject().set(to.threadDTO().subject());
        to.attachments().setAttachLevel(AttachLevel.Attached);
        to.attachments().set(bo.data().attachments());

    }

    @Override
    public void list(AsyncCallback<EntitySearchResult<CommunicationMessageDTO>> callback, EntityListCriteria<CommunicationMessageDTO> dtoCriteria) {
        List<CommunicationMessage> ms = filterRelevantMessages(false, AttachLevel.IdOnly);

        if (ms == null || ms.isEmpty()) {
            callback.onSuccess(null);
            return;
        }

        HashMap<Key, Key> visitedThreads = new HashMap<Key, Key>();
        for (CommunicationMessage m : ms) {
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
    protected void enhanceListRetrieved(CommunicationMessage entity, CommunicationMessageDTO dto) {
        enhanceAll(entity, dto);
        dto.subject().set(entity.thread().subject());

        dto.setAttachLevel(AttachLevel.Attached);
        dto.sender().setAttachLevel(AttachLevel.Attached);
        dto.sender().set(entity.data().sender());
        dto.date().set(entity.data().date());
        dto.to().setAttachLevel(AttachLevel.Attached);
        dto.to().add(generateEndpointDTO(entity.recipient()));
        dto.attachments().setAttachLevel(AttachLevel.Attached);
        dto.attachments().set(entity.data().attachments());

        //if (belongsToUser(CrmAppContext.getCurrentUser(), dto.recipient())) {
        //    dto.isRead().set(dto.isRead());
        //} else {
        //    dto.isRead().setValue(true);
        //}
    }

    private CommunicationEndpointDTO generateEndpointDTO(CommunicationEndpoint entity) {
        CommunicationEndpointDTO rec = EntityFactory.create(CommunicationEndpointDTO.class);
        rec.endpoint().set(entity);

        if (entity.getInstanceValueClass().equals(CommunicationGroup.class)) {
            CommunicationGroup e = entity.cast();
            rec.name().set(e.name());
            rec.type().setValue(ContactType.Group);
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

    protected void enhanceAll(CommunicationMessage entity, CommunicationMessageDTO to) {
        enhanceDbo(entity, to);
    }

    protected void enhanceDbo(CommunicationMessage dbo, CommunicationMessageDTO to) {
        Persistence.ensureRetrieve(dbo.thread(), AttachLevel.Attached);
        Persistence.ensureRetrieve(dbo.data().sender(), AttachLevel.Attached);
        Persistence.ensureRetrieve(dbo.recipient(), AttachLevel.Attached);
        Persistence.ensureRetrieve(dbo.data().attachments(), AttachLevel.Attached);
        CommunicationThread thread = dbo.thread();
        CommunicationThreadDTO toThread = EntityFactory.create(CommunicationThreadDTO.class);
        IList<CommunicationMessage> ms = thread.content();
        if (ms != null && !ms.isNull()) {
            HashMap<Key, CommunicationMessageDTO> visitedMessages = new HashMap<Key, CommunicationMessageDTO>();
            for (CommunicationMessage m : ms) {
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
                toThread.responsible().set(m.thread().responsible());
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

    }

    @Override
    public void retreiveCommunicationMessages(AsyncCallback<MessagesDTO> callback, boolean newOnly) {
        if (!Context.isUserLoggedIn()) {
            callback.onSuccess(null);
            return;
        }
        CrmUser currentUser = CrmAppContext.getCurrentUser();
        if (currentUser == null || currentUser.isEmpty()) {
            callback.onSuccess(null);
            return;
        }

        MessagesDTO data = prefilterMessages(newOnly);
        callback.onSuccess(data);

    }

    private MessagesDTO prefilterMessages(boolean newOnly) {
        List<CommunicationMessage> ms = filterRelevantMessages(newOnly, AttachLevel.Attached);

        if (ms == null || ms.isEmpty()) {
            return null;
        }

        HashMap<Key, CommunicationMessageDTO> visitedThreads = new HashMap<Key, CommunicationMessageDTO>();
        MessagesDTO data = EntityFactory.create(MessagesDTO.class);
        for (CommunicationMessage m : ms) {
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
            toThread.responsible().set(m.thread().responsible());
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
        return data;
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

    private List<CommunicationMessage> filterRelevantMessages(boolean newOnly, AttachLevel attachLevel) {
        EntityQueryCriteria<CommunicationMessage> messageCriteria = EntityQueryCriteria.create(CommunicationMessage.class);
        if (newOnly) {
            messageCriteria.eq(messageCriteria.proto().isRead(), false);
        }
        messageCriteria.desc(messageCriteria.proto().data().date());

        OrCriterion or2 = null;
        if (newOnly) {
            if (SecurityController.checkBehavior(VistaCrmBehavior.Commandant)) {
                or2 = messageCriteria.or();
                or2.left().eq(messageCriteria.proto().recipient(), CrmAppContext.getCurrentUser());
                or2.right().eq(messageCriteria.proto().recipient(),
                        ServerSideFactory.create(CommunicationMessageFacade.class).getCommunicationGroupFromCache(EndpointGroup.Commandant));
                or2.right().eq(messageCriteria.proto().thread().attentionRequiried(), true);
                OrCriterion or3 = or2.right().or();
                or3.left().eq(messageCriteria.proto().thread().responsible(), CrmAppContext.getCurrentUser());
                or3.right().isNull(messageCriteria.proto().thread().responsible());
            } else {
                messageCriteria.eq(messageCriteria.proto().recipient(), CrmAppContext.getCurrentUser());
            }
        }

        List<CommunicationMessage> ms = Persistence.secureQuery(messageCriteria, attachLevel);
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
        if (source.to().isNull() || source.to().isEmpty()) {
            source.recipient().set(ServerSideFactory.create(CommunicationMessageFacade.class).getCommunicationGroupFromCache(EndpointGroup.Commandant));
        }
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
        if (user.equals(recipient)
                || ServerSideFactory.create(CommunicationMessageFacade.class).getCommunicationGroupFromCache(EndpointGroup.Commandant).equals(recipient)) {
            return true;
        }

        return false;
    }
}