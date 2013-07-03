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
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.AttachLevel;

import com.propertyvista.biz.financial.ar.ARException;
import com.propertyvista.biz.system.YardiPaymentBatchContext;
import com.propertyvista.biz.system.YardiServiceException;
import com.propertyvista.domain.financial.yardi.YardiReceipt;
import com.propertyvista.domain.settings.PmcYardiCredential;
import com.propertyvista.yardi.stub.YardiSystemBatchesStub;

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

    public void validateReceipt(PmcYardiCredential yc, YardiReceipt receipt) throws YardiServiceException, RemoteException {
        YardiSystemBatchesStub stub = ServerSideFactory.create(YardiSystemBatchesStub.class);

        Persistence.service().retrieve(receipt.billingAccount());
        Persistence.service().retrieve(receipt.billingAccount().lease());
        Persistence.service().retrieve(receipt.billingAccount().lease().unit());
        Persistence.service().retrieve(receipt.billingAccount().lease().unit().building());

        String propertyCode = receipt.billingAccount().lease().unit().building().propertyCode().getValue();
        long batchId = stub.openReceiptBatch(yc, propertyCode);
        YardiPaymentProcessor paymentProcessor = new YardiPaymentProcessor();
        ResidentTransactions residentTransactions = paymentProcessor.addTransactionToBatch(paymentProcessor.createTransactionForPayment(receipt), null);
        stub.addReceiptsToBatch(yc, batchId, residentTransactions);
        stub.cancelReceiptBatch(yc, batchId);
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

    public void postReceipt(PmcYardiCredential yc, YardiReceipt receipt, YardiPaymentBatchContext paymentBatchContext) throws YardiServiceException,
            RemoteException, ARException {
        YardiSystemBatchesStub stub = ServerSideFactory.create(YardiSystemBatchesStub.class);

        Persistence.ensureRetrieve(receipt.billingAccount(), AttachLevel.Attached);
        Persistence.ensureRetrieve(receipt.billingAccount().lease(), AttachLevel.Attached);
        Persistence.ensureRetrieve(receipt.billingAccount().lease().unit(), AttachLevel.Attached);
        Persistence.ensureRetrieve(receipt.billingAccount().lease().unit().building(), AttachLevel.Attached);

        String propertyCode = receipt.billingAccount().lease().unit().building().propertyCode().getValue();

        boolean singleTrasactionBatch = false;
        if (paymentBatchContext == null) {
            paymentBatchContext = new YardiPaymentBatchContext();
            singleTrasactionBatch = true;
        }

        paymentBatchContext.ensureOpenBatch(yc, propertyCode);

        boolean success = false;
        try {

            YardiPaymentProcessor paymentProcessor = new YardiPaymentProcessor();
            ResidentTransactions residentTransactions = paymentProcessor.addTransactionToBatch(paymentProcessor.createTransactionForPayment(receipt), null);
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
