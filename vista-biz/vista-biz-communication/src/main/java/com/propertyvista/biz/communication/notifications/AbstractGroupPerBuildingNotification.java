/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Oct 16, 2013
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.biz.communication.notifications;

import java.util.ArrayList;
import java.util.List;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;

import com.propertyvista.domain.company.Notification.NotificationType;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.tenant.lease.Lease;

public abstract class AbstractGroupPerBuildingNotification extends AbstractNotification {

    private final Building buildingId;

    private final List<Lease> leaseIds = new ArrayList<Lease>();

    protected AbstractGroupPerBuildingNotification(NotificationType type, Lease leaseId) {
        super(type);
        this.leaseIds.add(leaseId);

        EntityQueryCriteria<Building> criteria = EntityQueryCriteria.create(Building.class);
        criteria.eq(criteria.proto().units().$()._Leases(), leaseId);
        buildingId = Persistence.service().retrieve(criteria, AttachLevel.IdOnly);
    }

    public Building getBuildingId() {
        return buildingId;
    }

    protected List<Lease> getLeaseIds() {
        return leaseIds;
    }

    @Override
    public boolean aggregate(AbstractNotification other) {
        if (buildingId.equals(((AbstractGroupPerBuildingNotification) other).buildingId)) {
            this.leaseIds.addAll(((AbstractGroupPerBuildingNotification) other).leaseIds);
            return true;
        } else {
            return false;
        }
    }
}
