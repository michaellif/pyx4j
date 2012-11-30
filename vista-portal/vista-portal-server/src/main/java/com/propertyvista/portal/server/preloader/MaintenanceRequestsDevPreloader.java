/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-11-29
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.server.preloader;

import java.util.GregorianCalendar;
import java.util.List;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.domain.maintenance.IssueClassification;
import com.propertyvista.domain.maintenance.MaintenanceRequest;
import com.propertyvista.domain.maintenance.MaintenanceRequestStatus;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.generator.util.RandomUtil;
import com.propertyvista.preloader.BaseVistaDevDataPreloader;

public class MaintenanceRequestsDevPreloader extends BaseVistaDevDataPreloader {

    @Override
    public String create() {
        if (!config().mockupData) {
            return null;
        }

        List<IssueClassification> issueClassifications = Persistence.service().query(EntityQueryCriteria.create(IssueClassification.class));
        EntityQueryCriteria<Lease> leaseCriteria = EntityQueryCriteria.create(Lease.class);
        leaseCriteria.add(PropertyCriterion.eq(leaseCriteria.proto().status(), Lease.Status.Active));
        List<Lease> leases = Persistence.service().query(leaseCriteria);
        if (leases.size() > 0) {
            final int NUM_OF_DAYS_AGO = config().maintenanceRequestsDaysBack;
            final long TODAY = new LogicalDate().getTime();
            GregorianCalendar cal = new GregorianCalendar();
            for (cal.add(GregorianCalendar.DAY_OF_YEAR, -NUM_OF_DAYS_AGO); cal.getTimeInMillis() < TODAY; cal.add(GregorianCalendar.DAY_OF_YEAR, 1)) {
                makeMaintenanceRequest(issueClassifications, leases.get(RandomUtil.randomInt(leases.size())), new LogicalDate(cal.getTime()));
            }
        }

        return null;
    }

    private void makeMaintenanceRequest(List<IssueClassification> issueClassifications, Lease lease, LogicalDate when) {
        MaintenanceRequest maintenanceRequest = EntityFactory.create(MaintenanceRequest.class);
        maintenanceRequest.submitted().setValue(when);
        maintenanceRequest.updated().setValue(when);
        maintenanceRequest.status().setValue(MaintenanceRequestStatus.Submitted);
        maintenanceRequest.description().setValue(RandomUtil.randomLetters(50));

        Persistence.service().retrieveMember(lease.leaseParticipants());
        maintenanceRequest.leaseParticipant().setPrimaryKey(lease.leaseParticipants().iterator().next().getPrimaryKey());
        maintenanceRequest.issueClassification().set(issueClassifications.get(RandomUtil.randomInt(issueClassifications.size())));
        Persistence.service().persist(maintenanceRequest);
    }

    @Override
    public String delete() {
        return null;
    }

}
