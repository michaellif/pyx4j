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
 */
package com.propertyvista.oapi.v1.marshaling;

import com.pyx4j.entity.core.EntityFactory;

import com.propertyvista.domain.person.Person;
import com.propertyvista.oapi.AbstractMarshaller;
import com.propertyvista.oapi.v1.model.TenantIO;
import com.propertyvista.oapi.v1.model.types.SexTypeIO;
import com.propertyvista.oapi.xml.StringIO;

public class TenantMarshaller extends AbstractMarshaller<Person, TenantIO> {

    private static class SingletonHolder {
        public static final TenantMarshaller INSTANCE = new TenantMarshaller();
    }

    private TenantMarshaller() {
    }

    public static TenantMarshaller getInstance() {
        return SingletonHolder.INSTANCE;
    }

    @Override
    protected TenantIO marshal(Person person) {
        if (person == null || person.isNull()) {
            return null;
        }
        TenantIO tenantIO = new TenantIO();
        tenantIO.firstName = getValue(person.name().firstName());
        tenantIO.lastName = getValue(person.name().lastName());
        tenantIO.middleName = getValue(person.name().middleName());

        tenantIO.sex = createIo(SexTypeIO.class, person.sex());
        tenantIO.phone = createIo(StringIO.class, person.homePhone());
        tenantIO.email = createIo(StringIO.class, person.email());
        return tenantIO;
    }

    @Override
    protected Person unmarshal(TenantIO tenantIO) {
        Person person = EntityFactory.create(Person.class);
        person.name().firstName().setValue(tenantIO.firstName);
        person.name().lastName().setValue(tenantIO.lastName);
        person.name().middleName().setValue(tenantIO.middleName);

        setValue(person.sex(), tenantIO.sex);
        setValue(person.homePhone(), tenantIO.phone);
        setValue(person.email(), tenantIO.email);
        return person;
    }
}
