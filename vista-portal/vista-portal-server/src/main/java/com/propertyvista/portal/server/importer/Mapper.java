/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 21, 2011
 * @author dmitry
 * @version $Id$
 */
package com.propertyvista.portal.server.importer;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.propertvista.generator.util.CommonsGenerator;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.domain.contact.Address;
import com.propertyvista.domain.contact.Email;
import com.propertyvista.domain.property.asset.AreaMeasurementUnit;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.building.BuildingInfo;
import com.propertyvista.domain.property.asset.building.BuildingInfo.StructureType;
import com.propertyvista.domain.property.asset.unit.AptUnitOccupancy;
import com.propertyvista.domain.property.asset.unit.AptUnitType;
import com.propertyvista.dto.FloorplanDTO;
import com.propertyvista.portal.server.importer.bean.City;
import com.propertyvista.portal.server.importer.bean.Property;
import com.propertyvista.portal.server.importer.bean.Region;
import com.propertyvista.portal.server.importer.bean.Residential;
import com.propertyvista.portal.server.importer.bean.Room;
import com.propertyvista.portal.server.importer.csv.AvailableUnit;
import com.propertyvista.server.common.generator.Model;
import com.propertyvista.server.common.generator.UnitRelatedData;
import com.propertyvista.server.common.reference.SharedData;

public class Mapper {
    private static final Logger log = LoggerFactory.getLogger(Mapper.class);

    private List<AvailableUnit> availableUnits = new ArrayList<AvailableUnit>();

    private final Model model;

    public Mapper(Model model) {
        this.model = model;
    }

    public void load(Residential residential, List<AvailableUnit> availableUnits) {
        this.availableUnits = availableUnits;
        log.info("Mapping residential");

        for (Region region : residential.getRegions()) {
            create(region);
        }

        log.info("-------------\n\n");
        log.info("" + model.getBuildings().get(0));
        log.info("-------------");
        log.info("" + model.getUnits().get(0));
        log.info("-------------");
        log.info("" + model.getFloorplans().get(0));
        // for (Building building : buildings) {
        // log.info("\n" + building);
        // }
        //
        // for (AptUnit unit : units) {
        // log.info("\n" + unit);
        // }

        log.info(model.getBuildings().size() + " buildings");
        log.info(model.getUnits().size() + " units");
        log.info(model.getFloorplans().size() + " floorplans");
    }

    private void create(Region region) {
        for (City city : region.getCities()) {
            create(city);
        }
    }

    private void create(City city) {
        for (Property property : city.getProperties()) {
            create(property);
        }
    }

    private void create(Property property) {
        Building building = EntityFactory.create(Building.class);

        building.propertyCode().setValue(property.getCode());
        building.info().name().setValue(property.getName());
        building.info().structureType().setValue(mapStructureType(property.getType()));
        building.info().type().setValue(BuildingInfo.Type.residential);

        for (Room room : property.getRooms().getRooms()) {
            createFloorplan(property, room, building);
        }

        building.info().address().set(mapAddress(property.getAddress()));

        building.contacts().email().set(CommonsGenerator.createEmail(property.getContact().getEmail(), Email.Type.other));

        building.contacts().phones().add(CommonsGenerator.createPhone(property.getContact().getTel()));

        building.contacts().website().setValue(property.getWebsite());

        model.getBuildings().add(building);

        // find available units for this building
        List<AvailableUnit> buildingUnits = new ArrayList<AvailableUnit>();
        for (AvailableUnit unit : availableUnits) {
            if (unit.getPropertyCode().equals(property.getCode())) {
                buildingUnits.add(unit);
            }
        }

        // create vista units for available units
        for (AvailableUnit availableUnit : buildingUnits) {
            createUnit(property, availableUnit, building);
        }
        // int numFloors = property.getFloors() == null
        // || property.getFloors() == 0 ? 1 : property.getFloors();
        // for (int i = 0; i < property.getUnitcount(); i++) {
        // int floor = i % numFloors;
        // createUnit(property, floor);
        // }
    }

