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
package com.propertyvista.portal.server.preloader;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Random;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.IVersionedEntity.SaveAction;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.biz.occupancy.OccupancyFacade;
import com.propertyvista.biz.tenant.LeaseFacade;
import com.propertyvista.domain.DemoData;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.property.asset.unit.occupancy.AptUnitOccupancySegment;
import com.propertyvista.domain.tenant.Customer;
import com.propertyvista.domain.tenant.Tenant;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseParticipant;
import com.propertyvista.generator.LeaseGenerator;
import com.propertyvista.generator.util.RandomUtil;
import com.propertyvista.portal.server.preloader.util.AptUnitSource;
import com.propertyvista.portal.server.preloader.util.BaseVistaDevDataPreloader;
import com.propertyvista.portal.server.preloader.util.LeaseLifecycleSimulator;
import com.propertyvista.portal.server.preloader.util.LeaseLifecycleSimulator.LeaseLifecycleSimulatorBuilder;

public class LeasePreloader extends BaseVistaDevDataPreloader {

    private static Random RND = new Random(1l);

    @Override
    public String create() {
        int numCreated = 0;
        int numCreatedWithBilling = 0;

        LeaseGenerator generator = new LeaseGenerator(config());

        AptUnitSource aptUnitSource = new AptUnitSource(1);

        //ensure LeaseLifecycleSimulator is fired during tests
        int numLeasesWithNoSimulation = DemoData.UserType.TENANT.getDefaultMax();
        if (numLeasesWithNoSimulation >= config().numTenants) {
            numLeasesWithNoSimulation = config().numTenants - config().minSimulatedLeases;
        }

        Customer dualTenantCustomer = null;
        for (int i = 0; i < config().numTenants; i++) {
            AptUnit unit = makeAvailable(aptUnitSource.next());
            Persistence.service().commit();
            Lease lease = generator.createLease(unit);
            LeaseGenerator.attachDocumentData(lease);
            LeaseGenerator.assigneLeaseProducts(lease);

            if (i < DemoData.UserType.TENANT.getDefaultMax()) {
                Tenant mainTenant = lease.currentTerm().version().tenants().get(0);
                String email = DemoData.UserType.TENANT.getEmail(i + 1);
                mainTenant.leaseCustomer().customer().person().email().setValue(email);
                // Make one (Third) Customer with Two Leases
                if (i == 2) {
                    dualTenantCustomer = mainTenant.leaseCustomer().customer();
                }
            } else if (i == DemoData.UserType.TENANT.getDefaultMax()) {
                Tenant mainTenant = lease.currentTerm().version().tenants().get(0);
                mainTenant.leaseCustomer().customer().set(dualTenantCustomer);
            }

            // Create normal Active Lease first for Shortcut users
            if (i < numLeasesWithNoSimulation) {
                lease = ServerSideFactory.create(LeaseFacade.class).persist(lease);
                for (Tenant tenant : lease.currentTerm().version().tenants()) {
                    tenant.leaseCustomer().customer().personScreening().saveAction().setValue(SaveAction.saveAsFinal);
                    Persistence.service().persist(tenant.leaseCustomer().customer().personScreening());
                }
                ServerSideFactory.create(LeaseFacade.class).approveApplication(lease, null, null);
                //TODO
                // leaseFacade.activate(lease.getPrimaryKey());
            } else {
                LeaseLifecycleSimulatorBuilder simBuilder = LeaseLifecycleSimulator.sim();

                if (numCreatedWithBilling < config().numOfPseudoRandomLeasesWithSimulatedBilling) {
                    // create simulation events that happen between 4 years ago, and and the end of the previous month 
                    Calendar cal = new GregorianCalendar();
                    cal.setTime(new Date());
                    cal.add(Calendar.YEAR, -4);
                    simBuilder.start(new LogicalDate(cal.getTime()));

                    cal.setTime(new Date());
                    cal.add(Calendar.MONTH, -1);
                    cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
                    simBuilder.end(new LogicalDate(cal.getTime()));

                    ++numCreatedWithBilling;
                    simBuilder.simulateBilling();
                } else {
                    Calendar cal = new GregorianCalendar();
                    cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
                    cal.add(Calendar.YEAR, -1);
                    simBuilder.start(new LogicalDate(cal.getTime()));

                    cal.setTime(new LogicalDate());
                    if (i % 2 == 0) {
                        cal.add(Calendar.MONTH, 1 + RND.nextInt() % 4);
                        simBuilder.leaseTo(new LogicalDate(cal.getTime()));
                    }
                    simBuilder.end(new LogicalDate());

                    simBuilder.availabilityTermConstraints(0l, 0l);
                    simBuilder.reservedTermConstraints(0l, 0l);
                    simBuilder.approveImmidately();
                }

                simBuilder.create().generateRandomLifeCycle(lease);
            }

            numCreated++;
        }

        Customer dualPotentialCustomer = null;
        for (int i = 0; i < config().numPotentialTenants; i++) {

            AptUnit unit = aptUnitSource.next();
            unit = makeAvailable(unit);

            Lease lease = generator.createLease(unit);
            LeaseGenerator.attachDocumentData(lease);

            //Set users that can login using UI
            if (i < DemoData.UserType.PTENANT.getDefaultMax()) {
                Tenant mainTenant = lease.currentTerm().version().tenants().get(0);
                String email = DemoData.UserType.PTENANT.getEmail(i + 1);
                mainTenant.leaseCustomer().customer().person().email().setValue(email);

                // Make one (Third) Customer with Two Applications
                if (i == 2) {
                    dualPotentialCustomer = mainTenant.leaseCustomer().customer();
                }

                //Set PCOAPPLICANT users that can login using UI
                if (lease.currentTerm().version().tenants().size() > 1) {
                    Tenant tenant = lease.currentTerm().version().tenants().get(1);
                    String email2 = DemoData.UserType.PCOAPPLICANT.getEmail(i + 1);
                    tenant.leaseCustomer().customer().person().email().setValue(email2);

                    tenant.role().setValue(LeaseParticipant.Role.CoApplicant);
                    tenant.takeOwnership().setValue(false);
                }
            } else if (i == DemoData.UserType.PTENANT.getDefaultMax()) {
                Tenant mainTenant = lease.currentTerm().version().tenants().get(0);
                mainTenant.leaseCustomer().customer().set(dualPotentialCustomer);
            }

            if (lease.currentTerm().termFrom().getValue().before(new Date())) {
                Persistence.service().setTransactionSystemTime(lease.currentTerm().termFrom().getValue());
            }
            ServerSideFactory.create(LeaseFacade.class).persist(lease);
            for (Tenant tenant : lease.currentTerm().version().tenants()) {
                tenant.leaseCustomer().customer().personScreening().saveAction().setValue(SaveAction.saveAsFinal);
                Persistence.service().persist(tenant.leaseCustomer().customer().personScreening());
            }
            if (RandomUtil.randomBoolean()) {
                ServerSideFactory.create(LeaseFacade.class).createMasterOnlineApplication(lease);
            }
            Persistence.service().setTransactionSystemTime(null);
        }

        StringBuilder b = new StringBuilder();
        b.append("Created " + numCreated + " leases");
        return b.toString();
    }

    private AptUnit makeAvailable(final AptUnit unit) {
        if (unit._availableForRent().isNull()) {
            Persistence.service().setTransactionSystemTime(getStatusFromDate(unit));
            ServerSideFactory.create(OccupancyFacade.class).scopeAvailable(unit.getPrimaryKey());
            Persistence.service().setTransactionSystemTime(null);
        }
        return Persistence.service().retrieve(AptUnit.class, unit.getPrimaryKey());
    }

    private LogicalDate getStatusFromDate(AptUnit unit) {
        EntityQueryCriteria<AptUnitOccupancySegment> criteria = EntityQueryCriteria.create(AptUnitOccupancySegment.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().unit(), unit));
        AptUnitOccupancySegment segment = Persistence.service().retrieve(criteria);

        if (segment == null || segment.status().getValue() != AptUnitOccupancySegment.Status.pending) {
            throw new IllegalStateException("the unit must be pending");
        } else {
            return new LogicalDate(segment.dateFrom().getValue());
        }
    }

    @Override
    public String delete() {
        return null;
    }

}
