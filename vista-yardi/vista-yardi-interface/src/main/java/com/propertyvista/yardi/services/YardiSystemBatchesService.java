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

    public void postReceipt(PmcYardiCredential yc, YardiReceipt receipt) throws YardiServiceException, RemoteException {
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
        stub.postReceiptBatch(yc, batchId);
    }

}
