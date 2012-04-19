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

import java.util.EnumSet;

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

    private final ScreeningGenerator screeningGenerator;

    public LeaseGenerator(VistaDevPreloadConfig config) {
        super(config);
        // TODO configure properly
        customerGenerator = new CustomerGenerator();
        screeningGenerator = new ScreeningGenerator();
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
        mainTenant.customer()._PersonScreenings().add(screeningGenerator.createScreening());
        mainTenant.role().setValue(Role.Applicant);
        lease.version().tenants().add(mainTenant);

        Guarantor guarantor = EntityFactory.create(Guarantor.class);
        guarantor.customer().set(customerGenerator.createCustomer());
        guarantor.customer()._PersonScreenings().add(screeningGenerator.createScreening());
        guarantor.relationship().setValue(RandomUtil.randomEnum(PersonRelationship.class));
        guarantor.tenant().set(mainTenant);
        lease.version().guarantors().add(guarantor);

        int maxTenants = RandomUtil.randomInt(4);//config.numTenantsInLease;
        for (int t = 0; t < maxTenants; t++) {
            Tenant tenant = EntityFactory.create(Tenant.class);
            tenant.customer().set(customerGenerator.createCustomer());
            tenant.customer().emergencyContacts().addAll(customerGenerator.createEmergencyContacts());
            tenant.customer()._PersonScreenings().add(screeningGenerator.createScreening());

            tenant.role().setValue(RandomUtil.random(EnumSet.of(Tenant.Role.CoApplicant, Tenant.Role.Dependent)));
            tenant.relationship().setValue(RandomUtil.randomEnum(PersonRelationship.class));
            tenant.takeOwnership().setValue(RandomUtil.randomBoolean());

            lease.version().tenants().add(tenant);
        }
    }
}
