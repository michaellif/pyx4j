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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.config.server.SystemDateManager;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IList;
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
import com.propertyvista.domain.communication.CommunicationMessage;
import com.propertyvista.domain.communication.CommunicationThread;
import com.propertyvista.domain.communication.SystemEndpoint.EndpointType;
import com.propertyvista.domain.security.CrmUser;
import com.propertyvista.dto.CommunicationMessageDTO;
import com.propertyvista.dto.MessagesDTO;

public class CommunicationMessageCrudServiceImpl extends AbstractCrudServiceDtoImpl<CommunicationMessage, CommunicationMessageDTO> implements
        CommunicationMessageCrudService {
    public CommunicationMessageCrudServiceImpl() {
        super(CommunicationMessage.class, CommunicationMessageDTO.class);
    }

    @Override
    protected void bind() {
        bindCompleteObject();
    }

    @Override
    protected CommunicationMessageDTO init(InitializationData initializationData) {
        CommunicationMessageDTO dto = EntityFactory.create(CommunicationMessageDTO.class);
        dto.date().setValue(SystemDateManager.getDate());
        dto.isRead().setValue(false);
        dto.sender().set(CrmAppContext.getCurrentUser());
        //////////////////dto.to().add(ServerSideFactory.create(CommunicationMessageFacade.class).getSystemEndpointFromCache(EndpointType.unassigned));

        return dto;

    }

    @Override
    protected void persist(CommunicationMessage bo, CommunicationMessageDTO to) {
        if (to.thread().created().isNull() || to.thread().created().isPrototype()) {
            to.thread().subject().set(to.subject());
            Persistence.service().persist(to.thread());
            Persistence.service().commit();
        }
        bo.thread().set(to.thread());
        bo.attachments().set(to.attachments());
        bo.date().setValue(SystemDateManager.getDate());
        bo.sender().set(CrmAppContext.getCurrentUser());
        /////////////bo.to().add(ServerSideFactory.create(CommunicationMessageFacade.class).getSystemEndpointFromCache(EndpointType.unassigned));

        bo.isRead().setValue(false);
        bo.text().set(to.text());
        bo.thread().set(to.thread());
        super.persist(bo, to);
    }

    @Override
    protected void enhanceRetrieved(CommunicationMessage bo, CommunicationMessageDTO to, RetrieveTarget retrieveTarget) {
        enhanceAll(bo);
        to.thread().setAttachLevel(AttachLevel.Attached);
        to.thread().set(bo.thread());
        to.subject().set(to.thread().subject());
        to.attachments().setAttachLevel(AttachLevel.Attached);
        to.attachments().set(bo.attachments());

    }

    @Override
    public void list(AsyncCallback<EntitySearchResult<CommunicationMessageDTO>> callback, EntityListCriteria<CommunicationMessageDTO> dtoCriteria) {
        MessagesDTO data = prefilterMessages(false);
        if (data != null && data.messages().size() > 0) {
            Set<Key> visited = new HashSet<Key>(data.messages().size());
            for (CommunicationMessage m : data.messages()) {
                visited.add(m.id().getValue());
            }

            dtoCriteria.in(dtoCriteria.proto().id(), visited);
        }

        super.list(callback, dtoCriteria);
    }

    @Override
    protected void enhanceListRetrieved(CommunicationMessage entity, CommunicationMessageDTO dto) {
        enhanceAll(entity);
        dto.thread().setAttachLevel(AttachLevel.Attached);
        dto.thread().set(entity.thread());
        dto.subject().set(entity.thread().subject());

        dto.sender().setAttachLevel(AttachLevel.Attached);
        dto.sender().set(entity.sender());
        dto.to().setAttachLevel(AttachLevel.Attached);
        dto.to().set(entity.to());
        dto.attachments().setAttachLevel(AttachLevel.Attached);
        dto.attachments().set(entity.attachments());

        if (userInList(CrmAppContext.getCurrentUser(), dto.to())) {
            dto.isRead().set(dto.isRead());
        } else {
            dto.isRead().setValue(true);
        }
    }

    protected void enhanceAll(CommunicationMessage dto) {
        enhanceDbo(dto);
    }

    protected void enhanceDbo(CommunicationMessage dbo) {
        Persistence.ensureRetrieve(dbo.thread(), AttachLevel.Attached);
        Persistence.ensureRetrieve(dbo.sender(), AttachLevel.Attached);
        Persistence.ensureRetrieve(dbo.to(), AttachLevel.Attached);
        Persistence.ensureRetrieve(dbo.attachments(), AttachLevel.Attached);
        CommunicationThread thread = dbo.thread();
        IList<CommunicationMessage> ms = thread.content();
        if (ms != null && !ms.isNull()) {
            for (CommunicationMessage m : ms) {
                Persistence.ensureRetrieve(m.attachments(), AttachLevel.Attached);
                Persistence.ensureRetrieve(m.sender(), AttachLevel.Attached);
                Persistence.ensureRetrieve(m.to(), AttachLevel.Attached);
                if (userInList(CrmAppContext.getCurrentUser(), m.to())) {
                    m.isRead().set(m.isRead());
                } else {
                    m.isRead().setValue(true);
                }
            }
        }
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
        EntityQueryCriteria<CommunicationMessage> messageCriteria = EntityQueryCriteria.create(CommunicationMessage.class);
        if (newOnly) {
            messageCriteria.eq(messageCriteria.proto().isRead(), false);
        }
        messageCriteria.desc(messageCriteria.proto().date());

        OrCriterion or2 = null;
        if (newOnly) {
            or2 = messageCriteria.or();
        } else {
            OrCriterion or = messageCriteria.or();
            or.left().eq(messageCriteria.proto().sender(), CrmAppContext.getCurrentUser());
            or2 = or.right().or();
        }
        or2.left().eq(messageCriteria.proto().to(), CrmAppContext.getCurrentUser());
        or2.right().eq(messageCriteria.proto().to(),
                ServerSideFactory.create(CommunicationMessageFacade.class).getSystemEndpointFromCache(EndpointType.unassigned));

        List<CommunicationMessage> ms = Persistence.service().query(messageCriteria);

        if (ms == null || ms.isEmpty()) {
            return null;
        }

        HashMap<Key, CommunicationMessageDTO> visitedThreads = new HashMap<Key, CommunicationMessageDTO>();
        MessagesDTO data = EntityFactory.create(MessagesDTO.class);
        for (CommunicationMessage m : ms) {
            Persistence.ensureRetrieve(m.thread(), AttachLevel.Attached);
            if (visitedThreads.containsKey(m.thread().id().getValue())) {
                CommunicationMessageDTO thread = visitedThreads.get(m.thread().id().getValue());
                if (CrmAppContext.getCurrentUser().equals(m.sender())) {
                    continue;
                }
                if (!m.isRead().isBooleanTrue()) {
                    thread.isRead().set(m.isRead());
                }
                continue;
            }
            Persistence.ensureRetrieve(m.sender(), AttachLevel.Attached);
            Persistence.ensureRetrieve(m.to(), AttachLevel.Attached);

            CommunicationMessageDTO message = EntityFactory.create(CommunicationMessageDTO.class);
            message.id().set(m.id());
            message.subject().set(m.thread().subject());
            message.text().set(m.text());
            message.date().set(m.date());
            message.thread().setAttachLevel(AttachLevel.Attached);
            message.thread().set(m.thread());
            message.sender().setAttachLevel(AttachLevel.Attached);
            message.sender().set(m.sender());
            message.to().setAttachLevel(AttachLevel.Attached);
            message.to().set(m.to());
            if (userInList(CrmAppContext.getCurrentUser(), m.to())) {
                message.isRead().set(m.isRead());
            } else {
                message.isRead().setValue(true);
            }

            visitedThreads.put(m.thread().id().getValue(), message);
            data.messages().add(message);

        }
        return data;
    }

    @Override
    public void saveMessage(AsyncCallback<CommunicationMessage> callback, CommunicationMessage source) {
        if (source.thread().created().isNull() || source.thread().created().isPrototype()) {
            Persistence.service().persist(source.thread());
        }

        if (source.date().isNull()) {
            source.date().setValue(SystemDateManager.getDate());
        }
        if (source.to().isNull() || source.to().isEmpty()) {
            source.to().add(ServerSideFactory.create(CommunicationMessageFacade.class).getSystemEndpointFromCache(EndpointType.unassigned));
        }
        if (source.sender().isNull()) {
            source.sender().set(CrmAppContext.getCurrentUser());
        }

        Persistence.service().persist(source);
        Persistence.service().commit();
        callback.onSuccess(source);
    }

    @Override
    public void takeOwnership(AsyncCallback<CommunicationMessage> callback, CommunicationMessage source) {
        source.thread().responsible().set(CrmAppContext.getCurrentUser());
        for (CommunicationMessage m : source.thread().content()) {
            if (!m.isRead().getValue() && userInList(CrmAppContext.getCurrentUser(), m.to())) {
                m.isRead().setValue(true);
            }
        }
        Persistence.service().persist(source.thread());
        Persistence.service().commit();
        callback.onSuccess(source);
    }

    private static boolean userInList(CommunicationEndpoint user, IList<CommunicationEndpoint> list) {
        if (list == null || list.isNull()) {
            return false;
        }

        for (CommunicationEndpoint ep : list) {
            if (user.equals(ep) || ServerSideFactory.create(CommunicationMessageFacade.class).getSystemEndpointFromCache(EndpointType.unassigned).equals(ep)) {
                return true;
            }
        }
        return false;
    }
}