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

import org.apache.axis2.AxisFault;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yardi.entity.resident.Property;
import com.yardi.entity.resident.RTCustomer;
import com.yardi.entity.resident.ResidentTransactions;
import com.yardi.entity.resident.Transactions;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.commons.SimpleMessageFormat;
import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.Executable;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.server.TransactionScopeOption;
import com.pyx4j.entity.server.UnitOfWork;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.server.mail.SMTPMailServiceConfig;

import com.propertyvista.biz.ExecutionMonitor;
import com.propertyvista.biz.asset.BuildingFacade;
import com.propertyvista.biz.communication.CommunicationFacade;
import com.propertyvista.biz.financial.ar.yardi.YardiARIntegrationAgent;
import com.propertyvista.biz.system.YardiServiceException;
import com.propertyvista.domain.financial.BillingAccount;
import com.propertyvista.domain.financial.billing.InvoiceLineItem;
import com.propertyvista.domain.financial.yardi.YardiBillingAccount;
import com.propertyvista.domain.financial.yardi.YardiPayment;
import com.propertyvista.domain.financial.yardi.YardiReceiptReversal;
import com.propertyvista.domain.property.PropertyContact;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.settings.PmcYardiCredential;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.yardi.bean.Properties;
import com.propertyvista.yardi.stub.YardiResidentTransactionsStub;

/**
 * Implementation functionality for updating properties/units/leases/tenants basing on getResidentTransactions from YARDI api
 * 
 * @author Mykola
 * 
 */
public class YardiResidentTransactionsService extends YardiAbstractService {

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
     * The Ping function accepts no parameters, but will return the
     * assembly name of the function being called. Use it to test
     * connectivity.
     * 
     * @throws RemoteException
     * @throws AxisFault
     */
    public String ping(YardiResidentTransactionsStub stub, PmcYardiCredential yc) throws AxisFault, RemoteException {
        return stub.ping(yc);
    }

    /**
     * Updates/creates entities basing on data from YARDI System.
     * 
     * @param yp
     *            the YARDI System connection parameters
     * @throws YardiServiceException
     *             if operation fails
     */
    public void updateAll(PmcYardiCredential yc, ExecutionMonitor executionMonitor) throws YardiServiceException, RemoteException {
        YardiResidentTransactionsStub stub = ServerSideFactory.create(YardiResidentTransactionsStub.class);

        List<ResidentTransactions> allTransactions;
        if (yc.propertyCode().isNull()) {
            List<String> propertyCodes = getPropertyCodes(stub, yc);
            allTransactions = getAllResidentTransactions(stub, yc, propertyCodes);
        } else {
            List<String> propertyCodes = Arrays.asList(yc.propertyCode().getValue().split("\\s*,\\s*"));
            allTransactions = getAllResidentTransactions(stub, yc, propertyCodes);
        }

        for (ResidentTransactions transaction : allTransactions) {
            importTransaction(transaction, executionMonitor);
        }

        log.info("Update completed.");
    }

    public void updateLease(PmcYardiCredential yc, Lease lease) throws YardiServiceException, RemoteException {
        YardiResidentTransactionsStub stub = ServerSideFactory.create(YardiResidentTransactionsStub.class);

        Persistence.service().retrieve(lease.unit().building());
        String propertyCode = lease.unit().building().propertyCode().getValue();
        ResidentTransactions transaction = stub.getResidentTransactionsForTenant(yc, propertyCode, lease.leaseId().getValue());
        if (transaction != null) {
            for (Property property : transaction.getProperty()) {
                for (RTCustomer rtCustomer : property.getRTCustomer()) {
                    importLease(propertyCode, rtCustomer);
                }
            }
        }
    }

    public void postReceiptReversal(PmcYardiCredential yc, YardiReceiptReversal reversal) throws YardiServiceException, RemoteException {
        YardiResidentTransactionsStub stub = ServerSideFactory.create(YardiResidentTransactionsStub.class);

        YardiPaymentProcessor paymentProcessor = new YardiPaymentProcessor();
        ResidentTransactions reversalTransactions = paymentProcessor.addTransactionToBatch(paymentProcessor.createTransactionForReversal(reversal), null);

        stub.importResidentTransactions(yc, reversalTransactions);

        if (reversal.applyNSF().isBooleanTrue()) {
            try {
                List<String> targetEmails = getEmailsForNsfNotification(reversal);
                ServerSideFactory.create(CommunicationFacade.class).sendPaymentReversalWithNsfNotification(targetEmails, reversal);
            } catch (Throwable e) {
                log.error("failed to send email", e);
            }
        }
    }

    public List<String> getPropertyCodes(YardiResidentTransactionsStub stub, PmcYardiCredential yc) throws YardiServiceException, RemoteException {
        List<String> propertyCodes = new ArrayList<String>();
        Properties properties = stub.getPropertyConfigurations(yc);
        for (com.propertyvista.yardi.bean.Property property : properties.getProperties()) {
            if (StringUtils.isNotEmpty(property.getCode())) {
                propertyCodes.add(property.getCode());
            }
        }
        return propertyCodes;
    }

