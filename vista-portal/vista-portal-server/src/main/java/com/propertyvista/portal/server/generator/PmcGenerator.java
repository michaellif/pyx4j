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

import com.propertyvista.domain.financial.offeringnew.Service.ServiceType;
import com.propertyvista.domain.financial.offeringnew.ServiceItemType;

public class PmcGenerator {

    public List<ServiceItemType> createServiceItemTypes() {
        List<ServiceItemType> types = new ArrayList<ServiceItemType>();
        types.add(createServiceItemType("Regular Residential Unit", ServiceType.residentialUnit));
        types.add(createServiceItemType("Regular Commercial Unit", ServiceType.commercialUnit));
        types.add(createServiceItemType("Regular Short Term Residential Unit", ServiceType.residentialShortTermUnit));
        types.add(createServiceItemType("Roof Spot", ServiceType.roof));
        types.add(createServiceItemType("Billboard", ServiceType.sundry));
        types.add(createServiceItemType("Regular Parking", ServiceType.parking));
        types.add(createServiceItemType("Wide Parking", ServiceType.parking));
        types.add(createServiceItemType("Nerrow Parking", ServiceType.parking));
        types.add(createServiceItemType("Disabled Parking", ServiceType.parking));
        types.add(createServiceItemType("Cat", ServiceType.pet));
        types.add(createServiceItemType("Dog", ServiceType.pet));
        types.add(createServiceItemType("Small Locker", ServiceType.locker));
        types.add(createServiceItemType("Medium Locker", ServiceType.locker));
        types.add(createServiceItemType("Large Locker", ServiceType.locker));
        types.add(createServiceItemType("Fitness", ServiceType.addOn));
        types.add(createServiceItemType("Pool", ServiceType.addOn));
        types.add(createServiceItemType("Furnished", ServiceType.addOn));
        types.add(createServiceItemType("Key", ServiceType.addOn));
        types.add(createServiceItemType("Access Card", ServiceType.addOn));
        types.add(createServiceItemType("Cable", ServiceType.addOn));
        return types;
    }

    public ServiceItemType createServiceItemType(String name, ServiceType serviceType) {
        ServiceItemType type = EntityFactory.create(ServiceItemType.class);
        type.name().setValue(name);
        type.serviceType().setValue(serviceType);
        return type;
    }

}
