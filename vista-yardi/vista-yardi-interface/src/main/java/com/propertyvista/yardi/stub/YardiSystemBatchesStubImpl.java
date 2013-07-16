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

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.essentials.j2se.util.MarshallUtil;

import com.propertyvista.biz.system.YardiServiceException;
import com.propertyvista.biz.system.encryption.PasswordEncryptorFacade;
import com.propertyvista.domain.settings.PmcYardiCredential;
import com.propertyvista.yardi.YardiConstants;
import com.propertyvista.yardi.YardiConstants.Action;
import com.propertyvista.yardi.bean.Messages;

public class YardiSystemBatchesStubImpl extends AbstractYardiStub implements YardiSystemBatchesStub {

    private final static Logger log = LoggerFactory.getLogger(YardiSystemBatchesStubImpl.class);

    @Override
    public long openReceiptBatch(PmcYardiCredential yc, String propertyId) throws RemoteException, YardiServiceException {
        boolean success = false;
        try {
            init(Action.OpenReceiptBatch);
            validateWriteAccess(yc);

            OpenReceiptBatch request = new OpenReceiptBatch();
            request.setUserName(yc.username().getValue());
            request.setPassword(ServerSideFactory.create(PasswordEncryptorFacade.class).decryptPassword(yc.password()));
            request.setServerName(yc.serverName().getValue());
            request.setDatabase(yc.database().getValue());
            request.setPlatform(yc.platform().getValue().name());
            request.setInterfaceEntity(YardiConstants.INTERFACE_ENTITY);
            request.setYardiPropertyId(propertyId);

            OpenReceiptBatchResponse response = getResidentTransactionsSysBatchService(yc).openReceiptBatch(request);

            long result = response.getOpenReceiptBatchResult();
            log.debug("OpenReceiptBatch: {}", result);
            if (result == 0) {
                throw new YardiServiceException("Could not open Receipt batch");
            } else if (result == -1) {
                throw new YardiServiceException("Web service user has insufficient privileges for this operation");
            } else if (result == -2) {
                throw new YardiServiceException("Interface Entity does not have access to Yardi Property " + propertyId);
            }

            success = true;
            return result;
        } finally {
            if (!success) {
                log.warn("Yardi transaction recorded at {}", recordedTracastionsLogs);
            }
        }
    }

    @Override
    public void addReceiptsToBatch(PmcYardiCredential yc, long batchId, ResidentTransactions residentTransactions) throws YardiServiceException,
            RemoteException {
        boolean success = false;
        try {
            init(Action.AddReceiptsToBatch);
            validateWriteAccess(yc);

            AddReceiptsToBatch request = new AddReceiptsToBatch();
            request.setUserName(yc.username().getValue());
            request.setPassword(ServerSideFactory.create(PasswordEncryptorFacade.class).decryptPassword(yc.password()));
            request.setServerName(yc.serverName().getValue());
            request.setDatabase(yc.database().getValue());
            request.setPlatform(yc.platform().getValue().name());
            request.setInterfaceEntity(YardiConstants.INTERFACE_ENTITY);
            request.setBatchId(batchId);

            TransactionXml_type1 transactionXml = new TransactionXml_type1();

            String batchXml = MarshallUtil.marshall(residentTransactions);
            log.debug("{}", batchXml);
            OMElement element = AXIOMUtil.stringToOM(batchXml);
            transactionXml.setExtraElement(element);

            request.setTransactionXml(transactionXml);

            AddReceiptsToBatchResponse response = getResidentTransactionsSysBatchService(yc).addReceiptsToBatch(request);
            if ((response == null) || (response.getAddReceiptsToBatchResult() == null) || (response.getAddReceiptsToBatchResult().getExtraElement() == null)) {
                throw new YardiServiceException("addReceiptsToBatch received NULL response");
            }
            String responseXml = response.getAddReceiptsToBatchResult().getExtraElement().toString();
            log.debug("AddReceiptsToBatch: {}", responseXml);

            Messages messages = MarshallUtil.unmarshal(Messages.class, responseXml);
            if (messages.isError()) {
                throw new YardiServiceException(messages.toString());
            } else {
                log.debug(messages.toString());
            }
            success = true;
        } catch (JAXBException e) {
            throw new Error(e);
        } catch (XMLStreamException e) {
            throw new Error(e);
        } finally {
            if (!success) {
                log.warn("Yardi transaction recorded at {}", recordedTracastionsLogs);
            }
        }
    }

