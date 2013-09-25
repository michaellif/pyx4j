/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-09
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.server.services.building;

import com.pyx4j.entity.server.AbstractCrudServiceDtoImpl;
import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.crm.rpc.services.building.ParkingCrudService;
import com.propertyvista.domain.property.asset.Parking;
import com.propertyvista.dto.ParkingDTO;

public class ParkingCrudServiceImpl extends AbstractCrudServiceDtoImpl<Parking, ParkingDTO> implements ParkingCrudService {

    public ParkingCrudServiceImpl() {
        super(Parking.class, ParkingDTO.class);
    }

    @Override
    protected void bind() {
        bindCompleteObject();
    }

    @Override
    protected ParkingDTO init(InitializationData initializationData) {
        ParkingDTO parking = EntityFactory.create(ParkingDTO.class);

        // do not allow null members!
        parking.totalSpaces().setValue(0);
        parking.disabledSpaces().setValue(0);
        parking.regularSpaces().setValue(0);
        parking.wideSpaces().setValue(0);
        parking.narrowSpaces().setValue(0);

        return parking;
    }
}
