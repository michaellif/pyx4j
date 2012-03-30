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
import com.propertvista.generator.util.RandomUtil;

import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.domain.financial.GlCode;
import com.propertyvista.domain.financial.offering.Feature;
import com.propertyvista.domain.financial.offering.FeatureItemType;
import com.propertyvista.domain.financial.offering.Service;
import com.propertyvista.domain.financial.offering.ServiceItemType;

public class ProductItemTypesGenerator {

    private final ProductItemTypesGDO gdo;

    private final List<GlCode> glCodes;

    public ProductItemTypesGenerator(List<GlCode> glCodes) {
        this.glCodes = glCodes;
        gdo = new ProductItemTypesGDO();

        // preload types:
        createChargeItemType("Residential Unit", Service.Type.residentialUnit);
        createChargeItemType("Commercial Unit", Service.Type.commercialUnit);
        createChargeItemType("Short Term Residential Unit", Service.Type.residentialShortTermUnit);
        createChargeItemType("Roof Spot", Service.Type.roof);
        createChargeItemType("Billboard", Service.Type.sundry);
        createChargeItemType("Garage", Service.Type.garage);
        createChargeItemType("Storage", Service.Type.storage);

        createChargeItemType("Regular Parking", Feature.Type.parking);
        createChargeItemType("Wide Parking", Feature.Type.parking);
        createChargeItemType("Narrow Parking", Feature.Type.parking);
        createChargeItemType("Disabled Parking", Feature.Type.parking);
        createChargeItemType("Cat", Feature.Type.pet);
        createChargeItemType("Dog", Feature.Type.pet);
        createChargeItemType("Small Locker", Feature.Type.locker);
        createChargeItemType("Medium Locker", Feature.Type.locker);
        createChargeItemType("Large Locker", Feature.Type.locker);
        createChargeItemType("Fitness", Feature.Type.addOn);
        createChargeItemType("Pool", Feature.Type.addOn);
        createChargeItemType("Furnished", Feature.Type.addOn);
        createChargeItemType("Key", Feature.Type.addOn);
        createChargeItemType("Access Card", Feature.Type.addOn);
        createChargeItemType("Cable", Feature.Type.addOn);
        createChargeItemType("Water", Feature.Type.utility);
        createChargeItemType("Gas", Feature.Type.utility);
        createChargeItemType("Hydro", Feature.Type.utility);
        createChargeItemType("Booking", Feature.Type.booking);
    }

    public List<ServiceItemType> getServiceItemTypes() {
        return gdo.serviceItemTypes;
    }

    public List<FeatureItemType> getFeatureItemTypes() {
        return gdo.featureItemTypes;
    }

    private void createChargeItemType(String name, Service.Type serviceType) {
        ServiceItemType type = EntityFactory.create(ServiceItemType.class);
        type.name().setValue(name);
        type.serviceType().setValue(serviceType);
        type.glCode().set(RandomUtil.random(glCodes));

        gdo.serviceItemTypes.add(type);
    }

    private void createChargeItemType(String name, Feature.Type featureType) {
        FeatureItemType type = EntityFactory.create(FeatureItemType.class);
        type.name().setValue(name);
        type.featureType().setValue(featureType);
        type.glCode().set(RandomUtil.random(glCodes));

        gdo.featureItemTypes.add(type);
    }

    public ProductItemTypesGDO getGdo() {
        return gdo;
    }

}
