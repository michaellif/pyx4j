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

import javax.xml.bind.JAXBException;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.stream.XMLStreamException;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.util.AXIOMUtil;
import org.apache.axis2.AxisFault;
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

import com.propertyvista.domain.settings.PmcYardiCredential;
import com.propertyvista.yardi.YardiClient;
import com.propertyvista.yardi.YardiConstants;
import com.propertyvista.yardi.YardiConstants.Action;
import com.propertyvista.yardi.YardiServiceException;

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

    public void postAllPayments(PmcYardiCredential yc) throws YardiServiceException, XMLStreamException, IOException, JAXBException,
            DatatypeConfigurationException {
        YardiClient client = new YardiClient(yc.sysBatchServiceURL().getValue());

        log.info("Get properties information...");
        for (String propertyCode : getPropertyCodes(client, yc)) {
            long batchId = openReceiptBatch(client, yc, propertyCode);
            ResidentTransactions residentTransactions = new YardiPaymentProcessor().getAllPaymentTransactions(propertyCode);
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

    private long openReceiptBatch(YardiClient c, PmcYardiCredential yc, String propertyId) throws AxisFault, RemoteException {
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

    private void addReceiptsToBatch(YardiClient c, PmcYardiCredential yc, long batchId, String batchXml) throws AxisFault, RemoteException, XMLStreamException {
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
    }

    private void postReceiptBatch(YardiClient c, PmcYardiCredential yc, long batchId) throws AxisFault, RemoteException {
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
    }
}
