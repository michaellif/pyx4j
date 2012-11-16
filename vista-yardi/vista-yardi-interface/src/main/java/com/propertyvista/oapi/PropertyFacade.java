/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 16, 2012
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.oapi;

import java.util.List;

import com.pyx4j.entity.server.IEntityPersistenceService;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.server.contexts.NamespaceManager;

import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.oapi.marshaling.BuildingMarshaller;
import com.propertyvista.oapi.model.BuildingsRS;

public class PropertyFacade {

    private static final IEntityPersistenceService service;

    static {
        service = Persistence.service();
    }

    public static BuildingsRS listAllBuildings() {

        //TODO
        NamespaceManager.setNamespace("vista");

        EntityQueryCriteria<Building> buildingCriteria = EntityQueryCriteria.create(Building.class);
        buildingCriteria.asc(buildingCriteria.proto().propertyCode());
        List<Building> buildings = service.query(buildingCriteria);

        BuildingsRS buildingsRs = new BuildingsRS();

        for (Building building : buildings) {
            BuildingMarshaller marshaller = new BuildingMarshaller();
            buildingsRs.buildings.add(marshaller.unmarshal(building));
        }

        return buildingsRs;
    }
}
