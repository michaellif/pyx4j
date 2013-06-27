/*
 * Pyx4j framework
 * Copyright (C) 2008-2011 pyx4j.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * Created on 2013-01-29
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.biz.asset;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.biz.occupancy.OccupancyFacade;
import com.propertyvista.biz.policy.IdAssignmentFacade;
import com.propertyvista.biz.policy.PolicyFacade;
import com.propertyvista.biz.preloader.DefaultProductCatalogFacade;
import com.propertyvista.domain.PublicVisibilityType;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.server.common.reference.PublicDataUpdater;

public class BuildingFacadeImpl implements BuildingFacade {

    @Override
    public Building persist(Building building) {
        boolean isNewBuilding = building.updated().isNull();
        if (building.id().isNull()) {
            ServerSideFactory.create(IdAssignmentFacade.class).assignId(building);
            if (building.marketing().visibility().isNull()) {
                building.marketing().visibility().setValue(PublicVisibilityType.global);
            }
        }
        Persistence.service().merge(building);

        if (isNewBuilding) {
            ServerSideFactory.create(DefaultProductCatalogFacade.class).createFor(building);
            ServerSideFactory.create(DefaultProductCatalogFacade.class).persistFor(building);
        } else {
            ServerSideFactory.create(DefaultProductCatalogFacade.class).updateFor(building);
        }

        PublicDataUpdater.updateIndexData(building);
        ServerSideFactory.create(PolicyFacade.class).resetPolicyCache();
        return building;
    }

    @Override
    public AptUnit persist(AptUnit unit) {
        boolean isNewUnit = unit.id().isNull();
        Persistence.service().merge(unit);

        if (isNewUnit) {
            ServerSideFactory.create(OccupancyFacade.class).setupNewUnit((AptUnit) unit.createIdentityStub());
            ServerSideFactory.create(DefaultProductCatalogFacade.class).addUnit(unit.building(), unit);
        } else {
            ServerSideFactory.create(DefaultProductCatalogFacade.class).updateUnit(unit.building(), unit);
        }

        return unit;
    }

    @Override
    public void suspend(Building building) {
        // TODO Auto-generated method stub

    }

}
