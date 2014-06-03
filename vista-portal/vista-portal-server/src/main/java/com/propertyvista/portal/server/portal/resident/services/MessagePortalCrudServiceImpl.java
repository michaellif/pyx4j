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

import java.util.HashSet;
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
import com.pyx4j.entity.core.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.security.EntityPermission;
import com.pyx4j.entity.server.AbstractCrudServiceDtoImpl;
import com.pyx4j.entity.server.IEntityPersistenceService.ICursorIterator;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.security.shared.SecurityController;

import com.propertyvista.biz.communication.CommunicationMessageFacade;
import com.propertyvista.domain.communication.CommunicationThread;
import com.propertyvista.domain.communication.CommunicationThread.ThreadStatus;
import com.propertyvista.domain.communication.DeliveryHandle;
import com.propertyvista.domain.communication.Message;
import com.propertyvista.domain.communication.MessageCategory.MessageGroupCategory;
import com.propertyvista.domain.communication.SystemEndpoint.SystemEndpointName;
import com.propertyvista.portal.rpc.portal.resident.communication.MessageDTO;
import com.propertyvista.portal.rpc.portal.resident.services.MessagePortalCrudService;
import com.propertyvista.portal.server.portal.resident.ResidentPortalContext;

public class MessagePortalCrudServiceImpl extends AbstractCrudServiceDtoImpl<Message, MessageDTO> implements MessagePortalCrudService {

    public MessagePortalCrudServiceImpl() {
        super(Message.class, MessageDTO.class);
    }

    @Override
    protected void bind() {
        bindCompleteObject();
    }

    @Override
    protected Path convertPropertyDTOPathToDBOPath(String path, Message boProto, MessageDTO toProto) {

        if (path.equals(toProto.thread().content().$().recipients().$().isRead().getPath().toString()) || path.equals(toProto.isRead().getPath().toString())) {
            return boProto.recipients().$().isRead().getPath();
        }
        if (path.equals(toProto.thread().content().$().recipients().$().star().getPath().toString()) || path.equals(toProto.star().getPath().toString())) {
            return boProto.recipients().$().star().getPath();
        }
        if (path.equals(toProto.thread().content().$().recipients().$().recipient().getPath().toString())) {
            return boProto.recipients().$().recipient().getPath();
        }
        if (path.equals(toProto.thread().content().$().date().getPath().toString())) {
            return boProto.date().getPath();
        }
        if (path.equals(toProto.thread().created().getPath().toString())) {
            return boProto.thread().created().getPath();
        }
        if (path.equals(toProto.thread().subject().getPath().toString())) {
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
            boCriteria.eq(boCriteria.proto().recipients().$().recipient(), ResidentPortalContext.getCurrentUser());
        }
        super.enhanceListCriteria(boCriteria, toCriteria);
    }

    @Override
    protected MessageDTO init(InitializationData initializationData) {
        MessageDTO dto = super.init(initializationData);
        dto.date().setValue(SystemDateManager.getDate());
        dto.isRead().setValue(false);
        dto.sender().set(ResidentPortalContext.getCurrentUser());
        return dto;
    }

    @Override
    protected boolean persist(Message bo, MessageDTO to) {
        if (bo.isPrototype()) {
            bo.date().setValue(SystemDateManager.getDate());
        }

        DeliveryHandle dh = EntityFactory.create(DeliveryHandle.class);
        dh.isRead().setValue(false);
        dh.star().setValue(false);
        dh.recipient().set(ServerSideFactory.create(CommunicationMessageFacade.class).getSystemEndpointFromCache(SystemEndpointName.Unassigned));

        bo.attachments().set(to.attachments());
        bo.date().setValue(SystemDateManager.getDate());
        bo.sender().set(ResidentPortalContext.getCurrentUser());
        bo.text().set(to.text());
        bo.highImportance().set(to.highImportance());
        bo.recipients().add(dh);

        CommunicationThread t = EntityFactory.create(CommunicationThread.class);
        t.subject().set(to.subject());
        t.created().setValue(SystemDateManager.getDate());
        t.allowedReply().setValue(true);
        t.status().setValue(ThreadStatus.New);
        t.topic().set(ServerSideFactory.create(CommunicationMessageFacade.class).getCommunicationGroupFromCache(MessageGroupCategory.TenantOriginated));
        t.content().add(bo);
        t.owner().set(ServerSideFactory.create(CommunicationMessageFacade.class).getSystemEndpointFromCache(SystemEndpointName.Unassigned));

        return Persistence.secureSave(t);
    }

    @Override
    protected EntitySearchResult<Message> query(EntityListCriteria<Message> criteria) {
        EntityListCriteria<Message> replaceCriteria = EntityListCriteria.create(Message.class);
        int pageSize = criteria.getPageSize();
        int totalRetrive = pageSize * (criteria.getPageNumber() + 1);
        replaceCriteria.setSorts(criteria.getSorts());
        if (criteria.getFilters() != null) {
            replaceCriteria.addAll(criteria.getFilters());
        }
        replaceCriteria.setVersionedCriteria(criteria.getVersionedCriteria());

        EntitySearchResult<Message> r = new EntitySearchResult<Message>();
        final ICursorIterator<Message> unfiltered = Persistence.secureQuery(null, replaceCriteria, AttachLevel.Attached);
        final HashSet<Key> visitedThreads = new HashSet<Key>();
        try {
            int i = 0;
            while (unfiltered.hasNext()) {
                Message ent = unfiltered.next();
                if (visitedThreads.contains(ent.thread().getPrimaryKey())) {
                    continue;
                }
                if (pageSize <= 0) {
                    r.add(ent);
                } else if (totalRetrive - pageSize <= i && i < totalRetrive) {
                    r.add(ent);
                }
                i++;
                visitedThreads.add(ent.thread().getPrimaryKey());
                if ((pageSize > 0) && visitedThreads.size() > totalRetrive) {
                    break;
                }
            }
            // The position is important, hasNext may retrieve one more row.
            r.setEncodedCursorReference(unfiltered.encodedCursorReference());
            r.hasMoreData(unfiltered.hasNext());
        } finally {
            unfiltered.close();
        }

        setRowsCount(r);
        return r;
    }

