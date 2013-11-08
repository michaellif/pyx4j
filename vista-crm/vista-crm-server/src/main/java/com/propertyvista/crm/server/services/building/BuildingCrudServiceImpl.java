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
import java.util.Vector;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.AbstractCrudServiceDtoImpl;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityListCriteria;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.geo.GeoPoint;

import com.propertyvista.biz.asset.BuildingFacade;
import com.propertyvista.biz.system.Vista2PmcFacade;
import com.propertyvista.crm.rpc.services.building.BuildingCrudService;
import com.propertyvista.domain.GeoLocation;
import com.propertyvista.domain.GeoLocation.LatitudeType;
import com.propertyvista.domain.GeoLocation.LongitudeType;
import com.propertyvista.domain.PublicVisibilityType;
import com.propertyvista.domain.dashboard.DashboardMetadata;
import com.propertyvista.domain.financial.ARCode;
import com.propertyvista.domain.financial.BuildingMerchantAccount;
import com.propertyvista.domain.financial.MerchantAccount;
import com.propertyvista.domain.marketing.ils.ILSProfileBuilding;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.settings.ILSConfig;
import com.propertyvista.domain.settings.ILSConfig.ILSVendor;
import com.propertyvista.domain.settings.ILSVendorConfig;
import com.propertyvista.dto.BuildingDTO;
import com.propertyvista.server.common.reference.geo.SharedGeoLocator;

public class BuildingCrudServiceImpl extends AbstractCrudServiceDtoImpl<Building, BuildingDTO> implements BuildingCrudService {

    public BuildingCrudServiceImpl() {
        super(Building.class, BuildingDTO.class);
    }

    @Override
    protected void bind() {
        bindCompleteObject();
    }

    @Override
    protected BuildingDTO init(InitializationData initializationData) {
        BuildingDTO entity = EntityFactory.create(BuildingDTO.class);

        entity.suspended().setValue(false);
        entity.marketing().visibility().setValue(PublicVisibilityType.global);
        entity.useDefaultProductCatalog().setValue(true);

        return entity;
    }

    @Override
    protected void retrievedSingle(Building bo, RetrieveTarget retrieveTarget) {
        super.retrievedSingle(bo, retrieveTarget);

        Persistence.service().retrieveMember(bo.amenities());
        Persistence.service().retrieveMember(bo.utilities());
        //TODO count only
        Persistence.service().retrieveMember(bo.floorplans(), AttachLevel.IdOnly);
    }

    @Override
    protected void enhanceRetrieved(Building in, BuildingDTO to, RetrieveTarget retrieveTarget) {
        // load detached entities/lists. Update other places: BuildingsResource and BuildingRetriever
        Persistence.service().retrieve(to.media());
        Persistence.service().retrieve(to.productCatalog());
        Persistence.service().retrieve(to.contacts().propertyContacts());
        Persistence.service().retrieve(to.contacts().organizationContacts());
        Persistence.service().retrieve(to.marketing().adBlurbs());
        Persistence.service().retrieve(to.marketing().openHouseSchedule());

        if (retrieveTarget == RetrieveTarget.View) {
            EntityQueryCriteria<DashboardMetadata> criteria = EntityQueryCriteria.create(DashboardMetadata.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().type(), DashboardMetadata.DashboardType.building));
            to.dashboards().addAll(Persistence.secureQuery(criteria, AttachLevel.ToStringMembers));
        }

        if (retrieveTarget == RetrieveTarget.Edit) {
            EntityQueryCriteria<ARCode> featureItemCriteria = EntityQueryCriteria.create(ARCode.class);
            featureItemCriteria.add(PropertyCriterion.in(featureItemCriteria.proto().type(), ARCode.Type.AddOn, ARCode.Type.Utility));
            to.availableUtilities().addAll(Persistence.service().query(featureItemCriteria));
        }

        // Geotagging:
        to.geoLocation().set(EntityFactory.create(GeoLocation.class));
        if (!in.info().location().isNull()) {
            double lat = in.info().location().getValue().getLat();
            if (lat < 0) {
                to.geoLocation().latitudeType().setValue(LatitudeType.South);
                to.geoLocation().latitude().setValue(-lat);
            } else {
                to.geoLocation().latitudeType().setValue(LatitudeType.North);
                to.geoLocation().latitude().setValue(lat);
            }
            double lng = in.info().location().getValue().getLng();
            if (lng < 0) {
                to.geoLocation().longitudeType().setValue(LongitudeType.West);
                to.geoLocation().longitude().setValue(-lng);
            } else {
                to.geoLocation().longitudeType().setValue(LongitudeType.East);
                to.geoLocation().longitude().setValue(lng);
            }
        }

