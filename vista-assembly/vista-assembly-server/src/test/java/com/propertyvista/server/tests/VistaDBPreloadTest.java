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
package com.propertyvista.server.tests;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.TimeUtils;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.rdb.RDBUtils;
import com.pyx4j.entity.server.Executable;
import com.pyx4j.entity.server.TransactionScopeOption;
import com.pyx4j.entity.server.UnitOfWork;
import com.pyx4j.entity.server.dataimport.DataPreloaderCollection;
import com.pyx4j.server.contexts.Lifecycle;
import com.pyx4j.server.contexts.NamespaceManager;

import com.propertyvista.biz.system.VistaSystemFacade;
import com.propertyvista.config.tests.VistaDBTestBase;
import com.propertyvista.domain.VistaNamespace;
import com.propertyvista.misc.VistaDataPreloaderParameter;
import com.propertyvista.misc.VistaDevPreloadConfig;
import com.propertyvista.operations.server.preloader.V2BPreloader;
import com.propertyvista.portal.server.preloader.PmcCreatorDev;
import com.propertyvista.portal.server.preloader.VistaDataPreloaders;

public class VistaDBPreloadTest extends VistaDBTestBase {

    private final static Logger log = LoggerFactory.getLogger(VistaDBPreloadTest.class);

    @Override
    public void setUp() throws Exception {
        super.setUp();
        ServerSideFactory.create(VistaSystemFacade.class).setCommunicationsDisabled(true);
    }

    public void testDefaultPreload() {
        long start = System.currentTimeMillis();
        DataPreloaderCollection dp = new VistaDataPreloaders(VistaDevPreloadConfig.createTest());
        dp.setParameterValue(VistaDataPreloaderParameter.attachMedia.name(), Boolean.FALSE);

        DataPreloaderCollection operationsData = new VistaOperationsDataForTesting();

        try {
            Lifecycle.startElevatedUserContext();
            NamespaceManager.setNamespace(VistaNamespace.operationsNamespace);
            new UnitOfWork(TransactionScopeOption.RequiresNew).execute(new Executable<Void, RuntimeException>() {
                @Override
                public Void execute() {
                    RDBUtils.initNameSpaceSpecificEntityTables();
                    return null;
                }
            });

            // Add minimal OPERATINONS banking data for tests
            operationsData.preloadAll();

            PmcCreatorDev.createPmc(VistaNamespace.demoNamespace, false);
            NamespaceManager.setNamespace(VistaNamespace.demoNamespace);

            new UnitOfWork(TransactionScopeOption.RequiresNew).execute(new Executable<Void, RuntimeException>() {
                @Override
                public Void execute() {
                    RDBUtils.initAllEntityTables();
                    return null;
                }
            });

            log.info(dp.preloadAll());

        } finally {
            Lifecycle.endElevatedUserContext();
        }

        log.info("Preload time {}", TimeUtils.secSince(start));
    }

    // TODO Extract class to a file and use in the future?? Or remove class and invoke V2BPreloader.create() directly
    public class VistaOperationsDataForTesting extends DataPreloaderCollection {
        public VistaOperationsDataForTesting() {
            add(new V2BPreloader());
        }
    }
}
