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
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.geo.GeoPoint;

import com.propertyvista.crm.rpc.services.BuildingCrudService;
import com.propertyvista.crm.server.util.GenericCrudServiceDtoImpl;
import com.propertyvista.domain.GeoLocation;
import com.propertyvista.domain.GeoLocation.LatitudeType;
import com.propertyvista.domain.GeoLocation.LongitudeType;
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
            Persistence.service().retrieve(dto.media());
            Persistence.service().retrieve(dto.serviceCatalog());
            Persistence.service().retrieve(dto.contacts().phones());
            Persistence.service().retrieve(dto.contacts().contacts());
            Persistence.service().retrieve(dto.marketing().adBlurbs());
            Persistence.service().retrieve(dto.dashboard());

            EntityQueryCriteria<BuildingAmenity> amenitysCriteria = EntityQueryCriteria.create(BuildingAmenity.class);
            amenitysCriteria.add(PropertyCriterion.eq(amenitysCriteria.proto().belongsTo(), in));
            dto.amenities().addAll(Persistence.service().query(amenitysCriteria));

            EntityQueryCriteria<ServiceItemType> serviceItemCriteria = EntityQueryCriteria.create(ServiceItemType.class);
            serviceItemCriteria.add(PropertyCriterion.in(serviceItemCriteria.proto().featureType(), Feature.Type.addOn, Feature.Type.utility));
            dto.availableUtilities().addAll(Persistence.service().query(serviceItemCriteria));

            // Geotagging:
            dto.geoLocation().set(EntityFactory.create(GeoLocation.class));
            if (!in.info().address().location().isNull()) {
                double lat = in.info().address().location().getValue().getLat();
                if (lat < 0) {
                    dto.geoLocation().latitudeType().setValue(LatitudeType.South);
                    dto.geoLocation().latitude().setValue(-lat);
                } else {
                    dto.geoLocation().latitudeType().setValue(LatitudeType.North);
                    dto.geoLocation().latitude().setValue(lat);
                }
                double lng = in.info().address().location().getValue().getLng();
                if (lng < 0) {
                    dto.geoLocation().longitudeType().setValue(LongitudeType.West);
                    dto.geoLocation().longitude().setValue(-lng);
                } else {
                    dto.geoLocation().longitudeType().setValue(LongitudeType.East);
                    dto.geoLocation().longitude().setValue(lng);
                }
            }
        } else {
            // just clear unnecessary data before serialization: 
            dto.marketing().description().setValue(null);
        }
    }

    @Override
    protected void persistDBO(Building dbo, BuildingDTO in) {
        for (Media item : dbo.media()) {
            Persistence.service().merge(item);
        }
        // save detached entities:
        Persistence.service().merge(dbo.serviceCatalog());
        PublicDataUpdater.updateIndexData(dbo);

        // Geotagging:
        if (!in.geoLocation().isNull()) {
            Double lat = in.geoLocation().latitude().getValue();
            Double lng = in.geoLocation().longitude().getValue();
            if ((lng != null) && (lat != null)) {
                if (LatitudeType.South.equals(in.geoLocation().latitudeType().getValue())) {
                    lat = -lat;
                }
                if (LongitudeType.West.equals(in.geoLocation().longitudeType().getValue())) {
                    lng = -lng;
                }
                dbo.info().address().location().setValue(new GeoPoint(lat, lng));
            }
        } else {
            dbo.info().address().location().set(null);
        }

        boolean isCreate = dbo.id().isNull();
        Persistence.service().merge(dbo);

        if (!isCreate) {
            EntityQueryCriteria<BuildingAmenity> amenitysCriteria = EntityQueryCriteria.create(BuildingAmenity.class);
            amenitysCriteria.add(PropertyCriterion.eq(amenitysCriteria.proto().belongsTo(), dbo));
            List<BuildingAmenity> existingAmenities = Persistence.service().query(amenitysCriteria);
            for (BuildingAmenity amenity : existingAmenities) {
                if (!in.amenities().contains(amenity)) {
                    Persistence.service().delete(amenity);
                }
            }
        }
        for (BuildingAmenity amenity : in.amenities()) {
            amenity.belongsTo().set(dbo);
        }
        Persistence.service().merge(in.amenities());
    }
}
