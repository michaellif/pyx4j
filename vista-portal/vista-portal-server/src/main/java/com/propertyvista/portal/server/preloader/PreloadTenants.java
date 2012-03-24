/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 4, 2011
 * @author dmitry
 * @version $Id$
 */
package com.propertyvista.portal.server.preloader;

import java.util.List;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.propertvista.generator.LeaseHelper;
import com.propertvista.generator.TenantsGenerator;
import com.propertvista.generator.util.RandomUtil;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.criterion.EntityListCriteria;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.domain.DemoData;
import com.propertyvista.domain.company.Company;
import com.propertyvista.domain.company.Employee;
import com.propertyvista.domain.maintenance.IssueClassification;
import com.propertyvista.domain.maintenance.MaintenanceRequest;
import com.propertyvista.domain.person.Person;
import com.propertyvista.domain.property.asset.Floorplan;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.security.TenantUser;
import com.propertyvista.domain.security.VistaTenantBehavior;
import com.propertyvista.domain.tenant.Tenant;
import com.propertyvista.domain.tenant.lead.Appointment;
import com.propertyvista.domain.tenant.lead.Lead;
import com.propertyvista.domain.tenant.lead.Showing;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.Lease.PaymentFrequency;
import com.propertyvista.portal.server.preloader.util.BaseVistaDevDataPreloader;
import com.propertyvista.server.common.util.LeaseLifecycleSim;

public class PreloadTenants extends BaseVistaDevDataPreloader {

    private final static Logger log = LoggerFactory.getLogger(PreloadTenants.class);

    private final static Random RND = new Random(1);

    private static final long MIN_RESERVE_TIME = 0L;

    private static final long MAX_RESERVE_TIME = 1000L * 60L * 60L * 24L * 60L; // 60 days

    private static final long MIN_LEASE_TERM = 1000L * 60L * 60L * 24L * 365L; // approx 1 Year

    private static final long MAX_LEASE_TERM = 1000L * 60L * 60L * 24L * 365L * 5L; // approx 5 Years

    private static final long MIN_NOTICE_TERM = 1000L * 60L * 60L * 24L * 31L;

    private static final long MAX_NOTICE_TERM = 1000L * 60L * 60L * 24L * 60L;

    private static final long MIN_AVAILABLE = 0;

    private static final long MAX_AVAILABLE = 1000L * 60L * 60L * 24L * 30L;

    @SuppressWarnings("unchecked")
    @Override
    public String delete() {
        if (ApplicationMode.isDevelopment()) {
            return deleteAll(Tenant.class, Person.class, Company.class, Lead.class, Appointment.class);
        } else {
            return "This is production";
        }
    }

    private class AptUnitSource {

        // Get the second building, initially
        int buildingNo = 1;

        int unitNo = 0;

        List<AptUnit> units = null;

        AptUnit next() {
            if ((units == null) || (units.size() == unitNo)) {
                nextBuilding();
            }
            return units.get(unitNo++);
        }

        void nextBuilding() {
            unitNo = 0;
            EntityListCriteria<Building> bcriteria = EntityListCriteria.create(Building.class);
            bcriteria.asc(bcriteria.proto().propertyCode().getPath().toString());
            bcriteria.setPageSize(1);
            bcriteria.setPageNumber(buildingNo++);
            Building building = Persistence.service().retrieve(bcriteria);
            if (building == null) {
                throw new Error("No more building and units available for PotentialTenants. Change configuration!");
            }

            EntityQueryCriteria<AptUnit> ucriteria = new EntityQueryCriteria<AptUnit>(AptUnit.class);
            ucriteria.add(PropertyCriterion.eq(ucriteria.proto().belongsTo(), building));
            units = Persistence.service().query(ucriteria);
            if (units.size() == 0) {
                nextBuilding();
            }
        }
    }

