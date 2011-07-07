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
package com.propertyvista.portal.server.generator;

import java.util.ArrayList;
import java.util.List;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.essentials.server.preloader.DataGenerator;

import com.propertyvista.common.domain.DemoData;
import com.propertyvista.common.domain.tenant.Tenant;
import com.propertyvista.domain.Company;
import com.propertyvista.domain.OrganizationContact;
import com.propertyvista.domain.OrganizationContacts;
import com.propertyvista.portal.server.preloader.RandomUtil;

public class TenantsGenerator {

    public TenantsGenerator(long seed) {
        DataGenerator.setRandomSeed(seed);
    }

    public List<Tenant> createTenants(int numTenants) {
        List<Tenant> tenants = new ArrayList<Tenant>();
        for (int i = 0; i < numTenants; i++) {
            tenants.add(createTenant());
        }
        return tenants;
    }

    public Tenant createTenant() {
        Tenant tenant = EntityFactory.create(Tenant.class);

        tenant.person().set(CommonsGenerator.createPerson());
//        tenant.company().set(createCompany());

        return tenant;
    }

    public Company createCompany() {
        Company company = EntityFactory.create(Company.class);

        company.name().setValue(RandomUtil.random(DemoData.EMPLOYER_NAMES));

        for (int i = 0; i < 1 + RandomUtil.randomInt(2); i++) {
            company.addresses().add(CommonsGenerator.createAddress());
        }

        for (int i = 0; i < 1 + RandomUtil.randomInt(2); i++) {
            company.phones().add(CommonsGenerator.createPhone());
        }

        String domain = company.name().getStringView().toLowerCase() + ".com";
        String website = "http://www." + domain;
        company.website().setValue(website);

        for (int i = 0; i < 1 + RandomUtil.randomInt(2); i++) {
            String email = "contact" + (i + 1) + "@" + domain;
            company.emails().add(CommonsGenerator.createEmail(email));
        }

        for (int i = 0; i < 1 + RandomUtil.randomInt(2); i++) {
            OrganizationContacts contacts = createOrganizationContacts();
            company.contacts().add(contacts);
        }

        // TODO Add logo
        //Picture logo();

        return company;
    }

    private OrganizationContacts createOrganizationContacts() {
        OrganizationContacts contacts = EntityFactory.create(OrganizationContacts.class);

        contacts.companyRole().name().setValue(RandomUtil.random(DemoData.COMPANY_ROLES));

        for (int i = 0; i < RandomUtil.randomInt(2); i++) {
            OrganizationContact contact = createOrganizationContact();
            contacts.contactList().add(contact);
        }
        return contacts;
    }

    private OrganizationContact createOrganizationContact() {
        OrganizationContact contact = EntityFactory.create(OrganizationContact.class);

        contact.contactRole().name().setValue(RandomUtil.random(DemoData.CONTACT_ROLES));
        contact.person().set(CommonsGenerator.createPerson());

        return contact;
    }
}
