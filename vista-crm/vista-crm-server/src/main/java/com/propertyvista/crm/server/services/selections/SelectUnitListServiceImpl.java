/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-09
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.server.services.selections;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.core.criterion.Criterion;
import com.pyx4j.entity.core.criterion.EntityListCriteria;
import com.pyx4j.entity.server.AbstractListServiceImpl;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.biz.occupancy.OccupancyFacade;
import com.propertyvista.crm.rpc.services.selections.SelectUnitListService;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.property.asset.unit.occupancy.UnitAvailabilityCriteria;

public class SelectUnitListServiceImpl extends AbstractListServiceImpl<AptUnit> implements SelectUnitListService {

    public SelectUnitListServiceImpl() {
        super(AptUnit.class);
    }

    @Override
    protected void bind() {
        bind(toProto.id(), boProto.id());
        bindCompleteObject();
    }

    @Override
    public Criterion convertCriterion(EntityListCriteria<AptUnit> criteria, Criterion cr) {
        if (cr instanceof UnitAvailabilityCriteria) {
            UnitAvailabilityCriteria availability = (UnitAvailabilityCriteria) cr;
            return ServerSideFactory.create(OccupancyFacade.class).buildAvalableCriteria(criteria.proto(), availability.getStatus(), availability.getFrom(),
                    null);
        } else {
            return super.convertCriterion(criteria, cr);
        }
    }

    @Override
    protected void enhanceListRetrieved(AptUnit entity, AptUnit dto) {
        Persistence.service().retrieve(dto.building());
        Persistence.service().retrieve(dto.floorplan());
        // TODO actually just this is necessary, but it' doesn't implemented still:
        //Persistence.service().retrieve(entity.floorplan().name());
        //Persistence.service().retrieve(entity.floorplan().marketingName());
    }
}
