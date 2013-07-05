/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 27, 2011
 * @author Misha
 * @version $Id$
 */
package com.propertyvista.config.tests;

import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.entity.rdb.RDBUtils;
import com.pyx4j.entity.rdb.cfg.Configuration;
import com.pyx4j.entity.rdb.cfg.Configuration.DatabaseType;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.server.contexts.NamespaceManager;
import com.pyx4j.server.mail.Mail;

import com.propertyvista.domain.VistaNamespace;

public class VistaTestDBSetup {

    private static ServerSideConfiguration initOnce = null;

    public static synchronized void init() {
        if (initOnce == null) {
            DatabaseType databaseType = DatabaseType.HSQLDB;
            //databaseType = DatabaseType.MySQL;
            //databaseType = DatabaseType.Derby;
            //databaseType = DatabaseType.PostgreSQL;

            // Fail safe if somebody committed the file by mistake 
            if (System.getProperty("bamboo.buildNumber") != null) {
                databaseType = DatabaseType.HSQLDB;
            }
            initOnce = new VistaTestsServerSideConfiguration(databaseType);
            ServerSideConfiguration.setInstance(initOnce);
            Mail.getMailService().setDisabled(true);
            if (databaseType == DatabaseType.PostgreSQL) {
                Persistence.service();
                NamespaceManager.setNamespace(VistaNamespace.operationsNamespace);
                RDBUtils.ensureNamespace();
                NamespaceManager.setNamespace(VistaTestsNamespaceResolver.demoNamespace);
                RDBUtils.ensureNamespace();
            }
        }
        NamespaceManager.setNamespace(VistaTestsNamespaceResolver.demoNamespace);
    }

    public static void resetDatabase() {
        RDBUtils.resetDatabase();
        switch (((Configuration) initOnce.getPersistenceConfiguration()).databaseType()) {
        case PostgreSQL:
            NamespaceManager.setNamespace(VistaNamespace.operationsNamespace);
            RDBUtils.ensureNamespace();
            NamespaceManager.setNamespace(VistaTestsNamespaceResolver.demoNamespace);
            RDBUtils.ensureNamespace();
            break;
        default:
            break;
        }
    }
}
