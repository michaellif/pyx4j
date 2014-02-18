/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 18, 2014
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.portal.server.security.access.resident;

import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.criterion.AndCriterion;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.entity.security.DatasetAccessRule;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.domain.maintenance.MaintenanceRequestPicture;
import com.propertyvista.portal.server.portal.resident.ResidentPortalContext;

public class MaintenanceRequestPictureTenantDatasetAccessRule implements DatasetAccessRule<MaintenanceRequestPicture> {

    private static final long serialVersionUID = 1L;

    @Override
    public void applyRule(EntityQueryCriteria<MaintenanceRequestPicture> criteria) {
        Persistence.ensureRetrieve(ResidentPortalContext.getLeaseIdStub().unit(), AttachLevel.Attached);
        criteria.or( //
                // everything opened by this tenant
                PropertyCriterion.eq(criteria.proto().maintenanceRequest().reporter(), ResidentPortalContext.getTenant()),
                // everything opened for his unit during his lease
                new AndCriterion( //
                        PropertyCriterion.eq(criteria.proto().maintenanceRequest().unit(), ResidentPortalContext.getLeaseIdStub().unit()), //
                        PropertyCriterion.ge(criteria.proto().maintenanceRequest().submitted(), ResidentPortalContext.getLeaseIdStub().leaseFrom()) //
                ) //
        );
    }

}
