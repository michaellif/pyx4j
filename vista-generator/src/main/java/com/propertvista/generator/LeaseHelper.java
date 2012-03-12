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
package com.propertvista.generator;

import java.util.List;

import com.propertvista.generator.util.RandomUtil;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.domain.financial.offering.Feature;
import com.propertyvista.domain.financial.offering.ProductItem;
import com.propertyvista.domain.financial.offering.Service;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.tenant.lease.BillableItem;
import com.propertyvista.domain.tenant.lease.Lease;

public class LeaseHelper {

    public static void updateLease(Lease lease) {
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
            serviceCriteria.add(PropertyCriterion.eq(serviceCriteria.proto().catalog(), building.productCatalog()));
            List<Service> services = Persistence.service().query(serviceCriteria);

            for (Service service : services) {
                Persistence.service().retrieve(service.version().items());
                for (ProductItem item : service.version().items()) {
                    if (lease.unit().equals(item.element())) {
                        lease.leaseProducts().serviceItem().set(createBillableItem(item));
                        selectedService = service;
                        lease.type().set(selectedService.version().type());
                        break;
                    }
                }
            }
        }

        if (!lease.leaseProducts().serviceItem().isEmpty()) {
            Persistence.service().retrieve(building.productCatalog());
            // pre-populate utilities for the new service: 
            Persistence.service().retrieve(selectedService.version().features());
            for (Feature feature : selectedService.version().features()) {
                if (Feature.Type.utility.equals(feature.version().type().getValue())) {
                    Persistence.service().retrieve(feature.version().items());
                    for (ProductItem item : feature.version().items()) {
                        if (!building.productCatalog().includedUtilities().contains(item.type())
                                && !building.productCatalog().externalUtilities().contains(item.type())) {
                            lease.leaseProducts().featureItems().add(createBillableItem(item));
                        }
                    }
                }

            }

            // pre-populate concessions for the new service: 
            Persistence.service().retrieve(selectedService.version().concessions());
            if (!selectedService.version().concessions().isEmpty()) {
                lease.leaseProducts().concessions().add(RandomUtil.random(selectedService.version().concessions()));
            }
        }
    }

    private static BillableItem createBillableItem(ProductItem serviceItem) {
        BillableItem newItem = EntityFactory.create(BillableItem.class);
        newItem.item().set(serviceItem);
        newItem.effectiveDate().setValue(new LogicalDate());
        return newItem;
    }
}
