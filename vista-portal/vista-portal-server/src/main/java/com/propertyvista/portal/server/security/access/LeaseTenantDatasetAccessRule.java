/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Oct 18, 2013
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.server.security.access;

import com.pyx4j.entity.security.DatasetAccessRule;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;

import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.portal.server.security.VistaCustomerContext;

public class LeaseTenantDatasetAccessRule implements DatasetAccessRule<Lease> {

    private static final long serialVersionUID = 3983933599019353931L;

    @Override
    public void applyRule(EntityQueryCriteria<Lease> criteria) {
        criteria.eq(criteria.proto().id(), VistaCustomerContext.getCurrentUserLeaseIdStub());
    }

}
