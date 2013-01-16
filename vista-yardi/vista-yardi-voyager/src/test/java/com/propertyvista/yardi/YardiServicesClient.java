/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 31, 2012
 * @author Mykola
 * @version $Id$
 */
package com.propertyvista.yardi;

import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.entity.rdb.cfg.Configuration.DatabaseType;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.server.contexts.Lifecycle;
import com.pyx4j.server.contexts.NamespaceManager;

import com.propertyvista.config.tests.VistaTestDBSetup;
import com.propertyvista.config.tests.VistaTestsServerSideConfiguration;
import com.propertyvista.domain.DemoData.DemoPmc;

public class YardiServicesClient {

    private static final String serviceURL = "https://www.iyardiasp.com/8223thirddev/webservices/itfresidenttransactions20.asmx";

    private static YardiGetResidentTransactionsService getResidentTransactions = new YardiGetResidentTransactionsService();

    /**
     * @param args
     * @throws YardiServiceException
     */
    public static void main(String[] args) throws YardiServiceException {
        YardiParameters yp = new YardiParameters();
        yp.setServiceURL(serviceURL);
        yp.setUsername(YardiConstants.USERNAME);
        yp.setPassword(YardiConstants.PASSWORD);
        yp.setServerName(YardiConstants.SERVER_NAME);
        yp.setDatabase(YardiConstants.DATABASE);
        yp.setPlatform(YardiConstants.PLATFORM);
        yp.setInterfaceEntity(YardiConstants.INTERFACE_ENTITY);
        yp.setYardiPropertyId(YardiConstants.YARDI_PROPERTY_ID);

        //db setup
        if (false) {
            VistaTestDBSetup.init();
        } else {
            ServerSideConfiguration.setInstance(new VistaTestsServerSideConfiguration(DatabaseType.PostgreSQL));
            NamespaceManager.setNamespace(DemoPmc.vista.name());
            Persistence.service().startBackgroundProcessTransaction();
            Lifecycle.startElevatedUserContext();
        }

        //test services
        getResidentTransactions.updateAll(yp);
        Persistence.service().commit();
    }
}
