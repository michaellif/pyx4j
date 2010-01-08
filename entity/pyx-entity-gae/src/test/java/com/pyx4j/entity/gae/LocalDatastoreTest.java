/*
 * Pyx4j framework
 * Copyright (C) 2008-2010 pyx4j.com.
 *
 * Created on Dec 24, 2009
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.entity.gae;

import java.io.File;

import org.junit.After;
import org.junit.Before;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.dev.LocalDatastoreService;
import com.google.appengine.tools.development.ApiProxyLocalImpl;
import com.google.apphosting.api.ApiProxy;

import com.pyx4j.entity.server.ServerEntityFactory;
import com.pyx4j.entity.shared.EntityFactory;

public abstract class LocalDatastoreTest {

    protected DatastoreService datastoreService;

    /** true to store saved changes, default to false */
    protected boolean storeChanges = false;

    private static int uniqueCount = 0;

    @Before
    public void setupDatastore() {
        ApiProxy.setEnvironmentForCurrentThread(new TestEnvironment());
        ApiProxyLocalImpl impl = new ApiProxyLocalImpl(new File(".")) {
        };
        impl.setProperty(LocalDatastoreService.NO_STORAGE_PROPERTY, Boolean.toString(!storeChanges));
        ApiProxy.setDelegate(impl);
        datastoreService = DatastoreServiceFactory.getDatastoreService();
        EntityFactory.setImplementation(new ServerEntityFactory());
    }

    @After
    public void teardownDatastore() {
        ApiProxyLocalImpl proxy = (ApiProxyLocalImpl) ApiProxy.getDelegate();
        LocalDatastoreService datastoreService = (LocalDatastoreService) proxy.getService(LocalDatastoreService.PACKAGE);
        datastoreService.clearProfiles();
        ApiProxy.setDelegate(null);
        ApiProxy.setEnvironmentForCurrentThread(null);
    }

    protected synchronized String uniqueString() {
        return Integer.toHexString(++uniqueCount) + "_" + Long.toHexString(System.currentTimeMillis());
    }
}