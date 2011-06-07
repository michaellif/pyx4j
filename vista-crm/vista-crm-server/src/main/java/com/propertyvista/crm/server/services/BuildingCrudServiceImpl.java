/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-21
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.crm.server.services;

import com.pyx4j.entity.server.PersistenceServicesFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.crm.rpc.services.BuildingCrudService;
import com.propertyvista.domain.property.asset.Boiler;
import com.propertyvista.domain.property.asset.Elevator;
import com.propertyvista.domain.property.asset.LockerArea;
import com.propertyvista.domain.property.asset.Parking;
import com.propertyvista.domain.property.asset.Roof;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.building.BuildingAmenity;
import com.propertyvista.dto.BoilerDTO;
import com.propertyvista.dto.BuildingDTO;
import com.propertyvista.dto.ElevatorDTO;
import com.propertyvista.dto.LockerAreaDTO;
import com.propertyvista.dto.ParkingDTO;
import com.propertyvista.dto.RoofDTO;

public class BuildingCrudServiceImpl extends GenericCrudServiceDtoImpl<Building, BuildingDTO> implements BuildingCrudService {

    public BuildingCrudServiceImpl() {
        super(Building.class, BuildingDTO.class);
    }

    @Override
    protected void enhanceRetrieveDTO(Building in, BuildingDTO dto) {

        EntityQueryCriteria<BuildingAmenity> amenitysCriteria = EntityQueryCriteria.create(BuildingAmenity.class);
        amenitysCriteria.add(PropertyCriterion.eq(amenitysCriteria.proto().belongsTo(), in));
        for (BuildingAmenity amenity : PersistenceServicesFactory.getPersistenceService().query(amenitysCriteria)) {
            dto.amenities().add(amenity);
        }

        EntityQueryCriteria<Elevator> elevatorsCriteria = EntityQueryCriteria.create(Elevator.class);
        elevatorsCriteria.add(PropertyCriterion.eq(elevatorsCriteria.proto().belongsTo(), in));
        for (Elevator elevator : PersistenceServicesFactory.getPersistenceService().query(elevatorsCriteria)) {
            dto.elevators().add(GenericConverter.convertDBO2DTO(elevator, ElevatorDTO.class));
        }

        EntityQueryCriteria<Boiler> boilersCriteria = EntityQueryCriteria.create(Boiler.class);
        boilersCriteria.add(PropertyCriterion.eq(boilersCriteria.proto().belongsTo(), in));
        for (Boiler boiler : PersistenceServicesFactory.getPersistenceService().query(boilersCriteria)) {
            dto.boilers().add(GenericConverter.convertDBO2DTO(boiler, BoilerDTO.class));
        }

        EntityQueryCriteria<Roof> roofsCriteria = EntityQueryCriteria.create(Roof.class);
        roofsCriteria.add(PropertyCriterion.eq(roofsCriteria.proto().belongsTo(), in));
        for (Roof roof : PersistenceServicesFactory.getPersistenceService().query(roofsCriteria)) {
            dto.roofs().add(GenericConverter.convertDBO2DTO(roof, RoofDTO.class));
        }

        EntityQueryCriteria<Parking> parkingsCriteria = EntityQueryCriteria.create(Parking.class);
        parkingsCriteria.add(PropertyCriterion.eq(parkingsCriteria.proto().belongsTo(), in));
        for (Parking parking : PersistenceServicesFactory.getPersistenceService().query(parkingsCriteria)) {
            dto.parkings().add(GenericConverter.convertDBO2DTO(parking, ParkingDTO.class));
        }

        EntityQueryCriteria<LockerArea> lockerAreasCriteria = EntityQueryCriteria.create(LockerArea.class);
        lockerAreasCriteria.add(PropertyCriterion.eq(lockerAreasCriteria.proto().belongsTo(), in));
        for (LockerArea lockerArea : PersistenceServicesFactory.getPersistenceService().query(lockerAreasCriteria)) {
            dto.lockers().add(GenericConverter.convertDBO2DTO(lockerArea, LockerAreaDTO.class));
        }
    }
}
