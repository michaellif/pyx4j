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
import com.pyx4j.quartz.SchedulerHelper;
import com.pyx4j.server.contexts.Lifecycle;
import com.pyx4j.server.contexts.NamespaceManager;
import com.pyx4j.server.mail.Mail;

import com.propertyvista.admin.domain.pmc.Pmc;
import com.propertyvista.admin.server.preloader.VistaAdminDataPreloaders;
import com.propertyvista.biz.communication.CommunicationFacade;
import com.propertyvista.domain.VistaNamespace;
import com.propertyvista.misc.VistaDataPreloaderParameter;
import com.propertyvista.misc.VistaDevPreloadConfig;
import com.propertyvista.portal.server.preloader.PmcCreatorDev;
import com.propertyvista.server.config.VistaServerSideConfiguration;
import com.propertyvista.server.config.VistaServerSideConfigurationDev;
import com.propertyvista.server.config.VistaServerSideConfigurationDevPostgreSQL;

public class VistaDBReset {

    private static final Logger log = LoggerFactory.getLogger(VistaDBReset.class);

    public static void main(String[] args) {
        long totalStart = System.currentTimeMillis();
        VistaServerSideConfiguration conf;
        if ((args.length > 0) && (args[0].equals("--postgre"))) {
            log.info("Use PostgreSQL");
            conf = new VistaServerSideConfigurationDevPostgreSQL();
        } else {
            log.info("Use MySQL");
            conf = new VistaServerSideConfigurationDev();
        }
        ServerSideConfiguration.setInstance(conf);
        Persistence.service().startBackgroundProcessTransaction();
        try {
            RDBUtils.resetDatabase();
            NamespaceManager.setNamespace(VistaNamespace.demoNamespace);
            RDBUtils.ensureNamespace();
            RDBUtils.dropAllEntityTables();
            SchedulerHelper.dbReset();
            RDBUtils.initAllEntityTables();
            log.info("Generating new Data...");
            long start = System.currentTimeMillis();

            NamespaceManager.setNamespace(VistaNamespace.adminNamespace);

            if (((EntityPersistenceServiceRDB) Persistence.service()).getMultitenancyType() == MultitenancyType.SeparateSchemas) {
                RDBUtils.ensureNamespace();
                // TODO Hack for non implemented SeparateSchemas DML 
                ((EntityPersistenceServiceRDB) Persistence.service()).resetMapping();
                RDBUtils.dropAllEntityTables();
            }

            Pmc pmc = PmcCreatorDev.createPmc(VistaNamespace.demoNamespace);

            new VistaAdminDataPreloaders().preloadAll();

            Persistence.service().commit();

            NamespaceManager.setNamespace(VistaNamespace.demoNamespace);
            RDBUtils.ensureNamespace();
            DataPreloaderCollection preloaders = ((VistaServerSideConfiguration) ServerSideConfiguration.instance()).getDataPreloaders();
            if ((args != null) && (args.length > 0)) {
                VistaDevPreloadConfig cfg = VistaDevPreloadConfig.createDefault();
                if (args[0].equals("--mockup")) {
                    cfg.mockupData = true;
                    preloaders.setParameterValue(VistaDataPreloaderParameter.devPreloadConfig.name(), cfg);
                }
            }

            try {
                Lifecycle.startElevatedUserContext();
                Mail.getMailService().setDisabled(true);
                ServerSideFactory.create(CommunicationFacade.class).setDisabled(true);
                log.info(preloaders.preloadAll());
                Persistence.service().commit();
            } finally {
                ServerSideFactory.create(CommunicationFacade.class).setDisabled(false);
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
            Persistence.service().endTransaction();
        }
    }
}
