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
package com.propertyvista.portal.server.security.access.resident;

import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.security.DatasetAccessRule;

import com.propertyvista.domain.financial.billing.Bill;
import com.propertyvista.portal.server.portal.resident.ResidentPortalContext;

public class BillTenantDatasetAccessRule implements DatasetAccessRule<Bill> {

    private static final long serialVersionUID = 208121970549388304L;

    @Override
    public void applyRule(EntityQueryCriteria<Bill> criteria) {
        criteria.eq(criteria.proto().billingAccount().lease(), ResidentPortalContext.getLeaseIdStub());
    }

}
