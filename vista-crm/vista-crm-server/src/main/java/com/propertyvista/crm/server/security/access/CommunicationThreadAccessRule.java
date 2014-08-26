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
package com.propertyvista.crm.server.security.access;

import java.util.List;

import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.core.criterion.OrCriterion;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.entity.security.DatasetAccessRule;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.crm.server.util.CrmAppContext;
import com.propertyvista.domain.communication.CommunicationThread;
import com.propertyvista.domain.communication.MessageCategory;
import com.propertyvista.domain.company.Employee;

public class CommunicationThreadAccessRule implements DatasetAccessRule<CommunicationThread> {

    private static final long serialVersionUID = 1L;

    @Override
    public void applyRule(EntityQueryCriteria<CommunicationThread> criteria) {
        Employee e = CrmAppContext.getCurrentUserEmployee();
        List<MessageCategory> userGroups = getUserGroups(e);
        OrCriterion inboxOr = new OrCriterion(PropertyCriterion.eq(criteria.proto().content().$().recipients().$().recipient(), e), //
                new OrCriterion(PropertyCriterion.eq(criteria.proto().content().$().sender(), e),//
                        PropertyCriterion.eq(criteria.proto().owner(), e)));//
        if (userGroups != null && userGroups.size() > 0) {
            criteria.or(PropertyCriterion.in(criteria.proto().category(), userGroups), inboxOr);//

        } else {
            criteria.add(inboxOr);
        }
    }

    private List<MessageCategory> getUserGroups(Employee e) {
        EntityQueryCriteria<MessageCategory> groupCriteria = EntityQueryCriteria.create(MessageCategory.class);

        PropertyCriterion byRoles = PropertyCriterion.in(groupCriteria.proto().roles().$().users(), CrmAppContext.getCurrentUser().getPrimaryKey());
        if (byRoles == null) {
            return null;
        }
        if (e == null) {
            groupCriteria.add(byRoles);
        } else {
            groupCriteria.or(byRoles, PropertyCriterion.in(groupCriteria.proto().dispatchers(), e.getPrimaryKey()));
        }
        return Persistence.service().query(groupCriteria, AttachLevel.IdOnly);
    }

}
