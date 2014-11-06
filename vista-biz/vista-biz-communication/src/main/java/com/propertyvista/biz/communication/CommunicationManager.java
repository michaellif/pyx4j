/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 4, 2014
 * @author smolka
 * @version $Id$
 */
package com.propertyvista.biz.communication;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Vector;

import com.pyx4j.commons.Key;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.config.server.SystemDateManager;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.AndCriterion;
import com.pyx4j.entity.core.criterion.EntityListCriteria;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.security.EntityPermission;
import com.pyx4j.entity.server.IEntityPersistenceService.ICursorIterator;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.shared.Context;
import com.pyx4j.security.shared.SecurityController;
import com.pyx4j.server.contexts.ServerContext;

import com.propertyvista.crm.rpc.dto.communication.CrmCommunicationSystemNotification;
import com.propertyvista.domain.communication.CommunicationEndpoint;
import com.propertyvista.domain.communication.CommunicationEndpoint.ContactType;
import com.propertyvista.domain.communication.CommunicationThread;
import com.propertyvista.domain.communication.CommunicationThread.ThreadStatus;
import com.propertyvista.domain.communication.DeliveryHandle;
import com.propertyvista.domain.communication.Message;
import com.propertyvista.domain.communication.MessageCategory;
import com.propertyvista.domain.communication.MessageCategory.CategoryType;
import com.propertyvista.domain.communication.SystemEndpoint.SystemEndpointName;
import com.propertyvista.domain.communication.ThreadPolicyHandle;
import com.propertyvista.domain.company.Employee;
import com.propertyvista.domain.security.VistaDataAccessBehavior;
import com.propertyvista.domain.security.common.VistaAccessGrantedBehavior;
import com.propertyvista.domain.security.common.VistaApplication;
import com.propertyvista.domain.tenant.lease.LeaseParticipant;
import com.propertyvista.dto.CommunicationEndpointDTO;
import com.propertyvista.dto.MessageDTO;
import com.propertyvista.portal.rpc.portal.PortalUserVisit;
import com.propertyvista.portal.rpc.shared.dto.communication.PortalCommunicationSystemNotification;
import com.propertyvista.shared.VistaUserVisit;

public class CommunicationManager {

    private final static I18n i18n = I18n.get(CommunicationManager.class);

    private static class SingletonHolder {
        public static final CommunicationManager INSTANCE = new CommunicationManager();
    }

    static CommunicationManager instance() {
        return SingletonHolder.INSTANCE;
    }

