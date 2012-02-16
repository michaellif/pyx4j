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
package com.propertyvista.server.billing.preload;

import com.propertvista.generator.util.RandomUtil;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;

import com.propertyvista.domain.financial.offering.Feature;
import com.propertyvista.domain.financial.offering.ProductItem;
import com.propertyvista.domain.financial.offering.Service;
import com.propertyvista.domain.tenant.TenantInLease;
import com.propertyvista.domain.tenant.TenantInLease.Role;
import com.propertyvista.domain.tenant.lease.BillableItem;
import com.propertyvista.domain.tenant.lease.Lease;

public class LeaseDataModel {

    private Lease lease;

    private final TenantDataModel tenantDataModel;

    private final BuildingDataModel buildingDataModel;

    private ProductItem serviceItem;

    public LeaseDataModel(BuildingDataModel buildingDataModel, TenantDataModel tenantDataModel) {
        this.buildingDataModel = buildingDataModel;
        this.tenantDataModel = tenantDataModel;

    }

    public void generate(boolean persist) {

        serviceItem = buildingDataModel.generateResidentialUnitServiceItem();

        lease = EntityFactory.create(Lease.class);

        lease.unit().set(serviceItem.element());
        lease.leaseFrom().setValue(RandomUtil.randomLogicalDate(2001, 2011));
        lease.signDate().setValue(lease.leaseFrom().getValue());
        lease.leaseTo().setValue(RandomUtil.randomLogicalDate(2012, 2014));

        addTenants();
        generateAgreement();

        if (persist) {
            Persistence.service().persist(lease);
        }
    }

    private void generateAgreement() {
        lease.serviceAgreement().serviceItem().item().set(serviceItem);
        Service service = serviceItem.product().cast();

        for (Feature feature : service.features()) {
            if (feature.type().getValue().isInAgreement() && feature.items().size() > 0) {
                BillableItem billableItem = EntityFactory.create(BillableItem.class);
                billableItem.item().set(feature.items().get(0));
                lease.serviceAgreement().featureItems().add(billableItem);
            }
        }

    }

    private void addTenants() {
        TenantInLease tenantInLease = EntityFactory.create(TenantInLease.class);
        tenantInLease.tenant().set(tenantDataModel.getTenant());
        tenantInLease.role().setValue(Role.Applicant);
        lease.tenants().add(tenantInLease);
    }

    public IEntity getLease() {
        return lease;
    }

}
