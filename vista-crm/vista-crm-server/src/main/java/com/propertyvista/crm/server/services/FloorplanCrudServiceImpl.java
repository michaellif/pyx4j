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
package com.propertyvista.crm.server.services;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.server.PersistenceServicesFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.crm.rpc.services.FloorplanCrudService;
import com.propertyvista.crm.server.util.GenericCrudServiceDtoImpl;
import com.propertyvista.domain.media.Media;
import com.propertyvista.domain.property.asset.Floorplan;
import com.propertyvista.domain.property.asset.FloorplanAmenity;
import com.propertyvista.dto.FloorplanDTO;

public class FloorplanCrudServiceImpl extends GenericCrudServiceDtoImpl<Floorplan, FloorplanDTO> implements FloorplanCrudService {

    public FloorplanCrudServiceImpl() {
        super(Floorplan.class, FloorplanDTO.class);
    }

    @Override
    protected void enhanceRetrieveDTO(Floorplan in, FloorplanDTO dto, boolean fromList) {

        EntityQueryCriteria<FloorplanAmenity> amenitysCriteria = EntityQueryCriteria.create(FloorplanAmenity.class);
        amenitysCriteria.add(PropertyCriterion.eq(amenitysCriteria.proto().belongsTo(), in));
        for (FloorplanAmenity amenity : PersistenceServicesFactory.getPersistenceService().query(amenitysCriteria)) {
            dto.amenities().add(amenity);
        }

        if (!fromList) {
            Persistence.service().retrieve(in.media());
        }
    }

    @Override
    protected void enhanceSaveDBO(Floorplan dbo, FloorplanDTO dto) {
        for (Media item : dbo.media()) {
            PersistenceServicesFactory.getPersistenceService().merge(item);
        }
    }
}
