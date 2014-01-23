/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 23, 2014
 * @author smolka
 * @version $Id$
 */
package com.propertyvista.biz.tenant.communityevent;

import java.util.List;

import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.domain.property.asset.CommunityEvent;
import com.propertyvista.domain.property.asset.building.Building;

public class CommunityEventFacadeImpl implements CommunityEventFacade {

    @Override
    public List<CommunityEvent> getCommunityEvents(Building building) {
        EntityQueryCriteria<CommunityEvent> criteria = EntityQueryCriteria.create(CommunityEvent.class);
        criteria.eq(criteria.proto().building(), building);
        return Persistence.service().query(criteria.desc(criteria.proto().id()));
    }

}
