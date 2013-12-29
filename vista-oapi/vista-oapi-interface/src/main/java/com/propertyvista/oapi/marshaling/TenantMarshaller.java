/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 16, 2012
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.oapi.marshaling;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.pyx4j.entity.core.EntityFactory;

import com.propertyvista.domain.person.Person;
import com.propertyvista.oapi.model.TenantIO;
import com.propertyvista.oapi.model.types.SexTypeIO;
import com.propertyvista.oapi.xml.StringIO;

public class TenantMarshaller implements Marshaller<Person, TenantIO> {

    private static class SingletonHolder {
        public static final TenantMarshaller INSTANCE = new TenantMarshaller();
    }

    private TenantMarshaller() {
    }

    public static TenantMarshaller getInstance() {
        return SingletonHolder.INSTANCE;
    }

    @Override
    public TenantIO marshal(Person person) {
        if (person == null || person.isNull()) {
            return null;
        }
        TenantIO tenantIO = new TenantIO();
        tenantIO.firstName = MarshallerUtils.getValue(person.name().firstName());
        tenantIO.lastName = MarshallerUtils.getValue(person.name().lastName());
        tenantIO.middleName = MarshallerUtils.getValue(person.name().middleName());

        tenantIO.sex = MarshallerUtils.createIo(SexTypeIO.class, person.sex());
        tenantIO.phone = MarshallerUtils.createIo(StringIO.class, person.homePhone());
        tenantIO.email = MarshallerUtils.createIo(StringIO.class, person.email());
        return tenantIO;
    }

    public List<TenantIO> marshal(Collection<Person> participants) {
        List<TenantIO> tenants = new ArrayList<TenantIO>();
        for (Person participant : participants) {
            tenants.add(marshal(participant));
        }
        return tenants;
    }

    @Override
    public Person unmarshal(TenantIO tenantIO) {
        Person person = EntityFactory.create(Person.class);
        person.name().firstName().setValue(tenantIO.firstName);
        person.name().lastName().setValue(tenantIO.lastName);
        person.name().middleName().setValue(tenantIO.middleName);

        MarshallerUtils.setValue(person.sex(), tenantIO.sex);
        MarshallerUtils.setValue(person.homePhone(), tenantIO.phone);
        MarshallerUtils.setValue(person.email(), tenantIO.email);
        return person;
    }

    public List<Person> unmarshal(Collection<TenantIO> tenantIOList) {
        List<Person> participants = new ArrayList<Person>();
        for (TenantIO tenantIO : tenantIOList) {
            Person participant = EntityFactory.create(Person.class);
            MarshallerUtils.set(participant, tenantIO, TenantMarshaller.getInstance());
            participants.add(participant);
        }
        return participants;
    }
}
