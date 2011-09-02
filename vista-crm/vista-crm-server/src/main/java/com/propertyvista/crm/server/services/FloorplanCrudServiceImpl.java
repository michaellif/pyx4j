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

import java.util.List;

import com.pyx4j.entity.server.Persistence;
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
    protected void enhanceDTO(Floorplan in, FloorplanDTO dto, boolean fromList) {

        EntityQueryCriteria<FloorplanAmenity> amenitysCriteria = EntityQueryCriteria.create(FloorplanAmenity.class);
        amenitysCriteria.add(PropertyCriterion.eq(amenitysCriteria.proto().belongsTo(), in));
        for (FloorplanAmenity amenity : Persistence.service().query(amenitysCriteria)) {
            dto.amenities().add(amenity);
        }

        if (!fromList) {
            Persistence.service().retrieve(in.media());
        }
    }

    @Override
    protected void persistDBO(Floorplan dbo, FloorplanDTO dto) {
        for (Media item : dbo.media()) {
            Persistence.service().merge(item);
        }
        boolean isCreate = dbo.id().isNull();
        Persistence.service().merge(dbo);

        if (!isCreate) {
            EntityQueryCriteria<FloorplanAmenity> amenitysCriteria = EntityQueryCriteria.create(FloorplanAmenity.class);
            amenitysCriteria.add(PropertyCriterion.eq(amenitysCriteria.proto().belongsTo(), dbo));
            List<FloorplanAmenity> existingAmenities = Persistence.service().query(amenitysCriteria);
            for (FloorplanAmenity amenity : existingAmenities) {
                if (!dto.amenities().contains(amenity)) {
                    Persistence.service().delete(amenity);
                }
            }
        }
        for (FloorplanAmenity amenity : dto.amenities()) {
            amenity.belongsTo().set(dbo);
        }
        Persistence.service().merge(dto.amenities());
    }
}
