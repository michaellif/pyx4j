/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 20, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.config.tests;

import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.cache.CacheService;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.server.PersistenceServicesFactory;

public abstract class VistaDBTestBase extends TestCase {

    private final static Logger log = LoggerFactory.getLogger(VistaDBTestBase.class);

    private static long initializationTime = System.currentTimeMillis();

    private static int uniqueCount = 0;

    private static int uniqueIntCount = 0;

    private static int runningTestsCount = 0;

    private final Set<Class<?>> registeredMocks = new HashSet<Class<?>>();

    @Override
    protected void setUp() throws Exception {
        log.debug("=================== setUp UnitTest {}.{}  ===================", this.getClass().getName(), this.getName());
        super.setUp();
        CacheService.resetAll();
        VistaTestDBSetup.init();

        // Avoid side effects from unfinished setUp() in another tests
        try {
            Persistence.service().removeThreadLocale();
        } catch (Throwable ignore) {
        }

        //TODO investigate memory problem
        if ((runningTestsCount > 0) && (false)) {
            PersistenceServicesFactory.dispose();
            VistaTestDBSetup.resetDatabase();
        }
        runningTestsCount++;
    }

    public <T> void registerFacadeMock(Class<T> interfaceCalss, Class<? extends T> implCalss) {
        ServerSideFactory.register(interfaceCalss, implCalss);
        registeredMocks.add(interfaceCalss);
    }

    @Override
    protected void tearDown() throws Exception {
        for (Class<?> mocks : registeredMocks) {
            ServerSideFactory.unregister(mocks);
        }
    }

    public synchronized String uniqueString() {
        return Integer.toHexString(++uniqueCount) + "_" + Long.toHexString(System.currentTimeMillis()) + " " + this.getName();
    }

    public synchronized long uniqueLong() {
        return initializationTime + (++uniqueCount);
    }

    public synchronized int uniqueForTestInt() {
        return (++uniqueIntCount);
    }
}