        // Financial
        Persistence.service().retrieveMember(in.merchantAccounts());
        if (!in.merchantAccounts().isEmpty()) {
            MerchantAccount oneAccount = in.merchantAccounts().iterator().next().merchantAccount();
            to.merchantAccount().set(ServerSideFactory.create(Vista2PmcFacade.class).calulateMerchantAccountStatus(oneAccount));
        }

        // ils
        EntityQueryCriteria<ILSProfileBuilding> criteria = EntityQueryCriteria.create(ILSProfileBuilding.class);
        criteria.eq(criteria.proto().building(), in);
        to.ilsProfile().addAll(Persistence.service().query(criteria));

    }

    @Override
    protected void enhanceListCriteria(EntityListCriteria<Building> dbCriteria, EntityListCriteria<BuildingDTO> dtoCriteria) {
        PropertyCriterion merchantAccountPresentCriteria = dtoCriteria.getCriterion(dtoCriteria.proto().merchantAccountPresent());
        if (merchantAccountPresentCriteria != null) {
            dtoCriteria.getFilters().remove(merchantAccountPresentCriteria);

            if (merchantAccountPresentCriteria.getValue() == Boolean.FALSE) {
                dbCriteria.notExists(dbCriteria.proto().merchantAccounts());
            } else {
                dbCriteria.isNotNull(dbCriteria.proto().merchantAccounts());
            }
        }

        super.enhanceListCriteria(dbCriteria, dtoCriteria);
    }

    @Override
    protected void enhanceListRetrieved(Building entity, BuildingDTO dto) {
        // just clear unnecessary data before serialization:
        dto.marketing().description().setValue(null);

        {
            EntityQueryCriteria<BuildingMerchantAccount> criteria = EntityQueryCriteria.create(BuildingMerchantAccount.class);
            criteria.eq(criteria.proto().building(), entity);
            dto.merchantAccountPresent().setValue(Persistence.service().count(criteria) != 0);
        }
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
                dbo.info().location().setValue(new GeoPoint(lat, lng));
            }
        } else {
            if (in.info().location().isNull()) {
                SharedGeoLocator.populateGeo(in);
            } else {
                dbo.info().location().set(null);
            }
        }

        {
            Persistence.service().retrieveMember(dbo.merchantAccounts());
            dbo.merchantAccounts().clear();
            if (!in.merchantAccount().isNull()) {
                BuildingMerchantAccount bma = dbo.merchantAccounts().$();
                bma.merchantAccount().set(in.merchantAccount());
                dbo.merchantAccounts().add(bma);
            }
        }

        // ils marketing
        {
            EntityQueryCriteria<ILSProfileBuilding> criteria = EntityQueryCriteria.create(ILSProfileBuilding.class);
            criteria.eq(criteria.proto().building(), in);
            List<ILSProfileBuilding> ilsData = Persistence.service().query(criteria);
            ilsData.clear();
            for (ILSProfileBuilding profile : in.ilsProfile()) {
                profile.building().set(dbo);
                ilsData.add(profile);
            }
            Persistence.service().persist(ilsData);
        }

        ServerSideFactory.create(BuildingFacade.class).persist(dbo);
    }

    @Override
    public void retrieveMerchantAccountStatus(AsyncCallback<MerchantAccount> callback, MerchantAccount merchantAccountStub) {
        MerchantAccount entity = Persistence.service().retrieve(MerchantAccount.class, merchantAccountStub.getPrimaryKey());
        ServerSideFactory.create(Vista2PmcFacade.class).calulateMerchantAccountStatus(entity);
        callback.onSuccess(entity);
    }

    @Override
    public void getILSVendors(AsyncCallback<Vector<ILSVendor>> callback, Building buildingStub) {
        // find configured vendors
        Vector<ILSVendor> vendors = new Vector<ILSVendor>();
        ILSConfig config = Persistence.service().retrieve(EntityQueryCriteria.create(ILSConfig.class));
        if (config != null) {
            for (ILSVendorConfig item : config.vendors()) {
                vendors.add(item.vendor().getValue());
            }
        }
        callback.onSuccess(vendors);
    }
}
