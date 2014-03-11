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
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.axis2.AxisFault;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yardi.entity.guestcard40.RentableItems;
import com.yardi.entity.ils.Availability;
import com.yardi.entity.ils.ILSUnit;
import com.yardi.entity.ils.PhysicalProperty;
import com.yardi.entity.mits.Address;
import com.yardi.entity.mits.Information;
import com.yardi.entity.mits.PropertyIDType;
import com.yardi.entity.mits.Unit;
import com.yardi.entity.resident.ChargeDetail;
import com.yardi.entity.resident.Property;
import com.yardi.entity.resident.RTCustomer;
import com.yardi.entity.resident.ResidentTransactions;
import com.yardi.entity.resident.Transactions;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.commons.SimpleMessageFormat;
import com.pyx4j.commons.TimeUtils;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.config.server.SystemDateManager;
import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.server.Executable;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.server.TransactionScopeOption;
import com.pyx4j.entity.server.UnitOfWork;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.biz.ExecutionMonitor;
import com.propertyvista.biz.asset.BuildingFacade;
import com.propertyvista.biz.communication.NotificationFacade;
import com.propertyvista.biz.financial.ar.yardi.YardiARIntegrationAgent;
import com.propertyvista.biz.financial.payment.PaymentMethodFacade;
import com.propertyvista.biz.occupancy.OccupancyFacade;
import com.propertyvista.biz.system.YardiPropertyNoAccessException;
import com.propertyvista.biz.system.YardiServiceException;
import com.propertyvista.biz.system.yardi.YardiConfigurationFacade;
import com.propertyvista.domain.dashboard.gadgets.availability.UnitAvailabilityStatus;
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
import com.propertyvista.yardi.beans.Properties;
import com.propertyvista.yardi.mappers.MappingUtils;
import com.propertyvista.yardi.processors.YardiBuildingProcessor;
import com.propertyvista.yardi.processors.YardiChargeProcessor;
import com.propertyvista.yardi.processors.YardiILSMarketingProcessor;
import com.propertyvista.yardi.processors.YardiLeaseProcessor;
import com.propertyvista.yardi.processors.YardiPaymentProcessor;
import com.propertyvista.yardi.processors.YardiProductCatalogProcessor;
import com.propertyvista.yardi.stubs.ExternalInterfaceLoggingStub;
import com.propertyvista.yardi.stubs.YardiGuestManagementStub;
import com.propertyvista.yardi.stubs.YardiILSGuestCardStub;
import com.propertyvista.yardi.stubs.YardiResidentNoTenantsExistException;
import com.propertyvista.yardi.stubs.YardiResidentTransactionsStub;

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
        List<String> propertyListCodes = null;
        try {
            ServerSideFactory.create(YardiConfigurationFacade.class).startYardiTimer();

            ServerSideFactory.create(NotificationFacade.class).aggregateNotificationsStart();
            if (yc.propertyListCodes().isNull()) {
                List<YardiPropertyConfiguration> propertyConfigurations = getPropertyConfigurations(stub, yc);
                propertyListCodes = new ArrayList<String>();
                for (YardiPropertyConfiguration yardiPropertyConfiguration : propertyConfigurations) {
                    propertyListCodes.add(yardiPropertyConfiguration.propertyID().getValue());
                }
            } else {
                propertyListCodes = Arrays.asList(yc.propertyListCodes().getValue().trim().split("\\s*,\\s*"));
            }

            List<Building> importedBuildings = new ArrayList<Building>();
            // resident transactions
            if (!executionMonitor.isTerminationRequested()) {
                List<ResidentTransactions> allTransactions = getAllResidentTransactions(stub, yc, executionMonitor, propertyListCodes);
                for (ResidentTransactions transaction : allTransactions) {
                    if (executionMonitor.isTerminationRequested()) {
                        break;
                    }
                    importedBuildings.addAll(importTransaction(yardiInterfaceId, transaction, executionMonitor, stub));
                }
            }

            // lease charges
            if (!executionMonitor.isTerminationRequested()) {
                List<ResidentTransactions> allLeaseCharges = getAllLeaseCharges(stub, yc, executionMonitor, importedBuildings);
                for (ResidentTransactions leaseCharges : allLeaseCharges) {
                    if (executionMonitor.isTerminationRequested()) {
                        break;
                    }

                    importLeaseCharges(yardiInterfaceId, leaseCharges, executionMonitor, stub);
                }
            }

            // availability
            List<PhysicalProperty> properties = null;
            if (!executionMonitor.isTerminationRequested() && (ApplicationMode.isDevelopment() || !VistaTODO.pendingYardiConfigPatchILS)) {
                properties = getILSPropertyMarketing(yc, executionMonitor, propertyListCodes);
                for (PhysicalProperty property : properties) {
                    if (executionMonitor.isTerminationRequested()) {
                        break;
                    }
                    // process each property info
                    importPropertyMarketingInfo(yardiInterfaceId, property, executionMonitor);
                }
            }

            // product catalog:
            if (!executionMonitor.isTerminationRequested() && (ApplicationMode.isDevelopment() || !VistaTODO.pendingYardiConfigPatchILS)) {
                for (Building building : importedBuildings) {
                    if (executionMonitor.isTerminationRequested()) {
                        break;
                    }
                    updateProductCatalog(yc, building, getBuildingDepositInfo(building, properties));
                }
            }

        } finally {
            long yardiTime = ServerSideFactory.create(YardiConfigurationFacade.class).stopYardiTimer();
            executionMonitor.addInfoEvent("yardiTime", TimeUtils.durationFormat(yardiTime), new BigDecimal(yardiTime));
            ServerSideFactory.create(NotificationFacade.class).aggregatedNotificationsSend();
        }

        log.info("Update completed.");
    }

    public void updateLease(PmcYardiCredential yc, Lease lease) throws YardiServiceException, RemoteException {
        // Each DB update function called in this method should be UnitOfWork, Unable to wrap here because for dual exception thrown.
        //This transaction should not update Lease, only in child unit of work

        YardiResidentTransactionsStub stub = ServerSideFactory.create(YardiResidentTransactionsStub.class);
        final Key yardiInterfaceId = yc.getPrimaryKey();
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
            // Do not remove lease charges from submitted lease applications
            if (lease.status().getValue() != Lease.Status.Approved) {
                terminateLeaseCharges(lease, null);
            }
        }
        if (leaseCharges != null) {
            boolean processed = false;
            // we should just get one element in the list for the requested leaseId
            for (Property property : leaseCharges.getProperty()) {
                for (RTCustomer rtCustomer : property.getRTCustomer()) {
                    processed |= importLeaseCharges(yardiInterfaceId, propertyCode, rtCustomer, null);
                }
            }
            // handle non-processed lease
            if ((!processed) && (lease.status().getValue() != Lease.Status.Approved)) {
                terminateLeaseCharges(lease, null);
            }
        }
    }

    public void updateProductCatalog(PmcYardiCredential yc, Building building) throws YardiServiceException {
        YardiILSGuestCardStub stub = ServerSideFactory.create(YardiILSGuestCardStub.class);
        PhysicalProperty propertyMarketing = stub.getPropertyMarketingInfo(yc, building.propertyCode().getValue());
        updateProductCatalog(yc, building, getBuildingDepositInfo(building, Arrays.asList(propertyMarketing)));
    }

    public void updateProductCatalog(PmcYardiCredential yc, Building building, Map<String, BigDecimal> depositInfo) throws YardiServiceException {
        YardiGuestManagementStub stub = ServerSideFactory.create(YardiGuestManagementStub.class);
        RentableItems rentableItems = stub.getRentableItems(yc, building.propertyCode().getValue());
        if (rentableItems != null && !rentableItems.getItemType().isEmpty()) {
            importProductCatalog(yc.getPrimaryKey(), building, rentableItems, depositInfo);
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
        return getPropertyConfigurations(stub.getPropertyConfigurations(yc));
    }

    private Map<String, BigDecimal> getBuildingDepositInfo(Building building, List<PhysicalProperty> marketingInfo) {
        for (PhysicalProperty propertyInfo : marketingInfo) {
            for (com.yardi.entity.ils.Property property : propertyInfo.getProperty()) {
                String propertyCode = property.getPropertyID().getIdentification().getPrimaryID();
                if (propertyCode != null && propertyCode.equals(building.propertyCode().getValue())) {
                    return new YardiILSMarketingProcessor().getDepositInfo(property);
                }
            }
        }
        return java.util.Collections.emptyMap();
    }

    private List<YardiPropertyConfiguration> getPropertyConfigurations(Properties properties) {
        List<YardiPropertyConfiguration> propertyConfigurations = new ArrayList<YardiPropertyConfiguration>();
        for (com.propertyvista.yardi.beans.Property property : properties.getProperties()) {
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
                for (final RTCustomer rtCustomer : YardiLeaseProcessor.sortRtCustomers(property.getRTCustomer())) {
                    String leaseId = rtCustomer.getCustomerID();
                    log.info("  for {}", leaseId);

                    removeLease(activeLeases, leaseId);

                    try {
                        Unit yardiUnit = rtCustomer.getRTUnit().getUnit();
                        // VISTA-3307 Yardi API ResidentTransactions is broken! the filed unitRent is actually Market Rent, Make vista internal API consistent wit ISL
                        {
                            Information yardiUnitInfo = yardiUnit.getInformation().get(0);
                            if ((yardiUnitInfo.getMarketRent() == null) || (yardiUnitInfo.getMarketRent().compareTo(BigDecimal.ZERO) == 0)) {
                                yardiUnitInfo.setMarketRent(yardiUnitInfo.getUnitRent());
                            }
                        }

                        AptUnit unit = importUnit(building, yardiUnit, executionMonitor);
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
        if (address.getAddress1() == null) {
            return;
        }

        StringBuilder importedAddressToString = new StringBuilder();
        importedAddressToString.append("(");
        importedAddressToString.append("address1: ").append(address.getAddress1()).append(", ");
        importedAddressToString.append("address2: ").append(address.getAddress2()).append(", ");
        importedAddressToString.append("city: ").append(address.getCity()).append(", ");
        importedAddressToString.append("state: ").append(address.getState()).append(", ");
        importedAddressToString.append("province: ").append(address.getProvince()).append(", ");
        importedAddressToString.append("postalCode: ").append(address.getPostalCode()).append(", ");
        importedAddressToString.append("country: ").append(address.getCountry()).append(", ");
        importedAddressToString.append("countyName: ").append(address.getCountyName()).append("");
        importedAddressToString.append(")");
        log.info("Unit pk={}: Trying to assign legal address: '{}'", unit.getPrimaryKey(), importedAddressToString);

        new UnitOfWork(TransactionScopeOption.RequiresNew).execute(new Executable<Void, YardiServiceException>() {
            @Override
            public Void execute() throws YardiServiceException {
                StringBuilder addrErr = new StringBuilder();
                unit.info().legalAddress().set(MappingUtils.getAddress(address, addrErr));
                if (addrErr.length() > 0) {
                    String msg = SimpleMessageFormat.format("Unit pk={0}: got invalid address {1}", unit.getPrimaryKey(), addrErr);
                    log.warn(msg);
                    executionMonitor.addInfoEvent("ParseAddress", msg);
                } else {
                    log.info("Unit pk={}: legal address has been assigned successfully", unit.getPrimaryKey());
                }

                ServerSideFactory.create(BuildingFacade.class).persist(unit);
                return null;
            }
        });
    }

    private LeaseFinancialStats importLease(final Key yardiInterfaceId, final String propertyCode, final RTCustomer rtCustomer,
            final ExecutionMonitor executionMonitor) throws YardiServiceException {
        final LeaseFinancialStats state = new LeaseFinancialStats();

        log.info("    Importing Lease (customerId={}):", rtCustomer.getCustomerID());

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
                        if (rtCustomer.getRTServiceTransactions().getTransactions().isEmpty()) {
                            log.info("          No Transactions for Lease customerId={} ", rtCustomer.getCustomerID());
                        }
                        for (final Transactions tr : rtCustomer.getRTServiceTransactions().getTransactions()) {
                            if (tr != null) {
                                if (tr.getCharge() != null) {
                                    ChargeDetail detail = tr.getCharge().getDetail();
                                    log.info("          Updating charge (transactionId={}, due={}, paid={}):", detail.getTransactionID(),
                                            detail.getBalanceDue(), detail.getAmountPaid());
                                    BigDecimal amountPaid = new BigDecimal(detail.getAmountPaid());
                                    BigDecimal balanceDue = new BigDecimal(detail.getBalanceDue());
                                    BigDecimal amount = amountPaid.add(balanceDue);
                                    InvoiceLineItem charge = YardiARIntegrationAgent.createCharge(account, detail);
                                    Persistence.service().persist(charge);
                                    state.addCharge(charge.amount().getValue());
                                    // for a partially paid charge add fully consumed credit for the amount paid
                                    if (amount.compareTo(BigDecimal.ZERO) > 0 && amountPaid.compareTo(BigDecimal.ZERO) > 0) {
                                        // negate amount
                                        detail.setAmount("-" + detail.getAmountPaid());
                                        detail.setBalanceDue("0.00"); // translates to fully consumed credit
                                        detail.setAmountPaid(detail.getAmount()); // ensure balance
                                        detail.setDescription(i18n.tr("{0} amount paid", detail.getDescription()));
                                        charge = YardiARIntegrationAgent.createCharge(account, detail);
                                        Persistence.service().persist(charge);
                                        state.addCharge(charge.amount().getValue());
                                    }
                                }

                                if (tr.getPayment() != null) {
                                    log.info("          Updating payment (transactionId={}, amount={}) ", tr.getPayment().getDetail().getTransactionID(), tr
                                            .getPayment().getDetail().getAmount());
                                    YardiPayment payment = YardiARIntegrationAgent.createPayment(account, tr.getPayment());
                                    Persistence.service().persist(payment);
                                    state.addPayment(payment.amount().getValue());
                                }
                            }
                        }
                    } else {
                        log.info("          No RT Service Transactions Received for Lease customerId={} ", rtCustomer.getCustomerID());
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

    private void importProductCatalog(final Key yardiInterfaceId, final Building building, final RentableItems rentableItems,
            final Map<String, BigDecimal> depositInfo) throws YardiServiceException {
        new UnitOfWork(TransactionScopeOption.RequiresNew).execute(new Executable<Void, YardiServiceException>() {
            @Override
            public Void execute() throws YardiServiceException {
                YardiProductCatalogProcessor processor = new YardiProductCatalogProcessor();

                processor.processCatalog(building, rentableItems, yardiInterfaceId);
                processor.updateUnits(building, depositInfo);
                processor.persistCatalog(building);

                return null;
            }
        });
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
                    // Do not remove lease charges from submitted lease applications
                    if (leaseId.status().getValue() == Lease.Status.Approved) {
                        continue;
                    }
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

    private boolean importLeaseCharges(Key yardiInterfaceId, final String propertyCode, final RTCustomer rtCustomer, final ExecutionMonitor executionMonitor)
            throws YardiServiceException {
        // make sure we have received any transactions
        if (rtCustomer.getRTServiceTransactions().getTransactions().size() == 0) {
            log.info("No Lease Charges received for property: ", propertyCode);
            return false;
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

        return true;
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

    private List<PhysicalProperty> getILSPropertyMarketing(PmcYardiCredential yc, ExecutionMonitor executionMonitor, List<String> propertyListCodes) {
        YardiILSGuestCardStub stub = ServerSideFactory.create(YardiILSGuestCardStub.class);
        List<PhysicalProperty> marketingInfo = new ArrayList<PhysicalProperty>();
        for (String propertyListCode : propertyListCodes) {
            if (executionMonitor.isTerminationRequested()) {
                break;
            }

            try {
                PhysicalProperty propertyMarketing = stub.getPropertyMarketingInfo(yc, propertyListCode);
                if (propertyMarketing != null) {
                    marketingInfo.add(propertyMarketing);
                }
                executionMonitor.addInfoEvent("ILSPropertyMarketing", propertyListCode);
            } catch (YardiServiceException e) {
                executionMonitor.addFailedEvent("ILSPropertyMarketing", propertyListCode, e);
            }
        }

        return marketingInfo;
    }

    private void importPropertyMarketingInfo(final Key yardiInterfaceId, PhysicalProperty propertyInfo, final ExecutionMonitor executionMonitor) {
        log.info("PropertyMarketing: import started...");

        for (final com.yardi.entity.ils.Property property : propertyInfo.getProperty()) {
            String propertyCode = property.getPropertyID().getIdentification().getPrimaryID();

            try {
                log.info("  Processing building: {}", propertyCode);

                Building building = importProperty(yardiInterfaceId, property.getPropertyID(), executionMonitor);
                executionMonitor.addProcessedEvent("Building");

                log.info("  Processing units for building: {}", propertyCode);

                // clear unit availability to handle yardi Unit "Exclude" checkbox (no data sent for those units)
                clearUnitAvailability(building, executionMonitor);

                // process new availability data
                for (ILSUnit ilsUnit : property.getILSUnit()) {
                    AptUnit aptUnit = importUnit(building, ilsUnit.getUnit(), executionMonitor);
                    updateAvailability(aptUnit, ilsUnit.getAvailability(), executionMonitor);
                    updateAvailabilityReport(aptUnit, ilsUnit, executionMonitor);
                    executionMonitor.addProcessedEvent("Unit");
                }

            } catch (YardiServiceException e) {
                executionMonitor.addFailedEvent("Building", propertyCode, e);
            } catch (Throwable t) {
                executionMonitor.addErredEvent("Building", propertyCode, t);
            }

            if (executionMonitor.isTerminationRequested()) {
                break;
            }
        }
    }

    private void clearUnitAvailability(final Building building, ExecutionMonitor executionMonitor) throws YardiServiceException {
        log.info("    clear unit availability: {}", building.propertyCode().getValue());
        new UnitOfWork(TransactionScopeOption.RequiresNew).execute(new Executable<Void, YardiServiceException>() {
            @Override
            public Void execute() throws YardiServiceException {
                EntityQueryCriteria<AptUnit> crit = EntityQueryCriteria.create(AptUnit.class);
                crit.eq(crit.proto().building(), building);
                for (AptUnit unit : Persistence.service().query(crit)) {
                    new YardiILSMarketingProcessor().updateAvailability(unit, null);
                }
                return null;
            }
        });
    }

    private void updateAvailability(final AptUnit unit, final Availability avail, ExecutionMonitor executionMonitor) throws YardiServiceException {
        log.info("    availability: {}: {}", unit.getStringView(), (avail == null || avail.getVacateDate() == null ? "Not " : "") + "Available");
        new UnitOfWork(TransactionScopeOption.RequiresNew).execute(new Executable<Void, YardiServiceException>() {
            @Override
            public Void execute() throws YardiServiceException {
                new YardiILSMarketingProcessor().updateAvailability(unit, avail);
                return null;
            }
        });
    }

    private void updateAvailabilityReport(final AptUnit unit, final ILSUnit ilsUnit, ExecutionMonitor executionMonitor) {
        final YardiUnitAvailabilityStatusAdapter unitAvailabilityStatusAdapter = new YardiUnitAvailabilityStatusAdapter();
        try {
            new UnitOfWork(TransactionScopeOption.RequiresNew).execute(new Executable<Void, Throwable>() {
                @Override
                public Void execute() throws YardiServiceException {
                    LogicalDate today = SystemDateManager.getLogicalDate();

                    UnitAvailabilityStatus currentAvailabilityStatus = retrieveCurrentAvailabilityStatus(unit);
                    if (currentAvailabilityStatus != null) {
                        if (currentAvailabilityStatus.statusFrom().getValue().compareTo(today) == 0) {
                            EntityQueryCriteria<UnitAvailabilityStatus> criteria = EntityQueryCriteria.create(UnitAvailabilityStatus.class);
                            Persistence.service().delete(criteria);
                        } else {
                            GregorianCalendar cal = new GregorianCalendar();
                            cal.setTime(today);
                            cal.add(GregorianCalendar.DAY_OF_MONTH, -1);
                            LogicalDate yesterday = new LogicalDate(cal.getTime());
                            currentAvailabilityStatus.statusUntil().setValue(yesterday);
                            Persistence.service().persist(currentAvailabilityStatus);
                        }
                    }

                    UnitAvailabilityStatus newAvailabilityStatus = unitAvailabilityStatusAdapter.extractAvailabilityStatus(ilsUnit);
                    newAvailabilityStatus.statusFrom().setValue(today);
                    newAvailabilityStatus.statusUntil().setValue(OccupancyFacade.MAX_DATE);
                    unitAvailabilityStatusAdapter.mergeUnitInfo(newAvailabilityStatus, unit);
                    unitAvailabilityStatusAdapter.mergeLeaseInfo(newAvailabilityStatus);
                    Persistence.service().persist(newAvailabilityStatus);
                    return null;
                }

            });
            executionMonitor.addProcessedEvent("unit availability status", new BigDecimal(1));
        } catch (Throwable e) {
            executionMonitor.addFailedEvent("unit availability status", new BigDecimal(1));
            log.error(SimpleMessageFormat.format("failed to import availability status for unit pk={0}", unit.getPrimaryKey()), e);
        }
    }

    private UnitAvailabilityStatus retrieveCurrentAvailabilityStatus(AptUnit unit) {
        EntityQueryCriteria<UnitAvailabilityStatus> criteria = EntityQueryCriteria.create(UnitAvailabilityStatus.class);
        criteria.eq(criteria.proto().unit(), unit);
        criteria.eq(criteria.proto().statusUntil(), OccupancyFacade.MAX_DATE);
        UnitAvailabilityStatus status = Persistence.service().retrieve(criteria);
        return status;
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
