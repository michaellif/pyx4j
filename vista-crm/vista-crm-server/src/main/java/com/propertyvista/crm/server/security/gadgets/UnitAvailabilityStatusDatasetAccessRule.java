/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-10-24
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.server.security.gadgets;

import com.pyx4j.entity.security.DatasetAccessRule;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.server.contexts.Context;

import com.propertyvista.domain.dashboard.gadgets.availability.UnitAvailabilityStatus;

public class UnitAvailabilityStatusDatasetAccessRule implements DatasetAccessRule<UnitAvailabilityStatus> {

    @Override
    public void applyRule(EntityQueryCriteria<UnitAvailabilityStatus> criteria) {
        criteria.add(PropertyCriterion.eq(criteria.proto().unit().building().userAccess(), Context.getVisit().getUserVisit().getPrincipalPrimaryKey()));
    }

}
