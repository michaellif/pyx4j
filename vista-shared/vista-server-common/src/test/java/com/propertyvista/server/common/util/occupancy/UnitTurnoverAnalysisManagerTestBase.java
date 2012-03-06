/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 6, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.server.common.util.occupancy;

import org.junit.Before;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;

import com.propertyvista.domain.dashboard.gadgets.availabilityreport.UnitTurnoverStats;
import com.propertyvista.domain.property.asset.Floorplan;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.property.asset.unit.occupancy.AptUnitOccupancySegment;

public class UnitTurnoverAnalysisManagerTestBase {

    private Building building;

    private Floorplan floorplan;

    @Before
    public void setUp() {
        // TODO clear tables
        Persistence.service().delete(EntityQueryCriteria.create(UnitTurnoverStats.class));
        Persistence.service().delete(EntityQueryCriteria.create(AptUnitOccupancySegment.class));
        Persistence.service().delete(EntityQueryCriteria.create(AptUnit.class));
        Persistence.service().delete(EntityQueryCriteria.create(Floorplan.class));
        Persistence.service().delete(EntityQueryCriteria.create(Building.class));

        // define common domain objects
        building = EntityFactory.create(Building.class);
        building.propertyCode().setValue("B1");
        building.info().name().setValue("building-1");
        Persistence.service().persist(building);

        floorplan = EntityFactory.create(Floorplan.class);
        floorplan.bathrooms().setValue(1);
        floorplan.bedrooms().setValue(2);
        floorplan.name().setValue("floorplan-1");
        floorplan.building().set(building);
        Persistence.service().persist(floorplan);
    }

    protected void lease(AptUnit unit, String dateFrom, String dateTo) {
        // TODO
    }

    protected AptUnit unit(long pk) {
        AptUnit unit = EntityFactory.create(AptUnit.class);
        unit.info().number().setValue("unit-" + pk);
        unit.id().setValue(new Key(pk));
        unit.belongsTo().set(building);
        unit.floorplan().set(floorplan);
        Persistence.service().persist(unit);
        return unit;
    }

    protected void recalcTurnovers(String date) {
        // TODO
    }

    protected void expect(String onDate, int turnovers) {
        // TODO
    }
}
