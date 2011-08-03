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
package com.propertvista.generator;

import java.util.ArrayList;
import java.util.List;

import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.domain.financial.offeringnew.Feature;
import com.propertyvista.domain.financial.offeringnew.Service;
import com.propertyvista.domain.financial.offeringnew.ServiceItemType;

public class PmcGenerator {

    public List<ServiceItemType> createChargeItemTypes() {
        List<ServiceItemType> types = new ArrayList<ServiceItemType>();
        types.add(createChargeItemType("Regular Residential Unit", Service.Type.residentialUnit));
        types.add(createChargeItemType("Regular Commercial Unit", Service.Type.commercialUnit));
        types.add(createChargeItemType("Regular Short Term Residential Unit", Service.Type.residentialShortTermUnit));
        types.add(createChargeItemType("Roof Spot", Service.Type.roof));
        types.add(createChargeItemType("Billboard", Service.Type.sundry));
        types.add(createChargeItemType("Regular Parking", Feature.Type.parking));
        types.add(createChargeItemType("Wide Parking", Feature.Type.parking));
        types.add(createChargeItemType("Nerrow Parking", Feature.Type.parking));
        types.add(createChargeItemType("Disabled Parking", Feature.Type.parking));
        types.add(createChargeItemType("Cat", Feature.Type.pet));
        types.add(createChargeItemType("Dog", Feature.Type.pet));
        types.add(createChargeItemType("Small Locker", Feature.Type.locker));
        types.add(createChargeItemType("Medium Locker", Feature.Type.locker));
        types.add(createChargeItemType("Large Locker", Feature.Type.locker));
        types.add(createChargeItemType("Fitness", Feature.Type.addOn));
        types.add(createChargeItemType("Pool", Feature.Type.addOn));
        types.add(createChargeItemType("Furnished", Feature.Type.addOn));
        types.add(createChargeItemType("Key", Feature.Type.addOn));
        types.add(createChargeItemType("Access Card", Feature.Type.addOn));
        types.add(createChargeItemType("Cable", Feature.Type.addOn));
        return types;
    }

    public ServiceItemType createChargeItemType(String name, Service.Type serviceType) {
        ServiceItemType type = EntityFactory.create(ServiceItemType.class);
        type.name().setValue(name);
        type.serviceType().setValue(serviceType);
        return type;
    }

    public ServiceItemType createChargeItemType(String name, Feature.Type featureType) {
        ServiceItemType type = EntityFactory.create(ServiceItemType.class);
        type.name().setValue(name);
        type.featureType().setValue(featureType);
        return type;
    }

}
