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

import com.propertvista.generator.TenantsGenerator;
import com.propertvista.generator.util.RandomUtil;

import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.domain.DemoData;
import com.propertyvista.domain.company.Employee;
import com.propertyvista.domain.policy.policies.domain.IdAssignmentItem.IdTarget;
import com.propertyvista.domain.property.asset.Floorplan;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.security.TenantUser;
import com.propertyvista.domain.tenant.Customer;
import com.propertyvista.domain.tenant.lead.Appointment;
import com.propertyvista.domain.tenant.lead.Lead;
import com.propertyvista.domain.tenant.lead.Showing;
import com.propertyvista.portal.server.preloader.util.BaseVistaDevDataPreloader;
import com.propertyvista.server.common.util.IdAssignmentSequenceUtil;

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
            TenantUser user = UserPreloader.createTenantUser(email, email, null);
            Customer tenant = generator.createTenant();
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

    public void persistTenant(Customer tenant) {
        tenant.customerId().setValue(IdAssignmentSequenceUtil.getId(IdTarget.tenant));
        Persistence.service().persist(tenant);
    }

}