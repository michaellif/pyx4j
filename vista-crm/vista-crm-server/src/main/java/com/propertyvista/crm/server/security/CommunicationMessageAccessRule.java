/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Apr 22, 2014
 * @author smolka
 * @version $Id$
 */
package com.propertyvista.crm.server.security;

import java.util.List;

import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.core.criterion.OrCriterion;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.entity.security.DatasetAccessRule;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.crm.server.util.CrmAppContext;
import com.propertyvista.domain.communication.Message;
import com.propertyvista.domain.communication.MessageCategory;
import com.propertyvista.domain.company.Employee;
import com.propertyvista.server.domain.security.CrmUserCredential;

public class CommunicationMessageAccessRule implements DatasetAccessRule<Message> {

    private static final long serialVersionUID = 1L;

    @Override
    public void applyRule(EntityQueryCriteria<Message> criteria) {
        List<MessageCategory> userGroups = getUserGroups();
        OrCriterion inboxOr = new OrCriterion(PropertyCriterion.eq(criteria.proto().recipients().$().recipient(), CrmAppContext.getCurrentUser()), //
                new OrCriterion(PropertyCriterion.eq(criteria.proto().sender(), CrmAppContext.getCurrentUser()),//
                        PropertyCriterion.eq(criteria.proto().thread().owner(), CrmAppContext.getCurrentUser())));//
        if (userGroups != null && userGroups.size() > 0) {
            criteria.or(PropertyCriterion.in(criteria.proto().thread().topic(), getUserGroups()), inboxOr);//

        } else {
            criteria.add(inboxOr);
        }
    }

    private List<MessageCategory> getUserGroups() {
        CrmUserCredential crs = Persistence.service().retrieve(CrmUserCredential.class, CrmAppContext.getCurrentUser().getPrimaryKey());
        EntityQueryCriteria<MessageCategory> groupCriteria = EntityQueryCriteria.create(MessageCategory.class);
        Employee e = CrmAppContext.getCurrentUserEmployee();
        if (crs.roles() == null || crs.roles().size() < 1) {
            return null;
        }

        PropertyCriterion byRoles = PropertyCriterion.in(groupCriteria.proto().roles(), crs.roles());
        if (e == null) {
            groupCriteria.add(byRoles);
        } else {
            groupCriteria.or(byRoles, PropertyCriterion.in(groupCriteria.proto().dispatchers(), e.getPrimaryKey()));
        }
        return Persistence.service().query(groupCriteria, AttachLevel.IdOnly);
    }

}
