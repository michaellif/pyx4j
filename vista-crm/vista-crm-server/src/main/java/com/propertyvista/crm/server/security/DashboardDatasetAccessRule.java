/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-10-04
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.server.security;

import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.entity.security.DatasetAccessRule;
import com.pyx4j.security.shared.SecurityController;

import com.propertyvista.biz.system.VistaContext;
import com.propertyvista.domain.dashboard.DashboardMetadata;
import com.propertyvista.domain.security.VistaCrmBehavior;

public class DashboardDatasetAccessRule implements DatasetAccessRule<DashboardMetadata> {

    private static final long serialVersionUID = 1L;

    @Override
    public void applyRule(EntityQueryCriteria<DashboardMetadata> criteria) {//@formatter:off
        if (!SecurityController.checkAnyBehavior(VistaCrmBehavior.DashboardManager)) {
            criteria.or(PropertyCriterion.eq(criteria.proto().ownerUser(), VistaContext.getCurrentUserPrimaryKey()),
                    PropertyCriterion.eq(criteria.proto().isShared(), true));
        }
    }//@formatter:on

}
