/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 11, 2012
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.crm.server.security;

import com.pyx4j.entity.security.DatasetAccessRule;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.server.contexts.Context;

import com.propertyvista.domain.tenant.Customer;

public class TenantDatasetAccessRule implements DatasetAccessRule<Customer> {

    @Override
    public void applyRule(EntityQueryCriteria<Customer> criteria) {

        criteria.or()

                .right(PropertyCriterion.eq(criteria.proto()._tenantInLease().$().leaseV().holder().unit().belongsTo().userAccess(), Context.getVisit()
                        .getUserVisit().getPrincipalPrimaryKey()))

                .left(PropertyCriterion.isNull(criteria.proto()._tenantInLease()));
    }
}
