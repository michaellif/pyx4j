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
 */
package com.propertyvista.portal.server.portal.resident.services;

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
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.server.AbstractCrudServiceDtoImpl;
import com.pyx4j.entity.server.CrudEntityBinder;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.rpc.shared.VoidSerializable;
import com.pyx4j.server.contexts.ServerContext;

import com.propertyvista.biz.communication.CommunicationMessageFacade;
import com.propertyvista.domain.communication.CommunicationThread;
import com.propertyvista.domain.communication.DeliveryHandle;
import com.propertyvista.domain.communication.Message;
import com.propertyvista.domain.communication.MessageCategory.TicketType;
import com.propertyvista.domain.communication.SystemEndpoint.SystemEndpointName;
import com.propertyvista.domain.communication.ThreadPolicyHandle;
import com.propertyvista.domain.tenant.lease.LeaseParticipant;
import com.propertyvista.dto.communication.CommunicationThreadDTO;
import com.propertyvista.dto.communication.MessageDTO;
import com.propertyvista.portal.rpc.portal.resident.services.CommunicationPortalCrudService;
import com.propertyvista.portal.server.portal.shared.PortalVistaContext;

public class CommunicationPortalCrudServiceImpl extends AbstractCrudServiceDtoImpl<CommunicationThread, CommunicationThreadDTO> implements
        CommunicationPortalCrudService {

    public CommunicationPortalCrudServiceImpl() {
        super(new CrudEntityBinder<CommunicationThread, CommunicationThreadDTO>(CommunicationThread.class, CommunicationThreadDTO.class) {

            @Override
            protected void bind() {
                bind(toProto.id(), boProto.id());
                bind(toProto.subject(), boProto.subject());
                bind(toProto.allowedReply(), boProto.allowedReply());
                bind(toProto.status(), boProto.status());
                bind(toProto.category(), boProto.category());
                bind(toProto.content(), boProto.content());
                bind(toProto.userPolicy(), boProto.userPolicy());
                bind(toProto.associated(), boProto.associated());
                bind(toProto.deliveryMethod(), boProto.deliveryMethod());
                bind(toProto.specialDelivery(), boProto.specialDelivery());
            }
        });
    }

    @Override
    protected Path convertPropertyDTOPathToDBOPath(String path, CommunicationThread boProto, CommunicationThreadDTO toProto) {

        if (path.equals(toProto.content().$().recipients().$().isRead().getPath().toString()) || path.equals(toProto.isRead().getPath().toString())) {
            return boProto.content().$().recipients().$().isRead().getPath();
        }
        if (path.equals(toProto.content().$().recipients().$().star().getPath().toString()) || path.equals(toProto.star().getPath().toString())) {
            return boProto.content().$().recipients().$().star().getPath();
        }
        if (path.equals(toProto.content().$().recipients().$().recipient().getPath().toString())) {
            return boProto.content().$().recipients().$().recipient().getPath();
        }
        if (path.equals(toProto.date().getPath().toString())) {
            return boProto.content().$().date().getPath();
        }
        return super.convertPropertyDTOPathToDBOPath(path, boProto, toProto);
    }

    @Override
    protected void enhanceListCriteria(EntityListCriteria<CommunicationThread> boCriteria, EntityListCriteria<CommunicationThreadDTO> toCriteria) {
        PropertyCriterion recipientCiteria = toCriteria.getCriterion(toCriteria.proto().content().$().recipients().$().recipient());
        if (recipientCiteria != null) {
            toCriteria.getFilters().remove(recipientCiteria);
            boCriteria.eq(boCriteria.proto().content().$().recipients().$().recipient(), PortalVistaContext.getLeaseParticipant());
        }
        super.enhanceListCriteria(boCriteria, toCriteria);
    }

    @Override
    protected CommunicationThreadDTO init(InitializationData initializationData) {
        CommunicationThreadDTO dto = super.init(initializationData);
        dto.category().set(ServerSideFactory.create(CommunicationMessageFacade.class).getMessageCategoryFromCache(TicketType.Tenant));
        MessageDTO m = EntityFactory.create(MessageDTO.class);
        dto.representingMessage().set(m);
        dto.childMessages().add(m);
        m.isRead().setValue(false);
        m.highImportance().setValue(false);
        dto.allowedReply().setValue(true);
        m.sender().set(PortalVistaContext.getLeaseParticipant());
        if (initializationData instanceof MessageInitializationData) {
            m.content().set(((MessageInitializationData) initializationData).initalizedText());
        }

        return dto;
    }

    @Override
    protected boolean persist(CommunicationThread bo, CommunicationThreadDTO to) {
        Message message = EntityFactory.create(Message.class);
        CommunicationMessageFacade communicationFacade = ServerSideFactory.create(CommunicationMessageFacade.class);

        message.attachments().set(to.representingMessage().attachments());
        message.date().setValue(SystemDateManager.getDate());
        message.sender().set(PortalVistaContext.getLeaseParticipant());
        message.content().set(to.representingMessage().content());
        message.highImportance().set(to.representingMessage().highImportance());
        message.recipients()
                .add(communicationFacade.createDeliveryHandle(communicationFacade.getSystemEndpointFromCache(SystemEndpointName.Unassigned), false));

        bo.content().add(message);
        bo.owner().set(communicationFacade.getSystemEndpointFromCache(SystemEndpointName.Unassigned));
        ServerContext.getVisit().setAttribute(CommunicationMessageFacade.class.getName(), new Long(0L));

        return super.persist(bo, to);
    }

    @Override
    public void listForHeader(AsyncCallback<EntitySearchResult<CommunicationThreadDTO>> callback) {
        CommunicationMessageFacade communicationFacade = ServerSideFactory.create(CommunicationMessageFacade.class);

        List<CommunicationThread> directThreads = communicationFacade.getDirectThreads(PortalVistaContext.getLeaseParticipant());

        EntityListCriteria<CommunicationThreadDTO> messageCriteria = EntityListCriteria.create(CommunicationThreadDTO.class);
        if (directThreads != null && directThreads.size() > 0) {
            messageCriteria.in(messageCriteria.proto().id(), directThreads);
        } else {
            messageCriteria.notExists(messageCriteria.proto().id());
        }

        ServerContext.getVisit().setAttribute(CommunicationMessageFacade.class.getName(), new Long(0L));

        messageCriteria.setPageSize(50);
        messageCriteria.setPageNumber(0);
        list(callback, messageCriteria);
    }

    @Override
    protected EntitySearchResult<CommunicationThread> query(EntityListCriteria<CommunicationThread> criteria) {
        return ServerSideFactory.create(CommunicationMessageFacade.class).query(criteria);
    }

    @Override
    protected void enhanceRetrieved(CommunicationThread bo, CommunicationThreadDTO to, RetrieveTarget retrieveTarget) {
        ServerSideFactory.create(CommunicationMessageFacade.class).enhanceThreadDbo(bo, to, false, PortalVistaContext.getLeaseParticipant());
    }

    @Override
    protected void enhanceListRetrieved(CommunicationThread bo, CommunicationThreadDTO to) {
        ServerSideFactory.create(CommunicationMessageFacade.class).enhanceThreadDbo(bo, to, true, PortalVistaContext.getLeaseParticipant());
    }

    @Override
    public void saveChildMessage(AsyncCallback<MessageDTO> callback, MessageDTO message) {
        if (message.date().isNull()) {
            CommunicationThread thread = Persistence.secureRetrieve(CommunicationThread.class, message.thread().id().getValue());

            Message m = EntityFactory.create(Message.class);
            m.thread().set(thread);
            m.attachments().set(message.attachments());
            m.date().setValue(SystemDateManager.getDate());
            m.sender().set(PortalVistaContext.getLeaseParticipant());
            m.content().set(message.content());
            m.highImportance().set(message.highImportance());
            if (message.recipients() != null && message.recipients().size() > 0) {
                m.recipients().add(message.recipients().get(0));
            } else {
                m.recipients().add(ServerSideFactory.create(CommunicationMessageFacade.class).createDeliveryHandle(thread.owner(), false));
            }
            Persistence.service().persist(m);
        } else {
            EntityQueryCriteria<DeliveryHandle> dhCriteria = EntityQueryCriteria.create(DeliveryHandle.class);
            dhCriteria.eq(dhCriteria.proto().recipient(), PortalVistaContext.getLeaseParticipant());
            dhCriteria.eq(dhCriteria.proto().message(), message);
            DeliveryHandle dh = Persistence.retrieveUnique(dhCriteria, AttachLevel.Attached);
            dh.isRead().set(message.isRead());
            dh.star().set(message.star());
            Persistence.service().persist(dh);
        }
        Persistence.service().commit();

        ServerContext.getVisit().setAttribute(CommunicationMessageFacade.class.getName(), new Long(0L));

        callback.onSuccess(message);
    }

    @Override
    public void hideThread(AsyncCallback<VoidSerializable> callback, Key entityId) {
        CommunicationThread thread = Persistence.secureRetrieve(CommunicationThread.class, entityId);
        if (thread == null) {
            callback.onFailure(new Error("The thread does not exist"));
            return;
        }

        LeaseParticipant<?> currentUser = PortalVistaContext.getLeaseParticipant();
        EntityQueryCriteria<ThreadPolicyHandle> policyCriteria = EntityQueryCriteria.create(ThreadPolicyHandle.class);

        policyCriteria.add(PropertyCriterion.in(policyCriteria.proto().thread(), thread));
        policyCriteria.eq(policyCriteria.proto().policyConsumer(), currentUser);

        ThreadPolicyHandle handle = Persistence.secureRetrieve(policyCriteria);
        if (handle == null) {
            handle = EntityFactory.create(ThreadPolicyHandle.class);
            handle.policyConsumer().set(currentUser);
            handle.thread().set(thread);
        }

        handle.hidden().setValue(true);
        Persistence.service().persist(handle);
        Persistence.service().commit();
        callback.onSuccess(null);
    }

    private boolean isRecipientOf(Message m, LeaseParticipant<?> lp) {
        for (DeliveryHandle dh : m.recipients()) {
            if (lp.equals(dh.recipient())) {
                return true;
            }
        }
        return false;
    }
}
