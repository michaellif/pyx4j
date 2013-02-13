/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 13, 2013
 * @author Mykola
 * @version $Id$
 */
package com.propertyvista.yardi.services;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.util.AXIOMUtil;
import org.apache.axis2.AxisFault;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yardi.entity.resident.ResidentTransactions;
import com.yardi.ws.operations.GetResidentTransaction_Login;
import com.yardi.ws.operations.GetResidentTransaction_LoginResponse;
import com.yardi.ws.operations.GetResidentTransactions_Login;
import com.yardi.ws.operations.GetResidentTransactions_LoginResponse;
import com.yardi.ws.operations.ImportResidentTransactions_Login;
import com.yardi.ws.operations.ImportResidentTransactions_LoginResponse;
import com.yardi.ws.operations.TransactionXml_type1;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.essentials.j2se.util.MarshallUtil;

import com.propertyvista.domain.StatisticsRecord;
import com.propertyvista.domain.financial.yardi.YardiReceiptReversal;
import com.propertyvista.domain.settings.PmcYardiCredential;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.server.jobs.StatisticsUtils;
import com.propertyvista.yardi.YardiClient;
import com.propertyvista.yardi.YardiConstants;
import com.propertyvista.yardi.YardiConstants.Action;
import com.propertyvista.yardi.YardiServiceException;
import com.propertyvista.yardi.bean.Messages;

/**
 * Implementation functionality for updating properties/units/leases/tenants basing on getResidentTransactions from YARDI api
 * 
 * @author Mykola
 * 
 */
public class YardiResidentTransactionsService extends YardiAbstarctService {

    private final static Logger log = LoggerFactory.getLogger(YardiResidentTransactionsService.class);

    private static class SingletonHolder {
        public static final YardiResidentTransactionsService INSTANCE = new YardiResidentTransactionsService();
    }

    private YardiResidentTransactionsService() {
    }

    public static YardiResidentTransactionsService getInstance() {
        return SingletonHolder.INSTANCE;
    }

    /**
     * Updates/creates entities basing on data from YARDI System.
     * 
     * @param yp
     *            the YARDI System connection parameters
     * @throws YardiServiceException
     *             if operation fails
     */
    public void updateAll(PmcYardiCredential yc, StatisticsRecord dynamicStatisticsRecord) throws YardiServiceException {

        YardiClient client = new YardiClient(yc.residentTransactionsServiceURL().getValue());

        List<ResidentTransactions> allTransactions;
        if (yc.propertyCode().isNull()) {
            List<String> propertyCodes = getPropertyCodes(client, yc);
            allTransactions = getAllResidentTransactions(client, yc, propertyCodes);
        } else {
            List<String> propertyCodes = Arrays.asList(new String[] { yc.propertyCode().getValue() });
            allTransactions = getAllResidentTransactions(client, yc, propertyCodes);
        }

        updateBuildings(allTransactions, dynamicStatisticsRecord);

        updateLeases(allTransactions, dynamicStatisticsRecord);

        updateCharges(allTransactions, dynamicStatisticsRecord);

        updatePayments(allTransactions, dynamicStatisticsRecord);

        Persistence.service().commit();

        log.info("Update completed.");
    }

    public void updateLease(PmcYardiCredential yc, Lease lease) throws YardiServiceException {
        Persistence.service().retrieve(lease.unit().building());
        YardiClient client = new YardiClient(yc.residentTransactionsServiceURL().getValue());
        ResidentTransactions transactions = getResidentTransaction(client, yc, lease.unit().building().propertyCode().getValue(), lease.leaseId().getValue());
        if (transactions != null) {
            new YardiLeaseProcessor().updateLeases(transactions);
            new YardiChargeProcessor().updateCharges(transactions);
            new YardiPaymentProcessor().updatePayments(transactions);
            Persistence.service().commit();
        }

    }

    public void postReceiptReversal(PmcYardiCredential yc, YardiReceiptReversal reversal) throws YardiServiceException {
        YardiClient client = new YardiClient(yc.residentTransactionsServiceURL().getValue());

        YardiPaymentProcessor paymentProcessor = new YardiPaymentProcessor();
        ResidentTransactions reversalTransactions = paymentProcessor.addTransactionToBatch(paymentProcessor.createTransactionForReversal(reversal), null);

        importResidentTransactions(client, yc, reversalTransactions);

        paymentProcessor.onPostReceiptReversalSuccess(reversal);
    }

