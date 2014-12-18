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
 */
package com.propertyvista.biz.communication;

import java.util.List;

import com.pyx4j.entity.cache.CacheService;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.domain.communication.MessageCategory;
import com.propertyvista.domain.communication.MessageCategory.TicketType;
import com.propertyvista.domain.company.Employee;
import com.propertyvista.domain.security.CrmUser;
import com.propertyvista.domain.security.CrmUserCredential;

public class MessageCategoryManager {

    private static class SingletonHolder {
        public static final MessageCategoryManager INSTANCE = new MessageCategoryManager();
    }

    static MessageCategoryManager instance() {
        return SingletonHolder.INSTANCE;
    }

    private static class CategoryCacheKey {
        static String getCacheKey(TicketType mgCategory) {
            return String.format("%s_%s", MessageCategory.class.getName(), mgCategory);
        }
    }

    private MessageCategoryManager() {
        cacheMessageCategories();
    }

    private void cacheMessageCategories() {
        EntityQueryCriteria<MessageCategory> criteria = EntityQueryCriteria.create(MessageCategory.class);
        criteria.ne(criteria.proto().ticketType(), TicketType.NotTicket);
        List<MessageCategory> predefinedEps = Persistence.service().query(criteria);
        if (predefinedEps != null) {
            for (MessageCategory ep : predefinedEps)
                CacheService.put(CategoryCacheKey.getCacheKey(ep.ticketType().getValue()), ep);
        }
    }

    public MessageCategory getMessageCategoryFromCache(TicketType mgCategory) {
        MessageCategory ep = CacheService.get(CategoryCacheKey.getCacheKey(mgCategory));
        if (ep == null) {
            cacheMessageCategories();
            ep = CacheService.get(CategoryCacheKey.getCacheKey(mgCategory));
        }
        return ep;
    }

    public List<MessageCategory> getUserMessageCategories(CrmUser user, Employee employee, AttachLevel attachLevel) {
        CrmUserCredential crs = Persistence.service().retrieve(CrmUserCredential.class, user.getPrimaryKey());
        EntityQueryCriteria<MessageCategory> groupCriteria = EntityQueryCriteria.create(MessageCategory.class);
        if (employee == null) {
            groupCriteria.in(groupCriteria.proto().roles(), crs.roles());
        } else {
            groupCriteria.or(PropertyCriterion.in(groupCriteria.proto().roles(), crs.roles()),
                    PropertyCriterion.in(groupCriteria.proto().dispatchers(), employee.getPrimaryKey()));
        }
        return Persistence.service().query(groupCriteria, attachLevel);
    }

    public List<MessageCategory> getDispatchedMessageCategegories(Employee employee, AttachLevel attachLevel) {
        EntityQueryCriteria<MessageCategory> groupCriteria = EntityQueryCriteria.create(MessageCategory.class);

        groupCriteria.in(groupCriteria.proto().dispatchers(), employee.getPrimaryKey());

        return Persistence.service().query(groupCriteria, attachLevel);
    }
}
