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

import java.math.BigDecimal;
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

import com.yardi.entity.resident.Property;
import com.yardi.entity.resident.RTCustomer;
import com.yardi.entity.resident.ResidentTransactions;
import com.yardi.entity.resident.Transactions;
import com.yardi.ws.operations.GetResidentTransaction_Login;
import com.yardi.ws.operations.GetResidentTransaction_LoginResponse;
import com.yardi.ws.operations.GetResidentTransactions_Login;
import com.yardi.ws.operations.GetResidentTransactions_LoginResponse;
import com.yardi.ws.operations.ImportResidentTransactions_Login;
import com.yardi.ws.operations.ImportResidentTransactions_LoginResponse;
import com.yardi.ws.operations.TransactionXml_type1;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.commons.SimpleMessageFormat;
import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.Executable;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.server.TransactionScopeOption;
import com.pyx4j.entity.server.UnitOfWork;
import com.pyx4j.essentials.j2se.util.MarshallUtil;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.server.mail.SMTPMailServiceConfig;

import com.propertyvista.biz.ExecutionMonitor;
import com.propertyvista.biz.asset.BuildingFacade;
import com.propertyvista.biz.communication.CommunicationFacade;
import com.propertyvista.biz.financial.ar.yardi.YardiIntegrationAgent;
import com.propertyvista.biz.system.YardiServiceException;
import com.propertyvista.biz.tenant.LeaseFacade;
import com.propertyvista.domain.financial.BillingAccount;
import com.propertyvista.domain.financial.billing.InvoiceLineItem;
import com.propertyvista.domain.financial.yardi.YardiBillingAccount;
import com.propertyvista.domain.financial.yardi.YardiPayment;
import com.propertyvista.domain.financial.yardi.YardiReceiptReversal;
import com.propertyvista.domain.property.PropertyContact;
import com.propertyvista.domain.property.PropertyContact.PropertyContactType;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.settings.PmcYardiCredential;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.yardi.YardiClient;
import com.propertyvista.yardi.YardiConstants;
import com.propertyvista.yardi.YardiConstants.Action;
import com.propertyvista.yardi.bean.Messages;

/**
 * Implementation functionality for updating properties/units/leases/tenants basing on getResidentTransactions from YARDI api
 * 
 * @author Mykola
 * 
 */
public class YardiResidentTransactionsService extends YardiAbstarctService {

    private final static Logger log = LoggerFactory.getLogger(YardiResidentTransactionsService.class);

    private final I18n i18n = I18n.get(YardiResidentTransactionsService.class);

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
    public void updateAll(PmcYardiCredential yc, ExecutionMonitor executionMonitor) throws YardiServiceException {

        YardiClient client = new YardiClient(yc.residentTransactionsServiceURL().getValue());

        List<ResidentTransactions> allTransactions;
        if (yc.propertyCode().isNull()) {
            List<String> propertyCodes = getPropertyCodes(client, yc);
            allTransactions = getAllResidentTransactions(client, yc, propertyCodes);
        } else {
            List<String> propertyCodes = Arrays.asList(new String[] { yc.propertyCode().getValue() });
            allTransactions = getAllResidentTransactions(client, yc, propertyCodes);
        }

        for (ResidentTransactions transaction : allTransactions) {
            importTransaction(transaction, executionMonitor);
        }

        log.info("Update completed.");
    }

    public void updateLease(PmcYardiCredential yc, Lease lease) throws YardiServiceException {
        Persistence.service().retrieve(lease.unit().building());
        YardiClient client = new YardiClient(yc.residentTransactionsServiceURL().getValue());
        String propertyCode = lease.unit().building().propertyCode().getValue();
        ResidentTransactions transaction = getResidentTransaction(client, yc, propertyCode, lease.leaseId().getValue());
        if (transaction != null) {

            for (Property property : transaction.getProperty()) {
                for (RTCustomer rtCustomer : property.getRTCustomer()) {
                    importLease(propertyCode, rtCustomer);
                }
            }
        }
    }

