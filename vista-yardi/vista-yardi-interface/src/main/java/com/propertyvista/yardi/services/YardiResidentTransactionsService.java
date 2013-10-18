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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.axis2.AxisFault;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yardi.entity.ils.Availability;
import com.yardi.entity.ils.ILSUnit;
import com.yardi.entity.ils.PhysicalProperty;
import com.yardi.entity.mits.Address;
import com.yardi.entity.mits.PropertyIDType;
import com.yardi.entity.mits.Unit;
import com.yardi.entity.resident.Property;
import com.yardi.entity.resident.RTCustomer;
import com.yardi.entity.resident.ResidentTransactions;
import com.yardi.entity.resident.Transactions;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.SimpleMessageFormat;
import com.pyx4j.commons.TimeUtils;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.Executable;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.server.TransactionScopeOption;
import com.pyx4j.entity.server.UnitOfWork;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.biz.ExecutionMonitor;
import com.propertyvista.biz.asset.BuildingFacade;
import com.propertyvista.biz.communication.NotificationFacade;
import com.propertyvista.biz.financial.ar.yardi.YardiARIntegrationAgent;
import com.propertyvista.biz.financial.payment.PaymentMethodFacade;
import com.propertyvista.biz.system.YardiServiceException;
import com.propertyvista.domain.financial.ARCode;
import com.propertyvista.domain.financial.BillingAccount;
import com.propertyvista.domain.financial.BillingAccount.BillingPeriod;
import com.propertyvista.domain.financial.billing.BillingCycle;
import com.propertyvista.domain.financial.billing.InvoiceLineItem;
import com.propertyvista.domain.financial.yardi.YardiPayment;
import com.propertyvista.domain.financial.yardi.YardiReceiptReversal;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.property.yardi.YardiPropertyConfiguration;
import com.propertyvista.domain.settings.PmcYardiCredential;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.misc.VistaTODO;
import com.propertyvista.operations.domain.scheduler.CompletionType;
import com.propertyvista.yardi.bean.Properties;
import com.propertyvista.yardi.mapper.MappingUtils;
import com.propertyvista.yardi.stub.ExternalInterfaceLoggingStub;
import com.propertyvista.yardi.stub.YardiILSGuestCardStub;
import com.propertyvista.yardi.stub.YardiPropertyNoAccessException;
import com.propertyvista.yardi.stub.YardiResidentNoTenantsExistException;
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
        final Key yardiInterfaceId = yc.getPrimaryKey();
        try {
            ServerSideFactory.create(NotificationFacade.class).aggregateNotificationsStart();
            List<String> propertyListCodes;
            if (yc.propertyListCodes().isNull()) {
                List<YardiPropertyConfiguration> propertyConfigurations = getPropertyConfigurations(stub, yc);
                propertyListCodes = new ArrayList<String>();
                for (YardiPropertyConfiguration yardiPropertyConfiguration : propertyConfigurations) {
                    propertyListCodes.add(yardiPropertyConfiguration.propertyID().getValue());
                }
            } else {
                propertyListCodes = Arrays.asList(yc.propertyListCodes().getValue().trim().split("\\s*,\\s*"));
            }

            List<ResidentTransactions> allTransactions = getAllResidentTransactions(stub, yc, executionMonitor, propertyListCodes);
            List<Building> importedBuildings = new ArrayList<Building>();
            for (ResidentTransactions transaction : allTransactions) {
                if (executionMonitor.isTerminationRequested()) {
                    break;
                }
                importedBuildings.addAll(importTransaction(yardiInterfaceId, transaction, executionMonitor, stub));
            }

            if (!executionMonitor.isTerminationRequested()) {
                List<ResidentTransactions> allLeaseCharges = getAllLeaseCharges(stub, yc, executionMonitor, importedBuildings);
                for (ResidentTransactions leaseCharges : allLeaseCharges) {
                    if (executionMonitor.isTerminationRequested()) {
                        break;
                    }

                    importLeaseCharges(yardiInterfaceId, leaseCharges, executionMonitor, stub);
                }
            }

            if (!executionMonitor.isTerminationRequested() && !VistaTODO.removedForProductionILS) {
                YardiILSGuestCardStub ilsStub = ServerSideFactory.create(YardiILSGuestCardStub.class);
                List<PhysicalProperty> properties = getILSPropertyMarketing(ilsStub, yc, executionMonitor, propertyListCodes);
                for (PhysicalProperty property : properties) {
                    if (executionMonitor.isTerminationRequested()) {
                        break;
                    }
                    // process each property info
                    importPropertyMarketingInfo(yardiInterfaceId, property, executionMonitor, ilsStub);
                }
            }
        } finally {
            executionMonitor.addInfoEvent("yardiTime", TimeUtils.durationFormat(stub.getRequestsTime()), new BigDecimal(stub.getRequestsTime()));
            ServerSideFactory.create(NotificationFacade.class).aggregatedNotificationsSend();
        }

        log.info("Update completed.");
    }

    public void updateLease(PmcYardiCredential yc, Lease lease) throws YardiServiceException, RemoteException {
        YardiResidentTransactionsStub stub = ServerSideFactory.create(YardiResidentTransactionsStub.class);
        final Key yardiInterfaceId = yc.getPrimaryKey();
        Persistence.service().retrieve(lease.unit().building());
        String propertyCode = lease.unit().building().propertyCode().getValue();
        ResidentTransactions transaction = stub.getResidentTransactionsForTenant(yc, propertyCode, lease.leaseId().getValue());
        if (transaction != null && !transaction.getProperty().isEmpty()) {
            Property property = transaction.getProperty().iterator().next();
            if (!property.getRTCustomer().isEmpty()) {
                importLease(yardiInterfaceId, propertyCode, property.getRTCustomer().iterator().next(), null);
            }
        }
        // import lease charges
        BillingCycle nextCycle = ServerSideFactory.create(PaymentMethodFacade.class).getNextAutopayBillingCycle(lease);
        ResidentTransactions leaseCharges = null;
        try {
            leaseCharges = stub.getLeaseChargesForTenant(yc, propertyCode, lease.leaseId().getValue(), nextCycle.billingCycleStartDate().getValue());
        } catch (YardiResidentNoTenantsExistException e) {
            log.warn("Can't get changes for {}; {}", lease.leaseId().getValue(), e.getMessage()); // log error and reset lease charges.
            new YardiLeaseProcessor().expireLeaseProducts(lease);
        }
        if (leaseCharges != null) {
            Lease processed = null;
            // we should just get one element in the list for the requested leaseId
            for (Property property : leaseCharges.getProperty()) {
                for (RTCustomer rtCustomer : property.getRTCustomer()) {
                    processed = importLeaseCharges(yardiInterfaceId, propertyCode, rtCustomer, null);
                }
            }
            // handle non-processed lease
            if (processed == null) {
                new YardiLeaseProcessor().expireLeaseProducts(lease);
            }
        }
    }

    public void postReceiptReversal(PmcYardiCredential yc, YardiReceiptReversal reversal) throws YardiServiceException, RemoteException {
        YardiResidentTransactionsStub stub = ServerSideFactory.create(YardiResidentTransactionsStub.class);

        YardiPaymentProcessor paymentProcessor = new YardiPaymentProcessor();
        ResidentTransactions reversalTransactions = paymentProcessor.createTransactions(paymentProcessor.createTransactionForReversal(reversal));

        stub.importResidentTransactions(yc, reversalTransactions);
    }

    public List<YardiPropertyConfiguration> getPropertyConfigurations(PmcYardiCredential yc) throws YardiServiceException, RemoteException {
        YardiResidentTransactionsStub stub = ServerSideFactory.create(YardiResidentTransactionsStub.class);
        return getPropertyConfigurations(stub, yc);
    }

    public List<YardiPropertyConfiguration> getPropertyConfigurations(YardiResidentTransactionsStub stub, PmcYardiCredential yc) throws YardiServiceException,
            RemoteException {
        List<YardiPropertyConfiguration> propertyConfigurations = new ArrayList<YardiPropertyConfiguration>();
        Properties properties = stub.getPropertyConfigurations(yc);
        for (com.propertyvista.yardi.bean.Property property : properties.getProperties()) {
            if (StringUtils.isNotEmpty(property.getCode())) {
                YardiPropertyConfiguration configuration = EntityFactory.create(YardiPropertyConfiguration.class);
                configuration.propertyID().setValue(property.getCode());
                configuration.accountsReceivable().setValue(property.getAccountsReceivable());
                propertyConfigurations.add(configuration);
            }
        }
        return propertyConfigurations;
    }

    private List<Building> importTransaction(Key yardiInterfaceId, ResidentTransactions transaction, final ExecutionMonitor executionMonitor,
            final ExternalInterfaceLoggingStub interfaceLog) {
        // this is (going to be) the core import process that updates buildings, units in them, leases and charges
        log.info("ResidentTransactions: Import started...");
        List<Building> importedBuildings = new ArrayList<Building>();
        List<Property> properties = getProperties(transaction);
        Map<String, List<Lease>> notProcessedLeases = new HashMap<String, List<Lease>>();
        for (final Property property : properties) {
            String propertyId = null;
            if ((property.getPropertyID() != null) && (property.getPropertyID().size() > 0)) {
                propertyId = property.getPropertyID().get(0).getIdentification().getPrimaryID();
            }

            try {
                Building building = importProperty(yardiInterfaceId, property.getPropertyID().get(0), executionMonitor);
                executionMonitor.addProcessedEvent("Building");
                importedBuildings.add(building);

                String propertyCode = building.propertyCode().getValue();
                log.info("Processing building: {}", propertyCode);

                List<Lease> activeLeases = getActiveLeases(yardiInterfaceId, propertyCode);
                for (final RTCustomer rtCustomer : property.getRTCustomer()) {
                    String leaseId = rtCustomer.getCustomerID();
                    log.info("  for {}", leaseId);

                    removeLease(activeLeases, leaseId);

                    try {

                        AptUnit unit = importUnit(building, rtCustomer.getRTUnit().getUnit(), executionMonitor);
                        // try to assign legal address for the unit
                        if (unit.info().legalAddress().isEmpty() && rtCustomer.getCustomers().getCustomer().get(0).getAddress().size() > 0) {
                            assignLegalAddress(unit, rtCustomer.getCustomers().getCustomer().get(0).getAddress().get(0), executionMonitor);
                        }
                        executionMonitor.addProcessedEvent("Unit");

                        try {
                            LeaseFinancialStats stats = importLease(yardiInterfaceId, propertyCode, rtCustomer, executionMonitor);
                            executionMonitor.addProcessedEvent("Charges", stats.getCharges());
                            executionMonitor.addProcessedEvent("Payments", stats.getPayments());
                            executionMonitor.addProcessedEvent("Lease");
                        } catch (YardiServiceException e) {
                            executionMonitor.addFailedEvent("Lease", SimpleMessageFormat.format("Lease for customer {0}", rtCustomer.getCustomerID()), e);
                            interfaceLog.logRecordedTracastions();
                        } catch (Throwable t) {
                            executionMonitor.addErredEvent("Lease", SimpleMessageFormat.format("Lease for customer {0}", rtCustomer.getCustomerID()), t);
                            interfaceLog.logRecordedTracastions();
                        }

                    } catch (YardiServiceException e) {
                        executionMonitor.addFailedEvent("Unit", e);
                        interfaceLog.logRecordedTracastions();
                    } catch (Throwable t) {
                        executionMonitor.addErredEvent("Unit", t);
                        interfaceLog.logRecordedTracastions();
                    }
                }

                if (activeLeases.size() > 0) {
                    notProcessedLeases.put(propertyCode, activeLeases);
                }

            } catch (YardiServiceException e) {
                executionMonitor.addFailedEvent("Building", propertyId, e);
                interfaceLog.logRecordedTracastions();
            } catch (Throwable t) {
                executionMonitor.addErredEvent("Building", propertyId, t);
                interfaceLog.logRecordedTracastions();
            }

            if (executionMonitor.isTerminationRequested()) {
                break;
            }
        }

        log.info("ResidentTransactions: Import complete.");

        if (notProcessedLeases.size() > 0 && !executionMonitor.isTerminationRequested() && executionMonitor.getErred() == 0) {
            log.info("ResidentTransactions: closing leases started.");
            // handle all non-processed leases
            for (String propertyCode : notProcessedLeases.keySet()) {
                closeLeases(notProcessedLeases.get(propertyCode));
            }
            log.info("ResidentTransactions: closing leases complete.");
        }

        return importedBuildings;
    }

    private Building importProperty(final Key yardiInterfaceId, final PropertyIDType propertyId, final ExecutionMonitor executionMonitor)
            throws YardiServiceException {
        log.info("Updating building {}", propertyId.getIdentification().getPrimaryID());

        Building building = new UnitOfWork(TransactionScopeOption.RequiresNew).execute(new Executable<Building, YardiServiceException>() {

            @Override
            public Building execute() throws YardiServiceException {
                Building building = new YardiBuildingProcessor(executionMonitor).updateBuilding(yardiInterfaceId, propertyId);
                ServerSideFactory.create(BuildingFacade.class).persist(building);
                return building;
            }
        });

        return building;
    }

    private AptUnit importUnit(final Building building, final Unit unit, ExecutionMonitor executionMonitor) throws YardiServiceException {
        log.info("    Updating unit #" + unit.getInformation().get(0).getUnitID());

        AptUnit aptUnit = new UnitOfWork(TransactionScopeOption.RequiresNew).execute(new Executable<AptUnit, YardiServiceException>() {
            @Override
            public AptUnit execute() throws YardiServiceException {
                AptUnit aptUnit = new YardiBuildingProcessor().updateUnit(building, unit);
                ServerSideFactory.create(BuildingFacade.class).persist(aptUnit);
                return aptUnit;
            }
        });

        return aptUnit;
    }

    private void assignLegalAddress(final AptUnit unit, final Address address, final ExecutionMonitor executionMonitor) throws YardiServiceException {
        log.info("    assign Legal Address: {}", address.getAddress1());

        new UnitOfWork(TransactionScopeOption.RequiresNew).execute(new Executable<Void, YardiServiceException>() {
            @Override
            public Void execute() throws YardiServiceException {
                unit.info().legalAddress().set(MappingUtils.getAddress(address));
                if (unit.info().legalAddress().streetNumber().isNull()) {
                    String msg = SimpleMessageFormat.format("    invalid address: {0}", unit.info().legalAddress().streetName().getValue());
                    log.info(msg);
                    executionMonitor.addErredEvent("ParseAddress", msg);
                }

                ServerSideFactory.create(BuildingFacade.class).persist(unit);
                return null;
            }
        });
    }

    private LeaseFinancialStats importLease(final Key yardiInterfaceId, final String propertyCode, final RTCustomer rtCustomer,
            final ExecutionMonitor executionMonitor) throws YardiServiceException {
        final LeaseFinancialStats state = new LeaseFinancialStats();

        log.info("    Importing lease:");
        if (YardiLeaseProcessor.isEligibleForProcessing(rtCustomer)) {
            new UnitOfWork(TransactionScopeOption.RequiresNew).execute(new Executable<Void, YardiServiceException>() {
                @Override
                public Void execute() throws YardiServiceException {
                    // update lease
                    new YardiLeaseProcessor(executionMonitor).processLease(rtCustomer, yardiInterfaceId, propertyCode);

                    // update charges and payments
                    final BillingAccount account = new YardiChargeProcessor().getAccount(yardiInterfaceId, rtCustomer);
                    new YardiChargeProcessor().removeOldCharges(account);
                    new YardiPaymentProcessor().removeOldPayments(account);

                    if (rtCustomer.getRTServiceTransactions() != null) {
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
                    }

                    return null;
                }
            });
        } else {
            log.info("    Lease and transactions for: {} skipped, lease does not meet criteria.", rtCustomer.getCustomerID());
            // TODO skipping monitor message
            return state;
        }

        return state;
    }

    private List<ResidentTransactions> getAllResidentTransactions(YardiResidentTransactionsStub stub, PmcYardiCredential yc, ExecutionMonitor executionMonitor,
            List<String> propertyListCodes) throws YardiServiceException, RemoteException {
        List<ResidentTransactions> transactions = new ArrayList<ResidentTransactions>();
        final Key yardiInterfaceId = yc.getPrimaryKey();
        for (String propertyListCode : propertyListCodes) {
            if (executionMonitor.isTerminationRequested()) {
                break;
            }
            Building bulding = findBuilding(yardiInterfaceId, propertyListCode);
            if (bulding != null) {
                if (bulding.suspended().getValue()) {
                    executionMonitor.addInfoEvent("skip suspended property code for transaction import", CompletionType.failed, propertyListCode, null);
                    continue;
                }
            } else {
                // process as propertyListCode or new building
            }
            try {
                ResidentTransactions residentTransactions = stub.getAllResidentTransactions(yc, propertyListCode);
                if (residentTransactions != null) {
                    transactions.add(residentTransactions);
                }
                executionMonitor.addInfoEvent("PropertyListTransactions", propertyListCode);
            } catch (YardiPropertyNoAccessException e) {
                if (suspendBuilding(yardiInterfaceId, propertyListCode)) {
                    executionMonitor.addErredEvent("BuildingSuspended", e);
                } else {
                    executionMonitor.addFailedEvent("PropertyListTransactions", propertyListCode, e);
                }
            }
        }
        return transactions;
    }

    private void importLeaseCharges(Key yardiInterfaceId, ResidentTransactions leaseCharges, final ExecutionMonitor executionMonitor,
            final ExternalInterfaceLoggingStub interfaceLog) {
        log.info("LeaseCharges: import started...");

        // although we get properties here, all data inside is empty until we get down to the ChargeDetail level
        List<Property> properties = getProperties(leaseCharges);
        Map<String, List<Lease>> notProcessedLeases = new HashMap<String, List<Lease>>();
        for (final Property property : properties) {
            String propertyCode = null;
            try {
                // make sure we have non-empty leases and transactions
                if (property.getRTCustomer().size() > 0) {
                    // grab propertyCode from the first available ChargeDetail element
                    for (RTCustomer rtCustomer : property.getRTCustomer()) {
                        if (rtCustomer.getRTServiceTransactions().getTransactions().size() > 0) {
                            propertyCode = rtCustomer.getRTServiceTransactions().getTransactions().get(0).getCharge().getDetail().getPropertyPrimaryID();
                            break;
                        }
                    }
                }

                if (propertyCode == null) {
                    log.info("Processed RTCustomer record with no transactions.");
                    continue;
                }

                log.info("Processing building: {}", propertyCode);
                executionMonitor.addProcessedEvent("Building", propertyCode);
                // retrieve active leases and keep track on those that have not been found in the response
                List<Lease> activeLeases = getActiveLeases(yardiInterfaceId, propertyCode);
                // process lease charges
                for (final RTCustomer rtCustomer : property.getRTCustomer()) {
                    if (rtCustomer.getRTServiceTransactions().getTransactions().size() == 0) {
                        continue;
                    }

                    String leaseId = null;
                    try {
                        leaseId = rtCustomer.getRTServiceTransactions().getTransactions().get(0).getCharge().getDetail().getCustomerID();
                        if (leaseId != null) {
                            removeLease(activeLeases, leaseId);
                            importLeaseCharges(yardiInterfaceId, propertyCode, rtCustomer, executionMonitor);
                        }
                    } catch (Throwable t) {
                        String msg = SimpleMessageFormat.format("Lease {0}", leaseId == null ? "undefined" : leaseId);
                        executionMonitor.addErredEvent("Lease", msg, t);
                        log.warn(msg, t);
                        interfaceLog.logRecordedTracastions();
                    }
                }

                if (activeLeases.size() > 0) {
                    notProcessedLeases.put(propertyCode, activeLeases);
                }
            } catch (Throwable t) {
                String msg = SimpleMessageFormat.format("Property {0}", propertyCode);
                executionMonitor.addErredEvent("Building", msg, t);
                log.warn(msg, t);
                interfaceLog.logRecordedTracastions();
            }

            if (executionMonitor.isTerminationRequested()) {
                break;
            }

        }
        log.info("LeaseCharges: import complete.");

        if (notProcessedLeases.size() > 0 && !executionMonitor.isTerminationRequested() && executionMonitor.getErred() == 0) {
            log.info("LeaseCharges: terminating not-received charges started.");
            // handle all non-processed leases
            for (String propertyCode : notProcessedLeases.keySet()) {
                for (Lease leaseId : notProcessedLeases.get(propertyCode)) {
                    try {
                        terminateLeaseCharges(leaseId, executionMonitor);
                    } catch (Throwable t) {
                        String msg = SimpleMessageFormat.format("Lease {0}", leaseId);
                        executionMonitor.addErredEvent("Lease", msg, t);
                        log.warn(msg, t);
                    }
                }
            }
            log.info("LeaseCharges: terminating not-received charges complete.");
        }
    }

    private Lease importLeaseCharges(Key yardiInterfaceId, final String propertyCode, final RTCustomer rtCustomer, final ExecutionMonitor executionMonitor)
            throws YardiServiceException {
        // make sure we have received any transactions
        if (rtCustomer.getRTServiceTransactions().getTransactions().size() == 0) {
            log.info("No Lease Charges received for property: ", propertyCode);
            return null;
        }
        // grab customerId from the first available ChargeDetail element
        String customerId = rtCustomer.getRTServiceTransactions().getTransactions().get(0).getCharge().getDetail().getCustomerID();
        final Lease lease = new YardiLeaseProcessor().findLease(yardiInterfaceId, propertyCode, customerId);
        if (lease == null) {
            throw new YardiServiceException(i18n.tr("Lease not found for customer: {0} on interface {1}", customerId, yardiInterfaceId));
        }
        log.info("    Processing lease: {}", customerId);

        new UnitOfWork(TransactionScopeOption.RequiresNew).execute(new Executable<Void, YardiServiceException>() {
            @Override
            public Void execute() throws YardiServiceException {
                // create/update billable items
                new YardiLeaseProcessor(executionMonitor).updateLeaseProducts(rtCustomer.getRTServiceTransactions().getTransactions(), lease);
                return null;
            }
        });

        return lease;
    }

    // create new term with no features and set service charge = 0
    private void terminateLeaseCharges(final Lease leaseId, final ExecutionMonitor executionMonitor) throws YardiServiceException {
        new UnitOfWork(TransactionScopeOption.RequiresNew).execute(new Executable<Void, YardiServiceException>() {
            @Override
            public Void execute() throws YardiServiceException {
                // expire current billable items
                if (new YardiLeaseProcessor(executionMonitor).expireLeaseProducts(leaseId)) {
                    String msg = SimpleMessageFormat.format("charges expired for lease {0}", leaseId.leaseId());
                    log.info(msg);
                    if (executionMonitor != null) {
                        executionMonitor.addInfoEvent("chargesExpired", msg);
                    }
                }
                return null;
            }
        });
    }

    // TODO - we may need to request Yardi charges for one more cycle forward
    private List<ResidentTransactions> getAllLeaseCharges(YardiResidentTransactionsStub stub, PmcYardiCredential yc, ExecutionMonitor executionMonitor,
            List<Building> buildings) throws YardiServiceException, RemoteException {
        final Key yardiInterfaceId = yc.getPrimaryKey();
        // Make sure YardiChargeCodes have been configured
        EntityQueryCriteria<ARCode> criteria = EntityQueryCriteria.create(ARCode.class);
        criteria.eq(criteria.proto().type(), ARCode.Type.Residential);
        criteria.isNotNull(criteria.proto().yardiChargeCodes());
        if (Persistence.service().count(criteria) < 1) {
            throw new YardiServiceException("Yardi Charge Codes not configured");
        }
        List<ResidentTransactions> transactions = new ArrayList<ResidentTransactions>();
        for (Building bulding : buildings) {
            if (executionMonitor.isTerminationRequested()) {
                break;
            }
            if (bulding.suspended().getValue()) {
                executionMonitor
                        .addInfoEvent("skip suspended property code for charges import", CompletionType.failed, bulding.propertyCode().getValue(), null);
            } else {
                BillingCycle nextCycle = ServerSideFactory.create(PaymentMethodFacade.class).getNextAutopayBillingCycle(bulding, BillingPeriod.Monthly, 1);
                try {
                    ResidentTransactions residentTransactions = stub.getAllLeaseCharges(yc, bulding.propertyCode().getValue(), nextCycle
                            .billingCycleStartDate().getValue());
                    if (residentTransactions != null) {
                        transactions.add(residentTransactions);
                    }
                    executionMonitor.addInfoEvent("PropertyListCharges", bulding.propertyCode().getValue());
                } catch (YardiPropertyNoAccessException e) {
                    if (suspendBuilding(yardiInterfaceId, bulding.propertyCode().getValue())) {
                        executionMonitor.addErredEvent("BuildingSuspended", e);
                    } else {
                        executionMonitor.addFailedEvent("PropertyListCharges", bulding.propertyCode().getValue(), e);
                    }
                }
            }
        }

        return transactions;
    }

    private List<PhysicalProperty> getILSPropertyMarketing(YardiILSGuestCardStub stub, PmcYardiCredential yc, ExecutionMonitor executionMonitor,
            List<String> propertyListCodes) {
        // update building contacts/amenities/pet policy, floorplan amenities/utilities
        List<PhysicalProperty> marketingInfo = new ArrayList<PhysicalProperty>();
        for (String propertyListCode : propertyListCodes) {
            if (executionMonitor.isTerminationRequested()) {
                break;
            }

            final Key yardiInterfaceId = yc.getPrimaryKey();
            Building bulding = findBuilding(yardiInterfaceId, propertyListCode);
            if (bulding == null) {
                //TODO need to implement this. But how?
                throw new Error("propertyListCode not implemented in this Vista version, use building codes instead of list '" + propertyListCode + "'");
            } else {
                if (bulding.suspended().getValue()) {
                    executionMonitor.addInfoEvent("skip suspended property code for charges import", CompletionType.failed, propertyListCode, null);
                } else {
                    try {
                        PhysicalProperty propertyMarketing = stub.getPropertyMarketingInfo(yc, bulding.propertyCode().getValue());
                        if (propertyMarketing != null) {
                            marketingInfo.add(propertyMarketing);
                        }
                        executionMonitor.addInfoEvent("ILSPropertyMarketing", propertyListCode);
                    } catch (YardiServiceException e) {
                        executionMonitor.addFailedEvent("ILSPropertyMarketing", propertyListCode, e);
                    }
                }
            }
        }

        return marketingInfo;
    }

    private void importPropertyMarketingInfo(final Key yardiInterfaceId, PhysicalProperty propertyInfo, final ExecutionMonitor executionMonitor,
            final ExternalInterfaceLoggingStub interfaceLog) {
        log.info("PropertyMarketing: import started...");

        for (final com.yardi.entity.ils.Property property : propertyInfo.getProperty()) {
            String propertyCode = property.getPropertyID().getIdentification().getPrimaryID();

            try {
                log.info("  Processing building: {}", propertyCode);

                Building building = importProperty(yardiInterfaceId, property.getPropertyID(), executionMonitor);
                executionMonitor.addProcessedEvent("Building");

                log.info("  Processing units for building: {}", propertyCode);

                for (ILSUnit ilsUnit : property.getILSUnit()) {
                    AptUnit aptUnit = importUnit(building, ilsUnit.getUnit(), executionMonitor);
                    updateAvailability(aptUnit, ilsUnit.getAvailability(), executionMonitor);
                    executionMonitor.addProcessedEvent("Unit");
                }

            } catch (YardiServiceException e) {
                executionMonitor.addFailedEvent("Building", propertyCode, e);
                interfaceLog.logRecordedTracastions();
            } catch (Throwable t) {
                executionMonitor.addErredEvent("Building", propertyCode, t);
                interfaceLog.logRecordedTracastions();
            }

            if (executionMonitor.isTerminationRequested()) {
                break;
            }
        }
    }

    private void updateAvailability(final AptUnit unit, final Availability avail, ExecutionMonitor executionMonitor) throws YardiServiceException {
        log.info("    availability: {}: {}", unit.getStringView(), (avail == null || avail.getVacateDate() == null ? "Not " : "") + "Available");
        new UnitOfWork(TransactionScopeOption.RequiresNew).execute(new Executable<Void, YardiServiceException>() {
            @Override
            public Void execute() throws YardiServiceException {
                new YardiILSMarketingProcessor().updateAvailability(unit, avail);
                Persistence.service().persist(unit);
                return null;
            }
        });
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

    private List<Lease> getActiveLeases(Key yardiInterfaceId, String propertyCode) {
        EntityQueryCriteria<Lease> criteria = EntityQueryCriteria.create(Lease.class);
        criteria.eq(criteria.proto().unit().building().propertyCode(), propertyCode);
        criteria.eq(criteria.proto().unit().building().integrationSystemId(), yardiInterfaceId);
        criteria.in(criteria.proto().status(), Lease.Status.active());
        return Persistence.service().query(criteria);
    }

    private boolean removeLease(List<Lease> leases, String leaseId) {
        if (leaseId == null) {
            return false;
        }
        Iterator<Lease> it = leases.iterator();
        while (it.hasNext()) {
            if (leaseId.equals(it.next().leaseId().getValue())) {
                it.remove();
                return true;
            }
        }
        return false;
    }

    private void closeLeases(List<Lease> leases) {
        for (Lease lease : leases) {
            YardiLeaseProcessor.completeLease(lease);
        }
    }

    private Building findBuilding(Key yardiInterfaceId, String propertyCode) {
        EntityQueryCriteria<Building> criteria = EntityQueryCriteria.create(Building.class);
        criteria.eq(criteria.proto().propertyCode(), propertyCode);
        criteria.eq(criteria.proto().integrationSystemId(), yardiInterfaceId);
        return Persistence.service().retrieve(criteria);
    }

    private boolean suspendBuilding(Key yardiInterfaceId, String propertyCode) {
        Building building = findBuilding(yardiInterfaceId, propertyCode);
        if ((building != null) && (!building.suspended().getValue())) {
            ServerSideFactory.create(BuildingFacade.class).suspend(building);
            return true;
        } else {
            return false;
        }
    }
}
