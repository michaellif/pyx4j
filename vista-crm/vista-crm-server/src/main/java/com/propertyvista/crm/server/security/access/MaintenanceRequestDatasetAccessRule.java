/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 18, 2013
 * @author VLadL
 * @version $Id$
 */
package com.propertyvista.crm.server.security.access;

import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.entity.security.DatasetAccessRule;
import com.pyx4j.server.contexts.ServerContext;

import com.propertyvista.domain.maintenance.MaintenanceRequest;

public class MaintenanceRequestDatasetAccessRule implements DatasetAccessRule<MaintenanceRequest> {

    private static final long serialVersionUID = 1L;

    @Override
    public void applyRule(EntityQueryCriteria<MaintenanceRequest> criteria) {
        criteria.add(PropertyCriterion.eq(criteria.proto().building().userAccess(), ServerContext.getVisit().getUserVisit().getPrincipalPrimaryKey()));
    }
}
