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

import com.pyx4j.entity.server.PersistenceServicesFactory;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.ref.City;

public class PublicDataUpdater {

    /**
     * Create data used on public portal, e.g. optimization
     */
    public static void updateIndexData(Building building) {
        if (building.info().address().location().isNull() || building.info().address().location().getValue().getLat() == 0) {
            return;
        }
        EntityQueryCriteria<City> criteriaCity = EntityQueryCriteria.create(City.class);
        criteriaCity.add(PropertyCriterion.eq(criteriaCity.proto().name(), building.info().address().city().getValue()));
        //TODO verify Province
        City city = PersistenceServicesFactory.getPersistenceService().retrieve(criteriaCity);
        if (city != null) {
            if (!city.hasProperties().isBooleanTrue()) {
                city.hasProperties().setValue(Boolean.TRUE);
                PersistenceServicesFactory.getPersistenceService().persist(city);
            }
        } else {
            city = EntityFactory.create(City.class);
            city.name().setValue(building.info().address().city().getValue());
            city.province().set(building.info().address().province());
            city.location().setValue(building.info().address().location().getValue());
            city.hasProperties().setValue(Boolean.TRUE);
            PersistenceServicesFactory.getPersistenceService().persist(city);
        }
    }
}
