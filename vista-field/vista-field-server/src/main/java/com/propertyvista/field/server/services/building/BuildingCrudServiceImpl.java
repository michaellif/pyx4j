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
package com.propertyvista.field.server.services.building;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.AbstractCrudServiceDtoImpl;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.geo.GeoPoint;
import com.pyx4j.security.shared.SecurityController;

import com.propertyvista.biz.asset.BuildingFacade;
import com.propertyvista.biz.system.Vista2PmcFacade;
import com.propertyvista.domain.GeoLocation;
import com.propertyvista.domain.GeoLocation.LatitudeType;
import com.propertyvista.domain.GeoLocation.LongitudeType;
import com.propertyvista.domain.dashboard.DashboardMetadata;
import com.propertyvista.domain.financial.ARCode;
import com.propertyvista.domain.financial.BuildingMerchantAccount;
import com.propertyvista.domain.financial.MerchantAccount;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.security.VistaCrmBehavior;
import com.propertyvista.dto.BuildingDTO;
import com.propertyvista.field.rpc.services.building.BuildingCrudService;
import com.propertyvista.server.common.reference.geo.SharedGeoLocator;

public class BuildingCrudServiceImpl extends AbstractCrudServiceDtoImpl<Building, BuildingDTO> implements BuildingCrudService {

    public BuildingCrudServiceImpl() {
        super(Building.class, BuildingDTO.class);
    }

    @Override
    protected void bind() {
        bindCompleteDBO();
    }

    @Override
    protected void retrievedSingle(Building entity, RetrieveTarget retrieveTarget) {
        super.retrievedSingle(entity, retrieveTarget);

        Persistence.service().retrieveMember(entity.amenities());
        Persistence.service().retrieveMember(entity.utilities());
        //TODO count only
        Persistence.service().retrieveMember(entity.floorplans(), AttachLevel.IdOnly);
    }

    @Override
    protected void enhanceRetrieved(Building in, BuildingDTO dto, RetrieveTarget retrieveTarget) {
        // load detached entities/lists. Update other places: BuildingsResource and BuildingRetriever
        Persistence.service().retrieve(dto.media());
        Persistence.service().retrieve(dto.productCatalog());
        Persistence.service().retrieve(dto.contacts().propertyContacts());
        Persistence.service().retrieve(dto.contacts().organizationContacts());
        Persistence.service().retrieve(dto.marketing().adBlurbs());

        if (retrieveTarget == RetrieveTarget.View) {
            EntityQueryCriteria<DashboardMetadata> criteria = EntityQueryCriteria.create(DashboardMetadata.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().type(), DashboardMetadata.DashboardType.building));
            dto.dashboards().addAll(Persistence.secureQuery(criteria, AttachLevel.ToStringMembers));
        }

        if (retrieveTarget == RetrieveTarget.Edit) {
            EntityQueryCriteria<ARCode> featureItemCriteria = EntityQueryCriteria.create(ARCode.class);
            featureItemCriteria.add(PropertyCriterion.in(featureItemCriteria.proto().type(), ARCode.Type.AddOn, ARCode.Type.Utility));
            dto.availableUtilities().addAll(Persistence.service().query(featureItemCriteria));
        }

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

        // Financial
        if (SecurityController.checkBehavior(VistaCrmBehavior.BuildingFinancial)) {
            Persistence.service().retrieveMember(in.merchantAccounts());
            if (!in.merchantAccounts().isEmpty()) {
                MerchantAccount oneAccount = in.merchantAccounts().iterator().next().merchantAccount();
                dto.merchantAccount().set(ServerSideFactory.create(Vista2PmcFacade.class).calulateMerchantAccountStatus(oneAccount));
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

        {
            Persistence.service().retrieveMember(dbo.merchantAccounts());
            if (SecurityController.checkBehavior(VistaCrmBehavior.BuildingFinancial)) {
                dbo.merchantAccounts().clear();
                if (!in.merchantAccount().isNull()) {
                    BuildingMerchantAccount bma = dbo.merchantAccounts().$();
                    bma.merchantAccount().set(in.merchantAccount());
                    dbo.merchantAccounts().add(bma);
                }
            }
        }

        ServerSideFactory.create(BuildingFacade.class).persist(dbo);
    }
}
