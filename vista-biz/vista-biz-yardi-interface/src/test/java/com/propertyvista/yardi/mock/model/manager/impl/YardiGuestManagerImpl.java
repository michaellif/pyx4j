/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 23, 2014
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.yardi.mock.model.manager.impl;

import com.pyx4j.entity.core.EntityFactory;

import com.propertyvista.yardi.mock.model.domain.YardiBuilding;
import com.propertyvista.yardi.mock.model.domain.YardiLease;
import com.propertyvista.yardi.mock.model.manager.YardiGuestManager;

public class YardiGuestManagerImpl implements YardiGuestManager {

    @Override
    public ApplicationBuilder addApplication(String buildingId, String leaseId) {
        assert buildingId != null : "building id cannot be null";
        assert leaseId != null : "lease id cannot be null";

        YardiBuilding building = YardiMockModelUtils.findBuilding(buildingId);
        if (building == null) {
            throw new Error("Building not found: " + buildingId);
        }

        YardiLease lease = YardiMockModelUtils.findLease(building, leaseId);
        if (lease == null) {
            lease = EntityFactory.create(YardiLease.class);
            lease.leaseId().setValue(leaseId);
            building.leases().add(lease);
        }

        return new ApplicationBuilderImpl(lease, building);
    }

    @Override
    public ApplicationBuilder getApplication(String buildingId, String leaseId) {
        assert buildingId != null : "building id cannot be null";
        assert leaseId != null : "lease id cannot be null";

        YardiBuilding building = YardiMockModelUtils.findBuilding(buildingId);
        if (building == null) {
            throw new Error("Building not found: " + buildingId);
        }

        YardiLease lease = YardiMockModelUtils.findLease(building, leaseId);
        if (lease == null) {
            throw new Error("Lease not found: " + leaseId);
        }

        return new ApplicationBuilderImpl(lease, building);
    }

}
