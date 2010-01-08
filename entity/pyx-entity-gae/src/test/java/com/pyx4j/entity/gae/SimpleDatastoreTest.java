/*
 * Pyx4j framework
 * Copyright (C) 2008-2010 pyx4j.com.
 *
 * Created on Dec 24, 2009
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.entity.gae;

import org.junit.Test;
import static org.junit.Assert.*;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

public class SimpleDatastoreTest extends LocalDatastoreTest {

    @Test
    public void testSinglePutSuccess() throws EntityNotFoundException {

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

        Key key = KeyFactory.createKey("Kind1", "Name1");

        Entity entityToPut = new Entity(key);
        entityToPut.setProperty("Property1", "Property1Value");

        Key keyFromPut = datastore.put(entityToPut);

        assertEquals("Keys should be equal", key, keyFromPut);

        Entity entityToGet = datastore.get(keyFromPut);

        assertEquals("Entities should be equal", entityToPut, entityToGet);
    }

}
