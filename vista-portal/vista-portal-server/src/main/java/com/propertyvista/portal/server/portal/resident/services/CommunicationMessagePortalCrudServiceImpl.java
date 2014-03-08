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
package com.propertyvista.portal.server.portal.resident.services;

import java.util.HashMap;
import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.config.server.SystemDateManager;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IList;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.core.criterion.OrCriterion;
import com.pyx4j.entity.server.AbstractCrudServiceDtoImpl;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.server.contexts.Context;

import com.propertyvista.biz.communication.CommunicationMessageFacade;
import com.propertyvista.domain.communication.CommunicationEndpoint;
import com.propertyvista.domain.communication.CommunicationMessage;
import com.propertyvista.domain.communication.CommunicationThread;
import com.propertyvista.domain.communication.SystemEndpoint.EndpointType;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.security.common.AbstractUser;
import com.propertyvista.dto.CommunicationMessageDTO;
import com.propertyvista.dto.MessagesDTO;
import com.propertyvista.portal.rpc.portal.resident.services.CommunicationMessagePortalCrudService;
import com.propertyvista.portal.server.portal.resident.ResidentPortalContext;

public class CommunicationMessagePortalCrudServiceImpl extends AbstractCrudServiceDtoImpl<CommunicationThread, CommunicationMessageDTO> implements
        CommunicationMessagePortalCrudService {

    public CommunicationMessagePortalCrudServiceImpl() {
        super(CommunicationThread.class, CommunicationMessageDTO.class);
    }

    @Override
    protected void bind() {
        bind(toProto.subject(), boProto.subject());
    }

    @Override
    protected CommunicationMessageDTO init(InitializationData initializationData) {
        CommunicationMessageDTO dto = EntityFactory.create(CommunicationMessageDTO.class);
        dto.date().setValue(SystemDateManager.getDate());
        dto.isRead().setValue(false);
        dto.sender().set(ResidentPortalContext.getCurrentUser());
        dto.to().add(ServerSideFactory.create(CommunicationMessageFacade.class).getSystemEndpointFromCache(EndpointType.unassigned));
        return dto;
    }

    @Override
    protected void persist(CommunicationThread bo, CommunicationMessageDTO to) {
        if (bo.isPrototype()) {
            bo.created().setValue(SystemDateManager.getDate());
        }

        CommunicationMessage m = EntityFactory.create(CommunicationMessage.class);
        m.isRead().setValue(false);
        m.attachments().set(to.attachments());
        m.date().setValue(SystemDateManager.getDate());
        m.sender().set(ResidentPortalContext.getCurrentUser());
        m.to().add(ServerSideFactory.create(CommunicationMessageFacade.class).getSystemEndpointFromCache(EndpointType.unassigned));

        m.text().set(to.text());
        bo.subject().set(to.subject());
        bo.content().add(m);
        super.persist(bo, to);
    }

    @Override
    protected void enhanceRetrieved(CommunicationThread bo, CommunicationMessageDTO to, RetrieveTarget retrieveTarget) {
        enhanceAll(bo, to);
        to.thread().setAttachLevel(AttachLevel.Attached);
        to.thread().set(bo);
        to.subject().set(bo.subject());
        if (bo.content() != null && !bo.content().isNull() && bo.content().size() > 0) {
            to.text().set(bo.content().get(0).text());
            to.date().set(bo.content().get(0).date());
            to.attachments().setAttachLevel(AttachLevel.Attached);
            to.attachments().set(bo.content().get(0).attachments());
        }
    }

    @Override
    protected void enhanceListRetrieved(CommunicationThread entity, CommunicationMessageDTO dto) {
        enhanceAll(entity, dto);
    }

    protected void enhanceAll(CommunicationThread dto, CommunicationMessageDTO to) {
        enhanceDbo(dto, to);
    }

    protected void enhanceDbo(CommunicationThread dbo, CommunicationMessageDTO to) {
        Persistence.ensureRetrieve(dbo.content(), AttachLevel.Attached);
        IList<CommunicationMessage> ms = dbo.content();
        if (ms != null && !ms.isNull()) {
            for (CommunicationMessage m : ms) {
                Persistence.ensureRetrieve(m.attachments(), AttachLevel.Attached);

                if (userInList(ResidentPortalContext.getCurrentUser(), m.to())) {
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
        AptUnit unit = ResidentPortalContext.getUnit();
        if (unit == null || unit.isEmpty()) {
            callback.onSuccess(null);
            return;
        }

        EntityQueryCriteria<CommunicationMessage> messageCriteria = EntityQueryCriteria.create(CommunicationMessage.class);
        if (newOnly) {
            messageCriteria.eq(messageCriteria.proto().isRead(), false);
        }
        messageCriteria.desc(messageCriteria.proto().date());
        if (newOnly) {
            messageCriteria.eq(messageCriteria.proto().to(), ResidentPortalContext.getCurrentUser());
        } else {
            OrCriterion or = messageCriteria.or();
            or.left().eq(messageCriteria.proto().sender(), ResidentPortalContext.getCurrentUser());
            or.right().eq(messageCriteria.proto().to(), ResidentPortalContext.getCurrentUser());
        }
        List<CommunicationMessage> ms = Persistence.service().query(messageCriteria);

        if (ms == null || ms.isEmpty()) {
            callback.onSuccess(null);
            return;
        }

        HashMap<Key, CommunicationMessageDTO> visitedThreads = new HashMap<Key, CommunicationMessageDTO>();
        MessagesDTO data = EntityFactory.create(MessagesDTO.class);
        for (CommunicationMessage m : ms) {
            Persistence.ensureRetrieve(m.thread(), AttachLevel.Attached);
            if (visitedThreads.containsKey(m.thread().id().getValue())) {
                CommunicationMessageDTO thread = visitedThreads.get(m.thread().id().getValue());
                if (ResidentPortalContext.getCurrentUser().equals(m.sender())) {
                    continue;
                }
                if (!m.isRead().isBooleanTrue()) {
                    thread.isRead().set(m.isRead());
                }
                continue;
            }

            CommunicationMessageDTO message = EntityFactory.create(CommunicationMessageDTO.class);
            message.subject().set(m.thread().subject());
            message.text().set(m.text());
            message.date().set(m.date());
            message.thread().setAttachLevel(AttachLevel.Attached);
            message.thread().set(m.thread());
            Persistence.ensureRetrieve(m.sender(), AttachLevel.Attached);
            message.sender().setAttachLevel(AttachLevel.Attached);
            message.sender().set(m.sender());
            message.to().set(m.to());
            if (userInList(ResidentPortalContext.getCurrentUser(), m.to())) {
                message.isRead().set(m.isRead());
            } else {
                message.isRead().setValue(true);
            }

            visitedThreads.put(m.thread().id().getValue(), message);
            data.messages().add(message);

        }
        callback.onSuccess(data);

    }

    @Override
    public void saveMessage(AsyncCallback<CommunicationMessage> callback, CommunicationMessage message) {
        if (message.date().isNull()) {
            message.date().setValue(SystemDateManager.getDate());
        }
        if (message.to().isNull() || message.to().isEmpty()) {
            message.to().add(ServerSideFactory.create(CommunicationMessageFacade.class).getSystemEndpointFromCache(EndpointType.unassigned));
        }
        if (message.sender().isNull()) {
            message.sender().set(ResidentPortalContext.getCurrentUser());
        }
        Persistence.service().persist(message);
        Persistence.service().commit();
        callback.onSuccess(message);
    }

    private static boolean userInList(AbstractUser user, IList<CommunicationEndpoint> list) {
        if (list == null || list.isNull()) {
            return false;
        }

        for (CommunicationEndpoint ep : list) {
            if (user.equals(ep)) {
                return true;
            }
        }
        return false;
    }
}
