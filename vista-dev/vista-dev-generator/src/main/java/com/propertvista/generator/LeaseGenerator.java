/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-04-10
 * @author vlads
 * @version $Id$
 */
package com.propertvista.generator;

import com.propertvista.generator.util.RandomUtil;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.domain.financial.offering.Service;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.tenant.Guarantor;
import com.propertyvista.domain.tenant.PersonRelationship;
import com.propertyvista.domain.tenant.Tenant;
import com.propertyvista.domain.tenant.Tenant.Role;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.misc.VistaDevPreloadConfig;

public class LeaseGenerator extends PTGenerator {

    private final CustomerGenerator customerGenerator;

    public LeaseGenerator(VistaDevPreloadConfig config) {
        super(config);
        // TODO configure properly
        customerGenerator = new CustomerGenerator();
    }

    public Lease createLease(AptUnit unit) {
        Lease lease = EntityFactory.create(Lease.class);

        lease.unit().set(unit);

        LogicalDate effectiveAvailableForRent = new LogicalDate(Math.max(unit._availableForRent().getValue().getTime(), RandomUtil
                .randomLogicalDate(2012, 2012).getTime()));
        LogicalDate createdDate = new LogicalDate(effectiveAvailableForRent.getTime() + Math.abs(rnd.nextLong()) % MAX_CREATE_WAIT);

        LogicalDate leaseFrom = new LogicalDate(createdDate.getTime() + Math.abs(rnd.nextLong()) % MAX_RESERVED_DURATION);
        LogicalDate leaseTo = new LogicalDate(Math.max(new LogicalDate().getTime(), leaseFrom.getTime()) + MIN_LEASE_DURATION + Math.abs(rnd.nextLong())
                % (MAX_LEASE_DURATION - MIN_LEASE_DURATION));
        LogicalDate expectedMoveIn = leaseFrom; // for simplicity's sake

        lease.type().setValue(Service.Type.residentialUnit);
        lease.leaseFrom().setValue(leaseFrom);
        lease.leaseTo().setValue(leaseTo);

        lease.createDate().setValue(createdDate);
        lease.version().expectedMoveIn().setValue(expectedMoveIn);

        addTenants(lease);

        return lease;
    }

    private void addTenants(Lease lease) {
        Tenant mainTenant = EntityFactory.create(Tenant.class);
        mainTenant.customer().set(customerGenerator.createCustomer());
        mainTenant.customer().emergencyContacts().addAll(customerGenerator.createEmergencyContacts());
        // TODO Add Screening
        mainTenant.role().setValue(Role.Applicant);
        lease.version().tenants().add(mainTenant);

        Guarantor guarantor1 = EntityFactory.create(Guarantor.class);
        guarantor1.customer().set(customerGenerator.createCustomer());
        guarantor1.customer().emergencyContacts().addAll(customerGenerator.createEmergencyContacts());
        // TODO Add Screening
        guarantor1.relationship().setValue(RandomUtil.randomEnum(PersonRelationship.class));
        guarantor1.tenant().set(mainTenant);
        lease.version().guarantors().add(guarantor1);
    }
}
