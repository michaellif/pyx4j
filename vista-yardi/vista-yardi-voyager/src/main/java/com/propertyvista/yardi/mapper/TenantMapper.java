/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 25, 2013
 * @author yuriyl
 * @version $Id$
 */
package com.propertyvista.yardi.mapper;

import com.yardi.entity.mits.YardiCustomer;

import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.domain.tenant.Customer;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant;
import com.propertyvista.domain.tenant.lease.LeaseTermTenant;

public class TenantMapper {

    public LeaseTermTenant map(YardiCustomer yardiCustomer) {
        Customer customer = EntityFactory.create(Customer.class);

        customer.person().name().firstName().setValue(yardiCustomer.getName().getFirstName());
        customer.person().name().lastName().setValue(yardiCustomer.getName().getLastName());
        customer.person().name().middleName().setValue(yardiCustomer.getName().getMiddleName());

        LeaseTermTenant tenantInLease = EntityFactory.create(LeaseTermTenant.class);
        tenantInLease.leaseParticipant().customer().set(customer);
        if (yardiCustomer.getLease().isResponsibleForLease()) {
            tenantInLease.role().setValue(LeaseTermParticipant.Role.Applicant);
        } else {
            tenantInLease.role().setValue(
                    yardiCustomer.getLease().isResponsibleForLease() ? LeaseTermParticipant.Role.CoApplicant : LeaseTermParticipant.Role.Dependent);
        }

        return tenantInLease;
    }

}
