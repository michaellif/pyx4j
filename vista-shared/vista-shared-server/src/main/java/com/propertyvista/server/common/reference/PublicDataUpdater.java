/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-06-06
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.server.common.reference;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.domain.PublicVisibilityType;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.ref.City;
import com.propertyvista.domain.ref.ISOCountry;
import com.propertyvista.domain.ref.ISOProvince;

public class PublicDataUpdater {

    /**
     * Create data used on public portal, e.g. optimization
     */
    public static void updateIndexData(Building building) {
        if (building.info().location().isNull() || building.info().location().getValue().getLat() == 0) {
            return;
        }

        EntityQueryCriteria<Building> buildingCriteria = EntityQueryCriteria.create(Building.class);
        buildingCriteria.add(PropertyCriterion.eq(buildingCriteria.proto().info().address().city(), building.info().address().city().getValue()));
        buildingCriteria.add(PropertyCriterion.eq(buildingCriteria.proto().marketing().visibility(), PublicVisibilityType.global));
        boolean visibleBuildingExists = Persistence.service().exists(buildingCriteria);

        ISOCountry country = building.info().address().country().getValue();
        ISOProvince prov = ISOProvince.forName(building.info().address().province().getValue(), country);
        EntityQueryCriteria<City> criteriaCity = EntityQueryCriteria.create(City.class);
        criteriaCity.eq(criteriaCity.proto().name(), building.info().address().city().getValue());
        criteriaCity.eq(criteriaCity.proto().province(), prov);
        City city = Persistence.service().retrieve(criteriaCity);
        if (city != null) {
            if (city.hasProperties().getValue(false) != visibleBuildingExists) {
                city.hasProperties().setValue(visibleBuildingExists);
                Persistence.service().persist(city);
            }
        } else {
            city = EntityFactory.create(City.class);
            city.name().setValue(building.info().address().city().getValue());
            city.province().setValue(prov);
            city.location().setValue(building.info().location().getValue());
            city.hasProperties().setValue(visibleBuildingExists);
            Persistence.service().persist(city);
        }
    }
}