    @Override
    public String create() {
        TenantsGenerator generator = new TenantsGenerator(config().tenantsGenerationSeed);
        AptUnitSource aptUnitSource = new AptUnitSource();

        // retrieve MaintenanceRequest issues
        List<IssueClassification> issues = Persistence.service().query(EntityQueryCriteria.create(IssueClassification.class));
        for (int i = 1; i <= config().numTenants; i++) {
            String email = DemoData.UserType.TENANT.getEmail(i);
            Tenant tenant = generator.createTenant();
            TenantUser user = UserPreloader.createTenantUser(tenant.person().name().getStringView(), email, email, VistaTenantBehavior.Tenant);
            tenant.person().email().setValue(email);
            tenant.user().set(user);
            persistTenant(tenant);

            Persistence.service().persist(generator.createPaymentMethods(tenant));

            AptUnit unit = aptUnitSource.next();
            if (unit._availableForRent().isNull()) {
                continue;
            }
            LeaseLifecycleSim leaseSim = new LeaseLifecycleSim();
            LogicalDate now = new LogicalDate();
            LogicalDate simStart = new LogicalDate(Math.max(unit._availableForRent().getValue().getTime(), new LogicalDate(2008 - 1900, 1, 1).getTime()));
            LogicalDate eventDate = add(simStart, random(MIN_AVAILABLE, MAX_AVAILABLE));
            LogicalDate leaseFrom = add(eventDate, random(MIN_RESERVE_TIME, MAX_RESERVE_TIME));
            LogicalDate expectedMoveIn = leaseFrom;
            LogicalDate leaseTo = add(leaseFrom, random(MIN_LEASE_TERM, MAX_LEASE_TERM));
            Lease lease = leaseSim.newLease(eventDate, RandomUtil.randomLetters(8), unit, leaseFrom, leaseTo, expectedMoveIn, PaymentFrequency.Monthly, tenant);
            LeaseHelper.updateLease(lease);
            Persistence.service().merge(lease);

            do {
                if (eventDate.after(now)) {
                    break;
                }

                eventDate = new LogicalDate(random(eventDate.getTime(), lease.leaseFrom().getValue().getTime()));
                if (eventDate.after(now)) {
                    break;
                }
                lease = leaseSim.approveApplication(lease.getPrimaryKey(), eventDate);

                eventDate = lease.leaseFrom().getValue();
                if (eventDate.after(now)) {
                    break;
                }
                lease = leaseSim.activate(lease.getPrimaryKey(), eventDate);

                // Maintenance Requests
                for (MaintenanceRequest req : generator.createMntRequests(config().numMntRequests)) {
                    req.submitted().setValue(new LogicalDate(random(lease.leaseFrom().getValue().getTime(), lease.leaseTo().getValue().getTime())));
                    req.issueClassification().set(RandomUtil.random(issues));
                    req.tenant().set(tenant);
                    Persistence.service().persist(req);
                }

                eventDate = new LogicalDate(Math.max(lease.leaseFrom().getValue().getTime(),
                        lease.leaseTo().getValue().getTime() - random(MIN_NOTICE_TERM, MAX_NOTICE_TERM)));
                if (eventDate.after(now)) {
                    break;
                }
                lease = leaseSim.notice(lease.getPrimaryKey(), eventDate, lease.leaseTo().getValue());

                eventDate = lease.leaseTo().getValue();
                if (eventDate.after(now)) {
                    break;
                }
                lease = leaseSim.complete(lease.getPrimaryKey(), lease.leaseTo().getValue());

            } while (false);
        }

        for (int i = 1; i <= config().numUnAssigendTenants; i++) {
            String email = DemoData.UserType.NEW_TENANT.getEmail(i);
            TenantUser user = UserPreloader.createTenantUser(email, email, null);
            Tenant tenant = generator.createTenant();
            tenant.person().email().setValue(email);
            tenant.user().set(user);
            // Update user name
            user.name().setValue(tenant.person().name().getStringView());
            Persistence.service().persist(user);
            persistTenant(tenant);
        }

        List<Floorplan> floorplans = Persistence.service().query(EntityQueryCriteria.create(Floorplan.class));
        List<Employee> employees = Persistence.service().query(EntityQueryCriteria.create(Employee.class));

        // Leads:
        List<Lead> leads = generator.createLeads(config().numLeads);
        for (Lead lead : leads) {
            lead.floorplan().set(RandomUtil.random(floorplans));
            lead.building().set(lead.floorplan().building());

            lead.agent().set(RandomUtil.random(employees));

            Persistence.service().persist(lead);

            EntityQueryCriteria<AptUnit> criteria = EntityQueryCriteria.create(AptUnit.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().floorplan(), lead.floorplan()));
            List<AptUnit> units = Persistence.service().query(criteria);

            List<Appointment> apps = generator.createAppointments(1 + RandomUtil.randomInt(3));
            for (Appointment app : apps) {
                app.lead().set(lead);
                app.agent().set(RandomUtil.random(employees));

                Persistence.service().persist(app);

                if (!units.isEmpty()) {
                    List<Showing> shws = generator.createShowings(1 + RandomUtil.randomInt(3));
                    for (Showing shw : shws) {
                        shw.unit().set(RandomUtil.random(units));
                        shw.appointment().set(app);

                        Persistence.service().persist(shw);
                    }
                }
            }
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Created ").append(config().numTenants).append(" tenants");
        return sb.toString();
    }

    public void persistTenant(Tenant tenant) {
        switch (tenant.type().getValue()) {
        case company:
            log.debug("Persisting tenant {}", tenant.company().name());
            CmpanyVendorPersistHelper.persistCompany(tenant.company());
            break;
        }
        Persistence.service().persist(tenant);
    }

    private long random(long min, long max) {
        assert min <= max;
        if (max == min) {
            return min;
        } else {
            return min + Math.abs(RND.nextLong()) % (max - min);
        }
    }

    private LogicalDate add(LogicalDate date, long term) {
        return new LogicalDate(date.getTime() + term);
    }

}