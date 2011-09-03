/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 10, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.server.ptapp.services;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.server.PersistenceServicesFactory;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.domain.financial.offering.Concession;
import com.propertyvista.domain.financial.offering.Feature;
import com.propertyvista.domain.financial.offering.Service;
import com.propertyvista.domain.financial.offering.ServiceCatalog;
import com.propertyvista.domain.financial.offering.ServiceConcession;
import com.propertyvista.domain.financial.offering.ServiceFeature;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.portal.rpc.ptapp.dto.UnitInfoDTO;
import com.propertyvista.portal.rpc.ptapp.services.ApartmentService;
import com.propertyvista.portal.server.ptapp.PtAppContext;

public class ApartmentServiceImpl implements ApartmentService {

    private final static Logger log = LoggerFactory.getLogger(ApartmentServiceImpl.class);

    @Override
    public void retrieve(AsyncCallback<UnitInfoDTO> callback, Key tenantId) {

        Lease lease = PtAppContext.getCurrentLease();
        if (!PersistenceServicesFactory.getPersistenceService().retrieve(lease)) {
            throw new Error("There is no current Lease set!");
        }
        if (!PersistenceServicesFactory.getPersistenceService().retrieve(lease.unit())) {
            throw new Error("There is no Unit selected!?.");
        }
        if (!PersistenceServicesFactory.getPersistenceService().retrieve(lease.unit().floorplan())) {
            throw new Error("There is no unit Floorplan data!?.");
        }
        if (!PersistenceServicesFactory.getPersistenceService().retrieve(lease.unit().belongsTo())) {
            throw new Error("There is no unit building data!?.");
        }

        // fill DTO:
        UnitInfoDTO unitInfo = EntityFactory.create(UnitInfoDTO.class);
        unitInfo.name().setValue(lease.unit().floorplan().name().getValue());
        unitInfo.beds().setValue(lease.unit().floorplan().bedrooms().getStringView());
        if (lease.unit().floorplan().dens().getValue() > 0) {
            unitInfo.beds().setValue(unitInfo.beds().getValue() + " + " + lease.unit().floorplan().dens().getStringView() + " den(s)");
        }
        unitInfo.baths().setValue(lease.unit().floorplan().bathrooms().getStringView());
        if (lease.unit().floorplan().halfBath().getValue() > 0) {
            unitInfo.baths().setValue(unitInfo.baths().getValue() + " + " + lease.unit().floorplan().halfBath().getStringView() + " half bath(s)");
        }

        unitInfo.number().setValue(lease.unit().info().number().getValue());
        unitInfo.area().setValue(lease.unit().info().area().getStringView() + " " + lease.unit().info().areaUnits().getStringView());

        // serviceCatalog processing:
        syncBuildingServiceCatalog(lease.unit().belongsTo());
        fillserviceItems(unitInfo, lease.unit().belongsTo(), lease);

        // Lease data:
        unitInfo.leaseFrom().setValue(lease.leaseFrom().getValue());
        unitInfo.leaseTo().setValue(lease.leaseTo().getValue());
        unitInfo.unitRent().setValue(lease.unit().financial().unitRent().getValue());

        callback.onSuccess(unitInfo);
    }

    @Override
    public void save(AsyncCallback<UnitInfoDTO> callback, UnitInfoDTO editableEntity) {
        callback.onSuccess(null); // this PT App. step is read-only!..
    }

    private ServiceCatalog syncBuildingServiceCatalog(Building building) {

        // load detached entities:
        PersistenceServicesFactory.getPersistenceService().retrieve(building.serviceCatalog());

        // update service catalogue double-reference lists:
        EntityQueryCriteria<Service> serviceCriteria = EntityQueryCriteria.create(Service.class);
        serviceCriteria.add(PropertyCriterion.eq(serviceCriteria.proto().catalog(), building.serviceCatalog()));
        List<Service> services = PersistenceServicesFactory.getPersistenceService().query(serviceCriteria);
        building.serviceCatalog().services().clear();
        building.serviceCatalog().services().addAll(services);

        EntityQueryCriteria<Feature> featureCriteria = EntityQueryCriteria.create(Feature.class);
        featureCriteria.add(PropertyCriterion.eq(featureCriteria.proto().catalog(), building.serviceCatalog()));
        List<Feature> features = PersistenceServicesFactory.getPersistenceService().query(featureCriteria);
        building.serviceCatalog().features().clear();
        building.serviceCatalog().features().addAll(features);

        EntityQueryCriteria<Concession> concessionCriteria = EntityQueryCriteria.create(Concession.class);
        concessionCriteria.add(PropertyCriterion.eq(concessionCriteria.proto().catalog(), building.serviceCatalog()));
        List<Concession> concessions = PersistenceServicesFactory.getPersistenceService().query(concessionCriteria);
        building.serviceCatalog().concessions().clear();
        building.serviceCatalog().concessions().addAll(concessions);

        return building.serviceCatalog();
    }

    private void fillserviceItems(UnitInfoDTO entity, Building building, Lease lease) {

        entity.utilities().clear();
        entity.addOns().clear();
        entity.concessions().clear();

        for (Service service : building.serviceCatalog().services()) {
            if (service.type().equals(lease.type())) {
                fillServiceEligibilityData(entity, service, building);
            }
        }
    }

    private boolean fillServiceEligibilityData(UnitInfoDTO entity, Service service, Building building) {

        // fill related features and concession:
        for (ServiceFeature feature : service.features()) {
            if (Feature.Type.addOn.equals(feature.feature().type().getValue())) {
                entity.addOns().addAll(feature.feature().items());
            } else {
                entity.utilities().addAll(feature.feature().items());
            }
        }
        for (ServiceConcession consession : service.concessions()) {
            entity.concessions().add(consession.concession());
        }

        return (service != null);
    }
}
