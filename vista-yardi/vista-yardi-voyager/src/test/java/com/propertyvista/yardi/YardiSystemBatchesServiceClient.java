/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-01-17
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.yardi;

import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.entity.rdb.cfg.Configuration.DatabaseType;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.server.contexts.Lifecycle;
import com.pyx4j.server.contexts.NamespaceManager;

import com.propertyvista.config.VistaDeployment;
import com.propertyvista.config.tests.VistaTestsServerSideConfiguration;
import com.propertyvista.domain.DemoData.DemoPmc;
import com.propertyvista.domain.settings.PmcYardiCredential;
import com.propertyvista.operations.domain.scheduler.StatisticsRecord;
import com.propertyvista.yardi.services.YardiSystemBatchesService;

public class YardiSystemBatchesServiceClient {

    public static void main(String[] args) throws Exception {
        ServerSideConfiguration.setInstance(new VistaTestsServerSideConfiguration(DatabaseType.PostgreSQL));
        NamespaceManager.setNamespace(DemoPmc.star.name());
        Persistence.service().startBackgroundProcessTransaction();
        Lifecycle.startElevatedUserContext();

        PmcYardiCredential yardiCredential = VistaDeployment.getPmcYardiCredential();
        try {
            StatisticsRecord dynamicStatisticsRecord = EntityFactory.create(StatisticsRecord.class);
            dynamicStatisticsRecord.total().setValue(0L);
            dynamicStatisticsRecord.failed().setValue(0L);
            dynamicStatisticsRecord.processed().setValue(0L);
            YardiSystemBatchesService.getInstance().postReceiptBatch(yardiCredential, dynamicStatisticsRecord);
        } finally {
            Persistence.service().commit();
        }

    }

}
