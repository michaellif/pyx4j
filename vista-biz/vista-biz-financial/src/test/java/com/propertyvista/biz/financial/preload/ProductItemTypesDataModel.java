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
package com.propertyvista.biz.financial.preload;

import java.util.ArrayList;
import java.util.List;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.domain.financial.offering.Feature;
import com.propertyvista.domain.financial.offering.FeatureItemType;
import com.propertyvista.domain.financial.offering.ProductItemType;
import com.propertyvista.domain.financial.offering.Service;
import com.propertyvista.domain.financial.offering.ServiceItemType;

public class ProductItemTypesDataModel {

    public final List<ServiceItemType> serviceItemTypes;

    public final List<FeatureItemType> featureItemTypes;

    public ProductItemTypesDataModel(PreloadConfig config) {
        serviceItemTypes = new ArrayList<ServiceItemType>();
        featureItemTypes = new ArrayList<FeatureItemType>();
    }

    public void generate() {
        generateChargeItemType("Regular Residential Unit", Service.ServiceType.residentialUnit);
        generateChargeItemType("Ocean View Residential Unit", Service.ServiceType.residentialUnit);
        generateChargeItemType("Regular Commercial Unit", Service.ServiceType.commercialUnit);

// VISTA-1622 - CRM:Product Dictionary:Service item Types - delete not supported
//        generateChargeItemType("Regular Short Term Residential Unit", Service.ServiceType.residentialShortTermUnit);
//        generateChargeItemType("Roof Spot", Service.ServiceType.roof);
//        generateChargeItemType("Billboard", Service.ServiceType.sundry);
//        generateChargeItemType("Garage", Service.ServiceType.garage);
//        generateChargeItemType("Storage", Service.ServiceType.storage);

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

        Persistence.service().persist(getProductItemTypes());
    }

    private void generateChargeItemType(String name, Service.ServiceType serviceType) {
        ServiceItemType type = EntityFactory.create(ServiceItemType.class);
        type.name().setValue(name);
        type.serviceType().setValue(serviceType);
        serviceItemTypes.add(type);
    }

    private void generateChargeItemType(String name, Feature.Type featureType) {
        FeatureItemType type = EntityFactory.create(FeatureItemType.class);
        type.name().setValue(name);
        type.featureType().setValue(featureType);
        featureItemTypes.add(type);
    }

    public List<ProductItemType> getProductItemTypes() {
        List<ProductItemType> productItemTypes = new ArrayList<ProductItemType>();
        productItemTypes.addAll(serviceItemTypes);
        productItemTypes.addAll(featureItemTypes);
        return productItemTypes;
    }

    public List<ServiceItemType> getServiceItemTypes() {
        return serviceItemTypes;
    }

    public List<FeatureItemType> getFeatureItemTypes() {
        return featureItemTypes;
    }

}
