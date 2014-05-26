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
import com.pyx4j.entity.server.AbstractCrudServiceDtoImpl;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.server.contexts.Context;

import com.propertyvista.biz.communication.CommunicationMessageFacade;
import com.propertyvista.domain.communication.CommunicationMessage;
import com.propertyvista.domain.communication.CommunicationMessageData;
import com.propertyvista.domain.communication.CommunicationThread;
import com.propertyvista.domain.communication.SystemEndpoint.SystemEndpointName;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.portal.rpc.portal.resident.communication.CommunicationMessageDTO;
import com.propertyvista.portal.rpc.portal.resident.communication.MessagesDTO;
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
        CommunicationMessageDTO dto = super.init(initializationData);
        dto.date().setValue(SystemDateManager.getDate());
        dto.isRead().setValue(false);
        dto.sender().set(ResidentPortalContext.getCurrentUser());
        dto.recipient().add(ServerSideFactory.create(CommunicationMessageFacade.class).getSystemEndpointFromCache(SystemEndpointName.Unassigned));
        return dto;
    }

    @Override
    protected boolean persist(CommunicationThread bo, CommunicationMessageDTO to) {
        if (bo.isPrototype()) {
            bo.created().setValue(SystemDateManager.getDate());
        }

        CommunicationMessageData c = EntityFactory.create(CommunicationMessageData.class);
        CommunicationMessage m = EntityFactory.create(CommunicationMessage.class);
        m.isRead().setValue(false);
        c.attachments().set(to.attachments());
        c.date().setValue(SystemDateManager.getDate());
        c.sender().set(ResidentPortalContext.getCurrentUser());
        m.recipient().set(ServerSideFactory.create(CommunicationMessageFacade.class).getSystemEndpointFromCache(SystemEndpointName.Unassigned));

        c.text().set(to.text());
        c.isHighImportance().set(to.isHighImportance());
        Persistence.service().persist(c);
        m.data().set(c);
        bo.subject().set(to.subject());
        bo.content().add(m);
        //bo.attentionRequiried().setValue(true);

        return super.persist(bo, to);
    }

    @Override
    protected void enhanceRetrieved(CommunicationThread bo, CommunicationMessageDTO to, RetrieveTarget retrieveTarget) {
        enhanceAll(bo, to);
        to.thread().setAttachLevel(AttachLevel.Attached);
        to.thread().set(bo);
        to.subject().set(bo.subject());
        if (bo.content() != null && !bo.content().isNull() && bo.content().size() > 0) {
            to.text().set(bo.content().get(0).data().text());
            to.isHighImportance().set(bo.content().get(0).data().isHighImportance());
            to.star().set(bo.content().get(0).star());
            to.date().set(bo.content().get(0).data().date());
            to.attachments().setAttachLevel(AttachLevel.Attached);
            to.attachments().set(bo.content().get(0).data().attachments());
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
                Persistence.ensureRetrieve(m.data(), AttachLevel.Attached);
                Persistence.ensureRetrieve(m.data().attachments(), AttachLevel.Attached);
                Persistence.ensureRetrieve(m.data().sender(), AttachLevel.Attached);
                Persistence.ensureRetrieve(m.recipient(), AttachLevel.Attached);

                if (ResidentPortalContext.getCurrentUser().equals(m.data().sender())) {
                    m.isRead().setValue(true);
                } else {
                    m.isRead().set(m.isRead());
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
        messageCriteria.desc(messageCriteria.proto().data().date());

        if (newOnly) {
            messageCriteria.eq(messageCriteria.proto().isRead(), false);
            messageCriteria.eq(messageCriteria.proto().recipient(), ResidentPortalContext.getCurrentUser());
        }
        List<CommunicationMessage> ms = Persistence.secureQuery(messageCriteria);

        if (ms == null || ms.isEmpty()) {
            callback.onSuccess(null);
            return;
        }

        HashMap<Key, CommunicationMessageDTO> visitedThreads = new HashMap<Key, CommunicationMessageDTO>();
        MessagesDTO data = EntityFactory.create(MessagesDTO.class);
        for (CommunicationMessage m : ms) {
            Persistence.ensureRetrieve(m.thread(), AttachLevel.Attached);
            Persistence.ensureRetrieve(m.data(), AttachLevel.Attached);
            if (visitedThreads.containsKey(m.thread().id().getValue())) {
                CommunicationMessageDTO thread = visitedThreads.get(m.thread().id().getValue());

                if (m.star().getValue(false)) {
                    thread.star().setValue(true);
                }

                if (m.data().isHighImportance().getValue()) {
                    thread.isHighImportance().setValue(m.data().isHighImportance().getValue());
                }
                if (ResidentPortalContext.getCurrentUser().equals(m.data().sender())) {
                    continue;
                }
                if (!m.isRead().getValue(false)) {
                    thread.isRead().set(m.isRead());
                }
                continue;
            }

            CommunicationMessageDTO message = EntityFactory.create(CommunicationMessageDTO.class);
            message.subject().set(m.thread().subject());
            message.text().set(m.data().text());
            message.date().set(m.data().date());
            message.thread().setAttachLevel(AttachLevel.Attached);
            message.thread().set(m.thread());
            Persistence.ensureRetrieve(m.data().sender(), AttachLevel.Attached);
            message.sender().setAttachLevel(AttachLevel.Attached);
            message.sender().set(m.data().sender());
            message.recipient().add(m.recipient());
            if (ResidentPortalContext.getCurrentUser().equals(m.data().sender())) {
                message.isRead().setValue(true);
            } else {
                message.isRead().set(m.isRead());
            }

            visitedThreads.put(m.thread().id().getValue(), message);
            data.messages().add(message);

        }
        callback.onSuccess(data);

    }

    @Override
    public void saveMessage(AsyncCallback<CommunicationMessage> callback, CommunicationMessage message) {
        if (message.data().date().isNull()) {
            message.data().date().setValue(SystemDateManager.getDate());
        }
        if (message.recipient().isNull() || message.recipient().isEmpty()) {
            message.recipient().set(ServerSideFactory.create(CommunicationMessageFacade.class).getSystemEndpointFromCache(SystemEndpointName.Unassigned));
        }
        if (message.data().sender().isNull()) {
            message.data().sender().set(ResidentPortalContext.getCurrentUser());
        }
        Persistence.service().persist(message.data());
        Persistence.service().persist(message);
        Persistence.service().commit();
        callback.onSuccess(message);
    }
}
