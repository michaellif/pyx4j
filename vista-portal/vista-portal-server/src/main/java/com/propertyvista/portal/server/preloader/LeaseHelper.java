/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 15, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.server.preloader;

import java.util.List;

import com.propertvista.generator.util.RandomUtil;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.domain.financial.offering.ChargeItem;
import com.propertyvista.domain.financial.offering.Feature;
import com.propertyvista.domain.financial.offering.Service;
import com.propertyvista.domain.financial.offering.ServiceFeature;
import com.propertyvista.domain.financial.offering.ServiceItem;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.tenant.lease.Lease;

class LeaseHelper {

    static void updateLease(Lease lease) {
        Building building = lease.unit().belongsTo();
        Persistence.service().retrieve(building);

        Service selectedService = null;
        // Proper way
//        {
//            EntityQueryCriteria<ServiceItem> serviceItemCriteria = EntityQueryCriteria.create(ServiceItem.class);
//            serviceItemCriteria.add(PropertyCriterion.eq(serviceItemCriteria.proto().element(), lease.unit()));
//            serviceItemCriteria.add(PropertyCriterion.eq(serviceItemCriteria.proto().sserviceOrFeatureervice().catalog(), building.serviceCatalog()));
//            ServiceItem item = Persistence.service().retrieve(serviceItemCriteria);
//            if (item != null) {
//                lease.serviceAgreement().serviceItem().set(createChargeItem(item));
//                selectedService = item.service();
//                Persistence.service().retrieve(selectedService);
//                lease.type().set(selectedService.type());
//            }
//        }
        // TODO use the code above when sserviceOrFeatureervice implemented
        if (true) {
            EntityQueryCriteria<Service> serviceCriteria = EntityQueryCriteria.create(Service.class);
            serviceCriteria.add(PropertyCriterion.eq(serviceCriteria.proto().catalog(), building.serviceCatalog()));
            List<Service> services = Persistence.service().query(serviceCriteria);

            for (Service service : services) {
                Persistence.service().retrieve(service.items());
                for (ServiceItem item : service.items()) {
                    if (lease.unit().equals(item.element())) {
                        lease.serviceAgreement().serviceItem().set(createChargeItem(item));
                        selectedService = service;
                        lease.type().set(selectedService.type());
                        break;
                    }
                }
            }
        }

        if (!lease.serviceAgreement().serviceItem().isEmpty()) {
            Persistence.service().retrieve(building.serviceCatalog());
            // pre-populate utilities for the new service: 
            Persistence.service().retrieve(selectedService.features());
            for (ServiceFeature feature : selectedService.features()) {
                if (Feature.Type.utility.equals(feature.feature().type().getValue())) {
                    Persistence.service().retrieve(feature.feature().items());
                    for (ServiceItem item : feature.feature().items()) {
                        if (!building.serviceCatalog().includedUtilities().contains(item.type())
                                && !building.serviceCatalog().externalUtilities().contains(item.type())) {
                            lease.serviceAgreement().featureItems().add(createChargeItem(item));
                        }
                    }
                }

            }

            // pre-populate concessions for the new service: 
            Persistence.service().retrieve(selectedService.concessions());
            if (!selectedService.concessions().isEmpty()) {
                lease.serviceAgreement().concessions().add(RandomUtil.random(selectedService.concessions()));
            }
        }
    }

    private static ChargeItem createChargeItem(ServiceItem serviceItem) {
        ChargeItem chargeItem = EntityFactory.create(ChargeItem.class);
        chargeItem.item().set(serviceItem);
        chargeItem.originalPrice().setValue(serviceItem.price().getValue());
        return chargeItem;
    }
}
