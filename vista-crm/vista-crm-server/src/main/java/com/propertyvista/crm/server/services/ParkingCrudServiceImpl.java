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
package com.propertyvista.crm.server.services;

import com.propertyvista.crm.rpc.services.ParkingCrudService;
import com.propertyvista.domain.property.asset.Parking;
import com.propertyvista.dto.ParkingDTO;

public class ParkingCrudServiceImpl extends GenericCrudServiceDtoImpl<Parking, ParkingDTO> implements ParkingCrudService {

    public ParkingCrudServiceImpl() {
        super(Parking.class, ParkingDTO.class);
    }
}
