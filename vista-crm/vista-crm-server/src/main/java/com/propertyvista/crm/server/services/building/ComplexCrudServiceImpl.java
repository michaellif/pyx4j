/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Oct 25, 2011
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.server.services.building;

import java.util.List;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.crm.rpc.services.building.ComplexCrudService;
import com.propertyvista.crm.server.util.GenericCrudServiceDtoImpl;
import com.propertyvista.domain.dashboard.DashboardMetadata;
import com.propertyvista.domain.property.asset.Complex;
import com.propertyvista.dto.ComplexDTO;

public class ComplexCrudServiceImpl extends GenericCrudServiceDtoImpl<Complex, ComplexDTO> implements ComplexCrudService {

    public ComplexCrudServiceImpl() {
        super(Complex.class, ComplexDTO.class);
    }

    @Override
    protected void enhanceDTO(Complex in, ComplexDTO dto, boolean fromList) {
        super.enhanceDTO(in, dto, fromList);
        Persistence.service().retrieve(dto.dashboard());

        if (dto.dashboard().isEmpty()) {
            // load first building  dashboard by default:
            EntityQueryCriteria<DashboardMetadata> criteria = EntityQueryCriteria.create(DashboardMetadata.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().type(), DashboardMetadata.DashboardType.building));
            List<DashboardMetadata> dashboards = Persistence.service().query(criteria);
            if (!dashboards.isEmpty()) {
                dto.dashboard().set(dashboards.get(0));
            }
        }

        if (!fromList) {
            // fill transient data:
//            EntityQueryCriteria<Building> criteria = EntityQueryCriteria.create(Building.class);
//            criteria.add(PropertyCriterion.eq(criteria.proto().complex(), in));
//            dto.buildings().addAll(Persistence.service().query(criteria));

            // load detached data:
            Persistence.service().retrieve(in.buildings());
        }
    }

    @Override
    protected void persistDBO(Complex dbo, ComplexDTO in) {
        super.persistDBO(dbo, in);

        // update possible primary building change: 
        Persistence.service().merge(in.buildings());
    }
}
