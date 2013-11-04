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

import junit.framework.TestCase;

import com.pyx4j.entity.cache.CacheService;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.server.PersistenceServicesFactory;

public abstract class VistaDBTestBase extends TestCase {

    private static long initializationTime = System.currentTimeMillis();

    private static int uniqueCount = 0;

    private static int uniqueIntCount = 0;

    private static int runningTestsCount = 0;

    @Override
    protected void setUp() throws Exception {
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
