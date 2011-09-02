/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 10, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.server.ptapp.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.server.PersistenceServicesFactory;
import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.portal.rpc.ptapp.dto.UnitInfoDTO;
import com.propertyvista.portal.rpc.ptapp.services.ApartmentService;
import com.propertyvista.portal.server.ptapp.PtAppContext;

public class ApartmentServiceImpl implements ApartmentService {

    private final static Logger log = LoggerFactory.getLogger(ApartmentServiceImpl.class);

    @Override
    public void retrieve(AsyncCallback<UnitInfoDTO> callback, Key tenantId) {

        Lease lease = PtAppContext.getCurrentLease();
        if (!PersistenceServicesFactory.getPersistenceService().retrieve(lease)) {
            throw new Error("There is no current Lease set!");
        }
        if (!PersistenceServicesFactory.getPersistenceService().retrieve(lease.unit())) {
            throw new Error("There is no Unit selected!");
        }
        if (!PersistenceServicesFactory.getPersistenceService().retrieve(lease.unit().floorplan())) {
            throw new Error("There is no unit Floorplan data!");
        }

        // fill DTO:
        UnitInfoDTO unitInfo = EntityFactory.create(UnitInfoDTO.class);
        unitInfo.name().setValue(lease.unit().floorplan().name().getValue());
        unitInfo.beds().setValue(lease.unit().floorplan().bedrooms().getStringView());
        if (lease.unit().floorplan().dens().getValue() > 0) {
            unitInfo.beds().setValue(unitInfo.beds().getValue() + " + " + lease.unit().floorplan().dens().getStringView() + " den(s)");
        }
        unitInfo.baths().setValue(lease.unit().floorplan().bathrooms().getStringView());
        if (lease.unit().floorplan().halfBath().getValue() > 0) {
            unitInfo.baths().setValue(unitInfo.baths().getValue() + " + " + lease.unit().floorplan().halfBath().getStringView() + " half bath(s)");
        }

        unitInfo.number().setValue(lease.unit().info().number().getValue());
        unitInfo.area().setValue(lease.unit().info().area().getStringView() + " " + lease.unit().info().areaUnits().getStringView());

        callback.onSuccess(unitInfo);
    }

    @Override
    public void save(AsyncCallback<UnitInfoDTO> callback, UnitInfoDTO editableEntity) {
        callback.onSuccess(null);
    }
}
