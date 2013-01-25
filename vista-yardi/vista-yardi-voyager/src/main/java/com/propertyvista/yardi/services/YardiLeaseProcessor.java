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
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.tenant.Customer;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseTerm;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant;
import com.propertyvista.domain.tenant.lease.LeaseTermTenant;
import com.propertyvista.yardi.merger.LeaseMerger;
import com.propertyvista.yardi.merger.TenantMerger;

public class YardiLeaseProcessor {
    private final static Logger log = LoggerFactory.getLogger(YardiLeaseProcessor.class);

    public void updateLeases(List<ResidentTransactions> allTransactions) {
        log.info("Updating leases...");
        for (ResidentTransactions transaction : allTransactions) {
            Property property = transaction.getProperty().get(0);
            for (RTCustomer rtCustomer : property.getRTCustomer()) {
                YardiLease yardiLease = rtCustomer.getCustomers().getCustomer().get(0).getLease();
                String propertyCode = YardiProcessorUtils.getPropertyId(property.getPropertyID().get(0));
                if (new LogicalDate(yardiLease.getLeaseToDate()).before(new LogicalDate())) {
                    log.info("Lease {} skipped, expired.", rtCustomer.getCustomerID());
                    continue;
                }

                {
                    EntityQueryCriteria<Lease> criteria = EntityQueryCriteria.create(Lease.class);
                    criteria.eq(criteria.proto().leaseId(), rtCustomer.getCustomerID());
                    if (!Persistence.service().query(criteria).isEmpty()) {
                        Lease lease = Persistence.service().query(criteria).get(0);
                        updateLease(rtCustomer, lease);
                        continue;
                    }
                }
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
        List<YardiCustomer> yardiCustomers = rtCustomer.getCustomers().getCustomer();
        List<LeaseTermTenant> tenants = lease.currentTerm().version().tenants();
        YardiLease yardiLease = yardiCustomers.get(0).getLease();
        if (new LeaseMerger().validateTermChanges(yardiLease, lease.currentTerm()) || new TenantMerger().validateChanges(yardiCustomers, tenants)) {
            LeaseTerm newTerm = Persistence.secureRetrieveDraft(LeaseTerm.class, lease.currentTerm().getPrimaryKey());
            newTerm = new LeaseMerger().updateTerm(yardiLease, newTerm);
//            newTerm = new TenantMerger().updateTenants(yardiCustomers, newTerm.version().tenants());
            lease.currentTerm().set(newTerm);
            lease = new LeaseMerger().mergeLease(yardiLease, lease);
            ServerSideFactory.create(LeaseFacade.class).finalize(lease);
            log.info("Lease {} successfully updated (term)", rtCustomer.getCustomerID());
        } else if (new LeaseMerger().validateLeaseChanges(yardiLease, lease)) {
            lease = new LeaseMerger().mergeLease(yardiLease, lease);
            ServerSideFactory.create(LeaseFacade.class).updateLeaseDates(lease);
            log.info("Lease {} successfully updated", rtCustomer.getCustomerID());
        } else {
            log.info("Lease {} was unchanged", rtCustomer.getCustomerID());
        }
    }

    private void createLease(RTCustomer rtCustomer, AptUnit unit, String propertyCode) {
        List<YardiCustomer> yardiCustomers = rtCustomer.getCustomers().getCustomer();
        YardiLease yardiLease = yardiCustomers.get(0).getLease();

        log.info("Lease {} in building {} is updating...", rtCustomer.getCustomerID(), propertyCode);

        LeaseFacade leaseFacade = ServerSideFactory.create(LeaseFacade.class);

        Lease lease = leaseFacade.create(Lease.Status.ExistingLease);
        lease.leaseId().setValue(rtCustomer.getCustomerID());
        lease.type().setValue(ServiceType.residentialUnit);

        // set unit:
        if (unit.getPrimaryKey() != null) {
            leaseFacade.setUnit(lease, unit);
            // TODO double into BigDecimal...might need to be handled differently
            leaseFacade.setLeaseAgreedPrice(lease, yardiLease.getCurrentRent());
        }

        // set dates:
        lease.currentTerm().termFrom().setValue(new LogicalDate(yardiLease.getLeaseFromDate()));
        lease.currentTerm().termTo().setValue(new LogicalDate(yardiLease.getLeaseToDate()));
        if (yardiLease.getExpectedMoveInDate() != null) {
            lease.expectedMoveIn().setValue(new LogicalDate(yardiLease.getExpectedMoveInDate()));
        }
        if (yardiLease.getActualMoveIn() != null) {
            lease.actualMoveIn().setValue(new LogicalDate(yardiLease.getActualMoveIn()));
        }

        // add tenants:
        for (YardiCustomer yardiCustomer : yardiCustomers) {
            Customer customer = EntityFactory.create(Customer.class);

            customer.person().name().firstName().setValue(yardiCustomer.getName().getFirstName());
            customer.person().name().lastName().setValue(yardiCustomer.getName().getLastName());
            customer.person().name().middleName().setValue(yardiCustomer.getName().getMiddleName());

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

        // almost done:
        lease = leaseFacade.persist(lease);

        // activate:
        leaseFacade.approve(lease, null, null);
        leaseFacade.activate(lease);

        log.info("Lease {} in building {} successfully created", rtCustomer.getCustomerID(), propertyCode);
    }
}