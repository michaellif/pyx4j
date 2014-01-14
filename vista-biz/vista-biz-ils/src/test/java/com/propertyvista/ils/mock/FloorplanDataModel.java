/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 21, 2013
 * @author smolka
 * @version $Id$
 */
package com.propertyvista.ils.mock;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.essentials.server.preloader.DataGenerator;

import com.propertyvista.domain.property.asset.AreaMeasurementUnit;
import com.propertyvista.domain.property.asset.Floorplan;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.test.mock.MockDataModel;

public class FloorplanDataModel extends MockDataModel<Floorplan> {

    @Override
    protected void generate() {
        // TODO Auto-generated method stub

    }

    public List<Floorplan> createFloorplans(Building building, int num) {
        List<Floorplan> floorplans = new ArrayList<Floorplan>();
        Set<String> uniqueFloorplanNames = new HashSet<String>();

        for (int i = 0; i < num; i++) {
            Floorplan floorplan;
            int attemptCounter = 0;
            do {
                attemptCounter++;
                if (attemptCounter > 10) {
                    throw new Error("Infinite loop protection");
                }
                floorplan = createFloorplan();
            } while (uniqueFloorplanNames.contains(floorplan.name().getValue()));

            uniqueFloorplanNames.add(floorplan.name().getValue());
            building.floorplans().setAttachLevel(AttachLevel.Attached);
            building.floorplans().add(floorplan);

            floorplans.add(floorplan);
        }
        Persistence.service().persist(floorplans);

        createUnits(building, floorplans, 2, 2);
        return floorplans;
    }

    private List<AptUnit> createUnits(Building building, List<Floorplan> floorplans, int numFloors, int numUnitsPerFloor) {
        List<AptUnit> units = new ArrayList<AptUnit>();
        for (int floor = 1; floor < numFloors + 1; floor++) {
            for (int j = 0; j < numUnitsPerFloor + 1; j++) {

                String suiteNumber = "#" + (floor * 100 + j);
                Floorplan floorplan = floorplans.get(j % floorplans.size());
                if (floorplan == null) {
                    throw new IllegalStateException("No floorplan");
                }
                AptUnit unit = createUnit(building, suiteNumber, floor, 1200d, floorplan);
                units.add(unit);

            }
        }
        Persistence.service().persist(units);

        return units;
    }

    private Floorplan createFloorplan() {
        Floorplan floorplan = EntityFactory.create(Floorplan.class);

        floorplan.description().setValue("floorplan description 1");

        floorplan.floorCount().setValue(1 + DataGenerator.randomInt(2));
        floorplan.bedrooms().setValue(1 + DataGenerator.randomInt(4));
        floorplan.dens().setValue(DataGenerator.randomInt(2));
        floorplan.bathrooms().setValue(1 + DataGenerator.randomInt(3));
        floorplan.halfBath().setValue(DataGenerator.randomInt(2));
        floorplan.area().setValue(DataGenerator.randomDouble(300.0, 3));
        floorplan.areaUnits().setValue(DataGenerator.randomEnum(AreaMeasurementUnit.class));

        floorplan.marketingName().setValue(createMarketingName(floorplan.bedrooms().getValue(), floorplan.dens().getValue()));
        floorplan.name().setValue(
                floorplan.marketingName().getValue() + ' ' + floorplan.bathrooms().getValue() + ' ' + ((char) (1 + DataGenerator.randomInt(5) + 'A')));

        floorplan.counters()._unitCount().setValue(0);
        floorplan.counters()._marketingUnitCount().setValue(0);

        return floorplan;
    }

    private String createMarketingName(int bedrooms, int dens) {
        String marketingName;

        if (bedrooms == 0) {
            marketingName = "Bachelor";
        } else if (bedrooms > 4) {
            marketingName = "Luxury " + bedrooms + "-bedroom";
        } else {
            marketingName = bedrooms + "-bedroom";
        }
        if (dens > 0) {
            marketingName = marketingName + " + den";
        }

        return marketingName;
    }

    private AptUnit createUnit(Building building, String suiteNumber, int floor, double area, Floorplan floorplan) {
        AptUnit unit = EntityFactory.create(AptUnit.class);
        unit.building().set(building);

        unit.info().floor().setValue(floor);
        unit.info().number().setValue(suiteNumber);

        unit.info()._bedrooms().setValue(floorplan.bedrooms().getValue());
        unit.info()._bathrooms().setValue(floorplan.bathrooms().getValue());

        unit.info().area().setValue(area);
        unit.info().areaUnits().setValue(AreaMeasurementUnit.sqFeet);

        unit.financial()._unitRent().setValue(new BigDecimal(800. + new Random().nextInt(200)));
        unit.financial()._marketRent().setValue(new BigDecimal(900. + new Random().nextInt(200)));

        unit.floorplan().set(floorplan);

        return unit;
    }

}
