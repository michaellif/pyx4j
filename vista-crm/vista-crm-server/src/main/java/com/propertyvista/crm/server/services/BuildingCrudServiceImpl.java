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
package com.propertyvista.crm.server.services;

import java.util.List;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.crm.rpc.services.BuildingCrudService;
import com.propertyvista.crm.server.util.GenericCrudServiceDtoImpl;
import com.propertyvista.domain.financial.offering.Feature;
import com.propertyvista.domain.financial.offering.ServiceItemType;
import com.propertyvista.domain.media.Media;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.building.BuildingAmenity;
import com.propertyvista.dto.BuildingDTO;
import com.propertyvista.server.common.reference.PublicDataUpdater;

public class BuildingCrudServiceImpl extends GenericCrudServiceDtoImpl<Building, BuildingDTO> implements BuildingCrudService {

    public BuildingCrudServiceImpl() {
        super(Building.class, BuildingDTO.class);
    }

    @Override
    protected void enhanceDTO(Building in, BuildingDTO dto, boolean fromList) {

        if (!fromList) {
            // load detached entities/lists. Update other places: BuildingsResource and BuildingRetriever
            Persistence.service().retrieve(in.media());
            Persistence.service().retrieve(in.serviceCatalog());
            Persistence.service().retrieve(in.contacts().phones());
            Persistence.service().retrieve(in.contacts().contacts());
            Persistence.service().retrieve(in.marketing().adBlurbs());

            EntityQueryCriteria<BuildingAmenity> amenitysCriteria = EntityQueryCriteria.create(BuildingAmenity.class);
            amenitysCriteria.add(PropertyCriterion.eq(amenitysCriteria.proto().belongsTo(), in));
            for (BuildingAmenity item : Persistence.service().query(amenitysCriteria)) {
                dto.amenities().add(item);
            }

            EntityQueryCriteria<ServiceItemType> serviceItemCriteria = EntityQueryCriteria.create(ServiceItemType.class);
            serviceItemCriteria.add(PropertyCriterion.eq(serviceItemCriteria.proto().featureType(), Feature.Type.utility));
            for (ServiceItemType item : Persistence.service().query(serviceItemCriteria)) {
                dto.availableUtilities().add(item);
            }
        } else {
            // just clear unnecessary data before serialisation: 
            in.marketing().description().setValue(null);
        }
    }

    @Override
    protected void persistDBO(Building dbo, BuildingDTO dto) {
        for (Media item : dbo.media()) {
            Persistence.service().merge(item);
        }
        // save detached entities:
        Persistence.service().merge(dbo.serviceCatalog());
        PublicDataUpdater.updateIndexData(dbo);

        boolean isCreate = dbo.id().isNull();
        Persistence.service().merge(dbo);

        if (!isCreate) {
            EntityQueryCriteria<BuildingAmenity> amenitysCriteria = EntityQueryCriteria.create(BuildingAmenity.class);
            amenitysCriteria.add(PropertyCriterion.eq(amenitysCriteria.proto().belongsTo(), dbo));
            List<BuildingAmenity> existingAmenities = Persistence.service().query(amenitysCriteria);
            for (BuildingAmenity amenity : existingAmenities) {
                if (!dto.amenities().contains(amenity)) {
                    Persistence.service().delete(amenity);
                }
            }
        }
        for (BuildingAmenity amenity : dto.amenities()) {
            amenity.belongsTo().set(dbo);
        }
        Persistence.service().merge(dto.amenities());
    }
}
