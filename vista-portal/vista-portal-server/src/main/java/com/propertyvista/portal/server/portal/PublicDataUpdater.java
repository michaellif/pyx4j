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
package com.propertyvista.portal.server.portal;

import com.pyx4j.entity.server.PersistenceServicesFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.common.domain.ref.City;
import com.propertyvista.domain.property.asset.building.Building;

public class PublicDataUpdater {

    /**
     * Create data used on public portal, e.g. optimization
     */
    public static void updateIndexData(Building building) {
        EntityQueryCriteria<City> criteriaCity = EntityQueryCriteria.create(City.class);
        criteriaCity.add(PropertyCriterion.eq(criteriaCity.proto().name(), building.info().address().city().getValue()));
        //TODO verify Province
        City city = PersistenceServicesFactory.getPersistenceService().retrieve(criteriaCity);
        if (city != null) {
            if (!city.hasProperties().isBooleanTrue()) {
                city.hasProperties().setValue(Boolean.TRUE);
                PersistenceServicesFactory.getPersistenceService().persist(city);
            }
        }
    }

}
