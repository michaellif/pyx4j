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

import com.propertyvista.config.VistaDeployment;
import com.propertyvista.domain.StatisticsRecord;
import com.propertyvista.domain.financial.yardi.YardiReceipt;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.shared.config.VistaFeatures;
import com.propertyvista.yardi.YardiServiceException;
import com.propertyvista.yardi.services.YardiResidentTransactionsService;
import com.propertyvista.yardi.services.YardiSystemBatchesService;

public class YardiProcessFacadeImpl implements YardiProcessFacade {

    private static final Logger log = LoggerFactory.getLogger(YardiProcessFacadeImpl.class);

    @Override
    public void doAllImport(StatisticsRecord dynamicStatisticsRecord) {
        assert VistaFeatures.instance().yardiIntegration();
        try {
            YardiResidentTransactionsService.getInstance().updateAll(VistaDeployment.getPmcYardiCredential());
        } catch (YardiServiceException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void postAllPayments(StatisticsRecord dynamicStatisticsRecord) {
        assert VistaFeatures.instance().yardiIntegration();
        try {
            YardiSystemBatchesService.getInstance().postAllPayments(VistaDeployment.getPmcYardiCredential());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void postAllNSF(StatisticsRecord dynamicStatisticsRecord) {
        assert VistaFeatures.instance().yardiIntegration();
        try {
            YardiResidentTransactionsService.getInstance().postAllNSF(VistaDeployment.getPmcYardiCredential());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updateLease(StatisticsRecord dynamicStatisticsRecord, Lease lease) {
        YardiResidentTransactionsService.getInstance().updateLease(VistaDeployment.getPmcYardiCredential(), lease);
    }

    @Override
    public void postReceipt(StatisticsRecord dynamicStatisticsRecord, YardiReceipt receipt) {
        // TODO Auto-generated method stub

    }

    @Override
    public void postReceiptReversal(StatisticsRecord dynamicStatisticsRecord, YardiReceipt receipt, boolean isNSF) {
        // TODO Auto-generated method stub

    }
}
