/*
 * Pyx4j framework
 * Copyright (C) 2008-2009 pyx4j.com.
 *
 * Created on Dec 23, 2009
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.test.shared;

import com.pyx4j.entity.shared.impl.EntityFactory;
import com.pyx4j.entity.test.shared.domain.Address;
import com.pyx4j.entity.test.shared.domain.Country;

public class FactoryTest extends InitializerTestCase {

    public void testObjectCreation() {

        Country country = EntityFactory.create(Country.class);
        country.name().setValue("Canada");

        assertEquals("name Value", "Canada", country.name().getValue());

        Address address = EntityFactory.create(Address.class);
        address.country().set(country);

        assertEquals("address.country Value", "Canada", address.country().name().getValue());
    }
}
