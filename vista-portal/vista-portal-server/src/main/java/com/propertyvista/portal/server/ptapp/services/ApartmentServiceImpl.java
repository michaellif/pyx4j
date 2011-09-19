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
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.domain.financial.offering.ChargeItem;
import com.propertyvista.domain.financial.offering.Concession;
import com.propertyvista.domain.financial.offering.Feature;
import com.propertyvista.domain.financial.offering.Service;
import com.propertyvista.domain.financial.offering.ServiceCatalog;
import com.propertyvista.domain.financial.offering.ServiceConcession;
import com.propertyvista.domain.financial.offering.ServiceFeature;
import com.propertyvista.domain.financial.offering.ServiceItem;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.portal.rpc.ptapp.dto.ApartmentInfoDTO;
import com.propertyvista.portal.rpc.ptapp.services.ApartmentService;
import com.propertyvista.portal.server.ptapp.PtAppContext;

public class ApartmentServiceImpl implements ApartmentService {

    private final static Logger log = LoggerFactory.getLogger(ApartmentServiceImpl.class);

    @Override
    public void retrieve(AsyncCallback<ApartmentInfoDTO> callback, Key tenantId) {
        callback.onSuccess(retrieveData());
    }

    @Override
    public void save(AsyncCallback<ApartmentInfoDTO> callback, ApartmentInfoDTO editableEntity) {
        callback.onSuccess(null); // this PT App. step is read-only!..
    }

    public ApartmentInfoDTO retrieveData() {
        Lease lease = PtAppContext.getCurrentUserLease();
        if (!Persistence.service().retrieve(lease.unit())) {
            throw new Error("There is no Unit selected!?.");
        }
        if (!Persistence.service().retrieve(lease.unit().floorplan())) {
            throw new Error("There is no unit Floorplan data!?.");
        }
        if (!Persistence.service().retrieve(lease.unit().belongsTo())) {
            throw new Error("There is no unit building data!?.");
        }

        // fill DTO:
        ApartmentInfoDTO unitInfo = EntityFactory.create(ApartmentInfoDTO.class);
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
        fillServiceItems(unitInfo, lease.unit().belongsTo(), lease);

        // Lease data:
        unitInfo.leaseFrom().setValue(lease.leaseFrom().getValue());
        unitInfo.leaseTo().setValue(lease.leaseTo().getValue());
        unitInfo.unitRent().setValue(lease.serviceAgreement().serviceItem().item().price().getValue());
        return unitInfo;
    }

    private ServiceCatalog syncBuildingServiceCatalog(Building building) {

        // load detached entities:
        Persistence.service().retrieve(building.serviceCatalog());

        // update service catalogue double-reference lists:
        EntityQueryCriteria<Service> serviceCriteria = EntityQueryCriteria.create(Service.class);
        serviceCriteria.add(PropertyCriterion.eq(serviceCriteria.proto().catalog(), building.serviceCatalog()));
        List<Service> services = Persistence.service().query(serviceCriteria);
        building.serviceCatalog().services().clear();
        building.serviceCatalog().services().addAll(services);

        EntityQueryCriteria<Feature> featureCriteria = EntityQueryCriteria.create(Feature.class);
        featureCriteria.add(PropertyCriterion.eq(featureCriteria.proto().catalog(), building.serviceCatalog()));
        List<Feature> features = Persistence.service().query(featureCriteria);
        building.serviceCatalog().features().clear();
        building.serviceCatalog().features().addAll(features);

        EntityQueryCriteria<Concession> concessionCriteria = EntityQueryCriteria.create(Concession.class);
        concessionCriteria.add(PropertyCriterion.eq(concessionCriteria.proto().catalog(), building.serviceCatalog()));
        List<Concession> concessions = Persistence.service().query(concessionCriteria);
        building.serviceCatalog().concessions().clear();
        building.serviceCatalog().concessions().addAll(concessions);

        return building.serviceCatalog();
    }

    private void fillServiceItems(ApartmentInfoDTO entity, Building building, Lease lease) {

        entity.agreedAddOns().clear();
        entity.availableAddOns().clear();
        entity.concessions().clear();

        for (ChargeItem utility : lease.serviceAgreement().featureItems()) {
            entity.agreedAddOns().add(utility.item());
        }

        for (ServiceConcession consession : lease.serviceAgreement().concessions()) {
            entity.concessions().add(consession.concession());
        }

        syncBuildingServiceCatalog(lease.unit().belongsTo());
        for (Service service : building.serviceCatalog().services()) {
            if (service.type().equals(lease.type())) {
                for (ServiceFeature feature : service.features()) {
                    for (ServiceItem item : feature.feature().items()) {
                        if (!entity.agreedAddOns().contains(item)) {
                            entity.availableAddOns().add(item);
                        }
                    }
                }
            }
        }
    }
}
