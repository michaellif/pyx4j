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
package com.propertyvista.server.common.security;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.domain.company.Employee;
import com.propertyvista.domain.company.Portfolio;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.security.CrmUser;
import com.propertyvista.domain.security.CrmUserBuildings;
import com.propertyvista.server.jobs.TaskRunner;

public class CrmUserBuildingDatasetAccessBuilder {

    public static void updateAccessList(CrmUser user) {
        EntityQueryCriteria<Employee> criteria = EntityQueryCriteria.create(Employee.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().user(), user));
        final Employee employee = Persistence.service().retrieve(criteria);
        if (employee == null) {
            throw new Error("Employee not found for CrmUser " + user.getPrimaryKey());
        }
        Persistence.service().retrieveMember(employee.portfolios());
        Persistence.service().retrieveMember(employee.buildingAccess());

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

                List<Building> haveAccessToBuilding = new ArrayList<Building>();
                for (Building b : employee.buildingAccess()) {
                    haveAccessToBuilding.add(b);
                }
                for (Portfolio portfolio : employee.portfolios()) {
                    for (Building b : portfolio.buildings()) {
                        haveAccessToBuilding.add(b);
                    }
                }

                for (Building b : haveAccessToBuilding) {
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
                for (Building b : unConfirmed) {
                    Persistence.service().delete(userBuildingMap.get(b));
                }
                Persistence.service().commit();
                return null;
            }
        });

    }
}
