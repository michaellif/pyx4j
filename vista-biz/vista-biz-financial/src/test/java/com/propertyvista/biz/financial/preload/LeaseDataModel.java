/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 3, 2012
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.biz.financial.preload;

import java.util.Calendar;
import java.util.GregorianCalendar;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.biz.occupancy.OccupancyFacade;
import com.propertyvista.biz.policy.IdAssignmentFacade;
import com.propertyvista.biz.tenant.LeaseFacade;
import com.propertyvista.domain.financial.offering.ProductItem;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.tenant.Tenant;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.Lease.Status;
import com.propertyvista.domain.tenant.lease.LeaseParticipant;

public class LeaseDataModel {

    private final PreloadConfig config;

    private Lease lease;

    private ProductItem serviceItem;

    private final TenantDataModel tenantDataModel;

    private final BuildingDataModel buildingDataModel;

    public LeaseDataModel(PreloadConfig config, BuildingDataModel buildingDataModel, TenantDataModel tenantDataModel) {
        this.config = config;
        this.buildingDataModel = buildingDataModel;
        this.tenantDataModel = tenantDataModel;

    }

    public void generate() {
        serviceItem = buildingDataModel.generateResidentialUnitServiceItem();

        if (config.existingLease) {
            lease = ServerSideFactory.create(LeaseFacade.class).create(Status.ExistingLease);
        } else {
            lease = ServerSideFactory.create(LeaseFacade.class).create(Status.Application);
            ServerSideFactory.create(OccupancyFacade.class).scopeAvailable(serviceItem.element().cast().getPrimaryKey());
        }

        lease = ServerSideFactory.create(LeaseFacade.class).setUnit(lease, (AptUnit) serviceItem.element().cast());
        lease.currentTerm().version().leaseProducts().serviceItem().agreedPrice().setValue(serviceItem.price().getValue());
        lease.currentTerm().termFrom().setValue(new LogicalDate(111, 1, 25));
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(lease.currentTerm().termFrom().getValue());
        calendar.add(Calendar.MONTH, 6);
        calendar.add(Calendar.DATE, -1);
        lease.currentTerm().termTo().setValue(new LogicalDate(calendar.getTime()));

        addTenants();

        lease.approvalDate().setValue(lease.currentTerm().termFrom().getValue());

        lease = ServerSideFactory.create(LeaseFacade.class).persist(lease);
    }

    private void addTenants() {
        Tenant tenantInLease = EntityFactory.create(Tenant.class);
        ServerSideFactory.create(IdAssignmentFacade.class).assignId(tenantInLease);
        tenantInLease.customer().set(tenantDataModel.getTenant());
        tenantInLease.role().setValue(LeaseParticipant.Role.Applicant);
        lease.currentTerm().version().tenants().add(tenantInLease);
    }

    public ProductItem getServiceItem() {
        return serviceItem;
    }

    public Lease getLeaseId() {
        return lease;
    }

}
