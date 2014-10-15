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
package com.propertyvista.yardi.stubs;

import java.rmi.RemoteException;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

import org.apache.axiom.om.util.AXIOMUtil;
import org.apache.axis2.AxisFault;

import com.yardi.entity.resident.ResidentTransactions;
import com.yardi.ws.ItfResidentTransactions20_SysBatch;
import com.yardi.ws.ItfResidentTransactions20_SysBatchStub;
import com.yardi.ws.operations.transactionsbatch.AddReceiptsToBatch;
import com.yardi.ws.operations.transactionsbatch.AddReceiptsToBatchResponse;
import com.yardi.ws.operations.transactionsbatch.CancelReceiptBatch;
import com.yardi.ws.operations.transactionsbatch.CancelReceiptBatchResponse;
import com.yardi.ws.operations.transactionsbatch.GetVersionNumber;
import com.yardi.ws.operations.transactionsbatch.GetVersionNumberResponse;
import com.yardi.ws.operations.transactionsbatch.OpenReceiptBatch;
import com.yardi.ws.operations.transactionsbatch.OpenReceiptBatchResponse;
import com.yardi.ws.operations.transactionsbatch.Ping;
import com.yardi.ws.operations.transactionsbatch.PingResponse;
import com.yardi.ws.operations.transactionsbatch.PostReceiptBatch;
import com.yardi.ws.operations.transactionsbatch.PostReceiptBatchResponse;
import com.yardi.ws.operations.transactionsbatch.TransactionXml_type0;

import com.pyx4j.essentials.j2se.util.MarshallUtil;

import com.propertyvista.biz.system.yardi.YardiServiceException;
import com.propertyvista.domain.settings.PmcYardiCredential;
import com.propertyvista.yardi.YardiConstants;
import com.propertyvista.yardi.YardiConstants.Action;
import com.propertyvista.yardi.YardiInterfaceType;

class YardiSystemBatchesStubImpl extends AbstractYardiStub implements YardiSystemBatchesStub {

    @Override
    public long openReceiptBatch(PmcYardiCredential yc, String propertyId) throws RemoteException {
        init(yc, Action.OpenReceiptBatch);
        validateWriteAccess(yc);

        OpenReceiptBatch request = new OpenReceiptBatch();
        request.setUserName(yc.username().getValue());
        request.setPassword(yc.password().number().getValue());
        request.setServerName(yc.serverName().getValue());
        request.setDatabase(yc.database().getValue());
        request.setPlatform(yc.platform().getValue().name());
        request.setInterfaceEntity(YardiConstants.INTERFACE_ENTITY);
        request.setInterfaceLicense(YardiLicense.getInterfaceLicense(YardiInterfaceType.BillingAndPayments, yc));
        request.setYardiPropertyId(propertyId);

        OpenReceiptBatchResponse response = getResidentTransactionsSysBatchService(yc).openReceiptBatch(request);
        return response.getOpenReceiptBatchResult();
    }

    @Override
    public void addReceiptsToBatch(PmcYardiCredential yc, long batchId, ResidentTransactions residentTransactions) throws YardiServiceException,
            RemoteException {
        init(yc, Action.AddReceiptsToBatch);
        addReceiptsToBatchImpl(yc, batchId, residentTransactions);
    }

    @Override
    public void addReceiptsReversalToBatch(PmcYardiCredential yc, long batchId, ResidentTransactions residentTransactions) throws YardiServiceException,
            RemoteException {
        init(yc, Action.AddReceiptsReversalToBatch);
        addReceiptsToBatchImpl(yc, batchId, residentTransactions);
    }

