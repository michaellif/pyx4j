/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 16, 2014
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.server.security.access.prospect;

import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.entity.security.DatasetAccessRule;

import com.propertyvista.domain.tenant.lease.LeaseTermTenant;
import com.propertyvista.portal.server.portal.prospect.ProspectPortalContext;

public class LeaseTermTenantProspectDatasetAccessRule implements DatasetAccessRule<LeaseTermTenant> {

    private static final long serialVersionUID = 1L;

    @Override
    public void applyRule(EntityQueryCriteria<LeaseTermTenant> criteria) {
        criteria.add(PropertyCriterion.eq(criteria.proto().leaseTermV().holder().lease().leaseApplication().onlineApplication(),
                ProspectPortalContext.getOnlineApplicationIdStub()));
    }

}