    private void importTransaction(ResidentTransactions transaction, final ExecutionMonitor executionMonitor) {
        // this is (going to be) the core import process that updates buildings, units in them, leases and charges
        log.info("Import started...");

        List<Property> properties = getProperties(transaction);

        for (final Property property : properties) {
            String propertyId = null;
            if ((property.getPropertyID() != null) && (property.getPropertyID().size() > 0)) {
                propertyId = property.getPropertyID().get(0).getIdentification().getPrimaryID();
            }

            try {
                final Building building = importProperty(property);
                executionMonitor.addProcessedEvent("Building");
                for (final RTCustomer rtCustomer : property.getRTCustomer()) {
                    log.info("  for {}", rtCustomer.getCustomerID());
                    try {
                        importUnit(building.propertyCode().getValue(), rtCustomer);
                        executionMonitor.addProcessedEvent("Unit");
                        try {
                            LeaseFinancialStats stats = importLease(building.propertyCode().getValue(), rtCustomer);
                            executionMonitor.addProcessedEvent("Charges", stats.getCharges());
                            executionMonitor.addProcessedEvent("Payments", stats.getPayments());
                            executionMonitor.addProcessedEvent("Lease");
                        } catch (YardiServiceException e) {
                            executionMonitor.addFailedEvent("Lease", SimpleMessageFormat.format("Lease for customer {0}", rtCustomer.getCustomerID()), e);
                        } catch (Throwable t) {
                            executionMonitor.addErredEvent("Lease", SimpleMessageFormat.format("Lease for customer {0}", rtCustomer.getCustomerID()), t);
                        }
                    } catch (YardiServiceException e) {
                        executionMonitor.addFailedEvent("Unit", e);
                    } catch (Throwable t) {
                        executionMonitor.addErredEvent("Unit", t);
                    }
                }
            } catch (YardiServiceException e) {
                executionMonitor.addFailedEvent("Building", propertyId, e);
            } catch (Throwable t) {
                executionMonitor.addErredEvent("Building", propertyId, t);
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
        log.info("  Updating unit #" + rtCustomer.getRTUnit().getUnitID());

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

    private LeaseFinancialStats importLease(final String propertyCode, final RTCustomer rtCustomer) throws YardiServiceException {
        final LeaseFinancialStats state = new LeaseFinancialStats();
        log.info("      Importing lease");
        if (YardiLeaseProcessor.isSkipped(rtCustomer)) {
            log.info("      Lease and transactions for: {} skipped, lease does not meet criteria.", rtCustomer.getCustomerID());
            // TODO skipping monitor message
            return state;
        }
        new UnitOfWork(TransactionScopeOption.RequiresNew).execute(new Executable<Void, YardiServiceException>() {

            @Override
            public Void execute() throws YardiServiceException {
                // update lease
                new YardiLeaseProcessor().processLease(rtCustomer, propertyCode);

                // update charges and payments
                final YardiBillingAccount account = new YardiChargeProcessor().getAccount(rtCustomer);
                new YardiChargeProcessor().removeOldCharges(account);
                new YardiPaymentProcessor().removeOldPayments(account);

                for (final Transactions tr : rtCustomer.getRTServiceTransactions().getTransactions()) {
                    if (tr != null) {
                        if (tr.getCharge() != null) {
                            log.info("          Updating charge");
                            InvoiceLineItem charge = YardiARIntegrationAgent.createCharge(account, tr.getCharge().getDetail());
                            Persistence.service().persist(charge);
                            state.addCharge(charge.amount().getValue());
                        }
                        if (tr.getPayment() != null) {
                            log.info("          Updating payment");
                            YardiPayment payment = YardiARIntegrationAgent.createPayment(account, tr.getPayment());
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

    List<ResidentTransactions> getAllResidentTransactions(YardiResidentTransactionsStub stub, PmcYardiCredential yc, List<String> propertyCodes)
            throws YardiServiceException, RemoteException {
        List<ResidentTransactions> transactions = new ArrayList<ResidentTransactions>();
        for (String propertyCode : propertyCodes) {
            ResidentTransactions residentTransactions = stub.getAllResidentTransactions(yc, propertyCode);
            if (residentTransactions != null) {
                transactions.add(residentTransactions);
            }
        }

        return transactions;
    }

    public List<Property> getProperties(ResidentTransactions transaction) {
        List<Property> properties = new ArrayList<Property>();
        for (Property property : transaction.getProperty()) {
            properties.add(property);
        }
        return properties;
    }

    private class LeaseFinancialStats {

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

    private List<String> getEmailsForNsfNotification(YardiReceiptReversal receiptReversal) throws Exception {
        List<String> emails = new ArrayList<String>();

        BillingAccount billingAccount = Persistence.service().retrieve(BillingAccount.class, receiptReversal.billingAccount().getPrimaryKey());
        Persistence.service().retrieve(billingAccount.lease());
        Persistence.service().retrieve(billingAccount.lease().unit().building());
        Persistence.service().retrieve(billingAccount.lease().unit().building().contacts().propertyContacts());

        for (PropertyContact contact : billingAccount.lease().unit().building().contacts().propertyContacts()) {
            if ("NSF_NOTIFICATIONS".equals(contact.name().getValue())) {
                emails.add(contact.email().getValue());
            }
        }

        if (!emails.isEmpty()) {
            SMTPMailServiceConfig mailConfig = (SMTPMailServiceConfig) ServerSideConfiguration.instance().getMailServiceConfigConfiguration();
            if (CommonsStringUtils.isStringSet(mailConfig.getForwardAllTo())) {
                String forwardToEmail = mailConfig.getForwardAllTo();
                int s = emails.size();
                emails.clear();
                for (int i = 0; i < s; ++i) {
                    emails.add(forwardToEmail);
                }
            }
            return emails;
        } else {
            throw new Exception(i18n.tr("Email address for NSF notification for payment record '" + receiptReversal.paymentRecord().getPrimaryKey()
                    + "' was not defined (define a contact named 'NSF_NOTIFICATIONS' at building/marketing/property_contacts)"));
        }
    }
}
