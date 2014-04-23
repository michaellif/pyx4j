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

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.core.criterion.OrCriterion;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.entity.security.DatasetAccessRule;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.biz.communication.CommunicationMessageFacade;
import com.propertyvista.crm.server.util.CrmAppContext;
import com.propertyvista.domain.communication.CommunicationGroup;
import com.propertyvista.domain.communication.CommunicationGroup.EndpointGroup;
import com.propertyvista.domain.communication.CommunicationMessage;
import com.propertyvista.server.domain.security.CrmUserCredential;

public class CommunicationMessageAccessRule implements DatasetAccessRule<CommunicationMessage> {

    private static final long serialVersionUID = 1L;

    @Override
    public void applyRule(EntityQueryCriteria<CommunicationMessage> criteria) {//@formatter:off
        criteria.or(PropertyCriterion.eq(criteria.proto().recipient(), ServerSideFactory.create(CommunicationMessageFacade.class).getCommunicationGroupFromCache(EndpointGroup.Commandant)), //,
                        new OrCriterion( //
                                PropertyCriterion.eq(criteria.proto().recipient(), CrmAppContext.getCurrentUser()), //
                                PropertyCriterion.eq(criteria.proto().data().sender(), CrmAppContext.getCurrentUser()))); //);


        // TODO: replace this condition to this one
        //criteria.or(PropertyCriterion.in(criteria.proto().recipient(), getUserGroups()),
       //        new OrCriterion( //
        //               PropertyCriterion.eq(criteria.proto().recipient(), CrmAppContext.getCurrentUser()), //
          //             PropertyCriterion.eq(criteria.proto().data().sender(), CrmAppContext.getCurrentUser()))); //);

    }//@formatter:on

    private List<CommunicationGroup> getUserGroups() {
        CrmUserCredential crs = Persistence.service().retrieve(CrmUserCredential.class, CrmAppContext.getCurrentUser().getPrimaryKey());
        EntityQueryCriteria<CommunicationGroup> groupCriteria = EntityQueryCriteria.create(CommunicationGroup.class);
        groupCriteria.in(groupCriteria.proto().roles(), crs.roles());
        return Persistence.service().query(groupCriteria, AttachLevel.IdOnly);
    }
}
