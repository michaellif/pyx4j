/*
 * Pyx4j framework
 * Copyright (C) 2008-2010 pyx4j.com.
 *
 * Created on Dec 22, 2009
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.server.test;

import junit.framework.TestCase;

import com.pyx4j.entity.server.ServerEntityFactory;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.tester.domain.Address;
import com.pyx4j.tester.domain.Country;

public class ServerEntityFactoryTest extends TestCase {

    public void testObjectCreation() {

        EntityFactory.setImplementation(new ServerEntityFactory());

        Country country = EntityFactory.create(Country.class);
        country.name().setValue("Canada");

        assertEquals("name Value", "Canada", country.name().getValue());

        Address address = EntityFactory.create(Address.class);
        address.country().set(country);

        assertEquals("address.country Value", "Canada", address.country().name().getValue());
    }
}
