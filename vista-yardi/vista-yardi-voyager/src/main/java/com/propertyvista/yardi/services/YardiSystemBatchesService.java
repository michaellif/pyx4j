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

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.util.AXIOMUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yardi.entity.resident.ResidentTransactions;
import com.yardi.ws.operations.AddReceiptsToBatch;
import com.yardi.ws.operations.AddReceiptsToBatchResponse;
import com.yardi.ws.operations.CancelReceiptBatch;
import com.yardi.ws.operations.CancelReceiptBatchResponse;
import com.yardi.ws.operations.OpenReceiptBatch;
import com.yardi.ws.operations.OpenReceiptBatchResponse;
import com.yardi.ws.operations.PostReceiptBatch;
import com.yardi.ws.operations.PostReceiptBatchResponse;
import com.yardi.ws.operations.TransactionXml_type1;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.essentials.j2se.util.MarshallUtil;

import com.propertyvista.biz.system.YardiServiceException;
import com.propertyvista.domain.financial.yardi.YardiReceipt;
import com.propertyvista.domain.settings.PmcYardiCredential;
import com.propertyvista.yardi.YardiClient;
import com.propertyvista.yardi.YardiConstants;
import com.propertyvista.yardi.YardiConstants.Action;
import com.propertyvista.yardi.bean.Messages;

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
        YardiClient client = ServerSideFactory.create(YardiClient.class);
        client.setPmcYardiCredential(yc);

        Persistence.service().retrieve(receipt.billingAccount());
        Persistence.service().retrieve(receipt.billingAccount().lease());
        Persistence.service().retrieve(receipt.billingAccount().lease().unit());
        Persistence.service().retrieve(receipt.billingAccount().lease().unit().building());

        String propertyCode = receipt.billingAccount().lease().unit().building().propertyCode().getValue();
        long batchId = openReceiptBatch(client, yc, propertyCode);
        YardiPaymentProcessor paymentProcessor = new YardiPaymentProcessor();
        ResidentTransactions residentTransactions = paymentProcessor.addTransactionToBatch(paymentProcessor.createTransactionForPayment(receipt), null);
        addReceiptsToBatch(client, yc, batchId, residentTransactions);
        cancelReceiptBatch(client, yc, batchId);
    }

    public void postReceipt(PmcYardiCredential yc, YardiReceipt receipt) throws YardiServiceException, RemoteException {
        YardiClient client = ServerSideFactory.create(YardiClient.class);
        client.setPmcYardiCredential(yc);

        Persistence.service().retrieve(receipt.billingAccount());
        Persistence.service().retrieve(receipt.billingAccount().lease());
        Persistence.service().retrieve(receipt.billingAccount().lease().unit());
        Persistence.service().retrieve(receipt.billingAccount().lease().unit().building());

        String propertyCode = receipt.billingAccount().lease().unit().building().propertyCode().getValue();
        long batchId = openReceiptBatch(client, yc, propertyCode);
        YardiPaymentProcessor paymentProcessor = new YardiPaymentProcessor();
        ResidentTransactions residentTransactions = paymentProcessor.addTransactionToBatch(paymentProcessor.createTransactionForPayment(receipt), null);
        addReceiptsToBatch(client, yc, batchId, residentTransactions);
        postReceiptBatch(client, yc, batchId);
    }

    private long openReceiptBatch(YardiClient c, PmcYardiCredential yc, String propertyId) throws RemoteException {

        c.transactionIdStart();
        c.setCurrentAction(Action.OpenReceiptBatch);

        OpenReceiptBatch l = new OpenReceiptBatch();
        l.setUserName(yc.username().getValue());
        l.setPassword(yc.credential().getValue());
        l.setServerName(yc.serverName().getValue());
        l.setDatabase(yc.database().getValue());
        l.setPlatform(yc.platform().getValue().name());
        l.setInterfaceEntity(YardiConstants.INTERFACE_ENTITY);
        l.setYardiPropertyId(propertyId);

        OpenReceiptBatchResponse response = c.getResidentTransactionsSysBatchService().openReceiptBatch(l);

        long result = response.getOpenReceiptBatchResult();
        log.info("OpenReceiptBatch: {}", result);
        return result;

    }

    private void addReceiptsToBatch(YardiClient c, PmcYardiCredential yc, long batchId, ResidentTransactions residentTransactions)
            throws YardiServiceException, RemoteException {
        try {
            c.transactionIdStart();
            c.setCurrentAction(Action.AddReceiptsToBatch);

            AddReceiptsToBatch l = new AddReceiptsToBatch();
            l.setUserName(yc.username().getValue());
            l.setPassword(yc.credential().getValue());
            l.setServerName(yc.serverName().getValue());
            l.setDatabase(yc.database().getValue());
            l.setPlatform(yc.platform().getValue().name());
            l.setInterfaceEntity(YardiConstants.INTERFACE_ENTITY);
            l.setBatchId(batchId);

            TransactionXml_type1 transactionXml = new TransactionXml_type1();

            String batchXml = MarshallUtil.marshall(residentTransactions);
            log.info(batchXml);
            OMElement element = AXIOMUtil.stringToOM(batchXml);
            transactionXml.setExtraElement(element);

            l.setTransactionXml(transactionXml);

            AddReceiptsToBatchResponse response = c.getResidentTransactionsSysBatchService().addReceiptsToBatch(l);
            String responseXml = response.getAddReceiptsToBatchResult().getExtraElement().toString();
            log.info("AddReceiptsToBatch: {}", responseXml);

            Messages messages = MarshallUtil.unmarshal(Messages.class, responseXml);
            if (messages.isError()) {
                throw new YardiServiceException(messages.toString());
            } else {
                log.info(messages.toString());
            }
        } catch (JAXBException e) {
            throw new Error(e);
        } catch (XMLStreamException e) {
            throw new Error(e);
        }
    }

    private void postReceiptBatch(YardiClient c, PmcYardiCredential yc, long batchId) throws YardiServiceException, RemoteException {
        try {
            c.transactionIdStart();
            c.setCurrentAction(Action.PostReceiptBatch);

            PostReceiptBatch l = new PostReceiptBatch();
            l.setUserName(yc.username().getValue());
            l.setPassword(yc.credential().getValue());
            l.setServerName(yc.serverName().getValue());
            l.setDatabase(yc.database().getValue());
            l.setPlatform(yc.platform().getValue().name());
            l.setInterfaceEntity(YardiConstants.INTERFACE_ENTITY);
            l.setBatchId(batchId);

            PostReceiptBatchResponse response = c.getResidentTransactionsSysBatchService().postReceiptBatch(l);
            String xml = response.getPostReceiptBatchResult().getExtraElement().toString();
            log.info("PostReceiptBatch: {}", xml);

            Messages messages = MarshallUtil.unmarshal(Messages.class, xml);
            if (messages.isError()) {
                throw new YardiServiceException(messages.toString());
            } else {
                log.info(messages.toString());
            }
        } catch (JAXBException e) {
            throw new Error(e);
        }
    }

    private void cancelReceiptBatch(YardiClient c, PmcYardiCredential yc, long batchId) throws YardiServiceException, RemoteException {
        try {
            c.transactionIdStart();
            c.setCurrentAction(Action.PostReceiptBatch);

            CancelReceiptBatch l = new CancelReceiptBatch();
            l.setUserName(yc.username().getValue());
            l.setPassword(yc.credential().getValue());
            l.setServerName(yc.serverName().getValue());
            l.setDatabase(yc.database().getValue());
            l.setPlatform(yc.platform().getValue().name());
            l.setInterfaceEntity(YardiConstants.INTERFACE_ENTITY);
            l.setBatchId(batchId);

            CancelReceiptBatchResponse response = c.getResidentTransactionsSysBatchService().cancelReceiptBatch(l);
            String xml = response.getCancelReceiptBatchResult().getExtraElement().toString();
            log.info("CancelReceiptBatch: {}", xml);

            Messages messages = MarshallUtil.unmarshal(Messages.class, xml);
            if (messages.isError()) {
                throw new YardiServiceException(messages.toString());
            } else {
                log.info(messages.toString());
            }
        } catch (JAXBException e) {
            throw new Error(e);
        }
    }
}
