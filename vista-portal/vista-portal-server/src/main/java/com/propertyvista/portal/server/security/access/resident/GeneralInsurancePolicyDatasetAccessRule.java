/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-10-28
 * @author ArtyomB
 */
package com.propertyvista.portal.server.security.access.resident;

import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.entity.security.DatasetAccessRule;

import com.propertyvista.domain.tenant.insurance.GeneralInsurancePolicy;
import com.propertyvista.portal.server.portal.resident.ResidentPortalContext;

public class GeneralInsurancePolicyDatasetAccessRule implements DatasetAccessRule<GeneralInsurancePolicy> {

    private static final long serialVersionUID = 1L;

    @Override
    public void applyRule(EntityQueryCriteria<GeneralInsurancePolicy> criteria) {
        criteria.or(PropertyCriterion.eq(criteria.proto().tenant(), ResidentPortalContext.getTenant().getPrimaryKey()),
                PropertyCriterion.eq(criteria.proto().tenant().lease().leaseParticipants(), ResidentPortalContext.getTenant().getPrimaryKey()));
    }

}