    public void postReceiptReversalBatch(PmcYardiCredential yc, StatisticsRecord dynamicStatisticsRecord) {
        for (YardiReceiptReversal reversals : new YardiPaymentProcessor().getAllReceiptReversals()) {
            try {
                postReceiptReversal(yc, reversals);
                StatisticsUtils.addProcessed(dynamicStatisticsRecord, 1);
            } catch (YardiServiceException e) {
                StatisticsUtils.addErred(dynamicStatisticsRecord, 1);
            }
        }
    }

    private void updateBuildings(List<ResidentTransactions> allTransactions, StatisticsRecord dynamicStatisticsRecord) {
        log.info("Updating buildings and units...");
        for (ResidentTransactions transaction : allTransactions) {
            try {
                new YardiBuildingProcessor().updateBuildings(transaction);
                StatisticsUtils.addProcessed(dynamicStatisticsRecord, 1);
            } catch (Exception e) {
                StatisticsUtils.addErred(dynamicStatisticsRecord, 1);
            }
        }
        log.info("All buildings, units updated.");
    }

    private void updateLeases(List<ResidentTransactions> allTransactions, StatisticsRecord dynamicStatisticsRecord) {
        log.info("Updating leases...");
        for (ResidentTransactions transaction : allTransactions) {
            try {
                new YardiLeaseProcessor().updateLeases(transaction);
                StatisticsUtils.addProcessed(dynamicStatisticsRecord, 1);
            } catch (Exception e) {
                StatisticsUtils.addErred(dynamicStatisticsRecord, 1);
            }
        }
        log.info("All leases updated.");
    }

    private void updateCharges(List<ResidentTransactions> allTransactions, StatisticsRecord dynamicStatisticsRecord) {
        log.info("updateCharges: started...");
        for (ResidentTransactions transaction : allTransactions) {
            try {
                new YardiChargeProcessor().updateCharges(transaction);
                StatisticsUtils.addProcessed(dynamicStatisticsRecord, 1);
            } catch (Exception e) {
                StatisticsUtils.addErred(dynamicStatisticsRecord, 1);
            }
        }
        log.info("All charges updated.");
    }

    private void updatePayments(List<ResidentTransactions> allTransactions, StatisticsRecord dynamicStatisticsRecord) {
        log.info("updatePayments: started...");
        for (ResidentTransactions transaction : allTransactions) {
            try {
                new YardiPaymentProcessor().updatePayments(transaction);
                StatisticsUtils.addProcessed(dynamicStatisticsRecord, 1);
            } catch (Exception e) {
                StatisticsUtils.addErred(dynamicStatisticsRecord, 1);
            }
        }
        log.info("All payments updated.");
    }

    private List<ResidentTransactions> getAllResidentTransactions(YardiClient client, PmcYardiCredential yc, List<String> propertyCodes) {
        List<ResidentTransactions> transactions = new ArrayList<ResidentTransactions>();
        for (String propertyCode : propertyCodes) {
            try {
                ResidentTransactions residentTransactions = getResidentTransactions(client, yc, propertyCode);
                if (residentTransactions != null) {
                    transactions.add(residentTransactions);
                }
            } catch (YardiServiceException e) {
                log.error("Errors during call getResidentTransactions operation for building {}", propertyCode, e);
            }
        }

        return transactions;
    }

