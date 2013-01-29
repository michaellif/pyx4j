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

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.List;

import javax.xml.bind.JAXBException;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.stream.XMLStreamException;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.util.AXIOMUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yardi.entity.resident.ResidentTransactions;
import com.yardi.ws.operations.AddReceiptsToBatch;
import com.yardi.ws.operations.AddReceiptsToBatchResponse;
import com.yardi.ws.operations.OpenReceiptBatch;
import com.yardi.ws.operations.OpenReceiptBatchResponse;
import com.yardi.ws.operations.PostReceiptBatch;
import com.yardi.ws.operations.PostReceiptBatchResponse;
import com.yardi.ws.operations.TransactionXml_type1;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.essentials.j2se.util.MarshallUtil;

import com.propertyvista.domain.financial.yardi.YardiReceipt;
import com.propertyvista.domain.settings.PmcYardiCredential;
import com.propertyvista.yardi.YardiClient;
import com.propertyvista.yardi.YardiConstants;
import com.propertyvista.yardi.YardiConstants.Action;
import com.propertyvista.yardi.YardiServiceException;
import com.propertyvista.yardi.bean.Messages;

public class YardiSystemBatchesService extends YardiAbstarctService {

    private final static Logger log = LoggerFactory.getLogger(YardiResidentTransactionsService.class);

    private static class SingletonHolder {
        public static final YardiSystemBatchesService INSTANCE = new YardiSystemBatchesService();
    }

    private YardiSystemBatchesService() {
    }

    public static YardiSystemBatchesService getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public void postReceiptBatch(PmcYardiCredential yc) throws YardiServiceException, XMLStreamException, IOException, JAXBException,
            DatatypeConfigurationException {

        List<String> propertyCodes = getPropertyCodes(new YardiClient(yc.residentTransactionsServiceURL().getValue()), yc);

        YardiClient client = new YardiClient(yc.sysBatchServiceURL().getValue());

        log.info("Get properties information...");
        for (String propertyCode : propertyCodes) {
            long batchId = openReceiptBatch(client, yc, propertyCode);
            ResidentTransactions residentTransactions = new YardiPaymentProcessor().getPaymentTransactionsForProperty(propertyCode);
            if (residentTransactions.getProperty().size() == 0) {
                continue;
            }
            String xml = MarshallUtil.marshall(residentTransactions);
            log.info(xml);
            addReceiptsToBatch(client, yc, batchId, xml);
            postReceiptBatch(client, yc, batchId);
            Persistence.service().commit();
        }
    }

    public void postReceipt(PmcYardiCredential yc, YardiReceipt receipt) throws RemoteException, JAXBException, XMLStreamException {
        YardiClient client = new YardiClient(yc.sysBatchServiceURL().getValue());

        YardiPaymentProcessor paymentProcessor = new YardiPaymentProcessor();
        ResidentTransactions residentTransactions = paymentProcessor.addTransactionToBatch(paymentProcessor.createTransactionForPayment(receipt), null);

        String propertyCode = receipt.billingAccount().lease().unit().building().propertyCode().getValue();
        long batchId = openReceiptBatch(client, yc, propertyCode);
        if (residentTransactions.getProperty().size() > 0) {
            String xml = MarshallUtil.marshall(residentTransactions);
            log.info(xml);
            addReceiptsToBatch(client, yc, batchId, xml);
            postReceiptBatch(client, yc, batchId);
            Persistence.service().commit();
        }
    }

    private long openReceiptBatch(YardiClient c, PmcYardiCredential yc, String propertyId) throws RemoteException {
        c.transactionId++;
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

    private Messages addReceiptsToBatch(YardiClient c, PmcYardiCredential yc, long batchId, String batchXml) throws RemoteException, XMLStreamException,
            JAXBException {
        c.transactionId++;
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
        OMElement element = AXIOMUtil.stringToOM(batchXml);
        transactionXml.setExtraElement(element);
        l.setTransactionXml(transactionXml);

        AddReceiptsToBatchResponse response = c.getResidentTransactionsSysBatchService().addReceiptsToBatch(l);
        String xml = response.getAddReceiptsToBatchResult().getExtraElement().toString();
        log.info("AddReceiptsToBatch: {}", xml);

        Messages messages = MarshallUtil.unmarshal(Messages.class, xml);
        log.info(YardiServiceUtils.toString(messages));

        return messages;
    }

    private Messages postReceiptBatch(YardiClient c, PmcYardiCredential yc, long batchId) throws RemoteException, JAXBException {
        c.transactionId++;
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
        log.info(YardiServiceUtils.toString(messages));

        return messages;
    }

}
