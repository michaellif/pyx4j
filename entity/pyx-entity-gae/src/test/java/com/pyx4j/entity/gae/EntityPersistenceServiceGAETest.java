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

import org.junit.Test;

import com.pyx4j.entity.server.IEntityPersistenceService;
import com.pyx4j.entity.server.PersistenceServicesFactory;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.test.shared.domain.Country;

public class EntityPersistenceServiceGAETest extends LocalDatastoreTest {

    @Test
    public void testPersist() {
        IEntityPersistenceService srv = PersistenceServicesFactory.getPersistenceService();
        Assert.assertNotNull("getPersistenceService", srv);

        Country country = EntityFactory.create(Country.class);
        country.name().setValue("Canada");

        srv.persist(country);

        String primaryKey = country.getPrimaryKey();

        Country country2 = srv.retrieve(Country.class, primaryKey);
        Assert.assertNotNull("retrieve", country2);
        Assert.assertEquals("name Value", "Canada", country2.name().getValue());
    }
}
