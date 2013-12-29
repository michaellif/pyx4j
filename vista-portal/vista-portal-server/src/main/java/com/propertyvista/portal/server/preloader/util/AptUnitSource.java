/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-04-10
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.server.preloader.util;

import java.util.List;

import com.pyx4j.entity.core.criterion.EntityListCriteria;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;

public class AptUnitSource {

    private int buildingNo;

    private int unitNo = 0;

    private List<AptUnit> units = null;

    public AptUnitSource(int startBuildingNo) {
        buildingNo = startBuildingNo;
    }

    public AptUnit next() {
        return nextUnit();
    }

    private AptUnit nextUnit() {
        if ((units == null) || (units.size() == unitNo)) {
            nextBuilding();
        }
        return units.get(unitNo++);
    }

    private void nextBuilding() {
        unitNo = 0;
        EntityListCriteria<Building> bcriteria = EntityListCriteria.create(Building.class);
        bcriteria.asc(bcriteria.proto().propertyCode());
        bcriteria.setPageSize(1);
        bcriteria.setPageNumber(buildingNo++);
        Building building = Persistence.service().retrieve(bcriteria);
        if (building == null) {
            throw new Error("No more building and units available for Tenants. Change configuration!");
        }

        EntityQueryCriteria<AptUnit> ucriteria = new EntityQueryCriteria<AptUnit>(AptUnit.class);
        ucriteria.add(PropertyCriterion.eq(ucriteria.proto().building(), building));
        ucriteria.asc(ucriteria.proto().info().number());
        units = Persistence.service().query(ucriteria);
        if (units.size() == 0) {
            nextBuilding();
        }
    }
}
