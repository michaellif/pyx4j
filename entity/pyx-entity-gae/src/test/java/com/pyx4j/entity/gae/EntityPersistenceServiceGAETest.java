/*
 * Pyx4j framework
 * Copyright (C) 2008-2009 pyx4j.com.
 *
 * Created on Jan 5, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.gae;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import com.pyx4j.entity.server.IEntityPersistenceService;
import com.pyx4j.entity.server.PersistenceServicesFactory;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.test.shared.domain.Address;
import com.pyx4j.entity.test.shared.domain.Country;
import com.pyx4j.entity.test.shared.domain.Employee;

public class EntityPersistenceServiceGAETest extends LocalDatastoreTest {

    private IEntityPersistenceService srv;

    @Before
    public void setupPersistenceService() {
        srv = PersistenceServicesFactory.getPersistenceService();
    }

    @Test
    public void testPersist() {
        Assert.assertNotNull("getPersistenceService", srv);

        Country country = EntityFactory.create(Country.class);
        String countryName = "Canada" + uniqueString();
        country.name().setValue(countryName);

        srv.persist(country);

        String primaryKey = country.getPrimaryKey();

        Country country2 = srv.retrieve(Country.class, primaryKey);
        Assert.assertNotNull("retrieve", country2);
        Assert.assertEquals("name Value", countryName, country2.name().getValue());
        Assert.assertEquals("primaryKey Value", primaryKey, country2.getPrimaryKey());
    }

    @Test
    public void unownedOneToOnePersist() {
        IEntityPersistenceService srv = PersistenceServicesFactory.getPersistenceService();
        Country country = EntityFactory.create(Country.class);
        String countryName = "Canada" + uniqueString();
        country.name().setValue(countryName);
        srv.persist(country);

        Address address = EntityFactory.create(Address.class);
        address.country().set(country);
        srv.persist(address);

        String primaryKey = address.getPrimaryKey();
        Address address2 = srv.retrieve(Address.class, primaryKey);
        Assert.assertNotNull("retrieve", address2);

        Assert.assertEquals("address.country Value", countryName, address2.country().name().getValue());

    }

    @Test
    public void ownedOneToOnePersist() {
        Employee employee = EntityFactory.create(Employee.class);
        employee.firstName().setValue("Firstname");

        String addressStreet = "Home Street " + uniqueString();

        Address address = EntityFactory.create(Address.class);
        address.streetName().setValue(addressStreet);
        employee.homeAddress().set(address);

        srv.persist(employee);
        String primaryKey = employee.getPrimaryKey();
        Employee employee2 = srv.retrieve(Employee.class, primaryKey);
        Assert.assertNotNull("retrieve", employee2);

        Assert.assertNotNull("retrieve owned", employee2.homeAddress());
        Assert.assertNotNull("retrieve owned member", employee2.homeAddress().streetName());

        Assert.assertEquals("streetName is wrong", addressStreet, employee2.homeAddress().streetName().getValue());
    }
}
