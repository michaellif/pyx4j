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

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.biz.financial.maintenance.MaintenanceFacade;
import com.propertyvista.domain.maintenance.MaintenanceRequest;
import com.propertyvista.domain.maintenance.MaintenanceRequestCategory;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.Tenant;
import com.propertyvista.generator.util.RandomUtil;
import com.propertyvista.preloader.BaseVistaDevDataPreloader;

public class MaintenanceRequestsDevPreloader extends BaseVistaDevDataPreloader {

    @Override
    public String create() {
        if (!config().mockupData) {
            return null;
        }

        EntityQueryCriteria<MaintenanceRequestCategory> crit = EntityQueryCriteria.create(MaintenanceRequestCategory.class);
        crit.add(PropertyCriterion.isNull(crit.proto().subCategories()));
        List<MaintenanceRequestCategory> issueClassifications = Persistence.service().query(crit);
        EntityQueryCriteria<Lease> leaseCriteria = EntityQueryCriteria.create(Lease.class);
        leaseCriteria.add(PropertyCriterion.eq(leaseCriteria.proto().status(), Lease.Status.Active));
        List<Lease> leases = Persistence.service().query(leaseCriteria);
        if (leases.size() > 0) {
            final int NUM_OF_DAYS_AGO = config().maintenanceRequestsDaysBack;
            final long TODAY = new LogicalDate().getTime();
            GregorianCalendar cal = new GregorianCalendar();
            for (cal.add(GregorianCalendar.DAY_OF_YEAR, -NUM_OF_DAYS_AGO); cal.getTimeInMillis() < TODAY; cal.add(GregorianCalendar.DAY_OF_YEAR, 1)) {
                makeMaintenanceRequest(issueClassifications, leases.get(RandomUtil.randomInt(leases.size())), cal.getTime());
            }
        }

        return null;
    }

    private void makeMaintenanceRequest(List<MaintenanceRequestCategory> issueClassifications, Lease lease, Date when) {
        if (issueClassifications.isEmpty()) {
            return;
        }
        Persistence.service().retrieveMember(lease.leaseParticipants());
        MaintenanceRequest maintenanceRequest = ServerSideFactory.create(MaintenanceFacade.class).createNewRequest(lease.unit());
        maintenanceRequest.reporter().set(lease.leaseParticipants().iterator().next().<Tenant> cast());
        maintenanceRequest.submitted().setValue(when);
        maintenanceRequest.updated().setValue(when);
        maintenanceRequest.description().setValue(RandomUtil.randomLetters(50));
        maintenanceRequest.permissionToEnter().setValue(RandomUtil.randomBoolean());
        maintenanceRequest.petInstructions().setValue(RandomUtil.randomLetters(50));
        maintenanceRequest.category().set(issueClassifications.get(RandomUtil.randomInt(issueClassifications.size())));
        Persistence.service().persist(maintenanceRequest);
    }

    @Override
    public String delete() {
        return null;
    }

}
