/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Oct 9, 2014
 * @author ernestog
 */
package com.propertyvista.server.config.filter.util;

import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.entity.rdb.RDBUtils;
import com.pyx4j.entity.rdb.cfg.Configuration.DatabaseType;
import com.pyx4j.server.mail.Mail;

import com.propertyvista.config.tests.VistaTestDBSetup;

public class VistaTestDBSetupForNamespace extends VistaTestDBSetup {

    private static ServerSideConfiguration initOnce = null;

    public static synchronized void init() {
        if (initOnce == null) {
            DatabaseType databaseType = DatabaseType.HSQLDB;

            initOnce = new VistaTestsServerSideConfigurationForNamespace(databaseType);
            ServerSideConfiguration.setInstance(initOnce);

            Mail.getMailService().setDisabled(true);
            initOperationsNamespace();
        }
    }

    public static void resetDatabase() {
        RDBUtils.resetDatabase();
    }

}
