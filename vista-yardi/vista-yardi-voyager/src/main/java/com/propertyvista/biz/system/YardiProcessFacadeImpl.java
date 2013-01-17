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

import com.pyx4j.entity.server.Persistence;

import com.propertyvista.config.VistaDeployment;
import com.propertyvista.domain.StatisticsRecord;
import com.propertyvista.domain.settings.PmcYardiCredential;
import com.propertyvista.server.jobs.YardiImportProcess;
import com.propertyvista.yardi.YardiServiceException;
import com.propertyvista.yardi.services.YardiResidentTransactionsService;

public class YardiProcessFacadeImpl implements YardiProcessFacade {

    private static final Logger log = LoggerFactory.getLogger(YardiImportProcess.class);

    @Override
    public void doAllImport(StatisticsRecord dynamicStatisticsRecord) {

        PmcYardiCredential yardiCredential = VistaDeployment.getPmcYardiCredential();

        try {
            YardiResidentTransactionsService.getInstance().updateAll(yardiCredential);
            Persistence.service().commit();
        } catch (YardiServiceException e) {
            log.error("Error", e);
        }

    }

    @Override
    public void postAllPayments(StatisticsRecord dynamicStatisticsRecord) {

        // YardiSystemBatchesService.getInstance().postAllPayments(yp);

    }

}
