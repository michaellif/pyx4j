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
package com.propertvista.generator;

import java.util.ArrayList;
import java.util.List;

import com.propertvista.generator.util.CommonsGenerator;
import com.propertvista.generator.util.CompanyVendor;
import com.propertvista.generator.util.RandomUtil;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.essentials.server.preloader.DataGenerator;

import com.propertyvista.domain.tenant.Tenant;
import com.propertyvista.domain.tenant.lead.Appointment;
import com.propertyvista.domain.tenant.lead.Lead;

public class TenantsGenerator {

    public TenantsGenerator(long seed) {
        DataGenerator.setRandomSeed(seed);
    }

    public List<Tenant> createTenants(int num) {
        List<Tenant> items = new ArrayList<Tenant>();
        for (int i = 0; i < num; i++) {
            items.add(createTenant());
        }
        return items;
    }

    public Tenant createTenant() {
        Tenant item = EntityFactory.create(Tenant.class);

        item.type().setValue(RandomUtil.random(Tenant.Type.values()));
        switch (item.type().getValue()) {
        case person:
            item.person().set(CommonsGenerator.createPerson());
            break;
        case company:
            item.company().set(CompanyVendor.createCompany());
            break;
        }

        return item;
    }

    public List<Lead> createLeads(int num) {
        List<Lead> item = new ArrayList<Lead>();
        for (int i = 0; i < num; i++) {
            item.add(createLead());
        }
        return item;
    }

    public Lead createLead() {
        Lead item = EntityFactory.create(Lead.class);

        item.person().set(CommonsGenerator.createPerson());
        item.informedFrom().setValue(RandomUtil.randomEnum(Lead.InformedFrom.class));
        item.moveInDate().setValue(RandomUtil.randomLogicalDate(2011, 2012));
        item.rent().min().setValue(800 + RandomUtil.randomDouble(100));
        item.rent().max().setValue(900 + RandomUtil.randomDouble(100));
        item.term().setValue(RandomUtil.randomEnum(Lead.Term.class));
        item.beds().setValue(1 + RandomUtil.randomInt(3));
        item.baths().setValue(1 + RandomUtil.randomInt(2));
        item.comments().setValue(CommonsGenerator.lipsum());

        item.source().setValue(RandomUtil.randomEnum(Lead.Source.class));
        item.createDate().setValue(RandomUtil.randomLogicalDate(2011, 2012));
        item.assignedTo().setValue(CommonsGenerator.createName().getStringView());
        item.status().setValue(RandomUtil.randomEnum(Lead.Status.class));

        int count = 1 + RandomUtil.randomInt(3);
        for (int i = 0; i < count; ++i) {
            item.appointments().add(createAppointment());
        }

        return item;
    }

    public Appointment createAppointment() {
        Appointment item = EntityFactory.create(Appointment.class);

        item.date().setValue(RandomUtil.randomLogicalDate(2011, 2012));
        item.time().setValue(RandomUtil.randomTime());
        item.address().setValue(RandomUtil.randomLetters(15).toLowerCase());
        item.agent().setValue(CommonsGenerator.createName().getStringView());
        item.phone().set(CommonsGenerator.createPhone());
        item.email().setValue(RandomUtil.randomLetters(5).toLowerCase() + "@sympatico.ca");
        item.status().setValue(RandomUtil.randomEnum(Appointment.Status.class));

        return item;
    }
}
