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

import java.rmi.RemoteException;
import java.util.Vector;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.EntityListCriteria;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.entity.server.AbstractCrudServiceDtoImpl;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.geo.GeoPoint;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.shared.VoidSerializable;

import com.propertyvista.biz.asset.BuildingFacade;
import com.propertyvista.biz.system.Vista2PmcFacade;
import com.propertyvista.biz.system.YardiARFacade;
import com.propertyvista.biz.system.YardiServiceException;
import com.propertyvista.crm.rpc.services.building.BuildingCrudService;
import com.propertyvista.domain.GeoLocation;
import com.propertyvista.domain.GeoLocation.LatitudeType;
import com.propertyvista.domain.GeoLocation.LongitudeType;
import com.propertyvista.domain.PublicVisibilityType;
import com.propertyvista.domain.company.Employee;
import com.propertyvista.domain.dashboard.DashboardMetadata;
import com.propertyvista.domain.financial.ARCode;
import com.propertyvista.domain.financial.BuildingMerchantAccount;
import com.propertyvista.domain.financial.MerchantAccount;
import com.propertyvista.domain.marketing.ils.ILSProfileBuilding;
import com.propertyvista.domain.marketing.ils.ILSProfileEmail;
import com.propertyvista.domain.marketing.ils.ILSSummaryBuilding;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.building.BuildingUtility;
import com.propertyvista.domain.settings.ILSConfig;
import com.propertyvista.domain.settings.ILSConfig.ILSVendor;
import com.propertyvista.domain.settings.ILSEmailConfig;
import com.propertyvista.domain.settings.ILSVendorConfig;
import com.propertyvista.dto.BuildingDTO;
import com.propertyvista.server.common.reference.geo.SharedGeoLocator;

public class BuildingCrudServiceImpl extends AbstractCrudServiceDtoImpl<Building, BuildingDTO> implements BuildingCrudService {

    private final static I18n i18n = I18n.get(BuildingCrudServiceImpl.class);

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
        entity.defaultProductCatalog().setValue(true);
        entity.ilsEmailConfigured().setValue(Persistence.service().count(EntityQueryCriteria.create(ILSEmailConfig.class)) > 0);