    public EntitySearchResult<Message> query(EntityListCriteria<Message> criteria) {
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

    public Serializable getCommunicationStatus() {
        CommunicationEndpoint ep = getCurrentUserAsEndpoint();
        if (ep == null) {
            return null;
        }
        if (VistaApplication.crm.equals(Context.visit(VistaUserVisit.class).getApplication()) && !SecurityController.check(VistaAccessGrantedBehavior.CRM)) {
            return null;
        }

        final List<CommunicationThread> directMessages = getDirectThreads(ep);
        boolean isCRM = VistaApplication.crm.equals(Context.visit(VistaUserVisit.class).getApplication());
        final List<CommunicationThread> dispatchedMessages = isCRM ? getDispatchedThreads((Employee) ep) : null;

        if (dispatchedMessages != null && dispatchedMessages.size() > 0 && directMessages != null && directMessages.size() > 0) {
            directMessages.removeAll(dispatchedMessages);
        }

        if (isCRM) {
            return new CrmCommunicationSystemNotification(directMessages == null ? 0 : directMessages.size(), dispatchedMessages == null ? 0
                    : dispatchedMessages.size());
        } else if (VistaApplication.resident.equals(Context.visit(VistaUserVisit.class).getApplication())
                || VistaApplication.prospect.equals(Context.visit(VistaUserVisit.class).getApplication())) {
            return new PortalCommunicationSystemNotification(directMessages == null ? 0 : directMessages.size());
        }

        return null;
    }

    public boolean isDispatchedThread(Key threadKey, boolean includeByRoles, Employee currentUser) {
        final EntityListCriteria<CommunicationThread> dispatchedCriteria = getDispatchedCriteria(includeByRoles, currentUser);
        dispatchedCriteria.in(dispatchedCriteria.proto().id(), threadKey);
        final List<CommunicationThread> dispatchedMessages = Persistence.service().query(dispatchedCriteria, AttachLevel.IdOnly);
        return dispatchedMessages != null && dispatchedMessages.size() > 0;
    }

    public List<CommunicationThread> getDispatchedThreads(Employee currentUser) {
        if (!VistaApplication.crm.equals(Context.visit(VistaUserVisit.class).getApplication())) {
            return new ArrayList<CommunicationThread>();
        }
        final EntityListCriteria<CommunicationThread> dispatchedCriteria = getUnassignedDispatchedCriteria(currentUser);
        if (dispatchedCriteria == null) {
            return new ArrayList<CommunicationThread>();
        }
        final List<CommunicationThread> dispatchedMessages = Persistence.secureQuery(dispatchedCriteria, AttachLevel.IdOnly);
        return dispatchedMessages;
    }

    private EntityListCriteria<CommunicationThread> getUnassignedDispatchedCriteria(Employee currentUser) {
        List<MessageCategory> userGroups = getUserGroups(currentUser);
        if (userGroups != null && userGroups.size() > 0) {
            final EntityListCriteria<CommunicationThread> dispatchedCriteria = EntityListCriteria.create(CommunicationThread.class);
            dispatchedCriteria.in(dispatchedCriteria.proto().status(), ThreadStatus.Open, ThreadStatus.Resolved);

            AndCriterion newDispatchedCriteria = new AndCriterion(PropertyCriterion.eq(dispatchedCriteria.proto().owner(),
                    ServerSideFactory.create(CommunicationMessageFacade.class).getSystemEndpointFromCache(SystemEndpointName.Unassigned)),
                    PropertyCriterion.in(dispatchedCriteria.proto().category(), userGroups));

            AndCriterion ownedUnreadCriteria = new AndCriterion(PropertyCriterion.eq(dispatchedCriteria.proto().owner(), currentUser), new AndCriterion(
                    PropertyCriterion.eq(dispatchedCriteria.proto().content().$().recipients().$().isRead(), false), PropertyCriterion.eq(dispatchedCriteria
                            .proto().content().$().recipients().$().recipient(), currentUser)));

            dispatchedCriteria.or(newDispatchedCriteria, ownedUnreadCriteria);
            return dispatchedCriteria;
        }
        return null;
    }

    private EntityListCriteria<CommunicationThread> getDispatchedCriteria(boolean includeByRoles, Employee currentUser) {
        final EntityListCriteria<CommunicationThread> dispatchedCriteria = EntityListCriteria.create(CommunicationThread.class);
        dispatchedCriteria.in(dispatchedCriteria.proto().status(), ThreadStatus.Open, ThreadStatus.Resolved);

        List<MessageCategory> userGroups = includeByRoles ? getUserGroupsIncludingRoles(currentUser) : getUserGroups(currentUser);
        if (userGroups != null && userGroups.size() > 0) {
            if (includeByRoles) {
                dispatchedCriteria.or(PropertyCriterion.in(dispatchedCriteria.proto().category(), userGroups),
                        PropertyCriterion.eq(dispatchedCriteria.proto().owner(), currentUser));
            } else {
                AndCriterion newDispatchedCriteria = new AndCriterion(PropertyCriterion.eq(dispatchedCriteria.proto().owner(),
                        ServerSideFactory.create(CommunicationMessageFacade.class).getSystemEndpointFromCache(SystemEndpointName.Unassigned)),
                        PropertyCriterion.in(dispatchedCriteria.proto().category(), userGroups));

                dispatchedCriteria.or(newDispatchedCriteria, PropertyCriterion.eq(dispatchedCriteria.proto().owner(), currentUser));
            }
        } else {
            dispatchedCriteria.eq(dispatchedCriteria.proto().owner(), currentUser);
        }
        return dispatchedCriteria;
    }

    public List<CommunicationThread> getDirectThreads(CommunicationEndpoint currentUser) {
        if (VistaApplication.resident.equals(Context.visit(VistaUserVisit.class).getApplication())
                && !SecurityController.check(VistaDataAccessBehavior.ResidentInPortal) && !SecurityController.check(VistaDataAccessBehavior.GuarantorInPortal)) {
            return new ArrayList<CommunicationThread>();
        }

        final EntityListCriteria<CommunicationThread> directCriteria = EntityListCriteria.create(CommunicationThread.class);
        directCriteria.eq(directCriteria.proto().content().$().recipients().$().isRead(), false);
        directCriteria.eq(directCriteria.proto().content().$().recipients().$().recipient(), currentUser);

        final List<CommunicationThread> directMessages = Persistence.secureQuery(directCriteria, AttachLevel.IdOnly);
        return directMessages;
    }

    private List<MessageCategory> getUserGroups(Employee e) {
        EntityQueryCriteria<MessageCategory> groupCriteria = EntityQueryCriteria.create(MessageCategory.class);
        if (e == null) {
            return null;
        }
        groupCriteria.in(groupCriteria.proto().dispatchers(), e.getPrimaryKey());

        return Persistence.service().query(groupCriteria, AttachLevel.IdOnly);
    }

    private List<MessageCategory> getUserGroupsIncludingRoles(Employee e) {
        EntityQueryCriteria<MessageCategory> groupCriteria = EntityQueryCriteria.create(MessageCategory.class);

        PropertyCriterion byRoles = PropertyCriterion.in(groupCriteria.proto().roles().$().users(), e);
        if (e == null) {
            groupCriteria.add(byRoles);
        } else {
            groupCriteria.or(byRoles, PropertyCriterion.in(groupCriteria.proto().dispatchers(), e.getPrimaryKey()));
        }
        return Persistence.service().query(groupCriteria, AttachLevel.IdOnly);
    }

    @SuppressWarnings("rawtypes")
    private CommunicationEndpoint getCurrentUserAsEndpoint() {
        if (VistaApplication.resident.equals(Context.visit(VistaUserVisit.class).getApplication())
                || VistaApplication.prospect.equals(Context.visit(VistaUserVisit.class).getApplication())) {

            return Persistence.service().retrieve(LeaseParticipant.class, ServerContext.visit(PortalUserVisit.class).getLeaseParticipantId().getPrimaryKey());

        } else if (VistaApplication.crm.equals(Context.visit(VistaUserVisit.class).getApplication())) {
            EntityQueryCriteria<Employee> criteria = EntityQueryCriteria.create(Employee.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().user(), Context.visit(VistaUserVisit.class).getCurrentUser()));
            return Persistence.service().retrieve(criteria);
        }
        return null;
    }

