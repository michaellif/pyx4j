/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-01-15
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.biz.system;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.entity.rdb.cfg.Configuration.DatabaseType;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.server.contexts.Lifecycle;
import com.pyx4j.server.contexts.NamespaceManager;

import com.propertyvista.config.VistaDeployment;
import com.propertyvista.config.tests.VistaTestsServerSideConfiguration;
import com.propertyvista.domain.DemoData.DemoPmc;
import com.propertyvista.domain.StatisticsRecord;
import com.propertyvista.domain.settings.PmcYardiCredential;
import com.propertyvista.server.jobs.YardiImportProcess;
import com.propertyvista.yardi.YardiConstants;
import com.propertyvista.yardi.YardiGetResidentTransactionsService;
import com.propertyvista.yardi.YardiParameters;
import com.propertyvista.yardi.YardiServiceException;

public class YardiProcessFacadeImpl implements YardiProcessFacade {

    private static final Logger log = LoggerFactory.getLogger(YardiImportProcess.class);

    private static YardiGetResidentTransactionsService getResidentTransactions = new YardiGetResidentTransactionsService();

    @Override
    public void doAllImport(StatisticsRecord dynamicStatisticsRecord) {
        PmcYardiCredential yardiCredential = VistaDeployment.getPmcYardiCredential();
        YardiParameters yp = new YardiParameters();
        yp.setServiceURL(yardiCredential.serviceURL().getValue());
        yp.setUsername(yardiCredential.username().getValue());
        yp.setPassword(yardiCredential.credential().getValue());
        yp.setServerName(yardiCredential.serverName().getValue());
        yp.setDatabase(yardiCredential.database().getValue());
        yp.setPlatform(yardiCredential.platform().toString());
        yp.setInterfaceEntity(YardiConstants.INTERFACE_ENTITY);
        yp.setYardiPropertyId(YardiConstants.YARDI_PROPERTY_ID);

        //db setup
        ServerSideConfiguration.setInstance(new VistaTestsServerSideConfiguration(DatabaseType.PostgreSQL));
        NamespaceManager.setNamespace(DemoPmc.vista.name());
        Persistence.service().startBackgroundProcessTransaction();
        Lifecycle.startElevatedUserContext();

        try {
            getResidentTransactions.updateAll(yp);
        } catch (YardiServiceException e) {
            log.error("Error", e);
        }

    }

}
