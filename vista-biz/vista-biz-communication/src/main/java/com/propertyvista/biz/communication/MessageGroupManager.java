/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 5, 2014
 * @author smolka
 * @version $Id$
 */
package com.propertyvista.biz.communication;

import java.util.List;

import com.pyx4j.entity.cache.CacheService;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.domain.communication.MessageGroup;
import com.propertyvista.domain.communication.MessageGroup.MessageGroupCategory;
import com.propertyvista.domain.company.Employee;
import com.propertyvista.domain.security.CrmUser;
import com.propertyvista.server.domain.security.CrmUserCredential;

public class MessageGroupManager {

    private static class SingletonHolder {
        public static final MessageGroupManager INSTANCE = new MessageGroupManager();
    }

    static MessageGroupManager instance() {
        return SingletonHolder.INSTANCE;
    }

    private static class CommunicationGroupCacheKey {
        static String getCacheKey(MessageGroupCategory mgCategory) {
            return String.format("%s_%s", MessageGroup.class.getName(), mgCategory);
        }
    }

    private MessageGroupManager() {
        cacheCommunicationGroups();
    }

    private void cacheCommunicationGroups() {
        EntityQueryCriteria<MessageGroup> criteria = EntityQueryCriteria.create(MessageGroup.class);
        criteria.ne(criteria.proto().category(), MessageGroupCategory.Custom);
        List<MessageGroup> predefinedEps = Persistence.service().query(criteria);
        if (predefinedEps != null) {
            for (MessageGroup ep : predefinedEps)
                CacheService.put(CommunicationGroupCacheKey.getCacheKey(ep.category().getValue()), ep);
        }
    }

    public MessageGroup getCommunicationGroupFromCache(MessageGroupCategory mgCategory) {
        MessageGroup ep = CacheService.get(CommunicationGroupCacheKey.getCacheKey(mgCategory));
        return ep;
    }

    public List<MessageGroup> getUserGroups(CrmUser user, Employee employee, AttachLevel attachLevel) {
        CrmUserCredential crs = Persistence.service().retrieve(CrmUserCredential.class, user.getPrimaryKey());
        EntityQueryCriteria<MessageGroup> groupCriteria = EntityQueryCriteria.create(MessageGroup.class);
        if (employee == null) {
            groupCriteria.in(groupCriteria.proto().roles(), crs.roles());
        } else {
            groupCriteria.or(PropertyCriterion.in(groupCriteria.proto().roles(), crs.roles()),
                    PropertyCriterion.in(groupCriteria.proto().dispatchers(), employee.getPrimaryKey()));
        }
        return Persistence.service().query(groupCriteria, attachLevel);
    }

    public List<MessageGroup> getDispatchedGroups(Employee employee, AttachLevel attachLevel) {
        EntityQueryCriteria<MessageGroup> groupCriteria = EntityQueryCriteria.create(MessageGroup.class);

        groupCriteria.in(groupCriteria.proto().dispatchers(), employee.getPrimaryKey());

        return Persistence.service().query(groupCriteria, attachLevel);
    }
}
