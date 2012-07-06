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

import com.pyx4j.commons.Key;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.biz.policy.IdAssignmentFacade;
import com.propertyvista.biz.tenant.LeaseFacade;
import com.propertyvista.domain.financial.offering.ProductItem;
import com.propertyvista.domain.financial.offering.Service;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.tenant.Tenant;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.Lease.PaymentFrequency;
import com.propertyvista.domain.tenant.lease.Lease.Status;
import com.propertyvista.domain.tenant.lease.LeaseParticipant;

public class LeaseDataModel {

    private Lease lease;

    private ProductItem serviceItem;

    private final TenantDataModel tenantDataModel;

    private final BuildingDataModel buildingDataModel;

    public LeaseDataModel(PreloadConfig config, BuildingDataModel buildingDataModel, TenantDataModel tenantDataModel) {
        this.buildingDataModel = buildingDataModel;
        this.tenantDataModel = tenantDataModel;

    }

    public void generate() {

        serviceItem = buildingDataModel.generateResidentialUnitServiceItem();

        lease = EntityFactory.create(Lease.class);

        lease.type().setValue(Service.ServiceType.residentialUnit);

        lease.version().leaseProducts().serviceItem().agreedPrice().setValue(serviceItem.price().getValue());
        lease.leaseFrom().setValue(new LogicalDate(111, 1, 25));
        lease.approvalDate().setValue(lease.leaseFrom().getValue());

        Calendar calendar = new GregorianCalendar();
        calendar.setTime(lease.leaseFrom().getValue());
        calendar.add(Calendar.MONTH, 6);
        calendar.add(Calendar.DATE, -1);
        lease.leaseTo().setValue(new LogicalDate(calendar.getTime()));

        lease.paymentFrequency().setValue(PaymentFrequency.Monthly);

        addTenants();

        lease = ServerSideFactory.create(LeaseFacade.class).init(lease);
        lease.version().status().setValue(Status.Application);
        lease = ServerSideFactory.create(LeaseFacade.class).setUnit(lease, (AptUnit) serviceItem.element().cast());
        lease = ServerSideFactory.create(LeaseFacade.class).persist(lease);
    }

    private void addTenants() {
        Tenant tenantInLease = EntityFactory.create(Tenant.class);
        ServerSideFactory.create(IdAssignmentFacade.class).assignId(tenantInLease);
        tenantInLease.customer().set(tenantDataModel.getTenant());
        tenantInLease.role().setValue(LeaseParticipant.Role.Applicant);
        lease.version().tenants().add(tenantInLease);
    }

    public ProductItem getServiceItem() {
        return serviceItem;
    }

    public Key getLeaseKey() {
        return lease.id().getValue();
    }

}
