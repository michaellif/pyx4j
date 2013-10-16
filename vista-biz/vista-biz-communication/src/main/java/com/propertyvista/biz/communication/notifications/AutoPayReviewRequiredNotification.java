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

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;

import com.propertyvista.biz.communication.CommunicationFacade;
import com.propertyvista.domain.company.Employee;
import com.propertyvista.domain.company.Notification;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.tenant.lease.Lease;

public class AutoPayReviewRequiredNotification extends AbstractNotification {

    private final Building buildingId;

    private final List<Lease> leaseIds = new ArrayList<Lease>();

    public AutoPayReviewRequiredNotification(Lease leaseId) {
        super(Notification.NotificationType.AutoPayReviewRequired);
        this.leaseIds.add(leaseId);

        EntityQueryCriteria<Building> criteria = EntityQueryCriteria.create(Building.class);
        criteria.eq(criteria.proto().units().$()._Leases(), leaseId);
        buildingId = Persistence.service().retrieve(criteria, AttachLevel.IdOnly);
    }

    @Override
    public void send() {
        List<Employee> employees = NotificationsUtils.getNotificationTraget(buildingId, Notification.NotificationType.AutoPayReviewRequired);
        if (!employees.isEmpty()) {
            ServerSideFactory.create(CommunicationFacade.class).sendAutoPayReviewRequiredNotification(NotificationsUtils.toEmails(employees), leaseIds);
        }
    }

    @Override
    public boolean aggregate(AbstractNotification other) {
        if (buildingId.equals(((AutoPayReviewRequiredNotification) other).buildingId)) {
            this.leaseIds.addAll(((AutoPayReviewRequiredNotification) other).leaseIds);
            return true;
        } else {
            return false;
        }
    }
}
