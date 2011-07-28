/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-07-27
 * @author Vlad
 * @version $Id$
 */
package com.propertvista.generator.util;


import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.domain.DemoData;
import com.propertyvista.domain.company.Company;
import com.propertyvista.domain.company.OrganizationContact;
import com.propertyvista.domain.company.OrganizationContacts;
import com.propertyvista.domain.property.vendor.Contract;
import com.propertyvista.domain.property.vendor.Maintenance;
import com.propertyvista.domain.property.vendor.Vendor;
import com.propertyvista.domain.property.vendor.Warranty;
import com.propertyvista.domain.property.vendor.WarrantyItem;

public class CompanyVendor {

    public static Company createCompany() {
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

    public static Warranty createnWarranty() {
        Warranty item = EntityFactory.create(Warranty.class);
        item.set(createContract());

        item.type().setValue(RandomUtil.randomEnum(Warranty.Type.class));
        item.title().setValue(RandomUtil.randomLetters(8));

        for (int j = 0; j < 1; j++) {
            WarrantyItem e = EntityFactory.create(WarrantyItem.class);
            e.name().setValue(RandomUtil.randomLetters(15).toLowerCase());
            item.items().add(e);
        }
        return item;
    }

    public static Maintenance createnMaintenance() {
        Maintenance item = EntityFactory.create(Maintenance.class);
        item.set(createContract());

        item.lastService().setValue(RandomUtil.randomLogicalDate());
        item.nextService().setValue(RandomUtil.randomLogicalDate());

        return item;
    }

    public static Contract createContract() {
        Contract item = EntityFactory.create(Contract.class);

        item.contractID().setValue(RandomUtil.randomLetters(8));

        item.contractor().set(EntityFactory.create(Vendor.class));
        item.contractor().set(createCompany());
        item.contractor().type().setValue(RandomUtil.randomEnum(Vendor.Type.class));

        item.cost().setValue(RandomUtil.randomDouble(8000));

        item.start().setValue(RandomUtil.randomLogicalDate());
        item.end().setValue(RandomUtil.randomLogicalDate());

        return item;
    }

    // internals:
    private static OrganizationContacts createOrganizationContacts() {
        OrganizationContacts contacts = EntityFactory.create(OrganizationContacts.class);

        contacts.companyRole().name().setValue(RandomUtil.random(DemoData.COMPANY_ROLES));

        for (int i = 0; i < RandomUtil.randomInt(2); i++) {
            OrganizationContact contact = createOrganizationContact();
            contacts.contactList().add(contact);
        }
        return contacts;
    }

    private static OrganizationContact createOrganizationContact() {
        OrganizationContact contact = EntityFactory.create(OrganizationContact.class);

        contact.contactRole().name().setValue(RandomUtil.random(DemoData.CONTACT_ROLES));
        contact.person().set(CommonsGenerator.createPerson());

        return contact;
    }
}
