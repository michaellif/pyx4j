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
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.biz.financial.maintenance.MaintenanceFacade;
import com.propertyvista.domain.maintenance.IssueElementType;
import com.propertyvista.domain.maintenance.MaintenanceRequest;
import com.propertyvista.domain.maintenance.MaintenanceRequestCategory;
import com.propertyvista.domain.maintenance.MaintenanceRequestMetadata;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.Tenant;
import com.propertyvista.generator.util.CommonsGenerator;
import com.propertyvista.generator.util.RandomUtil;
import com.propertyvista.preloader.BaseVistaDevDataPreloader;

public class MaintenanceRequestsMockupPreloader extends BaseVistaDevDataPreloader {

    @Override
    public String create() {
        final int NUM_OF_DAYS_AGO = config().mockupData ? config().maintenanceRequestsMaxDaysBack : config().maintenanceRequestsMinDaysBack;

        EntityQueryCriteria<Lease> leaseCriteria = EntityQueryCriteria.create(Lease.class);
        leaseCriteria.add(PropertyCriterion.eq(leaseCriteria.proto().status(), Lease.Status.Active));
        List<Lease> leases = Persistence.service().query(leaseCriteria);
        if (leases.size() > 0) {
            final long TODAY = new LogicalDate().getTime();
            GregorianCalendar cal = new GregorianCalendar();
            for (cal.add(GregorianCalendar.DAY_OF_YEAR, -NUM_OF_DAYS_AGO); cal.getTimeInMillis() < TODAY; cal.add(GregorianCalendar.DAY_OF_YEAR, 1)) {
                makeMaintenanceRequest(leases.get(RandomUtil.randomInt(leases.size())), cal.getTime());
            }
        }

        return null;
    }

    private void makeMaintenanceRequest(Lease lease, Date when) {
        Persistence.service().retrieveMember(lease.leaseParticipants());
        MaintenanceRequest maintenanceRequest = ServerSideFactory.create(MaintenanceFacade.class).createNewRequestForTenant(
                lease.leaseParticipants().iterator().next().<Tenant> cast());
        setRandomMetadata(maintenanceRequest);

        maintenanceRequest.submitted().setValue(when);
        maintenanceRequest.updated().setValue(when);
        maintenanceRequest.summary().setValue(CommonsGenerator.lipsumShort());
        maintenanceRequest.description().setValue(CommonsGenerator.lipsum());
        maintenanceRequest.permissionToEnter().setValue(RandomUtil.randomBoolean());
        if (maintenanceRequest.permissionToEnter().isBooleanTrue()) {
            maintenanceRequest.petInstructions().setValue(CommonsGenerator.lipsum());
        }

        ServerSideFactory.create(MaintenanceFacade.class).postMaintenanceRequest(maintenanceRequest);
    }

    private void setRandomMetadata(MaintenanceRequest mr) {
        MaintenanceRequestMetadata meta = ServerSideFactory.create(MaintenanceFacade.class).getMaintenanceMetadata(mr.building());
        // set category
        MaintenanceRequestCategory category = meta.rootCategory();
        while (category.subCategories().size() > 0) {
            MaintenanceRequestCategory tmp;
            do {
                // get unit-related category 
                tmp = category.subCategories().get(RandomUtil.randomInt(category.subCategories().size()));
            } while (!tmp.type().isNull() && tmp.type().getValue() != IssueElementType.ApartmentUnit);
            category = tmp;
        }
        mr.category().set(category);
        // set priority
        mr.priority().set(meta.priorities().get(RandomUtil.randomInt(meta.priorities().size())));
    }

    @Override
    public String delete() {
        return null;
    }

}
