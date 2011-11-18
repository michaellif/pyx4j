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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.propertvista.generator.TenantsGenerator;
import com.propertvista.generator.gdo.ApplicationSummaryGDO;
import com.propertvista.generator.gdo.TenantSummaryGDO;
import com.propertvista.generator.util.RandomUtil;

import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.criterion.EntityListCriteria;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.domain.DemoData;
import com.propertyvista.domain.User;
import com.propertyvista.domain.VistaBehavior;
import com.propertyvista.domain.company.Company;
import com.propertyvista.domain.company.Employee;
import com.propertyvista.domain.person.Person;
import com.propertyvista.domain.property.asset.Floorplan;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.tenant.Tenant;
import com.propertyvista.domain.tenant.lead.Appointment;
import com.propertyvista.domain.tenant.lead.Lead;
import com.propertyvista.domain.tenant.lead.Showing;

public class PreloadTenants extends BaseVistaDevDataPreloader {

    private final static Logger log = LoggerFactory.getLogger(PreloadTenants.class);

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

        for (int i = 1; i <= config().numTenants; i++) {
            String email = DemoData.UserType.TENANT.getEmail(i);
            Tenant tenant = generator.createTenant();
            User user = UserPreloader.createUser(tenant.person().name().getStringView(), email, email, VistaBehavior.TENANT);
            tenant.person().email().address().setValue(email);
            tenant.user().set(user);
            persistTenant(tenant);

            ApplicationSummaryGDO summary = generator.createLease(tenant, aptUnitSource.next());
            LeaseHelper.updateLease(summary.lease());
            Persistence.service().persist(summary.lease());
            for (TenantSummaryGDO tenantSummary : summary.tenants()) {
                Persistence.service().persist(tenantSummary.tenantInLease());
            }

        }

        for (int i = 1; i <= config().numUnAssigendTenants; i++) {
            String email = DemoData.UserType.NEW_TENANT.getEmail(i);
            User user = UserPreloader.createUser(email, email, null);
            Tenant tenant = generator.createTenant();
            tenant.person().email().address().setValue(email);
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

    @Override
    public String print() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n\nLoaded ");

        List<Tenant> tenants = Persistence.service().query(new EntityQueryCriteria<Tenant>(Tenant.class));
        sb.append(tenants.size()).append(" tenants\n");
        for (Tenant tenant : tenants) {
            sb.append("\t");
            sb.append(tenant);
            sb.append("\n");
        }

        sb.append("\n");
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

}