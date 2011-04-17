/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Apr 17, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.crm.server.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.propertyvista.crm.rpc.services.BuildingCrudService;
import com.propertyvista.portal.domain.Building;
import com.propertyvista.portal.domain.DemoData;

import com.pyx4j.entity.server.PersistenceServicesFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

public class BuildingCrudServiceImpl implements BuildingCrudService {

    private final static Logger log = LoggerFactory.getLogger(BuildingCrudServiceImpl.class);

    @Override
    public void create(AsyncCallback<Building> callback, Building building) {
        PersistenceServicesFactory.getPersistenceService().persist(building);
        callback.onSuccess(building);
    }

    @Override
    public void retrieve(AsyncCallback<Building> callback, long buildingId) {
        Building building = PersistenceServicesFactory.getPersistenceService().retrieve(Building.class, buildingId);
        callback.onSuccess(building);
    }

    @Override
    public void save(AsyncCallback<Building> callback, Building building) {
        PersistenceServicesFactory.getPersistenceService().merge(building);
        callback.onSuccess(building);
    }

    @Override
    public void getTestBuildingNomberOne(AsyncCallback<Building> callback) {
        EntityQueryCriteria<Building> buildingCriteria = EntityQueryCriteria.create(Building.class);
        buildingCriteria.add(PropertyCriterion.eq(buildingCriteria.proto().propertyCode(), DemoData.REGISTRATION_DEFAULT_PROPERTY_CODE));
        Building building = PersistenceServicesFactory.getPersistenceService().retrieve(buildingCriteria);
        log.info("this demo building {}", building);
        callback.onSuccess(building);
    }

}
