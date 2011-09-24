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
import com.propertyvista.domain.financial.offering.Service;
import com.propertyvista.domain.financial.offering.ServiceCatalog;
import com.propertyvista.domain.financial.offering.ServiceConcession;
import com.propertyvista.domain.financial.offering.ServiceFeature;
import com.propertyvista.domain.financial.offering.ServiceItem;
import com.propertyvista.domain.financial.offering.ServiceItemType;
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
        unitInfo.name().setValue(lease.unit().floorplan().marketingName().getValue());
        unitInfo.bedrooms().setValue(lease.unit().floorplan().bedrooms().getStringView());
        if (lease.unit().floorplan().dens().getValue() > 0) {
            unitInfo.bedrooms().setValue(unitInfo.bedrooms().getValue() + " + " + lease.unit().floorplan().dens().getStringView() + " den(s)");
        }
        unitInfo.bathrooms().setValue(lease.unit().floorplan().bathrooms().getStringView());
        if (lease.unit().floorplan().halfBath().getValue() > 0) {
            unitInfo.bathrooms().setValue(unitInfo.bathrooms().getValue() + " + " + lease.unit().floorplan().halfBath().getStringView() + " separate WC(s)");
        }

        unitInfo.suiteNumber().setValue(lease.unit().info().number().getValue());

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

        // load detached data:
        for (Service item : services) {
            Persistence.service().retrieve(item.items());
            Persistence.service().retrieve(item.features());
            for (ServiceFeature fi : item.features()) {
                Persistence.service().retrieve(fi.feature().items());
            }
            Persistence.service().retrieve(item.concessions());
        }
//        
//  Currently not used here:        
//
//        EntityQueryCriteria<Feature> featureCriteria = EntityQueryCriteria.create(Feature.class);
//        featureCriteria.add(PropertyCriterion.eq(featureCriteria.proto().catalog(), building.serviceCatalog()));
//        List<Feature> features = Persistence.service().query(featureCriteria);
//        building.serviceCatalog().features().clear();
//        building.serviceCatalog().features().addAll(features);
//
//        EntityQueryCriteria<Concession> concessionCriteria = EntityQueryCriteria.create(Concession.class);
//        concessionCriteria.add(PropertyCriterion.eq(concessionCriteria.proto().catalog(), building.serviceCatalog()));
//        List<Concession> concessions = Persistence.service().query(concessionCriteria);
//        building.serviceCatalog().concessions().clear();
//        building.serviceCatalog().concessions().addAll(concessions);

        return building.serviceCatalog();
    }

    private void fillServiceItems(ApartmentInfoDTO entity, Building building, Lease lease) {

        entity.includedUtilities().clear();
        entity.externalUtilities().clear();

        entity.agreedUtilities().clear();
        entity.availableUtilities().clear();

        entity.agreedOptions().clear();
        entity.availableOptions().clear();

        entity.concessions().clear();

        syncBuildingServiceCatalog(lease.unit().belongsTo());

        entity.includedUtilities().addAll(building.serviceCatalog().includedUtilities());
        entity.externalUtilities().addAll(building.serviceCatalog().externalUtilities());

        // fill agreed items:
        for (ChargeItem item : lease.serviceAgreement().featureItems()) {
            if (item.item().type().type().getValue().equals(ServiceItemType.Type.feature)) {
                switch (item.item().type().featureType().getValue()) {
                case utility:
                    entity.agreedUtilities().add(item.item());
                    break;
                default:
                    entity.agreedOptions().add(item.item());
                }
            }
        }

        // fill available items:
        for (Service service : building.serviceCatalog().services()) {
            if (service.type().equals(lease.type())) {
                for (ServiceFeature feature : service.features()) {
                    for (ServiceItem item : feature.feature().items()) {
                        switch (item.type().featureType().getValue()) {
                        case utility:
                            if (!entity.includedUtilities().contains(item.type()) && !entity.externalUtilities().contains(item.type())) {
                                entity.availableUtilities().add(item);
                            }
                            break;
                        default:
                            entity.availableOptions().add(item);
                        }
                    }
                }
            }
        }

        // fill concessions:
        for (ServiceConcession consession : lease.serviceAgreement().concessions()) {
            entity.concessions().add(consession.concession());
        }
    }
}
