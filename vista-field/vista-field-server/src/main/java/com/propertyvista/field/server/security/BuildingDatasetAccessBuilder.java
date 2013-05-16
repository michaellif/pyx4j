/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 11, 2012
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.field.server.security;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.domain.company.Employee;
import com.propertyvista.domain.company.Portfolio;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.security.CrmUser;
import com.propertyvista.domain.security.CrmUserBuildings;
import com.propertyvista.server.jobs.TaskRunner;

public class BuildingDatasetAccessBuilder {

    public static void updateAccessList(CrmUser user) {
        EntityQueryCriteria<Employee> criteria = EntityQueryCriteria.create(Employee.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().user(), user));
        final Employee employee = Persistence.service().retrieve(criteria);
        if (employee == null) {
            throw new Error("Employee not found for CrmUser " + user.getPrimaryKey());
        }
        Persistence.service().retrieve(employee.portfolios());

        //TODO move this code to more generic update function
        EntityQueryCriteria<CrmUserBuildings> userBuildingCriteria = EntityQueryCriteria.create(CrmUserBuildings.class);
        userBuildingCriteria.add(PropertyCriterion.eq(userBuildingCriteria.proto().user(), employee.user()));
        List<CrmUserBuildings> ubl = Persistence.service().query(userBuildingCriteria);
        final Map<Building, CrmUserBuildings> userBuildingMap = new HashMap<Building, CrmUserBuildings>();
        final Set<Building> unConfirmed = new HashSet<Building>();
        for (CrmUserBuildings ub : ubl) {
            userBuildingMap.put(ub.building(), ub);
            unConfirmed.add(ub.building());
        }
        TaskRunner.runAutonomousTransation(new Callable<Void>() {
            @Override
            public Void call() {
                for (Portfolio portfolio : employee.portfolios()) {
                    for (Building b : portfolio.buildings()) {
                        if (userBuildingMap.containsKey(b)) {
                            unConfirmed.remove(b);
                        } else {
                            CrmUserBuildings ub = EntityFactory.create(CrmUserBuildings.class);
                            ub.building().set(b);
                            ub.user().set(employee.user());
                            Persistence.service().persist(ub);
                            userBuildingMap.put(ub.building(), ub);
                        }
                    }
                }
                for (Building b : unConfirmed) {
                    Persistence.service().delete(userBuildingMap.get(b));
                }
                Persistence.service().commit();
                return null;
            }
        });

    }
}
