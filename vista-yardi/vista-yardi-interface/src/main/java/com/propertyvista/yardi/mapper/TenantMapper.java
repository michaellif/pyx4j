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

import java.util.List;

import com.yardi.entity.mits.Phone;
import com.yardi.entity.mits.YardiCustomer;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IPrimitive;

import com.propertyvista.domain.tenant.Customer;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant.Role;
import com.propertyvista.domain.tenant.lease.LeaseTermTenant;

public class TenantMapper {

    public LeaseTermTenant map(YardiCustomer yardiCustomer, List<LeaseTermTenant> tenants) {
        Customer customer = EntityFactory.create(Customer.class);

// TODO translate plane string to our Name.Prefix enum:
//        customer.person().name().namePrefix().setValue(yardiCustomer.getName().getNamePrefix());
        customer.person().name().firstName().setValue(yardiCustomer.getName().getFirstName());
        customer.person().name().middleName().setValue(yardiCustomer.getName().getMiddleName());
        customer.person().name().lastName().setValue(yardiCustomer.getName().getLastName());
        customer.person().name().maidenName().setValue(yardiCustomer.getName().getMaidenName());
        customer.person().name().nameSuffix().setValue(yardiCustomer.getName().getNameSuffix());

        for (Phone phone : yardiCustomer.getPhone()) {
            switch (phone.getType()) {
            case CELL:
                setPhone(phone, customer.person().mobilePhone());
                break;
            case FAX:
                break;
            case HOME:
                setPhone(phone, customer.person().homePhone());
                break;
            case OFFICE:
                setPhone(phone, customer.person().workPhone());
                break;
            case OTHER:
                break;
            case PERSONAL:
                break;
            default:
                break;
            }
        }

// TODO - find somewhere...
//        customer.person().email().setValue(value);
//        customer.person().birthDate().setValue(value);

        LeaseTermTenant tenantInLease = EntityFactory.create(LeaseTermTenant.class);

        tenantInLease.leaseParticipant().customer().set(customer);
        tenantInLease.leaseParticipant().participantId().setValue(yardiCustomer.getCustomerID());

        if (yardiCustomer.getLease().isResponsibleForLease()) {
            if (!isApplicantExists(tenants)) {
                tenantInLease.role().setValue(Role.Applicant);
            } else {
                tenantInLease.role().setValue(Role.CoApplicant);
            }
        } else {
            tenantInLease.role().setValue(Role.Dependent);
        }

        return tenantInLease;
    }

    private boolean isApplicantExists(List<LeaseTermTenant> tenants) {
        for (LeaseTermTenant tenant : tenants) {
            if (tenant.role().getValue().equals(Role.Applicant)) {
                return true;
            }
        }
        return false;
    }

    private void setPhone(Phone phone, IPrimitive<String> to) {
        if (CommonsStringUtils.isEmpty(phone.getExtension())) {
            to.setValue(phone.getPhoneNumber());
        } else {
            to.setValue(phone.getPhoneNumber() + " x" + phone.getExtension());
        }
    }
}
