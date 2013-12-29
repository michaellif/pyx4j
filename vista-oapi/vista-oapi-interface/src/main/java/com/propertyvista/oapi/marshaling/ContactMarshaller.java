/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 24, 2012
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.oapi.marshaling;

import java.util.ArrayList;
import java.util.List;

import com.pyx4j.entity.core.EntityFactory;

import com.propertyvista.domain.property.PropertyContact;
import com.propertyvista.oapi.model.ContactIO;
import com.propertyvista.oapi.xml.StringIO;

public class ContactMarshaller implements Marshaller<PropertyContact, ContactIO> {

    private static class SingletonHolder {
        public static final ContactMarshaller INSTANCE = new ContactMarshaller();
    }

    private ContactMarshaller() {
    }

    public static ContactMarshaller getInstance() {
        return SingletonHolder.INSTANCE;
    }

    @Override
    public ContactIO marshal(PropertyContact contact) {
        if (contact == null || contact.isNull()) {
            return null;
        }
        ContactIO contactIO = new ContactIO();
        contactIO.name = MarshallerUtils.getValue(contact.name());

        contactIO.email = MarshallerUtils.createIo(StringIO.class, contact.email());
        contactIO.phone = MarshallerUtils.createIo(StringIO.class, contact.phone());
        return contactIO;
    }

    public List<ContactIO> marshal(List<PropertyContact> contacts) {
        List<ContactIO> contactIOList = new ArrayList<ContactIO>();
        for (PropertyContact contact : contacts) {
            contactIOList.add(marshal(contact));
        }
        return contactIOList;
    }

    @Override
    public PropertyContact unmarshal(ContactIO contactIO) {

        PropertyContact contact = EntityFactory.create(PropertyContact.class);
        contact.name().setValue(contactIO.name);

        MarshallerUtils.setValue(contact.email(), contactIO.email);
        MarshallerUtils.setValue(contact.phone(), contactIO.phone);
        return contact;
    }

    public List<PropertyContact> unmarshal(List<ContactIO> contactIOList) {
        List<PropertyContact> contacts = new ArrayList<PropertyContact>();
        for (ContactIO contactIO : contactIOList) {
            PropertyContact contact = EntityFactory.create(PropertyContact.class);
            MarshallerUtils.set(contact, contactIO, ContactMarshaller.getInstance());
            contacts.add(contact);
        }
        return contacts;
    }
}