    private void createUnit(Property property, AvailableUnit availableUnit, Building building) {
        UnitRelatedData unit = EntityFactory.create(UnitRelatedData.class);

        AptUnitOccupancy occupancy = EntityFactory.create(AptUnitOccupancy.class);
        occupancy.status().setValue(AptUnitOccupancy.Status.available);
        occupancy.dateFrom().setValue(new LogicalDate(availableUnit.getAvailable().getTime()));
        unit.occupancies().add(occupancy);
        unit.availableForRent().setValue(occupancy.dateFrom().getValue()); // for consistency

        unit.belongsTo().set(building);
        unit.info().type().setValue(mapUnitType(availableUnit.getType()));
        unit.info().typeDescription().setValue(availableUnit.getDescription());
        unit.info().number().setValue(availableUnit.getUnitNumber());
        unit.info().area().setValue(availableUnit.getArea());
        unit.info().areaUnits().setValue(AreaMeasurementUnit.sqFeet);
        unit.financial().unitRent().setValue(availableUnit.getRent());

        // unit.info().floor().setValue(floor);

        model.getUnits().add(unit);
    }

    private void createFloorplan(Property property, Room room, Building building) {
        FloorplanDTO floorplan = EntityFactory.create(FloorplanDTO.class);

        floorplan.building().set(building);
        //floorplan.name().setValue(room.getName());
        floorplan.name().setValue(room.getDisplay());

        String filenamePart = "";
        if (room.getName().equals("1bdrm")) {
            floorplan.bedrooms().setValue(1d);
            filenamePart = "0101";
        } else if (room.getName().equals("2bdrm")) {
            floorplan.bedrooms().setValue(2d);
            filenamePart = "0102";
        } else {
            floorplan.bedrooms().setValue(3d);
        }

        floorplan.bathrooms().setValue(1d);

        // Removed, now we use only generated images for preloader
//        String filename = property.getCode() + "-" + filenamePart + ".jpg";
//        Picture picture = PictureUtil.loadPicture(filename, Mapper.class);
//        if (picture != null) {
//            log.info("Loaded image [" + filename + "]");
//            floorplan.pictures().add(picture);
//        }

        model.getFloorplans().add(floorplan);
    }

    private static AptUnitType mapUnitType(String type) {
        if (type == null || type.trim().isEmpty()) {
            return null;
        }
        // for now map bathrooms to bedrooms
        if (type.equals("1bdrm")) {
            return AptUnitType.oneBedroom;
        } else if (type.equals("2bdrm")) {
            return AptUnitType.twoBedroom;
        } else if (type.equals("3bdrm")) {
            return AptUnitType.threeBedroom;
        } else if (type.equals("1.den")) {
            return AptUnitType.oneBedroomAndDen;
        }
        log.info("Unknown value [" + type + "]");
        return null;
    }

    private static Address mapAddress(com.propertyvista.portal.server.importer.bean.Address from) {
        Address to = EntityFactory.create(Address.class);

        String street = from.getStreet();
        String streetNumber = street.substring(0, street.indexOf(' '));
        String streetName = street.substring(streetNumber.length() + 1);

        to.streetNumber().setValue(streetNumber);
        to.streetName().setValue(streetName);
        to.city().setValue(from.getCity());
        to.province().set(SharedData.findProvinceByCode(from.getPrv()));
        to.country().set(to.province().country());
        to.postalCode().setValue(from.getPost());

        to.addressType().setValue(Address.AddressType.property);

        return to;
    }

    private static StructureType mapStructureType(String type) {
        if (type.trim().isEmpty()) {
            return null;
        } else if (type.equals("High Rise")) {
            return StructureType.highRise;
        } else if (type.equals("Low Rise")) {
            return StructureType.lowRise;
        } else if (type.equals("Townhouse")) {
            return StructureType.townhouse;
        } else if (type.equals("Walk Up")) {
            return StructureType.walkUp;
        }
        log.info("Unknown structure type [" + type + "]");
        return null;
    }
}
