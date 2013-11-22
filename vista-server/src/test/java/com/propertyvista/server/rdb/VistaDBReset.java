/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 8, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.server.rdb;

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.TimeUtils;
import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.rdb.EntityPersistenceServiceRDB;
import com.pyx4j.entity.rdb.RDBUtils;
import com.pyx4j.entity.rdb.cfg.Configuration.MultitenancyType;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.server.dataimport.DataPreloaderCollection;
import com.pyx4j.log4j.LoggerConfig;
import com.pyx4j.quartz.SchedulerHelper;
import com.pyx4j.server.contexts.Lifecycle;
import com.pyx4j.server.contexts.NamespaceManager;
import com.pyx4j.server.mail.Mail;

import com.propertyvista.biz.system.VistaSystemFacade;
import com.propertyvista.domain.VistaNamespace;
import com.propertyvista.domain.pmc.Pmc;
import com.propertyvista.misc.VistaDataPreloaderParameter;
import com.propertyvista.misc.VistaDevPreloadConfig;
import com.propertyvista.operations.server.preloader.VistaOperationsDataPreloaders;
import com.propertyvista.portal.server.preloader.PmcCreatorDev;
import com.propertyvista.server.config.VistaServerSideConfiguration;
import com.propertyvista.server.config.VistaServerSideConfigurationDev;
import com.propertyvista.server.config.VistaServerSideConfigurationDevPostgreSQL;

public class VistaDBReset {

    private static final Logger log = LoggerFactory.getLogger(VistaDBReset.class);

    public static void main(String[] args) {
        List<String> arguments = Arrays.asList(args);

        long totalStart = System.currentTimeMillis();
        VistaServerSideConfiguration conf;
        if (arguments.contains("--postgre")) {
            log.info("Use PostgreSQL");
            conf = new VistaServerSideConfigurationDevPostgreSQL();
        } else {
            log.info("Use MySQL");
            conf = new VistaServerSideConfigurationDev();
        }
        LoggerConfig.setContextName("vista");
        ServerSideConfiguration.setInstance(conf);
        Persistence.service().startBackgroundProcessTransaction();
        try {
            long start = System.currentTimeMillis();
            RDBUtils.resetDatabase();
            SchedulerHelper.init();
            log.info("Generating new Data...");

            NamespaceManager.setNamespace(VistaNamespace.operationsNamespace);
            RDBUtils.ensureNamespace();

            if (((EntityPersistenceServiceRDB) Persistence.service()).getMultitenancyType() == MultitenancyType.SeparateSchemas) {
                RDBUtils.initNameSpaceSpecificEntityTables();
            } else {
                RDBUtils.initAllEntityTables();
            }
            Persistence.service().commit();

            Pmc pmc = PmcCreatorDev.createPmc(VistaNamespace.demoNamespace, false);
            Persistence.service().commit();

            new VistaOperationsDataPreloaders().preloadAll();

            if (((EntityPersistenceServiceRDB) Persistence.service()).getMultitenancyType() == MultitenancyType.SeparateSchemas) {
                NamespaceManager.setNamespace(VistaNamespace.expiringNamespace);
                RDBUtils.ensureNamespace();
                RDBUtils.initNameSpaceSpecificEntityTables();
                Persistence.service().commit();
            }

            Persistence.service().commit();

            NamespaceManager.setNamespace(VistaNamespace.demoNamespace);
            RDBUtils.ensureNamespace();
            if (((EntityPersistenceServiceRDB) Persistence.service()).getMultitenancyType() == MultitenancyType.SeparateSchemas) {
                RDBUtils.initAllEntityTables();
                Persistence.service().commit();
            }

            DataPreloaderCollection preloaders = ((VistaServerSideConfiguration) ServerSideConfiguration.instance()).getDataPreloaders();
            VistaDevPreloadConfig cfg = VistaDevPreloadConfig.createDefault();
            if (arguments.contains("--mockup")) {
                cfg = VistaDevPreloadConfig.createMockup();
                preloaders.setParameterValue(VistaDataPreloaderParameter.devPreloadConfig.name(), cfg);
            }

            try {
                Lifecycle.startElevatedUserContext();
                Mail.getMailService().setDisabled(true);
                ServerSideFactory.create(VistaSystemFacade.class).setCommunicationsDisabled(true);
                log.info(preloaders.preloadAll());
            } finally {
                ServerSideFactory.create(VistaSystemFacade.class).setCommunicationsDisabled(false);
                Mail.getMailService().setDisabled(false);
                Lifecycle.endElevatedUserContext();
            }

            log.info("Preload time: " + TimeUtils.secSince(start));
            log.info("Total time: " + TimeUtils.secSince(totalStart));

        } catch (Throwable t) {
            // This is test environment, we need to see what was created.
            Persistence.service().commit();
            log.error("", t);
        } finally {
            SchedulerHelper.shutdown();
            Persistence.service().endTransaction();
        }
    }
}
