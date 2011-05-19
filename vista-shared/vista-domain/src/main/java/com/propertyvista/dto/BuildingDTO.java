/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 19, 2011
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.dto;

import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.shared.IList;

import com.propertyvista.domain.property.asset.BuildingAmenity;
import com.propertyvista.domain.property.asset.building.BuildingContactInfo;
import com.propertyvista.domain.property.asset.building.BuildingFinancial;
import com.propertyvista.domain.property.asset.building.BuildingInfo;
import com.propertyvista.dto.basic.ElevatorBasicDTO;
import com.propertyvista.dto.basic.LockerBasicDTO;
import com.propertyvista.dto.basic.ParkingBasicDTO;
import com.propertyvista.dto.basic.RoofBasicDTO;

@Transient
public interface BuildingDTO extends BuildingInfo, BuildingFinancial, BuildingContactInfo {

    IList<BuildingAmenity> amenities();

    IList<ElevatorBasicDTO> elevators();

    IList<BoilerBasicDTO> boilers();

    IList<RoofBasicDTO> roofs();

    IList<ParkingBasicDTO> parkings();

    IList<LockerBasicDTO> lockers();
}
