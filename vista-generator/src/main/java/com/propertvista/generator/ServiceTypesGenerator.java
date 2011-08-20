/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Aug 20, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertvista.generator;

import java.util.List;

import com.propertvista.generator.gdo.ServiceItemTypes;

import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.domain.financial.offering.Feature;
import com.propertyvista.domain.financial.offering.Service;
import com.propertyvista.domain.financial.offering.ServiceItemType;

public class ServiceTypesGenerator extends ServiceItemTypes {

    public ServiceTypesGenerator() {

        // preload types:
        serviceItemTypes.add(createChargeItemType("Regular Residential Unit", Service.Type.residentialUnit));
        serviceItemTypes.add(createChargeItemType("Regular Commercial Unit", Service.Type.commercialUnit));
        serviceItemTypes.add(createChargeItemType("Regular Short Term Residential Unit", Service.Type.residentialShortTermUnit));
        serviceItemTypes.add(createChargeItemType("Roof Spot", Service.Type.roof));
        serviceItemTypes.add(createChargeItemType("Billboard", Service.Type.sundry));

        featureItemTypes.add(createChargeItemType("Regular Parking", Feature.Type.parking));
        featureItemTypes.add(createChargeItemType("Wide Parking", Feature.Type.parking));
        featureItemTypes.add(createChargeItemType("Narrow Parking", Feature.Type.parking));
        featureItemTypes.add(createChargeItemType("Disabled Parking", Feature.Type.parking));
        featureItemTypes.add(createChargeItemType("Cat", Feature.Type.pet));
        featureItemTypes.add(createChargeItemType("Dog", Feature.Type.pet));
        featureItemTypes.add(createChargeItemType("Small Locker", Feature.Type.locker));
        featureItemTypes.add(createChargeItemType("Medium Locker", Feature.Type.locker));
        featureItemTypes.add(createChargeItemType("Large Locker", Feature.Type.locker));
        featureItemTypes.add(createChargeItemType("Fitness", Feature.Type.addOn));
        featureItemTypes.add(createChargeItemType("Pool", Feature.Type.addOn));
        featureItemTypes.add(createChargeItemType("Furnished", Feature.Type.addOn));
        featureItemTypes.add(createChargeItemType("Key", Feature.Type.addOn));
        featureItemTypes.add(createChargeItemType("Access Card", Feature.Type.addOn));
        featureItemTypes.add(createChargeItemType("Cable", Feature.Type.addOn));
        featureItemTypes.add(createChargeItemType("Water", Feature.Type.utility));
        featureItemTypes.add(createChargeItemType("Gas", Feature.Type.utility));
        featureItemTypes.add(createChargeItemType("Hydro", Feature.Type.utility));
    }

    public List<ServiceItemType> getServiceItemTypes() {
        return serviceItemTypes;
    }

    public List<ServiceItemType> getFeatureItemTypes() {
        return featureItemTypes;
    }

    private ServiceItemType createChargeItemType(String name, Service.Type serviceType) {
        ServiceItemType type = EntityFactory.create(ServiceItemType.class);
        type.name().setValue(name);
        type.type().setValue(ServiceItemType.Type.service);
        type.serviceType().setValue(serviceType);
        return type;
    }

    private ServiceItemType createChargeItemType(String name, Feature.Type featureType) {
        ServiceItemType type = EntityFactory.create(ServiceItemType.class);
        type.name().setValue(name);
        type.type().setValue(ServiceItemType.Type.feature);
        type.featureType().setValue(featureType);
        return type;
    }

}
