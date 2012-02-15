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
package com.propertyvista.server.billing.preload;

import java.util.ArrayList;
import java.util.List;

import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.domain.financial.offering.Feature;
import com.propertyvista.domain.financial.offering.ProductItemType;
import com.propertyvista.domain.financial.offering.Service;

public class ProductItemTypesGenerator {

    public final List<ProductItemType> productItemTypes;

    public ProductItemTypesGenerator(DataModel dataModel) {
        productItemTypes = new ArrayList<ProductItemType>();
    }

    private void generate() {
        generateChargeItemType("Regular Residential Unit", Service.Type.residentialUnit);
        generateChargeItemType("Ocean View Residential Unit", Service.Type.residentialUnit);
        generateChargeItemType("Regular Commercial Unit", Service.Type.commercialUnit);
        generateChargeItemType("Regular Short Term Residential Unit", Service.Type.residentialShortTermUnit);
        generateChargeItemType("Roof Spot", Service.Type.roof);
        generateChargeItemType("Billboard", Service.Type.sundry);
        generateChargeItemType("Garage", Service.Type.garage);
        generateChargeItemType("Storage", Service.Type.storage);

        generateChargeItemType("Regular Parking", Feature.Type.parking);
        generateChargeItemType("Wide Parking", Feature.Type.parking);
        generateChargeItemType("Narrow Parking", Feature.Type.parking);
        generateChargeItemType("Disabled Parking", Feature.Type.parking);
        generateChargeItemType("Cat", Feature.Type.pet);
        generateChargeItemType("Dog", Feature.Type.pet);
        generateChargeItemType("Small Locker", Feature.Type.locker);
        generateChargeItemType("Medium Locker", Feature.Type.locker);
        generateChargeItemType("Large Locker", Feature.Type.locker);
        generateChargeItemType("Fitness", Feature.Type.addOn);
        generateChargeItemType("Pool", Feature.Type.addOn);
        generateChargeItemType("Furnished", Feature.Type.addOn);
        generateChargeItemType("Key", Feature.Type.addOn);
        generateChargeItemType("Access Card", Feature.Type.addOn);
        generateChargeItemType("Cable", Feature.Type.addOn);
        generateChargeItemType("Water", Feature.Type.utility);
        generateChargeItemType("Gas", Feature.Type.utility);
        generateChargeItemType("Hydro", Feature.Type.utility);
        generateChargeItemType("Booking", Feature.Type.booking);
    }

    private void generateChargeItemType(String name, Service.Type serviceType) {
        ProductItemType type = EntityFactory.create(ProductItemType.class);
        type.name().setValue(name);
        type.type().setValue(ProductItemType.Type.service);
        type.serviceType().setValue(serviceType);
        productItemTypes.add(type);
    }

    private void generateChargeItemType(String name, Feature.Type featureType) {
        ProductItemType type = EntityFactory.create(ProductItemType.class);
        type.name().setValue(name);
        type.type().setValue(ProductItemType.Type.feature);
        type.featureType().setValue(featureType);
        productItemTypes.add(type);
    }

    public static List<ProductItemType> generate(DataModel dataModel) {
        ProductItemTypesGenerator generator = new ProductItemTypesGenerator(dataModel);
        generator.generate();
        return generator.productItemTypes;
    }
}
