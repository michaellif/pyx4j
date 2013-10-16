/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-06-19
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.biz.communication.notifications;

import java.util.ArrayList;
import java.util.List;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.OrCriterion;

import com.propertyvista.domain.company.Employee;
import com.propertyvista.domain.company.Notification;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.tenant.lease.Lease;

public class NotificationsUtils {

    public static List<String> toEmails(List<Employee> employees) {
        List<String> emails = new ArrayList<String>();
        for (Employee emp : employees) {
            emails.add(emp.email().getValue());
        }
        return emails;
    }

    public static List<Employee> getNotificationTraget(Lease leaseId, Notification.NotificationType notificationType) {
        EntityQueryCriteria<Building> criteria = EntityQueryCriteria.create(Building.class);
        criteria.eq(criteria.proto().units().$()._Leases(), leaseId);
        return getNotificationTraget(Persistence.service().retrieve(criteria, AttachLevel.IdOnly), notificationType);
    }

    public static List<Employee> getNotificationTraget(Building buildingId, Notification.NotificationType notificationType) {
        EntityQueryCriteria<Employee> criteria = EntityQueryCriteria.create(Employee.class);

        OrCriterion or = criteria.or();
        or.left().eq(criteria.proto().notifications().$().buildings(), buildingId);
        or.left().eq(criteria.proto().notifications().$().type(), notificationType);
        or.right().eq(criteria.proto().notifications().$().portfolios().$().buildings(), buildingId);
        or.right().eq(criteria.proto().notifications().$().type(), notificationType);

        return Persistence.service().query(criteria);
    }
}