    private void setRowsCount(EntitySearchResult<Message> r) {
        EntityListCriteria<CommunicationThread> threadCriteria = EntityListCriteria.create(CommunicationThread.class);
        SecurityController.assertPermission(new EntityPermission(threadCriteria.getEntityClass(), EntityPermission.READ));
        Persistence.applyDatasetAccessRule(threadCriteria);

        r.setTotalRows(Persistence.service().count(threadCriteria));
    }

    @Override
    protected void enhanceRetrieved(Message bo, MessageDTO to, RetrieveTarget retrieveTarget) {
        enhanceDbo(bo, to);
    }

    @Override
    protected void enhanceListRetrieved(Message bo, MessageDTO dto) {
        enhanceDbo(bo, dto);
    }

    protected void enhanceDbo(Message bo, MessageDTO to) {
        Persistence.ensureRetrieve(bo.thread(), AttachLevel.Attached);
        Persistence.ensureRetrieve(bo.thread().content(), AttachLevel.Attached);
        Persistence.ensureRetrieve(bo.recipients(), AttachLevel.Attached);
        IList<Message> ms = bo.thread().content();
        if (ms != null && !ms.isNull()) {
            boolean star = false;
            boolean isRead = true;
            boolean isHighImportance = false;
            for (Message m : ms) {
                Persistence.ensureRetrieve(m.recipients(), AttachLevel.Attached);
                Persistence.ensureRetrieve(m.attachments(), AttachLevel.Attached);
                Persistence.ensureRetrieve(m.sender(), AttachLevel.Attached);
                MessageDTO currentDTO = copyChildDTO(m, EntityFactory.create(MessageDTO.class));
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
                    copyChildDTO(m, to);
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

    private MessageDTO copyChildDTO(Message m, MessageDTO messageDTO) {
        Persistence.ensureRetrieve(m.sender(), AttachLevel.Attached);
        boolean star = false;
        boolean isRead = true;

        for (DeliveryHandle dh : m.recipients()) {
            if (!ResidentPortalContext.getCurrentUser().equals(dh.recipient())) {
                continue;
            }
            if (dh.star().getValue(false)) {
                star = true;
            }
            if (!dh.isRead().getValue(false)) {
                isRead = false;
            }
        }

        messageDTO.id().set(m.id());
        messageDTO.subject().set(m.thread().subject());
        messageDTO.allowedReply().set(m.thread().allowedReply());
        messageDTO.status().set(m.thread().status());
        messageDTO.created().set(m.thread().created());
        messageDTO.text().set(m.text());
        messageDTO.date().set(m.date());
        messageDTO.thread().setAttachLevel(AttachLevel.Attached);
        messageDTO.thread().set(m.thread());
        messageDTO.attachments().set(m.attachments());
        messageDTO.highImportance().set(m.highImportance());
        messageDTO.sender().setAttachLevel(AttachLevel.Attached);
        messageDTO.sender().set(m.sender());
        messageDTO.isRead().setValue(isRead);
        messageDTO.star().setValue(star);

        return messageDTO;
    }

    @Override
    public void saveChildMessage(AsyncCallback<MessageDTO> callback, MessageDTO message) {
        if (message.date().isNull()) {
            DeliveryHandle dh = EntityFactory.create(DeliveryHandle.class);
            dh.isRead().setValue(false);
            dh.star().setValue(false);
            dh.recipient().set(ServerSideFactory.create(CommunicationMessageFacade.class).getSystemEndpointFromCache(SystemEndpointName.Unassigned));

            CommunicationThread thread = Persistence.secureRetrieve(CommunicationThread.class, message.thread().id().getValue());
            Message m = EntityFactory.create(Message.class);
            m.thread().set(thread);
            m.attachments().set(message.attachments());
            m.date().setValue(SystemDateManager.getDate());
            m.sender().set(ResidentPortalContext.getCurrentUser());
            m.text().set(message.text());
            m.highImportance().set(message.highImportance());
            m.recipients().add(dh);
            Persistence.service().persist(m);
        } else {
            EntityQueryCriteria<DeliveryHandle> dhCriteria = EntityQueryCriteria.create(DeliveryHandle.class);
            dhCriteria.eq(dhCriteria.proto().recipient(), ResidentPortalContext.getCurrentUser());
            dhCriteria.eq(dhCriteria.proto().message(), message);
            DeliveryHandle dh = Persistence.retrieveUnique(dhCriteria, AttachLevel.Attached);
            dh.isRead().set(message.isRead());
            dh.star().set(message.star());
            Persistence.service().persist(dh);
        }

        Persistence.service().commit();
        callback.onSuccess(message);
    }
}
