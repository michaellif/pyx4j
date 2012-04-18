/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-04-18
 * @author vlads
 * @version $Id$
 */
package com.propertvista.generator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.propertvista.generator.util.CommonsGenerator;

import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.domain.EmergencyContact;
import com.propertyvista.domain.tenant.Customer;

public class CustomerGenerator {

    public Customer createCustomer() {
        Customer item = EntityFactory.create(Customer.class);
        item.person().set(CommonsGenerator.createPerson());
        return item;
    }

    public Collection<EmergencyContact> createEmergencyContacts() {
        List<EmergencyContact> contacts = new ArrayList<EmergencyContact>();
        contacts.add(createEmergencyContact());
        contacts.add(createEmergencyContact());
        return contacts;
    }

    public EmergencyContact createEmergencyContact() {
        EmergencyContact contact = EntityFactory.create(EmergencyContact.class);

        contact.name().set(CommonsGenerator.createName());

        contact.homePhone().setValue(CommonsGenerator.createPhone());
        contact.mobilePhone().setValue(CommonsGenerator.createPhone());

        contact.email().setValue(CommonsGenerator.createEmail(contact.name()));

        contact.address().set(CommonsGenerator.createAddress());

        return contact;
    }
}