        return entity;
    }

    @Override
    protected void retrievedSingle(Building bo, RetrieveTarget retrieveTarget) {
        super.retrievedSingle(bo, retrieveTarget);

        Persistence.service().retrieveMember(bo.amenities());
        loadUtilities(bo);

        //TODO count only
        Persistence.service().retrieveMember(bo.floorplans(), AttachLevel.CollectionSizeOnly);
        Persistence.ensureRetrieve(bo.landlord(), AttachLevel.ToStringMembers);
    }

    @Override
    protected void enhanceRetrieved(Building bo, BuildingDTO to, RetrieveTarget retrieveTarget) {
        // load detached entities/lists. Update other places: BuildingsResource and BuildingRetriever
        Persistence.service().retrieveMember(bo.media());
        to.media().set(bo.media());

        Persistence.service().retrieve(to.productCatalog());
        Persistence.service().retrieve(to.contacts().propertyContacts());
        Persistence.service().retrieve(to.contacts().organizationContacts());

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

        // Financial
        Persistence.service().retrieveMember(bo.merchantAccounts());
        if (!bo.merchantAccounts().isEmpty()) {
            MerchantAccount oneAccount = bo.merchantAccounts().iterator().next().merchantAccount();
            to.merchantAccount().set(ServerSideFactory.create(Vista2PmcFacade.class).calulateMerchantAccountStatus(oneAccount));
        }

        retrieveGeotagging(bo, to);
        retrieveILS(bo, to);
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
    protected void persist(Building bo, BuildingDTO to) {

        {
            Persistence.service().retrieveMember(bo.merchantAccounts());
            bo.merchantAccounts().clear();
            if (!to.merchantAccount().isNull()) {
                BuildingMerchantAccount bma = bo.merchantAccounts().$();
                bma.merchantAccount().set(to.merchantAccount());
                bo.merchantAccounts().add(bma);
            }
        }

        saveGeotagging(bo, to);

        ServerSideFactory.create(BuildingFacade.class).persist(bo);

        saveUtilities(bo, to);

        saveILS(bo, to);
    }

    private void loadUtilities(Building bo) {
        EntityQueryCriteria<BuildingUtility> criteria = EntityQueryCriteria.create(BuildingUtility.class);
        criteria.eq(criteria.proto().building(), bo);
        criteria.eq(criteria.proto().isDeleted(), Boolean.FALSE);
        bo.utilities().setAttachLevel(AttachLevel.Attached);
        bo.utilities().addAll(Persistence.service().query(criteria));
    }

    private void saveUtilities(Building bo, BuildingDTO to) {
        if (to.getPrimaryKey() != null) {
            // Remove old Utility
            EntityQueryCriteria<BuildingUtility> criteria = EntityQueryCriteria.create(BuildingUtility.class);
            criteria.eq(criteria.proto().building(), to);
            criteria.eq(criteria.proto().isDeleted(), Boolean.FALSE);

            for (BuildingUtility utility : Persistence.service().query(criteria)) {
                if (!to.utilities().contains(utility)) {
                    utility.isDeleted().setValue(true);
                    Persistence.service().merge(utility);
                }
            }
        }
        // Save new and update existing
        for (BuildingUtility utility : to.utilities()) {
            utility.building().set(bo);
            utility.isDeleted().setValue(false);
            Persistence.service().merge(utility);
        }
    }

    private void retrieveGeotagging(Building bo, BuildingDTO to) {
        to.geoLocation().set(EntityFactory.create(GeoLocation.class));
        if (!bo.info().location().isNull()) {
            double lat = bo.info().location().getValue().getLat();
            if (lat < 0) {
                to.geoLocation().latitudeType().setValue(LatitudeType.South);
                to.geoLocation().latitude().setValue(-lat);
            } else {
                to.geoLocation().latitudeType().setValue(LatitudeType.North);
                to.geoLocation().latitude().setValue(lat);
            }
            double lng = bo.info().location().getValue().getLng();
            if (lng < 0) {
                to.geoLocation().longitudeType().setValue(LongitudeType.West);
                to.geoLocation().longitude().setValue(-lng);
            } else {
                to.geoLocation().longitudeType().setValue(LongitudeType.East);
                to.geoLocation().longitude().setValue(lng);
            }
        }
    }

    private void saveGeotagging(Building bo, BuildingDTO to) {
        if (!to.geoLocation().isNull()) {
            Double lat = to.geoLocation().latitude().getValue();
            Double lng = to.geoLocation().longitude().getValue();
            if ((lng != null) && (lat != null)) {
                if (LatitudeType.South.equals(to.geoLocation().latitudeType().getValue())) {
                    lat = -lat;
                }
                if (LongitudeType.West.equals(to.geoLocation().longitudeType().getValue())) {
                    lng = -lng;
                }
                bo.info().location().setValue(new GeoPoint(lat, lng));
            }
        } else {
            if (to.info().location().isNull()) {
                SharedGeoLocator.populateGeo(to);
            } else {
                bo.info().location().set(null);
            }
        }
    }

    private void retrieveILS(Building bo, BuildingDTO to) {
        {
            EntityQueryCriteria<ILSSummaryBuilding> criteria = EntityQueryCriteria.create(ILSSummaryBuilding.class);
            criteria.eq(criteria.proto().building(), bo);
            to.ilsSummary().addAll(Persistence.service().query(criteria));
        }
        {
            EntityQueryCriteria<ILSProfileBuilding> criteria = EntityQueryCriteria.create(ILSProfileBuilding.class);
            criteria.eq(criteria.proto().building(), bo);
            to.ilsProfile().addAll(Persistence.service().query(criteria));
        }
        {
            EntityQueryCriteria<ILSProfileEmail> criteria = EntityQueryCriteria.create(ILSProfileEmail.class);
            criteria.eq(criteria.proto().building(), bo);
            to.ilsEmail().set(Persistence.service().retrieve(criteria));
        }
        to.ilsEmailConfigured().setValue(Persistence.service().count(EntityQueryCriteria.create(ILSEmailConfig.class)) > 0);
    }

    private void saveILS(Building bo, BuildingDTO to) {
        {
            EntityQueryCriteria<ILSSummaryBuilding> criteria = EntityQueryCriteria.create(ILSSummaryBuilding.class);
            criteria.eq(criteria.proto().building(), to);
            for (ILSSummaryBuilding summary : Persistence.service().query(criteria)) {
                if (!to.ilsSummary().contains(summary)) {
                    Persistence.service().delete(summary);
                }
            }
            for (ILSSummaryBuilding summary : to.ilsSummary()) {
                summary.building().set(bo);
                Persistence.service().merge(summary);
            }
        }
        {
            EntityQueryCriteria<ILSProfileBuilding> criteria = EntityQueryCriteria.create(ILSProfileBuilding.class);
            criteria.eq(criteria.proto().building(), to);
            for (ILSProfileBuilding profile : Persistence.service().query(criteria)) {
                if (!to.ilsProfile().contains(profile)) {
                    Persistence.service().delete(profile);
                }
            }
            for (ILSProfileBuilding profile : to.ilsProfile()) {
                profile.building().set(bo);
                Persistence.service().merge(profile);
            }
        }
        to.ilsEmail().building().set(bo);

        Persistence.service().merge(to.ilsEmail());
    }

    @Override
    public void retrieveMerchantAccountStatus(AsyncCallback<MerchantAccount> callback, MerchantAccount merchantAccountStub) {
        MerchantAccount entity = Persistence.service().retrieve(MerchantAccount.class, merchantAccountStub.getPrimaryKey());
        ServerSideFactory.create(Vista2PmcFacade.class).calulateMerchantAccountStatus(entity);
        callback.onSuccess(entity);
    }

    @Override
    public void retrieveEmployee(AsyncCallback<Employee> callback, Employee employeeId) {
        callback.onSuccess(Persistence.service().retrieve(Employee.class, employeeId.getPrimaryKey()));
    }

    @Override
    public void updateFromYardi(AsyncCallback<VoidSerializable> callback, Building buildingId) {
        Building building = Persistence.service().retrieve(boClass, buildingId.getPrimaryKey());

        try {
            ServerSideFactory.create(YardiARFacade.class).updateProductCatalog(building);
        } catch (RemoteException e) {
            throw new UserRuntimeException(i18n.tr("Yardi connection problem"), e);
        } catch (YardiServiceException e) {
            throw new UserRuntimeException(i18n.tr("Error updating lease form Yardi"), e);
        }

        Persistence.service().commit();
        callback.onSuccess(null);
    }

    @Override
    public void getILSVendors(AsyncCallback<Vector<ILSVendor>> callback) {
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
