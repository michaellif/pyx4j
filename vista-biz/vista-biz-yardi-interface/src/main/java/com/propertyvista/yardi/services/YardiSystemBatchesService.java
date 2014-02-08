/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 17, 2013
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.yardi.services;

import java.rmi.RemoteException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yardi.entity.resident.ResidentTransactions;

import com.pyx4j.config.server.ServerSideFactory;

import com.propertyvista.biz.financial.ar.ARException;
import com.propertyvista.biz.system.YardiPaymentBatchContext;
import com.propertyvista.biz.system.YardiServiceException;
import com.propertyvista.domain.financial.yardi.YardiReceipt;
import com.propertyvista.domain.settings.PmcYardiCredential;
import com.propertyvista.yardi.processors.YardiPaymentProcessor;
import com.propertyvista.yardi.stubs.YardiSystemBatchesStub;

public class YardiSystemBatchesService extends YardiAbstractService {

    private final static Logger log = LoggerFactory.getLogger(YardiResidentTransactionsService.class);

    private static class SingletonHolder {
        public static final YardiSystemBatchesService INSTANCE = new YardiSystemBatchesService();
    }

    private YardiSystemBatchesService() {
    }

    public static YardiSystemBatchesService getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public long openReceiptBatch(PmcYardiCredential yc, String propertyCode) throws RemoteException, YardiServiceException {
        YardiSystemBatchesStub stub = ServerSideFactory.create(YardiSystemBatchesStub.class);
        return stub.openReceiptBatch(yc, propertyCode);
    }

    public void cancelBatch(PmcYardiCredential yc, YardiPaymentBatchContext paymentBatchContext) throws RemoteException, YardiServiceException {
        ServerSideFactory.create(YardiSystemBatchesStub.class).cancelReceiptBatch(yc, paymentBatchContext.getBatchId());
    }

    public void postBatch(PmcYardiCredential yc, YardiPaymentBatchContext paymentBatchContext) throws RemoteException, YardiServiceException {
        ServerSideFactory.create(YardiSystemBatchesStub.class).postReceiptBatch(yc, paymentBatchContext.getBatchId());
    }

    public void postReceipt(PmcYardiCredential yc, YardiReceipt receipt, String propertyCode, YardiPaymentBatchContext paymentBatchContext)
            throws YardiServiceException, RemoteException, ARException {
        YardiSystemBatchesStub stub = ServerSideFactory.create(YardiSystemBatchesStub.class);

        boolean singleTrasactionBatch = false;
        if (paymentBatchContext == null) {
            paymentBatchContext = new YardiPaymentBatchContext();
            singleTrasactionBatch = true;
        }

        paymentBatchContext.ensureOpenBatch(yc, propertyCode);

        boolean success = false;
        try {

            YardiPaymentProcessor paymentProcessor = new YardiPaymentProcessor();
            ResidentTransactions residentTransactions = paymentProcessor.createTransactions(paymentProcessor.createTransactionForPayment(receipt));
            stub.addReceiptsToBatch(yc, paymentBatchContext.getBatchId(), residentTransactions);

            paymentBatchContext.incrementRecordCount();

            if (singleTrasactionBatch) {
                paymentBatchContext.postBatch();
            }
            success = true;
        } finally {
            if (singleTrasactionBatch && !success) {
                log.debug("Single transaction {} failed, call CancelReceiptBatch", receipt.id().getValue());
                paymentBatchContext.cancelBatch();
            }
        }
    }

}
