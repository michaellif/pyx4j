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

import com.propertvista.generator.gdo.ProductItemTypesGDO;

import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.domain.financial.offering.Feature;
import com.propertyvista.domain.financial.offering.FeatureItemType;
import com.propertyvista.domain.financial.offering.Service;
import com.propertyvista.domain.financial.offering.ServiceItemType;

public class ProductItemTypesGenerator {

    private final ProductItemTypesGDO gdo;

    public ProductItemTypesGenerator() {
        gdo = new ProductItemTypesGDO();

        // preload types:
        createChargeItemType("Residential Unit", Service.ServiceType.residentialUnit, 5110);
        createChargeItemType("Commercial Unit", Service.ServiceType.commercialUnit, 5110);
        createChargeItemType("Short Term Residential Unit", Service.ServiceType.residentialShortTermUnit, 5110);
        createChargeItemType("Roof Spot", Service.ServiceType.roof, 5110);
        createChargeItemType("Billboard", Service.ServiceType.sundry, 5110);
        createChargeItemType("Garage", Service.ServiceType.garage, 5110);
        createChargeItemType("Storage", Service.ServiceType.storage, 5110);

        createChargeItemType("Regular Parking", Feature.Type.parking, 5110);
        createChargeItemType("Wide Parking", Feature.Type.parking, 5110);
        createChargeItemType("Narrow Parking", Feature.Type.parking, 5110);
        createChargeItemType("Disabled Parking", Feature.Type.parking, 5110);
        createChargeItemType("Cat", Feature.Type.pet, 5930);
        createChargeItemType("Dog", Feature.Type.pet, 5930);
        createChargeItemType("Small Locker", Feature.Type.locker, 5110);
        createChargeItemType("Medium Locker", Feature.Type.locker, 5110);
        createChargeItemType("Large Locker", Feature.Type.locker, 5110);
        createChargeItemType("Fitness", Feature.Type.addOn, 5110);
        createChargeItemType("Pool", Feature.Type.addOn, 5110);
        createChargeItemType("Furnished", Feature.Type.addOn, 5110);
        createChargeItemType("Key", Feature.Type.addOn, 6240);
        createChargeItemType("Access Card", Feature.Type.addOn, 6240);
        createChargeItemType("Cable", Feature.Type.addOn, 5110);
        createChargeItemType("Water", Feature.Type.utility, 6440);
        createChargeItemType("Gas", Feature.Type.utility, 5997);
        createChargeItemType("Hydro", Feature.Type.utility, 5998);
        createChargeItemType("Booking", Feature.Type.booking, 5934);
    }

    public List<ServiceItemType> getServiceItemTypes() {
        return gdo.serviceItemTypes;
    }

    public List<FeatureItemType> getFeatureItemTypes() {
        return gdo.featureItemTypes;
    }

    private void createChargeItemType(String name, Service.ServiceType serviceType, int glCode) {
        ServiceItemType type = EntityFactory.create(ServiceItemType.class);
        type.name().setValue(name);
        type.serviceType().setValue(serviceType);
        type.glCode().codeId().setValue(glCode);

        gdo.serviceItemTypes.add(type);
    }

    private void createChargeItemType(String name, Feature.Type featureType, int glCode) {
        FeatureItemType type = EntityFactory.create(FeatureItemType.class);
        type.name().setValue(name);
        type.featureType().setValue(featureType);
        type.glCode().codeId().setValue(glCode);

        gdo.featureItemTypes.add(type);
    }

}
