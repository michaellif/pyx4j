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
package com.propertyvista.test.mock.models;

import java.util.ArrayList;
import java.util.List;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.domain.financial.offering.Service;
import com.propertyvista.domain.financial.offering.ServiceItemType;
import com.propertyvista.test.mock.MockDataModel;

public class ServiceItemTypeDataModel extends MockDataModel<ServiceItemType> {

    public ServiceItemTypeDataModel() {
    }

    @Override
    protected void generate() {

        List<ServiceItemType> serviceItemTypes = new ArrayList<ServiceItemType>();

        serviceItemTypes.add(generateChargeItemType("Regular Residential Unit", Service.ServiceType.residentialUnit));
        serviceItemTypes.add(generateChargeItemType("Ocean View Residential Unit", Service.ServiceType.residentialUnit));
        serviceItemTypes.add(generateChargeItemType("Regular Short Term Residential Unit", Service.ServiceType.residentialShortTermUnit));
        serviceItemTypes.add(generateChargeItemType("Regular Commercial Unit", Service.ServiceType.commercialUnit));

        Persistence.service().persist(serviceItemTypes);
    }

    private ServiceItemType generateChargeItemType(String name, Service.ServiceType serviceType) {
        ServiceItemType type = EntityFactory.create(ServiceItemType.class);
        type.name().setValue(name);
        type.serviceType().setValue(serviceType);
        addItem(type);
        return type;
    }

}
