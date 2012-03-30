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
package com.propertyvista.crm.server.services.building;

import java.util.List;

import com.pyx4j.entity.server.AbstractCrudServiceDtoImpl;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.geo.GeoPoint;

import com.propertyvista.crm.rpc.services.building.BuildingCrudService;
import com.propertyvista.domain.GeoLocation;
import com.propertyvista.domain.GeoLocation.LatitudeType;
import com.propertyvista.domain.GeoLocation.LongitudeType;
import com.propertyvista.domain.dashboard.DashboardMetadata;
import com.propertyvista.domain.financial.offering.Feature;
import com.propertyvista.domain.financial.offering.FeatureItemType;
import com.propertyvista.domain.policy.policies.domain.IdAssignmentItem.IdTarget;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.dto.BuildingDTO;
import com.propertyvista.server.common.reference.PublicDataUpdater;
import com.propertyvista.server.common.reference.geo.SharedGeoLocator;
import com.propertyvista.server.common.util.IdAssignmentSequenceUtil;

public class BuildingCrudServiceImpl extends AbstractCrudServiceDtoImpl<Building, BuildingDTO> implements BuildingCrudService {

    public BuildingCrudServiceImpl() {
        super(Building.class, BuildingDTO.class);
    }

    @Override
    protected void bind() {
        bindCompleateDBO();
    }

    @Override
    protected void enhanceRetrieved(Building in, BuildingDTO dto) {
        // load detached entities/lists. Update other places: BuildingsResource and BuildingRetriever
        Persistence.service().retrieve(dto.media());
        Persistence.service().retrieve(dto.productCatalog());
        Persistence.service().retrieve(dto.contacts().propertyContacts());
        Persistence.service().retrieve(dto.contacts().organizationContacts());
        Persistence.service().retrieve(dto.marketing().adBlurbs());
        Persistence.service().retrieve(dto.dashboard());
        Persistence.service().retrieve(dto.amenities());

        if (dto.dashboard().isEmpty()) {
            // load first building  dashoard by default:
            EntityQueryCriteria<DashboardMetadata> criteria = EntityQueryCriteria.create(DashboardMetadata.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().type(), DashboardMetadata.DashboardType.building));
            List<DashboardMetadata> dashboards = Persistence.service().query(criteria);
            if (!dashboards.isEmpty()) {
                dto.dashboard().set(dashboards.get(0));
            }
        }

        EntityQueryCriteria<FeatureItemType> featureItemCriteria = EntityQueryCriteria.create(FeatureItemType.class);
        featureItemCriteria.add(PropertyCriterion.in(featureItemCriteria.proto().featureType(), Feature.Type.addOn, Feature.Type.utility));
        dto.availableUtilities().addAll(Persistence.service().query(featureItemCriteria));

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

    }

    @Override
    protected void enhanceListRetrieved(Building entity, BuildingDTO dto) {
        // just clear unnecessary data before serialization:
        dto.marketing().description().setValue(null);
    }

    @Override
    protected void persist(Building dbo, BuildingDTO in) {
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
            if (in.info().address().location().isNull()) {
                SharedGeoLocator.populateGeo(in.info().address());
            } else {
                dbo.info().address().location().set(null);
            }
        }

        if (dbo.id().isNull() && IdAssignmentSequenceUtil.needsGeneratedId(IdTarget.propertyCode)) {
            dbo.propertyCode().setValue(IdAssignmentSequenceUtil.getId(IdTarget.propertyCode));
        }

        Persistence.service().merge(dbo);
        PublicDataUpdater.updateIndexData(dbo);
    }
}
