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
import java.util.HashSet;
import java.util.List;

import com.pyx4j.commons.Key;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.criterion.AndCriterion;
import com.pyx4j.entity.core.criterion.EntityListCriteria;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.security.EntityPermission;
import com.pyx4j.entity.server.IEntityPersistenceService.ICursorIterator;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.security.shared.Context;
import com.pyx4j.security.shared.SecurityController;

import com.propertyvista.crm.rpc.dto.communication.CrmCommunicationSystemNotification;
import com.propertyvista.domain.communication.CommunicationThread;
import com.propertyvista.domain.communication.CommunicationThread.ThreadStatus;
import com.propertyvista.domain.communication.Message;
import com.propertyvista.domain.communication.MessageCategory;
import com.propertyvista.domain.communication.SystemEndpoint.SystemEndpointName;
import com.propertyvista.domain.company.Employee;
import com.propertyvista.portal.rpc.shared.dto.communication.PortalCommunicationSystemNotification;
import com.propertyvista.shared.VistaUserVisit;

public class CommunicationManager {
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
        final List<CommunicationThread> directMessages = getDirectThreads();

        final List<CommunicationThread> dispatchedMessages = getDispatchedThreads();

        if (dispatchedMessages != null && dispatchedMessages.size() > 0 && directMessages != null && directMessages.size() > 0) {
            directMessages.removeAll(dispatchedMessages);
        }

        switch (Context.visit(VistaUserVisit.class).getApplication()) {
        case crm:
            return new CrmCommunicationSystemNotification(directMessages == null ? 0 : directMessages.size(), dispatchedMessages == null ? 0
                    : dispatchedMessages.size());

        case resident:
            return new PortalCommunicationSystemNotification(directMessages == null ? 0 : directMessages.size());

        default:
            return null;
        }
    }

    public boolean isDispatchedThread(Key threadKey) {
        final EntityListCriteria<CommunicationThread> dispatchedCriteria = getDispatchedCriteria();
        dispatchedCriteria.in(dispatchedCriteria.proto().id(), threadKey);
        final List<CommunicationThread> dispatchedMessages = Persistence.service().query(dispatchedCriteria, AttachLevel.IdOnly);
        return dispatchedMessages != null && dispatchedMessages.size() > 0;
    }

    public List<CommunicationThread> getDispatchedThreads() {
        final EntityListCriteria<CommunicationThread> dispatchedCriteria = getDispatchedCriteria();
        final List<CommunicationThread> dispatchedMessages = Persistence.service().query(dispatchedCriteria, AttachLevel.IdOnly);
        return dispatchedMessages;
    }

    private EntityListCriteria<CommunicationThread> getDispatchedCriteria() {
        final EntityListCriteria<CommunicationThread> dispatchedCriteria = EntityListCriteria.create(CommunicationThread.class);
        dispatchedCriteria.in(dispatchedCriteria.proto().status(), ThreadStatus.New, ThreadStatus.Open, ThreadStatus.Unassigned);

        List<MessageCategory> userGroups = getUserGroups();
        if (userGroups != null && userGroups.size() > 0) {
            AndCriterion newDispatchedCriteria = new AndCriterion(PropertyCriterion.eq(dispatchedCriteria.proto().owner(),
                    ServerSideFactory.create(CommunicationMessageFacade.class).getSystemEndpointFromCache(SystemEndpointName.Unassigned)),
                    PropertyCriterion.in(dispatchedCriteria.proto().topic(), userGroups));

            dispatchedCriteria.or(newDispatchedCriteria,
                    PropertyCriterion.eq(dispatchedCriteria.proto().owner(), Context.visit(VistaUserVisit.class).getCurrentUser()));

        } else {
            dispatchedCriteria.eq(dispatchedCriteria.proto().owner(), Context.visit(VistaUserVisit.class).getCurrentUser());
        }
        return dispatchedCriteria;
    }

    public List<CommunicationThread> getDirectThreads() {
        final EntityListCriteria<CommunicationThread> directCriteria = EntityListCriteria.create(CommunicationThread.class);
        directCriteria.eq(directCriteria.proto().content().$().recipients().$().isRead(), false);
        directCriteria.eq(directCriteria.proto().content().$().recipients().$().recipient(), Context.visit(VistaUserVisit.class).getCurrentUser());

        final List<CommunicationThread> directMessages = Persistence.service().query(directCriteria, AttachLevel.IdOnly);
        return directMessages;
    }

    private List<MessageCategory> getUserGroups() {
        EntityQueryCriteria<MessageCategory> groupCriteria = EntityQueryCriteria.create(MessageCategory.class);

        EntityQueryCriteria<Employee> criteria = EntityQueryCriteria.create(Employee.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().user(), Context.visit(VistaUserVisit.class).getCurrentUser()));
        Employee e = Persistence.service().retrieve(criteria);
        if (e == null) {
            return null;
        }
        groupCriteria.in(groupCriteria.proto().dispatchers(), e.getPrimaryKey());

        return Persistence.service().query(groupCriteria, AttachLevel.IdOnly);
    }
}