    /**
     * Allows export of resident/transactional data for a given property/property list.
     * 
     * @throws RemoteException
     * @throws AxisFault
     * 
     * @throws JAXBException
     */
    private ResidentTransactions getResidentTransactions(YardiClient c, PmcYardiCredential yc, String propertyId) throws YardiServiceException {
        try {

            c.transactionId++;
            c.setCurrentAction(Action.GetResidentTransactions);

            GetResidentTransactions_Login l = new GetResidentTransactions_Login();
            l.setUserName(yc.username().getValue());
            l.setPassword(yc.credential().getValue());
            l.setServerName(yc.serverName().getValue());
            l.setDatabase(yc.database().getValue());
            l.setPlatform(yc.platform().getValue().name());
            l.setInterfaceEntity(YardiConstants.INTERFACE_ENTITY);
            l.setYardiPropertyId(propertyId);

            GetResidentTransactions_LoginResponse response = c.getResidentTransactionsService().getResidentTransactions_Login(l);
            String xml = response.getGetResidentTransactions_LoginResult().getExtraElement().toString();

            log.info("GetResidentTransactions: {}", xml);
            if (Messages.isMessageResponse(xml)) {
                Messages messages = MarshallUtil.unmarshal(Messages.class, xml);
                if (messages.isError()) {
                    throw new YardiServiceException(messages.toString());
                } else {
                    log.info(messages.toString());
                }
            }

            ResidentTransactions transactions = MarshallUtil.unmarshal(ResidentTransactions.class, xml);
            return transactions;

        } catch (JAXBException e) {
            throw new Error(e);
        } catch (RemoteException e) {
            throw new Error(e);
        }

    }

    private ResidentTransactions getResidentTransaction(YardiClient c, PmcYardiCredential yc, String propertyId, String tenantId) throws YardiServiceException {
        try {
            c.transactionId++;
            c.setCurrentAction(Action.GetResidentTransaction);

            GetResidentTransaction_Login l = new GetResidentTransaction_Login();
            l.setUserName(yc.username().getValue());
            l.setPassword(yc.credential().getValue());
            l.setServerName(yc.serverName().getValue());
            l.setDatabase(yc.database().getValue());
            l.setPlatform(yc.platform().getValue().name());
            l.setInterfaceEntity(YardiConstants.INTERFACE_ENTITY);
            l.setYardiPropertyId(propertyId);
            l.setTenantId(tenantId);

            GetResidentTransaction_LoginResponse response = c.getResidentTransactionsService().getResidentTransaction_Login(l);
            String xml = response.getGetResidentTransaction_LoginResult().getExtraElement().toString();

            log.info("GetResidentTransaction: {}", xml);
            if (Messages.isMessageResponse(xml)) {
                Messages messages = MarshallUtil.unmarshal(Messages.class, xml);
                if (messages.isError()) {
                    throw new YardiServiceException(messages.toString());
                } else {
                    log.info(messages.toString());
                }
            }
            ResidentTransactions transactions = MarshallUtil.unmarshal(ResidentTransactions.class, xml);
            return transactions;

        } catch (JAXBException e) {
            throw new Error(e);
        } catch (RemoteException e) {
            throw new Error(e);
        }
    }

    private void importResidentTransactions(YardiClient c, PmcYardiCredential yc, ResidentTransactions reversalTransactions) throws YardiServiceException {
        try {
            c.transactionId++;
            c.setCurrentAction(Action.ImportResidentTransactions);

            ImportResidentTransactions_Login l = new ImportResidentTransactions_Login();
            l.setUserName(yc.username().getValue());
            l.setPassword(yc.credential().getValue());
            l.setServerName(yc.serverName().getValue());
            l.setDatabase(yc.database().getValue());
            l.setPlatform(yc.platform().getValue().name());
            l.setInterfaceEntity(YardiConstants.INTERFACE_ENTITY);

            String trXml = MarshallUtil.marshall(reversalTransactions);
            log.info(trXml);
            TransactionXml_type1 transactionXml = new TransactionXml_type1();
            OMElement element = AXIOMUtil.stringToOM(trXml);
            transactionXml.setExtraElement(element);
            l.setTransactionXml(transactionXml);

            ImportResidentTransactions_LoginResponse response = c.getResidentTransactionsService().importResidentTransactions_Login(l);
            String xml = response.getImportResidentTransactions_LoginResult().getExtraElement().toString();

            log.info("ImportResidentTransactions: {}", xml);

            Messages messages = MarshallUtil.unmarshal(Messages.class, xml);
            if (messages.isError()) {
                throw new YardiServiceException(messages.toString());
            } else {
                log.info(messages.toString());
            }
        } catch (JAXBException e) {
            throw new Error(e);
        } catch (RemoteException e) {
            throw new Error(e);
        } catch (XMLStreamException e) {
            throw new Error(e);
        }
    }

}
