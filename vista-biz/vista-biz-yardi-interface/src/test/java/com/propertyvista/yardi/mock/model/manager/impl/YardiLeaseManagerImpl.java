/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 17, 2014
 * @author stanp
 */
package com.propertyvista.yardi.mock.model.manager.impl;

import com.pyx4j.entity.core.EntityFactory;

import com.propertyvista.yardi.mock.model.domain.YardiBuilding;
import com.propertyvista.yardi.mock.model.domain.YardiLease;
import com.propertyvista.yardi.mock.model.domain.YardiUnit;
import com.propertyvista.yardi.mock.model.manager.YardiBuildingManager;
import com.propertyvista.yardi.mock.model.manager.YardiLeaseManager;

public class YardiLeaseManagerImpl implements YardiLeaseManager {

    @Override
    public LeaseBuilder addDefaultLease() {
        YardiLease lease = EntityFactory.create(YardiLease.class);
        YardiBuilding building = YardiMockModelUtils.findBuilding(YardiBuildingManager.DEFAULT_PROPERTY_CODE);
        // TODO - add default lease impl
        return new LeaseBuilderImpl(lease, new BuildingBuilderImpl(building));
    }

    @Override
    public LeaseBuilder addLease(String leaseId, String buildingId, String unitId) {
        assert buildingId != null : "building id cannot be null";
        assert unitId != null : "unit id cannot be null";
        assert leaseId != null : "lease id cannot be null";
        assert leaseId.startsWith("t") : "lease is should start with 't'";

        YardiBuilding building = YardiMockModelUtils.findBuilding(buildingId);
        if (building == null) {
            throw new Error("Building not found: " + buildingId);
        }
        YardiUnit unit = YardiMockModelUtils.findUnit(building, unitId);
        if (unit == null) {
            throw new Error("Unit not found: " + buildingId + ":" + unitId);
        }
        if (YardiMockModelUtils.findLease(building, leaseId) != null) {
            throw new Error("Lease already exists: " + leaseId);
        }

        YardiLease lease = EntityFactory.create(YardiLease.class);
        lease.leaseId().setValue(leaseId);
        lease.unit().set(unit);
        lease.currentRent().set(unit.rent());

        building.leases().add(lease);

        return new LeaseBuilderImpl(lease, new BuildingBuilderImpl(building));
    }

    @Override
    public LeaseBuilder getLease(String leaseId, String buildingId) {
        YardiBuilding building = YardiMockModelUtils.findBuilding(buildingId);
        if (building == null) {
            throw new Error("Building not found: " + buildingId);
        }
        YardiLease lease = YardiMockModelUtils.findLease(building, leaseId);
        if (lease == null) {
            throw new Error("Lease not found: " + leaseId);
        }

        return new LeaseBuilderImpl(lease, new BuildingBuilderImpl(building));
    }
}