    @Override
    public void postReceiptBatch(PmcYardiCredential yc, long batchId) throws YardiServiceException, RemoteException {
        boolean success = false;
        try {
            init(Action.PostReceiptBatch);
            validateWriteAccess(yc);

            PostReceiptBatch request = new PostReceiptBatch();
            request.setUserName(yc.username().getValue());
            request.setPassword(ServerSideFactory.create(PasswordEncryptorFacade.class).decryptPassword(yc.password()));
            request.setServerName(yc.serverName().getValue());
            request.setDatabase(yc.database().getValue());
            request.setPlatform(yc.platform().getValue().name());
            request.setInterfaceEntity(YardiConstants.INTERFACE_ENTITY);
            request.setBatchId(batchId);

            PostReceiptBatchResponse response = getResidentTransactionsSysBatchService(yc).postReceiptBatch(request);
            if ((response == null) || (response.getPostReceiptBatchResult() == null) || (response.getPostReceiptBatchResult().getExtraElement() == null)) {
                throw new YardiServiceException("postReceiptBatch received NULL response");
            }
            String xml = response.getPostReceiptBatchResult().getExtraElement().toString();
            log.debug("PostReceiptBatch: {}", xml);

            Messages messages = MarshallUtil.unmarshal(Messages.class, xml);
            if (messages.isError()) {
                throw new YardiServiceException(messages.toString());
            } else {
                log.debug(messages.toString());
            }
            success = true;
        } catch (JAXBException e) {
            throw new Error(e);
        } finally {
            if (!success) {
                log.warn("Yardi transaction recorded at {}", recordedTracastionsLogs);
            }
        }
    }

    @Override
    public void cancelReceiptBatch(PmcYardiCredential yc, long batchId) throws YardiServiceException, RemoteException {
        boolean success = false;
        try {
            init(Action.CancelReceiptBatch);
            validateWriteAccess(yc);

            CancelReceiptBatch request = new CancelReceiptBatch();
            request.setUserName(yc.username().getValue());
            request.setPassword(ServerSideFactory.create(PasswordEncryptorFacade.class).decryptPassword(yc.password()));
            request.setServerName(yc.serverName().getValue());
            request.setDatabase(yc.database().getValue());
            request.setPlatform(yc.platform().getValue().name());
            request.setInterfaceEntity(YardiConstants.INTERFACE_ENTITY);
            request.setBatchId(batchId);

            CancelReceiptBatchResponse response = getResidentTransactionsSysBatchService(yc).cancelReceiptBatch(request);
            if ((response == null) || (response.getCancelReceiptBatchResult() == null) || (response.getCancelReceiptBatchResult().getExtraElement() == null)) {
                throw new YardiServiceException("cancelReceiptBatch received NULL response");
            }
            String xml = response.getCancelReceiptBatchResult().getExtraElement().toString();
            log.debug("CancelReceiptBatch: {}", xml);

            Messages messages = MarshallUtil.unmarshal(Messages.class, xml);
            if (messages.isError()) {
                throw new YardiServiceException(messages.toString());
            } else {
                log.debug(messages.toString());
            }
            success = true;
        } catch (JAXBException e) {
            throw new Error(e);
        } finally {
            if (!success) {
                log.warn("Yardi transaction recorded at {}", recordedTracastionsLogs);
            }
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
