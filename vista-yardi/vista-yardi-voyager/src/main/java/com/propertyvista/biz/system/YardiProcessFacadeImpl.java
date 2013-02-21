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

import java.rmi.RemoteException;

import com.propertyvista.config.VistaDeployment;
import com.propertyvista.domain.StatisticsRecord;
import com.propertyvista.domain.financial.yardi.YardiReceipt;
import com.propertyvista.domain.financial.yardi.YardiReceiptReversal;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.shared.config.VistaFeatures;
import com.propertyvista.yardi.services.YardiResidentTransactionsService;
import com.propertyvista.yardi.services.YardiSystemBatchesService;

public class YardiProcessFacadeImpl implements YardiProcessFacade {

    @Override
    public void doAllImport(StatisticsRecord dynamicStatisticsRecord) {
        assert VistaFeatures.instance().yardiIntegration();
        try {
            YardiResidentTransactionsService.getInstance().updateAll(VistaDeployment.getPmcYardiCredential(), dynamicStatisticsRecord);
        } catch (YardiServiceException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void postReceiptBatch(StatisticsRecord dynamicStatisticsRecord) {
        assert VistaFeatures.instance().yardiIntegration();
        try {
            YardiSystemBatchesService.getInstance().postReceiptBatch(VistaDeployment.getPmcYardiCredential(), dynamicStatisticsRecord);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void postReceiptReversalBatch(StatisticsRecord dynamicStatisticsRecord) {
        assert VistaFeatures.instance().yardiIntegration();
        try {
            YardiResidentTransactionsService.getInstance().postReceiptReversalBatch(VistaDeployment.getPmcYardiCredential(), dynamicStatisticsRecord);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updateLease(Lease lease) {
        assert VistaFeatures.instance().yardiIntegration();
        try {
            YardiResidentTransactionsService.getInstance().updateLease(VistaDeployment.getPmcYardiCredential(), lease);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void postReceipt(YardiReceipt receipt) throws YardiServiceException, RemoteException {
        assert VistaFeatures.instance().yardiIntegration();
        YardiSystemBatchesService.getInstance().postReceipt(VistaDeployment.getPmcYardiCredential(), receipt);
    }

    @Override
    public void postReceiptReversal(YardiReceiptReversal reversal) {
        assert VistaFeatures.instance().yardiIntegration();
        try {
            YardiResidentTransactionsService.getInstance().postReceiptReversal(VistaDeployment.getPmcYardiCredential(), reversal);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
