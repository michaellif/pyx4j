/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 24, 2011
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.portal.server.generator;

import java.util.ArrayList;
import java.util.List;

import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.domain.financial.offeringnew.ServiceItemType;
import com.propertyvista.domain.financial.offeringnew.Feature.FeatureType;
import com.propertyvista.domain.financial.offeringnew.Service.ServiceType;

public class PmcGenerator {

    public List<ServiceItemType> createChargeItemTypes() {
        List<ServiceItemType> types = new ArrayList<ServiceItemType>();
        types.add(createChargeItemType("Regular Residential Unit", ServiceType.residentialUnit));
        types.add(createChargeItemType("Regular Commercial Unit", ServiceType.commercialUnit));
        types.add(createChargeItemType("Regular Short Term Residential Unit", ServiceType.residentialShortTermUnit));
        types.add(createChargeItemType("Roof Spot", ServiceType.roof));
        types.add(createChargeItemType("Billboard", ServiceType.sundry));
        types.add(createChargeItemType("Regular Parking", FeatureType.parking));
        types.add(createChargeItemType("Wide Parking", FeatureType.parking));
        types.add(createChargeItemType("Nerrow Parking", FeatureType.parking));
        types.add(createChargeItemType("Disabled Parking", FeatureType.parking));
        types.add(createChargeItemType("Cat", FeatureType.pet));
        types.add(createChargeItemType("Dog", FeatureType.pet));
        types.add(createChargeItemType("Small Locker", FeatureType.locker));
        types.add(createChargeItemType("Medium Locker", FeatureType.locker));
        types.add(createChargeItemType("Large Locker", FeatureType.locker));
        types.add(createChargeItemType("Fitness", FeatureType.addOn));
        types.add(createChargeItemType("Pool", FeatureType.addOn));
        types.add(createChargeItemType("Furnished", FeatureType.addOn));
        types.add(createChargeItemType("Key", FeatureType.addOn));
        types.add(createChargeItemType("Access Card", FeatureType.addOn));
        types.add(createChargeItemType("Cable", FeatureType.addOn));
        return types;
    }

    public ServiceItemType createChargeItemType(String name, ServiceType serviceType) {
        ServiceItemType type = EntityFactory.create(ServiceItemType.class);
        type.name().setValue(name);
        type.serviceType().setValue(serviceType);
        return type;
    }

    public ServiceItemType createChargeItemType(String name, FeatureType featureType) {
        ServiceItemType type = EntityFactory.create(ServiceItemType.class);
        type.name().setValue(name);
        type.featureType().setValue(featureType);
        return type;
    }

}
