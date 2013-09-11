/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 16, 2012
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.oapi.marshaling;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.gwt.server.DateUtils;

import com.propertyvista.biz.tenant.lease.LeaseFacade;
import com.propertyvista.domain.person.Person;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.tenant.Customer;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseParticipant;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant;
import com.propertyvista.domain.tenant.lease.LeaseTermTenant;
import com.propertyvista.oapi.model.LeaseIO;
import com.propertyvista.oapi.model.TenantIO;
import com.propertyvista.oapi.model.types.LeaseStatusIO;
import com.propertyvista.oapi.model.types.PaymentFrequencyIO;
import com.propertyvista.oapi.xml.LogicalDateIO;

public class LeaseMarshaller implements Marshaller<Lease, LeaseIO> {

    private static class SingletonHolder {
        public static final LeaseMarshaller INSTANCE = new LeaseMarshaller();
    }

    private LeaseMarshaller() {
    }

    public static LeaseMarshaller getInstance() {
        return SingletonHolder.INSTANCE;
    }

    @Override
    public LeaseIO marshal(Lease lease) {
        if (lease == null || lease.isNull()) {
            return null;
        }
        LeaseIO leaseIO = new LeaseIO();
        leaseIO.leaseId = MarshallerUtils.getValue(lease.leaseId());
        leaseIO.propertyCode = MarshallerUtils.getValue(lease.unit().building().propertyCode());
        leaseIO.unitNumber = MarshallerUtils.getValue(lease.unit().info().number());

        leaseIO.status = MarshallerUtils.createIo(LeaseStatusIO.class, lease.status());
        leaseIO.paymentFrequency = MarshallerUtils.createIo(PaymentFrequencyIO.class, lease.billingAccount().billingPeriod());
        leaseIO.leaseFrom = MarshallerUtils.createIo(LogicalDateIO.class, lease.leaseFrom());
        leaseIO.leaseTo = MarshallerUtils.createIo(LogicalDateIO.class, lease.currentTerm().termTo());

        Persistence.service().retrieveMember(lease.leaseParticipants());
        List<Person> persons = new ArrayList<Person>();
        for (LeaseParticipant<?> participant : lease.leaseParticipants()) {
            Person person = participant.customer().person();
            persons.add(person);
        }
        List<TenantIO> tenants = new ArrayList<TenantIO>();
        tenants.addAll(TenantMarshaller.getInstance().marshal(persons));
        leaseIO.tenants = tenants;
        return leaseIO;
    }

    @Override
    public Lease unmarshal(LeaseIO leaseIO) {

        // unit
        AptUnit unit;
        {
            EntityQueryCriteria<AptUnit> criteria = EntityQueryCriteria.create(AptUnit.class);
            criteria.eq(criteria.proto().building().propertyCode(), leaseIO.propertyCode);
            criteria.eq(criteria.proto().info().number(), leaseIO.unitNumber);
            List<AptUnit> units = Persistence.service().query(criteria);
            if (units.size() > 0) {
                unit = units.get(0);
            } else {
                throw new Error("Unit not found in the database");
            }
        }

        Date leaseEnd = null;
        switch (leaseIO.leaseTerm.getValue()) {
        case months6:
            leaseEnd = DateUtils.monthAdd(leaseIO.leaseFrom.getValue(), 6 + 1);
            break;
        case months12:
            leaseEnd = DateUtils.monthAdd(leaseIO.leaseFrom.getValue(), 12 + 1);
            break;
        case months18:
            leaseEnd = DateUtils.monthAdd(leaseIO.leaseFrom.getValue(), 18 + 1);
            break;
        case other:
            leaseEnd = DateUtils.monthAdd(leaseIO.leaseFrom.getValue(), 12 + 1);
            break;
        }

        LeaseFacade leaseFacade = ServerSideFactory.create(LeaseFacade.class);
        Lease lease = leaseFacade.create(Lease.Status.ExistingLease);

        lease.type().setValue(leaseIO.leaseType.getValue());

        lease.currentTerm().termFrom().setValue(leaseIO.leaseFrom.getValue());
        lease.currentTerm().termTo().setValue(new LogicalDate(leaseEnd));
//        lease.currentTerm()

        lease.expectedMoveIn().setValue(leaseIO.leaseFrom.getValue());

        boolean asApplicant = true;

        List<Person> persons = TenantMarshaller.getInstance().unmarshal(leaseIO.tenants);
        for (Person person : persons) {
            Customer customer = EntityFactory.create(Customer.class);
            customer.person().set(person);

            LeaseTermTenant tenantInLease = EntityFactory.create(LeaseTermTenant.class);
            tenantInLease.leaseParticipant().customer().set(customer);
            tenantInLease.role().setValue(asApplicant ? LeaseTermParticipant.Role.Applicant : LeaseTermParticipant.Role.CoApplicant);
            lease.currentTerm().version().tenants().add(tenantInLease);
            asApplicant = false;
        }
        lease = leaseFacade.init(lease);

        if (unit.getPrimaryKey() != null) {
            leaseFacade.setUnit(lease, unit);
        }

        return lease;
    }
}
