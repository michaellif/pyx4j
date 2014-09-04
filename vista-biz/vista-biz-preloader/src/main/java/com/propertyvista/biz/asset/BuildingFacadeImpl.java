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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.biz.communication.OperationsNotificationFacade;
import com.propertyvista.biz.occupancy.OccupancyFacade;
import com.propertyvista.biz.policy.IdAssignmentFacade;
import com.propertyvista.biz.policy.PolicyFacade;
import com.propertyvista.biz.preloader.DefaultProductCatalogFacade;
import com.propertyvista.biz.system.AuditFacade;
import com.propertyvista.domain.PublicVisibilityType;
import com.propertyvista.domain.pmc.IntegrationSystem;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.server.common.reference.PublicDataUpdater;
import com.propertyvista.shared.config.VistaFeatures;

public class BuildingFacadeImpl implements BuildingFacade {

    private static final Logger log = LoggerFactory.getLogger(BuildingFacadeImpl.class);

    @Override
    public Building persist(Building building) {
        boolean isNewBuilding = building.updated().isNull();
        if (building.id().isNull()) {
            ServerSideFactory.create(IdAssignmentFacade.class).assignId(building);
            if (building.marketing().visibility().isNull()) {
                building.marketing().visibility().setValue(PublicVisibilityType.global);
            }
        }
        boolean statusModified = false;
        if (building.suspended().isNull()) {
            building.suspended().setValue(false);
        }
        if (building.getPrimaryKey() != null) {
            // compare with the original status
            Building orig = Persistence.service().retrieve(Building.class, building.getPrimaryKey());
            if (!orig.suspended().equals(building.suspended())) {
                statusModified = true;
            }
        }
        if (building.integrationSystemId().isNull()) {
            building.integrationSystemId().setValue(IntegrationSystem.internal);
        }
        if (building.defaultProductCatalog().isNull()) {
            building.defaultProductCatalog().setValue(!VistaFeatures.instance().yardiIntegration());
        }
        Persistence.service().merge(building);

        if (statusModified) {
            notifySuspended(building);
        }

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
        log.info("Building {} Suspended", building);
        building.suspended().setValue(true);
        Persistence.service().merge(building);
        notifySuspended(building);

    }

    private void notifySuspended(Building building) {
        ServerSideFactory.create(OperationsNotificationFacade.class).buildingSuspended(building);
        ServerSideFactory.create(AuditFacade.class).updated(building, building.suspended().getValue(false) ? "Suspended" : "Unsuspended");
    }

    @Override
    public boolean isSuspend(Building buildingId) {
        EntityQueryCriteria<Building> criteria = EntityQueryCriteria.create(Building.class);
        criteria.eq(criteria.proto().id(), buildingId.getPrimaryKey());
        return isSuspend(criteria);
    }

    @Override
    public boolean isSuspend(EntityQueryCriteria<Building> criteria) {
        criteria.eq(criteria.proto().suspended(), true);
        return Persistence.service().count(criteria) > 0;
    }

}