    private void addReceiptsToBatchImpl(PmcYardiCredential yc, long batchId, ResidentTransactions residentTransactions) throws YardiServiceException,
            RemoteException {
        validateWriteAccess(yc);

        AddReceiptsToBatch request = new AddReceiptsToBatch();
        request.setUserName(yc.username().getValue());
        request.setPassword(yc.password().number().getValue());
        request.setServerName(yc.serverName().getValue());
        request.setDatabase(yc.database().getValue());
        request.setPlatform(yc.platform().getValue().name());
        request.setInterfaceEntity(YardiConstants.INTERFACE_ENTITY);
        request.setInterfaceLicense(YardiLicense.getInterfaceLicense(YardiInterfaceType.BillingAndPayments, yc));
        request.setBatchId(batchId);

        try {
            String batchXml = MarshallUtil.marshall(residentTransactions);
            TransactionXml_type0 transactionXml = new TransactionXml_type0();
            transactionXml.setExtraElement(AXIOMUtil.stringToOM(batchXml));
            request.setTransactionXml(transactionXml);

            AddReceiptsToBatchResponse response = getResidentTransactionsSysBatchService(yc).addReceiptsToBatch(request);
            String xml = response.getAddReceiptsToBatchResult().getExtraElement().toString();
            ensureValid(xml);
        } catch (JAXBException e) {
            throw new Error(e);
        } catch (XMLStreamException e) {
            throw new Error(e);
        }
    }

    @Override
    public void postReceiptBatch(PmcYardiCredential yc, long batchId) throws YardiServiceException, RemoteException {
        init(yc, Action.PostReceiptBatch);
        validateWriteAccess(yc);

        PostReceiptBatch request = new PostReceiptBatch();
        request.setUserName(yc.username().getValue());
        request.setPassword(yc.password().number().getValue());
        request.setServerName(yc.serverName().getValue());
        request.setDatabase(yc.database().getValue());
        request.setPlatform(yc.platform().getValue().name());
        request.setInterfaceEntity(YardiConstants.INTERFACE_ENTITY);
        request.setInterfaceLicense(YardiLicense.getInterfaceLicense(YardiInterfaceType.BillingAndPayments, yc));
        request.setBatchId(batchId);

        PostReceiptBatchResponse response = getResidentTransactionsSysBatchService(yc).postReceiptBatch(request);
        String xml = response.getPostReceiptBatchResult().getExtraElement().toString();
        ensureValid(xml);
    }

    @Override
    public void cancelReceiptBatch(PmcYardiCredential yc, long batchId) throws YardiServiceException, RemoteException {
        init(yc, Action.CancelReceiptBatch);
        validateWriteAccess(yc);

        CancelReceiptBatch request = new CancelReceiptBatch();
        request.setUserName(yc.username().getValue());
        request.setPassword(yc.password().number().getValue());
        request.setServerName(yc.serverName().getValue());
        request.setDatabase(yc.database().getValue());
        request.setPlatform(yc.platform().getValue().name());
        request.setInterfaceEntity(YardiConstants.INTERFACE_ENTITY);
        request.setInterfaceLicense(YardiLicense.getInterfaceLicense(YardiInterfaceType.BillingAndPayments, yc));
        request.setBatchId(batchId);

        CancelReceiptBatchResponse response = getResidentTransactionsSysBatchService(yc).cancelReceiptBatch(request);
        String xml = response.getCancelReceiptBatchResult().getExtraElement().toString();
        ensureValid(xml);
    }

    @Override
    public String ping(PmcYardiCredential yc) {
        try {
            init(yc, Action.Ping);
            PingResponse pr = getResidentTransactionsSysBatchService(yc).ping(new Ping());
            return pr.getPingResult();
        } catch (RemoteException e) {
            throw new Error(e);
        }
    }

    @Override
    public void validate(PmcYardiCredential yc) throws YardiServiceException {
        // no READ-ONLY method available to use for validation
    }

    @Override
    public String getPluginVersion(PmcYardiCredential yc) {
        try {
            init(yc, Action.GetVersionNumber);
            GetVersionNumberResponse response = getResidentTransactionsSysBatchService(yc).getVersionNumber(new GetVersionNumber());
            return response.getGetVersionNumberResult();
        } catch (RemoteException e) {
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
