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
 */
package com.propertyvista.portal.server.security.access.resident;

import com.pyx4j.entity.core.criterion.AndCriterion;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.core.criterion.OrCriterion;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.entity.security.DatasetAccessRule;

import com.propertyvista.domain.communication.Message;
import com.propertyvista.domain.tenant.lease.LeaseParticipant;
import com.propertyvista.portal.server.portal.shared.PortalVistaContext;

public class MessagePortalAccessRule implements DatasetAccessRule<Message> {

    private static final long serialVersionUID = 1L;

    @Override
    public void applyRule(EntityQueryCriteria<Message> criteria) {
        LeaseParticipant<?> lp = PortalVistaContext.getLeaseParticipant();

        OrCriterion senderOrRecipientCriteria = new OrCriterion(PropertyCriterion.eq(criteria.proto().sender(), lp), PropertyCriterion.eq(criteria.proto()
                .recipients().$().recipient(), lp));

        AndCriterion onBehalfCriteria = new AndCriterion(PropertyCriterion.eq(criteria.proto().onBehalf(), lp), PropertyCriterion.eq(criteria.proto()
                .onBehalfVisible(), true));

        criteria.or(senderOrRecipientCriteria, onBehalfCriteria);

        AndCriterion hiddenCriteria = new AndCriterion(PropertyCriterion.eq(criteria.proto().thread().userPolicy().$().hidden(), true), PropertyCriterion.eq(
                criteria.proto().thread().userPolicy().$().policyConsumer(), lp));

        criteria.notExists(criteria.proto().thread().userPolicy(), hiddenCriteria);
    }
}