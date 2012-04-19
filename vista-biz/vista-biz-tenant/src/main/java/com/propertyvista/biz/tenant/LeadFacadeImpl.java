/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-04-19
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.biz.tenant;

import java.util.Date;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.gwt.server.DateUtils;
import com.pyx4j.rpc.shared.UserRuntimeException;

import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.tenant.Customer;
import com.propertyvista.domain.tenant.Tenant;
import com.propertyvista.domain.tenant.lead.Guest;
import com.propertyvista.domain.tenant.lead.Lead;
import com.propertyvista.domain.tenant.lead.Lead.Status;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.Lease.PaymentFrequency;

public class LeadFacadeImpl implements LeadFacade {

    @Override
    public void createLead(Lead lead) {
        // TODO Auto-generated method stub

    }

    @Override
    public void persistLead(Lead lead) {
        // TODO Auto-generated method stub

    }

    @Override
    public void convertToApplication(Key leadId, AptUnit unitId) {
        Lead lead = Persistence.service().retrieve(Lead.class, leadId);
        if (!lead.lease().isNull()) {
            throw new UserRuntimeException("The Lead is converted to Lease already!");
        }

        Date leaseEnd = null;
        switch (lead.leaseTerm().getValue()) {
        case months6:
            leaseEnd = DateUtils.monthAdd(lead.moveInDate().getValue(), 6 + 1);
            break;
        case months12:
            leaseEnd = DateUtils.monthAdd(lead.moveInDate().getValue(), 12 + 1);
            break;
        case months18:
            leaseEnd = DateUtils.monthAdd(lead.moveInDate().getValue(), 18 + 1);
            break;
        case other:
            leaseEnd = DateUtils.monthAdd(lead.moveInDate().getValue(), 12 + 1);
            break;
        }

        Lease lease = EntityFactory.create(Lease.class);

        lease.type().set(lead.leaseType());
        lease.paymentFrequency().setValue(PaymentFrequency.Monthly);
        lease.unit().set(unitId);

        lease.leaseFrom().setValue(lead.moveInDate().getValue());
        lease.leaseTo().setValue(new LogicalDate(leaseEnd));

        lease.version().status().setValue(Lease.Status.Created);

        lease.version().expectedMoveIn().setValue(lead.moveInDate().getValue());

        boolean asApplicant = true;
        for (Guest guest : lead.guests()) {
            Customer tenant = EntityFactory.create(Customer.class);
            tenant.person().set(guest.person());
            Persistence.service().persist(tenant);

            Tenant tenantInLease = EntityFactory.create(Tenant.class);
            tenantInLease.customer().set(tenant);
            tenantInLease.role().setValue(asApplicant ? Tenant.Role.Applicant : Tenant.Role.CoApplicant);
            lease.version().tenants().add(tenantInLease);
            asApplicant = false;
        }

        //lm.save(lease);

        // mark Lead as converted:
        lead.lease().set(lease);
        Persistence.service().persist(lead);

    }

    @Override
    public void close(Key leadId) {
        Lead lead = Persistence.service().retrieve(Lead.class, leadId);
        lead.status().setValue(Status.closed);
        Persistence.secureSave(lead);
    }

}