    public void postReceiptReversal(PmcYardiCredential yc, YardiReceiptReversal reversal) throws YardiServiceException {
        YardiClient client = new YardiClient(yc.residentTransactionsServiceURL().getValue());

        YardiPaymentProcessor paymentProcessor = new YardiPaymentProcessor();
        ResidentTransactions reversalTransactions = paymentProcessor.addTransactionToBatch(paymentProcessor.createTransactionForReversal(reversal), null);

        importResidentTransactions(client, yc, reversalTransactions);

        if (reversal.applyNSF().isBooleanTrue()) {

            try {
                String targetEmail = getEmailForNsfNotification(reversal);
                ServerSideFactory.create(CommunicationFacade.class).sendPaymentReversalWithNsfNotification(targetEmail, reversal);
            } catch (Throwable e) {
                log.error("failed to send email", e);
            }
        }
    }

    private void importTransaction(ResidentTransactions transaction, final ExecutionMonitor executionMonitor) {
        // this is (going to be) the core import process that updates buildings, units in them, leases and charges
        log.info("Import started...");

        List<Property> properties = getProperties(transaction);

        for (final Property property : properties) {
            try {
                final Building building = importProperty(property);
                executionMonitor.addProcessedEvent("Building");
                for (final RTCustomer rtCustomer : property.getRTCustomer()) {
                    log.info("  for {}", rtCustomer.getCustomerID());
                    try {
                        importUnit(building.propertyCode().getValue(), rtCustomer);
                        executionMonitor.addProcessedEvent("Unit");
                        try {
                            LeaseFinancialState stats = importLease(building.propertyCode().getValue(), rtCustomer);
                            executionMonitor.addProcessedEvent("Charges", stats.getCharges(),
                                    SimpleMessageFormat.format("Charges for {0}", rtCustomer.getCustomerID()));
                            executionMonitor.addProcessedEvent("Payments", stats.getPayments(),
                                    SimpleMessageFormat.format("Payments for {0}", rtCustomer.getCustomerID()));
                            executionMonitor.addProcessedEvent("Lease");
                        } catch (YardiServiceException e) {
                            executionMonitor.addFailedEvent("Lease", e);
                        } catch (Throwable t) {
                            executionMonitor.addErredEvent("Lease", t);
                        }
                    } catch (YardiServiceException e) {
                        executionMonitor.addFailedEvent("Unit", e);
                    } catch (Throwable t) {
                        executionMonitor.addErredEvent("Unit", t);
                    }
                }
            } catch (YardiServiceException e) {
                executionMonitor.addFailedEvent("Building", e);
            } catch (Throwable t) {
                executionMonitor.addErredEvent("Building", t);
            }

        }
        log.info("Import complete.");
    }

    private Building importProperty(final Property property) throws YardiServiceException {
        log.info("Updating building {}", property.getPropertyID().get(0).getIdentification().getPrimaryID());

        Building building = new UnitOfWork(TransactionScopeOption.RequiresNew).execute(new Executable<Building, YardiServiceException>() {

            @Override
            public Building execute() throws YardiServiceException {
                Building building = new YardiBuildingProcessor().updateBuilding(property);
                ServerSideFactory.create(BuildingFacade.class).persist(building);
                return building;
            }
        });
        return building;
    }

    private AptUnit importUnit(final String propertyCode, final RTCustomer rtCustomer) throws YardiServiceException {
        log.info("      Updating unit #" + rtCustomer.getRTUnit().getUnitID());

        AptUnit unit = new UnitOfWork(TransactionScopeOption.RequiresNew).execute(new Executable<AptUnit, YardiServiceException>() {

            @Override
            public AptUnit execute() throws YardiServiceException {
                AptUnit unit = new YardiBuildingProcessor().updateUnit(propertyCode, rtCustomer.getRTUnit());
                ServerSideFactory.create(BuildingFacade.class).persist(unit);
                return unit;
            }
        });
        return unit;
    }

