/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-07-25
 * @author ArtyomB
 */
package com.propertyvista.crm.server.security.access;

import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.entity.security.DatasetAccessRule;
import com.pyx4j.server.contexts.ServerContext;

import com.propertyvista.domain.tenant.lead.Lead;

public class LeadDatasetAccessRule implements DatasetAccessRule<Lead> {

    private static final long serialVersionUID = 0l;

    @Override
    public void applyRule(EntityQueryCriteria<Lead> criteria) {
        criteria.add(PropertyCriterion.eq(criteria.proto().floorplan().building().userAccess(), ServerContext.getVisit().getUserVisit().getPrincipalPrimaryKey()));
    }

}
