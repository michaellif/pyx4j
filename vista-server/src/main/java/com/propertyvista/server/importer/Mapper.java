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
package com.propertyvista.server.importer;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.propertyvista.domain.Address;
import com.propertyvista.domain.property.asset.Floorplan;
import com.propertyvista.domain.property.asset.Utility;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.building.BuildingInfo.StructureType;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.portal.server.generator.CommonsGenerator;
import com.propertyvista.portal.server.generator.SharedData;
import com.propertyvista.portal.server.importer.bean.City;
import com.propertyvista.portal.server.importer.bean.Include;
import com.propertyvista.portal.server.importer.bean.Property;
import com.propertyvista.portal.server.importer.bean.Region;
import com.propertyvista.portal.server.importer.bean.Residential;
import com.propertyvista.portal.server.importer.bean.Room;
import com.pyx4j.entity.shared.EntityFactory;

public class Mapper {
	private static final Logger log = LoggerFactory.getLogger(Mapper.class);

	private List<Building> buildings = new ArrayList<Building>();

	private List<AptUnit> units = new ArrayList<AptUnit>();

	private List<Floorplan> floorplans = new ArrayList<Floorplan>();

	public void load(Residential residential) {
		log.info("Mapping residential");

		for (Region region : residential.getRegions()) {
			create(region);
		}

		log.info("-------------\n\n");
		log.info("" + buildings.get(0));
		log.info("-------------");
		log.info("" + units.get(0));
		log.info("-------------");
		log.info("" + floorplans.get(0));
		// for (Building building : buildings) {
		// log.info("\n" + building);
		// }
		//
		// for (AptUnit unit : units) {
		// log.info("\n" + unit);
		// }

		log.info(buildings.size() + " buildings");
		log.info(units.size() + " units");
		log.info(floorplans.size() + " floorplans");
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

		building.info().propertyCode().setValue(property.getCode());
		building.info().name().setValue(property.getName());
		building.info().structureType()
				.setValue(mapStructureType(property.getType()));

		for (Room room : property.getRooms().getRooms()) {
			createFloorplan(room);
		}

		int numFloors = property.getFloors() == null
				|| property.getFloors() == 0 ? 1 : property.getFloors();
		for (int i = 0; i < property.getUnitcount(); i++) {
			int floor = i % numFloors;
			createUnit(property, floor);
		}

		building.info().address().set(mapAddress(property.getAddress()));

		building.contacts()
				.email()
				.set(CommonsGenerator.createEmail(property.getContact()
						.getEmail()));

		building.contacts()
				.phoneList()
				.add(CommonsGenerator.createPhone(property.getContact()
						.getTel()));

		building.contacts().website().setValue(property.getWebsite());

		buildings.add(building);
	}

	private void createUnit(Property property, int floor) {
		AptUnit unit = EntityFactory.create(AptUnit.class);

		unit.info().floor().setValue(floor);

		for (Include include : property.getIncludes().getIncludes()) {
			unit.info().utilities().add(mapUtility(include));
		}

		units.add(unit);
	}

	private void createFloorplan(Room room) {
		Floorplan floorplan = EntityFactory.create(Floorplan.class);

		floorplan.name().setValue(room.getName());
		floorplan.description().setValue(room.getDisplay());

		floorplans.add(floorplan);
	}

	private static Utility mapUtility(Include include) {
		Utility utility = EntityFactory.create(Utility.class);

		String name = include.getValue();

		Utility.Type type = null;

		if (name.trim().isEmpty()) {
			type = null;
		} else if (name.equals("Hot Water")) {
			type = Utility.Type.hotWater;
		} else if (name.equals("Heat")) {
			type = Utility.Type.heat;
		} else if (name.equals("Electricity")) { // not sure about this one
			type = Utility.Type.electric;
		} else if (name.equals("Hydro")) { // not sure about this one
			type = Utility.Type.water;
		} else {
			log.info("Unknown utility [" + name + "]");
		}

		utility.type().setValue(type);
		utility.description().setValue(name);

		return utility;
	}

	private static Address mapAddress(
			com.propertyvista.portal.server.importer.bean.Address from) {
		Address to = EntityFactory.create(Address.class);

		// TODO this can be improved by breaking down the street into logical
		// parts
		to.streetName().setValue(from.getStreet());
		to.city().setValue(from.getCity());
		to.province().set(SharedData.findProvinceByCode(from.getPrv()));
		to.country().set(to.province().country());
		to.postalCode().setValue(from.getPost());

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

	public static Logger getLog() {
		return log;
	}

	public List<Building> getBuildings() {
		return buildings;
	}

	public List<AptUnit> getUnits() {
		return units;
	}

	public List<Floorplan> getFloorplans() {
		return floorplans;
	}
}
