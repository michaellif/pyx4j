/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 9, 2013
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.yardi.stub;

import java.rmi.RemoteException;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.util.AXIOMUtil;
import org.apache.axis2.AxisFault;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yardi.entity.resident.ResidentTransactions;
import com.yardi.ws.ItfResidentTransactions20_SysBatch;
import com.yardi.ws.ItfResidentTransactions20_SysBatchStub;
import com.yardi.ws.operations.transactionsbatch.AddReceiptsToBatch;
import com.yardi.ws.operations.transactionsbatch.AddReceiptsToBatchResponse;
import com.yardi.ws.operations.transactionsbatch.CancelReceiptBatch;
import com.yardi.ws.operations.transactionsbatch.CancelReceiptBatchResponse;
import com.yardi.ws.operations.transactionsbatch.OpenReceiptBatch;
import com.yardi.ws.operations.transactionsbatch.OpenReceiptBatchResponse;
import com.yardi.ws.operations.transactionsbatch.PostReceiptBatch;
import com.yardi.ws.operations.transactionsbatch.PostReceiptBatchResponse;
import com.yardi.ws.operations.transactionsbatch.TransactionXml_type1;

import com.pyx4j.essentials.j2se.util.MarshallUtil;

import com.propertyvista.biz.system.YardiServiceException;
import com.propertyvista.domain.settings.PmcYardiCredential;
import com.propertyvista.yardi.YardiConstants;
import com.propertyvista.yardi.YardiConstants.Action;
import com.propertyvista.yardi.bean.Messages;

public class YardiSystemBatchesStubImpl extends AbstractYardiStub implements YardiSystemBatchesStub {

    private final static Logger log = LoggerFactory.getLogger(YardiSystemBatchesStubImpl.class);

    @Override
    public long openReceiptBatch(PmcYardiCredential yc, String propertyId) throws RemoteException {
        init(Action.OpenReceiptBatch);
        validateWriteAccess(yc);

        OpenReceiptBatch l = new OpenReceiptBatch();
        l.setUserName(yc.username().getValue());
        l.setPassword(yc.credential().getValue());
        l.setServerName(yc.serverName().getValue());
        l.setDatabase(yc.database().getValue());
        l.setPlatform(yc.platform().getValue().name());
        l.setInterfaceEntity(YardiConstants.INTERFACE_ENTITY);
        l.setYardiPropertyId(propertyId);

        OpenReceiptBatchResponse response = getResidentTransactionsSysBatchService(yc).openReceiptBatch(l);

        long result = response.getOpenReceiptBatchResult();
        log.debug("OpenReceiptBatch: {}", result);
        return result;
    }

    @Override
    public void addReceiptsToBatch(PmcYardiCredential yc, long batchId, ResidentTransactions residentTransactions) throws YardiServiceException,
            RemoteException {
        try {
            init(Action.AddReceiptsToBatch);
            validateWriteAccess(yc);

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
            log.debug("{}", batchXml);
            OMElement element = AXIOMUtil.stringToOM(batchXml);
            transactionXml.setExtraElement(element);

            l.setTransactionXml(transactionXml);

            AddReceiptsToBatchResponse response = getResidentTransactionsSysBatchService(yc).addReceiptsToBatch(l);
            String responseXml = response.getAddReceiptsToBatchResult().getExtraElement().toString();
            log.debug("AddReceiptsToBatch: {}", responseXml);

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

    @Override
    public void postReceiptBatch(PmcYardiCredential yc, long batchId) throws YardiServiceException, RemoteException {
        try {
            init(Action.PostReceiptBatch);
            validateWriteAccess(yc);

            PostReceiptBatch l = new PostReceiptBatch();
            l.setUserName(yc.username().getValue());
            l.setPassword(yc.credential().getValue());
            l.setServerName(yc.serverName().getValue());
            l.setDatabase(yc.database().getValue());
            l.setPlatform(yc.platform().getValue().name());
            l.setInterfaceEntity(YardiConstants.INTERFACE_ENTITY);
            l.setBatchId(batchId);

            PostReceiptBatchResponse response = getResidentTransactionsSysBatchService(yc).postReceiptBatch(l);
            String xml = response.getPostReceiptBatchResult().getExtraElement().toString();
            log.debug("PostReceiptBatch: {}", xml);

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

    @Override
    public void cancelReceiptBatch(PmcYardiCredential yc, long batchId) throws YardiServiceException, RemoteException {
        try {
            init(Action.PostReceiptBatch);
            validateWriteAccess(yc);

            CancelReceiptBatch l = new CancelReceiptBatch();
            l.setUserName(yc.username().getValue());
            l.setPassword(yc.credential().getValue());
            l.setServerName(yc.serverName().getValue());
            l.setDatabase(yc.database().getValue());
            l.setPlatform(yc.platform().getValue().name());
            l.setInterfaceEntity(YardiConstants.INTERFACE_ENTITY);
            l.setBatchId(batchId);

            CancelReceiptBatchResponse response = getResidentTransactionsSysBatchService(yc).cancelReceiptBatch(l);
            String xml = response.getCancelReceiptBatchResult().getExtraElement().toString();
            log.debug("CancelReceiptBatch: {}", xml);

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

    private ItfResidentTransactions20_SysBatch getResidentTransactionsSysBatchService(PmcYardiCredential yc) throws AxisFault {
        ItfResidentTransactions20_SysBatchStub serviceStub = new ItfResidentTransactions20_SysBatchStub(sysBatchServiceURL(yc));
        addMessageContextListener("ResidentTransactions", serviceStub, null);
        setTransportOptions(serviceStub, yc);
        return serviceStub;
    }

    private String sysBatchServiceURL(PmcYardiCredential yc) {
        if (yc.sysBatchServiceURL().isNull()) {
            return serviceWithPath(yc, "webservices/itfresidenttransactions20_SysBatch.asmx");
        } else {
            return yc.sysBatchServiceURL().getValue();
        }
    }
}
