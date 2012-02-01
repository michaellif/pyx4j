/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-21
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.crm.server.services.dashboard;

import com.pyx4j.entity.shared.criterion.EntityListCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.entity.shared.criterion.PropertyCriterion.Restriction;

import com.propertyvista.crm.rpc.services.dashboard.DashboardMetadataCrudService;
import com.propertyvista.domain.dashboard.DashboardMetadata;

public class DashboardMetadataCrudServiceImpl extends AbstractMetadataCrudServiceImpl implements DashboardMetadataCrudService {

    public DashboardMetadataCrudServiceImpl() {
        super();
    }

    @Override
    void addTypeCriteria(EntityListCriteria<DashboardMetadata> criteria) {
        criteria.add(new PropertyCriterion(criteria.proto().layoutType(), Restriction.NOT_EQUAL, DashboardMetadata.LayoutType.Report));
    }
}
