/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 17, 2013
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.yardi.services;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yardi.entity.mits.YardiCustomer;
import com.yardi.entity.mits.YardiLease;
import com.yardi.entity.resident.Property;
import com.yardi.entity.resident.RTCustomer;
import com.yardi.entity.resident.ResidentTransactions;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;

import com.propertyvista.biz.tenant.LeaseFacade;
import com.propertyvista.domain.financial.offering.Service.ServiceType;
import com.propertyvista.domain.person.Person;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.tenant.Customer;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant;
import com.propertyvista.domain.tenant.lease.LeaseTermTenant;

public class YardiLeaseProcessor {
    private final static Logger log = LoggerFactory.getLogger(YardiLeaseProcessor.class);

    public void updateLeases(List<ResidentTransactions> allTransactions) {
        log.info("Updating leases...");
        for (ResidentTransactions transaction : allTransactions) {
            Property property = transaction.getProperty().get(0);
            for (RTCustomer rtCustomer : property.getRTCustomer()) {
                {
                    EntityQueryCriteria<Lease> criteria = EntityQueryCriteria.create(Lease.class);
                    criteria.eq(criteria.proto().leaseId(), rtCustomer.getCustomerID());
                    if (!Persistence.service().query(criteria).isEmpty()) {
                        Lease lease = Persistence.service().query(criteria).get(0);
                        updateLease(rtCustomer, lease);
                    }
                }

                String propertyCode = YardiProcessorUtils.getPropertyId(property.getPropertyID().get(0));
                EntityQueryCriteria<AptUnit> criteria = EntityQueryCriteria.create(AptUnit.class);
                criteria.eq(criteria.proto().building().propertyCode(), propertyCode);
                criteria.eq(criteria.proto().info().number(), YardiProcessorUtils.getUnitId(rtCustomer));
                AptUnit unit = Persistence.service().query(criteria).get(0);

                try {
                    createLease(rtCustomer, unit, propertyCode);
                } catch (Throwable t) {
                    log.info("ERROR - lease not created: ", t);
                }
                Persistence.service().commit();
            }
        }
        log.info("All leases updated.");
    }

    private void updateLease(RTCustomer rtCustomer, Lease lease) {
        // TODO if lease already exists - do something

    }

    private void createLease(RTCustomer rtCustomer, AptUnit unit, String propertyCode) {
        List<YardiCustomer> yardiCustomers = rtCustomer.getCustomers().getCustomer();
        YardiLease yardiLease = yardiCustomers.get(0).getLease();

        LeaseFacade leaseFacade = ServerSideFactory.create(LeaseFacade.class);
        Lease lease = leaseFacade.create(Lease.Status.ExistingLease);

        lease.type().setValue(ServiceType.residentialUnit);

        lease.currentTerm().termFrom().setValue(new LogicalDate(yardiLease.getLeaseFromDate()));
        lease.currentTerm().termTo().setValue(new LogicalDate(yardiLease.getLeaseToDate()));
        if (yardiLease.getExpectedMoveInDate() != null) {
            lease.expectedMoveIn().setValue(new LogicalDate(yardiLease.getExpectedMoveInDate()));
        }
        if (yardiLease.getActualMoveIn() != null) {
            lease.actualMoveIn().setValue(new LogicalDate(yardiLease.getActualMoveIn()));
        }
// add price

        for (YardiCustomer yardiCustomer : yardiCustomers) {
            Customer customer = EntityFactory.create(Customer.class);
            Person person = EntityFactory.create(Person.class);
            person.name().firstName().setValue(yardiCustomer.getName().getFirstName());
            person.name().lastName().setValue(yardiCustomer.getName().getLastName());
            person.name().middleName().setValue(yardiCustomer.getName().getMiddleName());
            customer.person().set(person);
            LeaseTermTenant tenantInLease = EntityFactory.create(LeaseTermTenant.class);
            tenantInLease.leaseParticipant().customer().set(customer);
            if (rtCustomer.getCustomerID().equals(yardiCustomer.getCustomerID()) && yardiCustomer.getLease().isResponsibleForLease()) {
                tenantInLease.role().setValue(LeaseTermParticipant.Role.Applicant);
            } else {
                tenantInLease.role().setValue(
                        yardiCustomer.getLease().isResponsibleForLease() ? LeaseTermParticipant.Role.CoApplicant : LeaseTermParticipant.Role.Dependent);
            }
            lease.currentTerm().version().tenants().add(tenantInLease);
        }

        lease = leaseFacade.init(lease);
        if (unit.getPrimaryKey() != null) {
            leaseFacade.setUnit(lease, unit);
            // TODO double into BigDecimal...might need to be handled differently
            leaseFacade.setLeaseAgreedPrice(lease, yardiLease.getCurrentRent());
        }
        lease.leaseId().setValue(rtCustomer.getCustomerID());
        lease = leaseFacade.persist(lease);
        leaseFacade.activate(lease);
        log.info("Lease {} in building {} successfully updated", rtCustomer.getCustomerID(), propertyCode);
    }

}