    public void enhanceMessageDbo(Message bo, MessageDTO to, boolean isForList, CommunicationEndpoint currentUser) {
        EntityListCriteria<Message> visibleMessageCriteria = EntityListCriteria.create(Message.class);
        visibleMessageCriteria.eq(visibleMessageCriteria.proto().thread(), bo.thread());
        final Vector<Message> ms = Persistence.secureQuery(visibleMessageCriteria, AttachLevel.Attached);

        Persistence.ensureRetrieve(bo.thread(), AttachLevel.Attached);
        Persistence.ensureRetrieve(bo.thread().category(), AttachLevel.Attached);
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
                Persistence.ensureRetrieve(m.onBehalf(), AttachLevel.Attached);

                if (!isForList || !hasAttachment) {
                    Persistence.ensureRetrieve(m.attachments(), AttachLevel.Attached);
                }
                hasAttachment = hasAttachment || m.attachments().size() > 0;
                MessageDTO currentDTO = copyChildDTO(bo.thread(), m, EntityFactory.create(MessageDTO.class), isForList, communicationFacade, currentUser);
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
            copyChildDTO(bo.thread(), lastMessage, to, isForList, communicationFacade, currentUser);
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

            if (currentUser instanceof Employee) {
                to.isDirect().setValue(!communicationFacade.isDispatchedThread(bo.thread().getPrimaryKey(), !isForList, (Employee) currentUser));
            }
            evaluateHiddenProperty(bo, to);
        }
    }

    private void evaluateHiddenProperty(Message bo, MessageDTO to) {
        EntityQueryCriteria<ThreadPolicyHandle> policyCriteria = EntityQueryCriteria.create(ThreadPolicyHandle.class);
        policyCriteria.eq(policyCriteria.proto().thread(), bo.thread());

        ThreadPolicyHandle ph = Persistence.secureRetrieve(policyCriteria);
        to.hidden().setValue(ph != null && !ph.isNull() && ph.hidden().getValue(false));
    }

    private MessageDTO copyChildDTO(CommunicationThread thread, Message m, MessageDTO messageDTO, boolean isForList,
            CommunicationMessageFacade communicationFacade, CommunicationEndpoint currentUser) {
        boolean star = false;
        boolean isRead = true;

        messageDTO.isInRecipients().setValue(false);
        for (DeliveryHandle dh : m.recipients()) {
            if (!isForList && !dh.generatedFromGroup().getValue(false)) {

                Persistence.ensureRetrieve(dh.recipient(), AttachLevel.Attached);
                CommunicationEndpointDTO ep = communicationFacade.generateEndpointDTO(dh.recipient());

                if (dh.communicationGroup() != null && !dh.communicationGroup().isNull() && !dh.communicationGroup().isEmpty()) {
                    Persistence.ensureRetrieve(dh.recipient(), AttachLevel.Attached);
                    ep.type().setValue(
                            dh.communicationGroup().portfolio() != null && !dh.communicationGroup().portfolio().isNull()
                                    && !dh.communicationGroup().portfolio().isEmpty() ? ContactType.Portfolio : ContactType.Building);
                    ep.name().set(
                            dh.communicationGroup().portfolio() != null && !dh.communicationGroup().portfolio().isNull()
                                    && !dh.communicationGroup().portfolio().isEmpty() ? dh.communicationGroup().portfolio().name() : dh.communicationGroup()
                                    .building().propertyCode());
                }
                messageDTO.to().add(ep);
            }
            if (!currentUser.equals(dh.recipient())) {
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
        if (isForList && thread.owner().getInstanceValueClass().equals(Employee.class)) {
            messageDTO.ownerForList().set((thread.owner()));
        }
        messageDTO.ownerForList();

        messageDTO.text().set(m.text());
        messageDTO.date().set(m.date());
        messageDTO.thread().setAttachLevel(AttachLevel.Attached);
        messageDTO.thread().set(thread);
        messageDTO.attachments().set(m.attachments());
        messageDTO.hasAttachments().setValue(m.attachments().size() > 0);
        messageDTO.highImportance().set(m.highImportance());
        messageDTO.senderDTO().setAttachLevel(AttachLevel.Attached);
        messageDTO.senderDTO().set((communicationFacade.generateEndpointDTO(m.sender())));
        messageDTO.isRead().setValue(isRead);
        messageDTO.star().setValue(star);
        messageDTO.category().set(thread.category());
        messageDTO.header().sender().setValue(communicationFacade.extractEndpointName(m.sender()));
        if (!isForList) {
            messageDTO.associated().set(thread.associated());
        }
        messageDTO.header().date().set(m.date());
        if (m.onBehalf() != null && !m.onBehalf().isNull()) {
            messageDTO.header().onBehalf().setValue(communicationFacade.extractEndpointName(m.onBehalf()));
            messageDTO.header().onBehalfVisible().setValue(m.onBehalfVisible().getValue(false) ? "" : i18n.tr("hidden from tenant"));
        }
        return messageDTO;
    }

    public Message saveMessage(MessageDTO dto, ThreadStatus threadStatus, CommunicationEndpoint currentUser, boolean updateOwner) {
        CommunicationMessageFacade communicationFacade = ServerSideFactory.create(CommunicationMessageFacade.class);

        CommunicationThread thread = Persistence.secureRetrieve(CommunicationThread.class, dto.thread().id().getValue());
        Message dbo = EntityFactory.create(Message.class);
        if (threadStatus != null) {
            dto.status().setValue(threadStatus);
            thread.status().setValue(threadStatus);
            if (currentUser instanceof Employee) {
                thread.owner().set(currentUser);
            } else if (updateOwner && CategoryType.Ticket.equals(dto.category().categoryType().getValue())
                    && thread.owner().equals(communicationFacade.getSystemEndpointFromCache(SystemEndpointName.Unassigned))) {
                thread.owner().set(communicationFacade.getSystemEndpointFromCache(SystemEndpointName.Archive));
                thread.allowedReply().setValue(false);
            }

            Persistence.service().persist(thread);
            dbo.recipients()
                    .add(communicationFacade.createDeliveryHandle(communicationFacade.getSystemEndpointFromCache(SystemEndpointName.Unassigned), false));

        } else {
            if (updateOwner && CategoryType.Ticket.equals(dto.category().categoryType().getValue())
                    && thread.owner().equals(communicationFacade.getSystemEndpointFromCache(SystemEndpointName.Unassigned))) {
                thread.owner().set(currentUser);
                Persistence.service().persist(thread);
            }
            communicationFacade.buildRecipientList(dbo, dto, thread);
        }
        dbo.thread().set(thread);
        dbo.onBehalf().set(dto.onBehalf());
        dbo.onBehalfVisible().set(dto.onBehalfVisible());
        dbo.attachments().set(dto.attachments());
        dbo.date().setValue(SystemDateManager.getDate());
        dbo.sender().set(currentUser);
        dbo.text().set(dto.text());
        dbo.highImportance().setValue(false);
        Persistence.service().persist(dbo);
        Persistence.service().commit();

        return dbo;
    }
}
