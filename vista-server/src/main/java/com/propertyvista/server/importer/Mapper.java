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

import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.building.BuildingInfo.StructureType;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.portal.server.importer.bean.City;
import com.propertyvista.portal.server.importer.bean.Property;
import com.propertyvista.portal.server.importer.bean.Region;
import com.propertyvista.portal.server.importer.bean.Residential;
import com.pyx4j.entity.shared.EntityFactory;

public class Mapper {
	private static final Logger log = LoggerFactory.getLogger(Mapper.class);

	private List<Building> buildings = new ArrayList<Building>();

	private List<AptUnit> units = new ArrayList<AptUnit>();

	public void load(Residential residential) {
		log.info("Mapping residential");

		for (Region region : residential.getRegions()) {
			create(region);
		}

		log.info("-------------\n\n");
		log.info("" + buildings.get(0));
		log.info("-------------");
		log.info("" + units.get(0));
		// for (Building building : buildings) {
		// log.info("\n" + building);
		// }
		//
		// for (AptUnit unit : units) {
		// log.info("\n" + unit);
		// }

		log.info(buildings.size() + " buildings");
		log.info(units.size() + " units");
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

		int numFloors = property.getFloors() == null
				|| property.getFloors() == 0 ? 1 : property.getFloors();
		for (int i = 0; i < property.getUnitcount(); i++) {
			int floor = i % numFloors;
			createUnit(property, floor);
		}

		buildings.add(building);
	}

	private void createUnit(Property property, int floor) {
		AptUnit unit = EntityFactory.create(AptUnit.class);

		unit.info().floor().setValue(floor);

		units.add(unit);
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
