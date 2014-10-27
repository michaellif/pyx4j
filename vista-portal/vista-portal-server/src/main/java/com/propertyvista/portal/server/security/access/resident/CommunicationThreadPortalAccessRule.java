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
package com.propertyvista.portal.server.security.access.resident;

import com.pyx4j.entity.core.criterion.AndCriterion;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.core.criterion.OrCriterion;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.entity.security.DatasetAccessRule;

import com.propertyvista.domain.communication.CommunicationThread;
import com.propertyvista.domain.tenant.lease.LeaseParticipant;
import com.propertyvista.portal.server.portal.resident.ResidentPortalContext;

public class CommunicationThreadPortalAccessRule implements DatasetAccessRule<CommunicationThread> {

    private static final long serialVersionUID = 1L;

    @Override
    public void applyRule(EntityQueryCriteria<CommunicationThread> criteria) {
        LeaseParticipant<?> lp = ResidentPortalContext.getLeaseParticipant();
        OrCriterion senderOrRecipientCriteria = new OrCriterion(PropertyCriterion.eq(criteria.proto().content().$().sender(), lp), PropertyCriterion.eq(
                criteria.proto().content().$().recipients().$().recipient(), lp));

        AndCriterion onBehalfCriteria = new AndCriterion(PropertyCriterion.eq(criteria.proto().content().$().onBehalf(), lp), PropertyCriterion.eq(criteria
                .proto().content().$().onBehalfVisible(), true));

        criteria.or(senderOrRecipientCriteria, onBehalfCriteria);

        AndCriterion hiddenCriteria = new AndCriterion(PropertyCriterion.eq(criteria.proto().userPolicy().$().hidden(), true), PropertyCriterion.eq(criteria
                .proto().userPolicy().$().policyConsumer(), lp));
        criteria.notExists(criteria.proto().userPolicy(), hiddenCriteria);
    }

}