    private LeaseFinancialState importLease(final String propertyCode, final RTCustomer rtCustomer) throws YardiServiceException {
        final LeaseFinancialState state = new LeaseFinancialState();
        log.info("      Updating lease");
        if (new YardiLeaseProcessor().isSkipped(rtCustomer)) {
            log.info("      Lease and transactions for: {} skipped, lease does not meet criteria.", rtCustomer.getCustomerID());
            // TODO skipping monitor message
            return state;
        }
        new UnitOfWork(TransactionScopeOption.RequiresNew).execute(new Executable<Void, YardiServiceException>() {

            @Override
            public Void execute() throws YardiServiceException {
                // update lease
                LeaseFacade leaseFacade = ServerSideFactory.create(LeaseFacade.class);
                Lease lease = new YardiLeaseProcessor().processLease(rtCustomer, propertyCode);

                if (lease != null) {
                    lease = leaseFacade.persist(lease);

                    // activate:
                    leaseFacade.approve(lease, null, null);
                    leaseFacade.activate(lease);
                } else {
                    // TODO "lease information was unchanged" monitor message
                    log.info("          Lease information unchanged");
                }

                // update charges and payments

                final YardiBillingAccount account = new YardiChargeProcessor().getAccount(rtCustomer);
                new YardiChargeProcessor().removeOldCharges(account);
                new YardiPaymentProcessor().removeOldPayments(account);

                for (final Transactions tr : rtCustomer.getRTServiceTransactions().getTransactions()) {
                    if (tr != null) {
                        if (tr.getCharge() != null) {
                            log.info("          Updating charge");
                            InvoiceLineItem charge = YardiIntegrationAgent.createCharge(account, tr.getCharge().getDetail());
                            Persistence.service().persist(charge);
                            state.addCharge(charge.amount().getValue());
                        }
                        if (tr.getPayment() != null) {
                            log.info("          Updating payment");
                            YardiPayment payment = YardiIntegrationAgent.createPayment(account, tr.getPayment());
                            Persistence.service().persist(payment);
                            state.addPayment(payment.amount().getValue());

                        }
                    }
                }
                return null;
            }
        });
        return state;
    }

    private List<ResidentTransactions> getAllResidentTransactions(YardiClient client, PmcYardiCredential yc, List<String> propertyCodes)
            throws YardiServiceException {
        List<ResidentTransactions> transactions = new ArrayList<ResidentTransactions>();
        for (String propertyCode : propertyCodes) {
            ResidentTransactions residentTransactions = getResidentTransactions(client, yc, propertyCode);
            if (residentTransactions != null) {
                transactions.add(residentTransactions);
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

            c.transactionIdStart();
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
            c.transactionIdStart();
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
            c.transactionIdStart();
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

    public List<Property> getProperties(ResidentTransactions transaction) {
        List<Property> properties = new ArrayList<Property>();
        for (Property property : transaction.getProperty()) {
            properties.add(property);
        }
        return properties;
    }

    private class LeaseFinancialState {

        private BigDecimal chargesAmount = BigDecimal.ZERO;

        private BigDecimal paymentsAmount = BigDecimal.ZERO;

        public void addCharge(BigDecimal payment) {
            this.chargesAmount = chargesAmount.add(payment);
        }

        public void addPayment(BigDecimal payment) {
            this.paymentsAmount = paymentsAmount.add(payment);
        }

        public BigDecimal getCharges() {
            return chargesAmount;
        }

        public BigDecimal getPayments() {
            return paymentsAmount;
        }
    }

    private String getEmailForNsfNotification(YardiReceiptReversal receiptReversal) throws Exception {
        String email = null;

        BillingAccount billingAccount = Persistence.service().retrieve(BillingAccount.class, receiptReversal.billingAccount().getPrimaryKey());
        Persistence.service().retrieve(billingAccount.lease());
        Persistence.service().retrieve(billingAccount.lease().unit().building());
        Persistence.service().retrieve(billingAccount.lease().unit().building().contacts().propertyContacts());

        for (PropertyContact contact : billingAccount.lease().unit().building().contacts().propertyContacts()) {
            if (contact.type().getValue() == PropertyContactType.superintendent) {
                email = contact.email().getValue();
                break;
            }
        }

        if (email != null) {
            SMTPMailServiceConfig mailConfig = (SMTPMailServiceConfig) ServerSideConfiguration.instance().getMailServiceConfigConfiguration();
            if (CommonsStringUtils.isStringSet(mailConfig.getForwardAllTo())) {
                email = mailConfig.getForwardAllTo();
            }
            return email;
        } else {
            throw new Exception(i18n.tr("Email address for NSF notification for payment record '" + receiptReversal.paymentRecord().getPrimaryKey()
                    + "' was not defined (superintendant's email at building/marketing/propertyContacts)"));
        }
    }
}
