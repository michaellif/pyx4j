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

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.biz.policy.IdAssignmentFacade;
import com.propertyvista.biz.tenant.LeadFacade;
import com.propertyvista.domain.DemoData;
import com.propertyvista.domain.company.Employee;
import com.propertyvista.domain.property.asset.Floorplan;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.security.CustomerUser;
import com.propertyvista.domain.tenant.Customer;
import com.propertyvista.domain.tenant.lead.Appointment;
import com.propertyvista.domain.tenant.lead.Lead;
import com.propertyvista.domain.tenant.lead.Showing;
import com.propertyvista.generator.TenantsGenerator;
import com.propertyvista.generator.util.RandomUtil;
import com.propertyvista.preloader.BaseVistaDevDataPreloader;

public class PreloadNewTenantsAndLeads extends BaseVistaDevDataPreloader {

    @SuppressWarnings("unchecked")
    @Override
    public String delete() {
        if (ApplicationMode.isDevelopment()) {
            return deleteAll(Lead.class, Appointment.class);
        } else {
            return "This is production";
        }
    }

    @Override
    public String create() {

        TenantsGenerator generator = new TenantsGenerator(config().tenantsGenerationSeed);

        for (int i = 1; i <= config().numUnAssigendTenants; i++) {
            String email = DemoData.UserType.NEW_TENANT.getEmail(i);
            CustomerUser user = UserPreloader.createTenantUser(email, email, null);
            Customer tenant = generator.createTenant();
            tenant.person().email().setValue(email);
            tenant.user().set(user);
            // Update user name
            user.name().setValue(tenant.person().name().getStringView());
            Persistence.service().persist(user);
            ServerSideFactory.create(IdAssignmentFacade.class).assignId(tenant);
            Persistence.service().persist(tenant);
        }

        List<Floorplan> floorplans;
        {
            EntityQueryCriteria<Floorplan> criteria = EntityQueryCriteria.create(Floorplan.class);
            criteria.asc(criteria.proto().id());
            floorplans = Persistence.service().query(criteria);
        }
        List<Employee> employees;
        {
            EntityQueryCriteria<Employee> criteria = EntityQueryCriteria.create(Employee.class);
            criteria.asc(criteria.proto().id());
            employees = Persistence.service().query(criteria);
        }

        // Leads:
        List<Lead> leads = generator.createLeads(config().numLeads);
        for (Lead lead : leads) {
            ServerSideFactory.create(LeadFacade.class).init(lead);

            lead.floorplan().set(RandomUtil.random(floorplans));
            lead.agent().set(RandomUtil.random(employees));

            ServerSideFactory.create(LeadFacade.class).persist(lead);

            EntityQueryCriteria<AptUnit> criteria = EntityQueryCriteria.create(AptUnit.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().floorplan(), lead.floorplan()));
            criteria.asc(criteria.proto().id());
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

}