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
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.axis2.AxisFault;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Predicate;
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
import com.yardi.entity.resident.Property;
import com.yardi.entity.resident.RTCustomer;
import com.yardi.entity.resident.ResidentTransactions;

import com.pyx4j.commons.ConverterUtils;
import com.pyx4j.commons.ConverterUtils.ToStringConverter;
import com.pyx4j.commons.Key;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.commons.SimpleMessageFormat;
import com.pyx4j.commons.TimeUtils;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.config.server.SystemDateManager;
import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.server.Executable;
import com.pyx4j.entity.server.IEntityPersistenceService.ICursorIterator;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.server.TransactionScopeOption;
import com.pyx4j.entity.server.UnitOfWork;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.biz.ExecutionMonitor;
import com.propertyvista.biz.ExecutionMonitor.IterationProgressCounter;
import com.propertyvista.biz.asset.BuildingFacade;
import com.propertyvista.biz.communication.NotificationFacade;
import com.propertyvista.biz.financial.payment.PaymentMethodFacade;
import com.propertyvista.biz.occupancy.OccupancyFacade;
import com.propertyvista.biz.system.YardiPropertyNoAccessException;
import com.propertyvista.biz.system.YardiServiceException;
import com.propertyvista.biz.system.yardi.YardiConfigurationFacade;
import com.propertyvista.domain.dashboard.gadgets.availability.UnitAvailabilityStatus;
import com.propertyvista.domain.financial.ARCode;
import com.propertyvista.domain.financial.BillingAccount.BillingPeriod;
import com.propertyvista.domain.financial.billing.BillingCycle;
import com.propertyvista.domain.financial.yardi.YardiReceiptReversal;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.property.yardi.YardiPropertyConfiguration;
import com.propertyvista.domain.settings.PmcYardiCredential;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.misc.VistaTODO;
import com.propertyvista.operations.domain.scheduler.CompletionType;
import com.propertyvista.yardi.beans.Properties;
import com.propertyvista.yardi.mappers.BuildingsMapper;
import com.propertyvista.yardi.mappers.MappingUtils;
import com.propertyvista.yardi.processors.YardiBuildingProcessor;
import com.propertyvista.yardi.processors.YardiILSMarketingProcessor;
import com.propertyvista.yardi.processors.YardiLeaseFinancialProcessor;
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

    // Public interface:

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
    public static String ping(YardiResidentTransactionsStub stub, PmcYardiCredential yc) throws AxisFault, RemoteException {
        return stub.ping(yc);
    }

    static public List<Property> getProperties(ResidentTransactions transaction) {
        List<Property> properties = new ArrayList<Property>();
        for (Property property : transaction.getProperty()) {
            properties.add(property);
        }
        return properties;
    }

    public List<YardiPropertyConfiguration> getPropertyConfigurations(PmcYardiCredential yc) throws YardiServiceException, RemoteException {
        return getPropertyConfigurations(ServerSideFactory.create(YardiResidentTransactionsStub.class), yc);
    }

    public List<YardiPropertyConfiguration> getPropertyConfigurations(YardiResidentTransactionsStub stub, PmcYardiCredential yc) throws YardiServiceException,
            RemoteException {
        return getPropertyConfigurations(stub.getPropertyConfigurations(yc));
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
        // retrieve list of property codes configured for PMC in Yardi
        List<String> propertyCodes = ServerSideFactory.create(YardiConfigurationFacade.class).retrievePropertyCodes(yc, executionMonitor);

        // Find buildings that are no longer in the list and suspend them
        EntityQueryCriteria<Building> criteria = EntityQueryCriteria.create(Building.class);
        criteria.eq(criteria.proto().integrationSystemId(), yc.getPrimaryKey());
        criteria.in(criteria.proto().suspended(), false);
        ICursorIterator<Building> buildings = Persistence.service().query(null, criteria, AttachLevel.Attached);
        try {
            while (buildings.hasNext()) {
                final Building building = buildings.next();
                if (!CollectionUtils.exists(propertyCodes, new Predicate<String>() {
                    @Override
                    public boolean evaluate(String object) {
                        return BuildingsMapper.getPropertyCode(object).equals(building.propertyCode().getValue());
                    }
                })) {
                    suspendBuilding(building);
                    executionMonitor.addInfoEvent("BuildingSuspended", building.propertyCode().getValue());
                }
            }
        } finally {
            buildings.close();
        }

        updateProperties(yc, propertyCodes, executionMonitor);
    }

    public void updateBuilding(PmcYardiCredential yc, Building building, ExecutionMonitor executionMonitor) throws YardiServiceException, RemoteException {
        updateProperties(yc, Arrays.asList(building.propertyCode().getValue()), executionMonitor);
    }

    public void updateLease(PmcYardiCredential yc, Lease lease, ExecutionMonitor executionMonitor) throws YardiServiceException, RemoteException {
        // Each DB update function called in this method should be UnitOfWork, Unable to wrap here because for dual exception thrown.
        //This transaction should not update Lease, only in child unit of work
        if (executionMonitor == null) {
            executionMonitor = new ExecutionMonitor();
        }
        executionMonitor.setExpectedTotal(4L);

        final Key yardiInterfaceId = yc.getPrimaryKey();
        String propertyCode = lease.unit().building().propertyCode().getValue();
        YardiResidentTransactionsStub stub = ServerSideFactory.create(YardiResidentTransactionsStub.class);

        ResidentTransactions transaction = stub.getResidentTransactionsForTenant(yc, propertyCode, lease.leaseId().getValue());
        executionMonitor.addProcessedEvent("ResidentTransactions Request");
        if (transaction != null && !transaction.getProperty().isEmpty()) {
            Property property = transaction.getProperty().iterator().next();
            if (!property.getRTCustomer().isEmpty()) {
                importLease(yardiInterfaceId, propertyCode, property.getRTCustomer().iterator().next(), executionMonitor);
            }
        }
        executionMonitor.addProcessedEvent("Import Lease");

        // import lease charges
        BillingCycle nextCycle = ServerSideFactory.create(PaymentMethodFacade.class).getNextAutopayBillingCycle(lease);
        ResidentTransactions leaseCharges = null;
        try {
            leaseCharges = stub.getLeaseChargesForTenant(yc, propertyCode, lease.leaseId().getValue(), nextCycle.billingCycleStartDate().getValue());
        } catch (YardiResidentNoTenantsExistException e) {
            log.warn("Can't get changes for {}; {}", lease.leaseId().getValue(), e.getMessage()); // log error and reset lease charges.
            // Do not remove lease charges from submitted lease applications
            if (lease.status().getValue() != Lease.Status.Approved) {
                terminateLeaseCharges(lease, executionMonitor);
            }
        }
        executionMonitor.addProcessedEvent("LeaseCharges Request");
        if (leaseCharges != null) {
            boolean processed = false;
            // we should just get one element in the list for the requested leaseId
            for (Property property : leaseCharges.getProperty()) {
                for (RTCustomer rtCustomer : property.getRTCustomer()) {
                    processed |= importLeaseCharges(yardiInterfaceId, propertyCode, rtCustomer, executionMonitor);
                }
            }
            // handle non-processed lease
            if ((!processed) && (lease.status().getValue() != Lease.Status.Approved)) {
                terminateLeaseCharges(lease, executionMonitor);
            }
        }
        executionMonitor.addProcessedEvent("Import LeaseCharges");
    }

    public void updateProductCatalog(PmcYardiCredential yc, Building building, ExecutionMonitor executionMonitor) throws YardiServiceException {
        PhysicalProperty propertyMarketing = ServerSideFactory.create(YardiILSGuestCardStub.class).getPropertyMarketingInfo(yc,
                building.propertyCode().getValue());
        Map<String, BigDecimal> depositInfo = getBuildingDepositInfo(building, Arrays.asList(propertyMarketing));

        updateProductCatalog(yc, building, depositInfo, executionMonitor);
    }

    public void updateProductCatalog(PmcYardiCredential yc, Building building, Map<String, BigDecimal> depositInfo, ExecutionMonitor executionMonitor)
            throws YardiServiceException {
        RentableItems rentableItems = ServerSideFactory.create(YardiGuestManagementStub.class).getRentableItems(yc, building.propertyCode().getValue());

        importProductCatalog(yc.getPrimaryKey(), building, rentableItems, depositInfo, executionMonitor);
    }

    public void postReceiptReversal(PmcYardiCredential yc, YardiReceiptReversal reversal) throws YardiServiceException, RemoteException {
        YardiPaymentProcessor paymentProcessor = new YardiPaymentProcessor();
        ResidentTransactions reversalTransactions = paymentProcessor.createTransactions(paymentProcessor.createTransactionForReversal(reversal));

        ServerSideFactory.create(YardiResidentTransactionsStub.class).importResidentTransactions(yc, reversalTransactions);
    }

    // Internal implementation:

    private Map<String, BigDecimal> getBuildingDepositInfo(Building building, List<PhysicalProperty> marketingInfo) {
        for (PhysicalProperty propertyInfo : marketingInfo) {
            for (com.yardi.entity.ils.Property property : propertyInfo.getProperty()) {
                String propertyCode = BuildingsMapper.getPropertyCode(property.getPropertyID());
                if (propertyCode != null && propertyCode.equals(building.propertyCode().getValue())) {
                    return new YardiILSMarketingProcessor().getDepositInfo(property);
                }
            }
        }
        return Collections.emptyMap();
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

    private List<ResidentTransactions> getResidentTransactions(YardiResidentTransactionsStub stub, PmcYardiCredential yc, ExecutionMonitor executionMonitor,
            List<String> propertyCodes) throws YardiServiceException, RemoteException {
        List<ResidentTransactions> transactions = new ArrayList<ResidentTransactions>();

        final Key yardiInterfaceId = yc.getPrimaryKey();
        for (String propertyCode : propertyCodes) {
            if (executionMonitor.isTerminationRequested()) {
                break;
            }
            Building building = MappingUtils.getBuilding(yardiInterfaceId, propertyCode);
            if (building != null) {
                if (building.suspended().getValue()) {
                    executionMonitor.addInfoEvent("skip suspended property code for transaction import", CompletionType.failed, propertyCode, null);
                    continue;
                }
            } else {
                // process as propertyCode or new building
            }
            try {
                ResidentTransactions residentTransactions = stub.getAllResidentTransactions(yc, propertyCode);
                if (residentTransactions != null) {
                    transactions.add(residentTransactions);
                }
                executionMonitor.addInfoEvent("PropertyTransactions", propertyCode);
            } catch (YardiPropertyNoAccessException e) {
                if (suspendBuilding(yardiInterfaceId, propertyCode)) {
                    executionMonitor.addErredEvent("BuildingSuspended", e);
                } else {
                    executionMonitor.addFailedEvent("PropertyTransactions", propertyCode, e);
                }
            }
        }

        return transactions;
    }

    // TODO - we may need to request Yardi charges for one more cycle forward
    private List<ResidentTransactions> getLeaseCharges(YardiResidentTransactionsStub stub, PmcYardiCredential yc, ExecutionMonitor executionMonitor,
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
        for (Building building : buildings) {
            if (executionMonitor.isTerminationRequested()) {
                break;
            }
            if (building.suspended().getValue()) {
                executionMonitor.addInfoEvent("skip suspended property code for charges import", CompletionType.failed, building.propertyCode().getValue(),
                        null);
            } else {
                BillingCycle nextCycle = ServerSideFactory.create(PaymentMethodFacade.class).getNextAutopayBillingCycle(building, BillingPeriod.Monthly, 1);
                try {
                    ResidentTransactions residentTransactions = stub.getAllLeaseCharges(yc, building.propertyCode().getValue(), nextCycle
                            .billingCycleStartDate().getValue());
                    if (residentTransactions != null) {
                        transactions.add(residentTransactions);
                    }
                    executionMonitor.addInfoEvent("PropertyCharges", building.propertyCode().getValue());
                } catch (YardiPropertyNoAccessException e) {
                    if (suspendBuilding(yardiInterfaceId, building.propertyCode().getValue())) {
                        executionMonitor.addErredEvent("BuildingSuspended", e);
                    } else {
                        executionMonitor.addFailedEvent("PropertyCharges", building.propertyCode().getValue(), e);
                    }
                }
            }
        }

        return transactions;
    }

    private List<PhysicalProperty> getILSPropertyMarketing(PmcYardiCredential yc, ExecutionMonitor executionMonitor, List<String> propertyCodes) {
        YardiILSGuestCardStub stub = ServerSideFactory.create(YardiILSGuestCardStub.class);
        List<PhysicalProperty> marketingInfo = new ArrayList<PhysicalProperty>();

        for (String propertyCode : propertyCodes) {
            if (executionMonitor.isTerminationRequested()) {
                break;
            }

            Building building = MappingUtils.getBuilding(yc.getPrimaryKey(), propertyCode);
            if (building != null && building.suspended().isBooleanTrue()) {
                executionMonitor.addInfoEvent("ILSPropertyMarketing", "skipped suspended building: " + propertyCode);
                continue;
            }

            try {
                PhysicalProperty propertyMarketing = stub.getPropertyMarketingInfo(yc, propertyCode);
                if (propertyMarketing != null) {
                    marketingInfo.add(propertyMarketing);
                }
                executionMonitor.addInfoEvent("ILSPropertyMarketing", "accessing building info: " + propertyCode);
            } catch (YardiServiceException e) {
                executionMonitor.addErredEvent("ILSPropertyMarketing", "accessing building info: " + propertyCode, e);
            }
        }

        return marketingInfo;
    }

    private void updateProperties(PmcYardiCredential yc, List<String> propertyCodes, ExecutionMonitor executionMonitor) throws YardiServiceException,
            RemoteException {
        try {
            ServerSideFactory.create(YardiConfigurationFacade.class).startYardiTimer();
            ServerSideFactory.create(NotificationFacade.class).aggregateNotificationsStart();

            YardiResidentTransactionsStub stub = ServerSideFactory.create(YardiResidentTransactionsStub.class);
            final Key yardiInterfaceId = yc.getPrimaryKey();

            List<Building> importedBuildings = new ArrayList<>();

            // resident transactions
            if (!executionMonitor.isTerminationRequested()) {
                List<ResidentTransactions> transactions = getResidentTransactions(stub, yc, executionMonitor, propertyCodes);
                for (ResidentTransactions transaction : transactions) {
                    if (executionMonitor.isTerminationRequested()) {
                        break;
                    }
                    importedBuildings.addAll(importTransaction(yardiInterfaceId, transaction, executionMonitor, stub));
                }
            }

            // lease charges:
            if (!executionMonitor.isTerminationRequested()) {
                List<ResidentTransactions> allLeaseCharges = getLeaseCharges(stub, yc, executionMonitor, importedBuildings);
                for (ResidentTransactions leaseCharges : allLeaseCharges) {
                    if (executionMonitor.isTerminationRequested()) {
                        break;
                    }
                    importLeaseCharges(yardiInterfaceId, leaseCharges, executionMonitor, stub);
                }
            }

            // availability:
            List<PhysicalProperty> properties = Collections.emptyList();
            if (!executionMonitor.isTerminationRequested() && (ApplicationMode.isDevelopment() || !VistaTODO.pendingYardiConfigPatchILS)) {
                properties = getILSPropertyMarketing(yc, executionMonitor, propertyCodes);
                for (PhysicalProperty property : properties) {
                    if (executionMonitor.isTerminationRequested()) {
                        break;
                    }
                    importedBuildings.addAll(importPropertyMarketingInfo(yardiInterfaceId, property, importedBuildings, executionMonitor));
                }
            }

            // product catalog:
            if (!executionMonitor.isTerminationRequested() && (ApplicationMode.isDevelopment() || !VistaTODO.pendingYardiConfigPatchILS)) {
                for (Building building : importedBuildings) {
                    if (executionMonitor.isTerminationRequested()) {
                        break;
                    }
                    updateProductCatalog(yc, building, getBuildingDepositInfo(building, properties), executionMonitor);
                }
            }

        } finally {
            AtomicReference<Long> maxRequestTime = new AtomicReference<>();
            long yardiTime = ServerSideFactory.create(YardiConfigurationFacade.class).stopYardiTimer(maxRequestTime);

            executionMonitor.addInfoEvent("yardiTime", TimeUtils.durationFormat(yardiTime), new BigDecimal(yardiTime));
            executionMonitor.addInfoEvent("yardiMaxRequestTime", TimeUtils.durationFormat(maxRequestTime.get()), new BigDecimal(maxRequestTime.get()));

            ServerSideFactory.create(NotificationFacade.class).aggregatedNotificationsSend();
        }

        log.info("Update completed.");
    }

    private List<Building> importTransaction(Key yardiInterfaceId, ResidentTransactions transaction, final ExecutionMonitor executionMonitor,
            final ExternalInterfaceLoggingStub interfaceLog) {
        // this is (going to be) the core import process that updates buildings, units in them, leases and charges
        log.info("ResidentTransactions: Import started...");

        List<Building> importedBuildings = new ArrayList<Building>();
        Map<String, List<Lease>> notProcessedLeases = new HashMap<String, List<Lease>>();

        List<Property> properties = getProperties(transaction);
        for (final Property property : properties) {
            if (executionMonitor != null) {
                IterationProgressCounter expectedTotal = executionMonitor.getIterationProgressCounter("Unit");
                if (expectedTotal == null) {
                    executionMonitor.setIterationProgressCounter("Unit", new IterationProgressCounter(properties.size(), property.getRTCustomer().size()));
                } else {
                    expectedTotal.onIterationCompleted(property.getRTCustomer().size());
                }
            }

            try {
                Building building = importBuiling(yardiInterfaceId, property.getPropertyID().get(0), executionMonitor);
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
                            importLease(yardiInterfaceId, propertyCode, rtCustomer, executionMonitor);
                        } catch (YardiServiceException e) {
                            executionMonitor.addFailedEvent("Transactions",
                                    SimpleMessageFormat.format("Lease for customer {0} ({1})", rtCustomer.getCustomerID(), propertyCode), e);
                            interfaceLog.logRecordedTracastions();
                        } catch (Throwable t) {
                            executionMonitor.addErredEvent("Transactions",
                                    SimpleMessageFormat.format("Lease for customer {0} ({1})", rtCustomer.getCustomerID(), propertyCode), t);
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
                executionMonitor.addFailedEvent("Building", BuildingsMapper.getPropertyCode(property.getPropertyID().get(0)), e);
                interfaceLog.logRecordedTracastions();
            } catch (Throwable t) {
                executionMonitor.addErredEvent("Building", BuildingsMapper.getPropertyCode(property.getPropertyID().get(0)), t);
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

    private Building importBuiling(final Key yardiInterfaceId, final PropertyIDType propertyId, final ExecutionMonitor executionMonitor)
            throws YardiServiceException {
        log.info("Updating building {}", propertyId.getIdentification().getPrimaryID());

        return new UnitOfWork(TransactionScopeOption.RequiresNew).execute(new Executable<Building, YardiServiceException>() {
            @Override
            public Building execute() throws YardiServiceException {
                Building building = new YardiBuildingProcessor(executionMonitor).updateBuilding(yardiInterfaceId, propertyId);
                ServerSideFactory.create(BuildingFacade.class).persist(building);
                return building;
            }
        });
    }

    private AptUnit importUnit(final Building building, final Unit unit, ExecutionMonitor executionMonitor) throws YardiServiceException {
        log.info("    Updating unit #" + unit.getInformation().get(0).getUnitID());

        return new UnitOfWork(TransactionScopeOption.RequiresNew).execute(new Executable<AptUnit, YardiServiceException>() {
            @Override
            public AptUnit execute() throws YardiServiceException {
                AptUnit aptUnit = new YardiBuildingProcessor().updateUnit(building, unit);
                ServerSideFactory.create(BuildingFacade.class).persist(aptUnit);
                return aptUnit;
            }
        });
    }

    private void importProductCatalog(final Key yardiInterfaceId, final Building building, final RentableItems rentableItems,
            final Map<String, BigDecimal> depositInfo, final ExecutionMonitor executionMonitor) throws YardiServiceException {
        new UnitOfWork(TransactionScopeOption.RequiresNew).execute(new Executable<Void, YardiServiceException>() {
            @Override
            public Void execute() throws YardiServiceException {
                YardiProductCatalogProcessor processor = new YardiProductCatalogProcessor(executionMonitor);

                processor.processCatalog(building, rentableItems, yardiInterfaceId);
                processor.updateUnits(building, depositInfo);
                processor.persistCatalog(building);

                return null;
            }
        });
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

    private void importLease(final Key yardiInterfaceId, final String propertyCode, final RTCustomer rtCustomer, final ExecutionMonitor executionMonitor)
            throws YardiServiceException {

        log.info("    Importing Lease (customerId={}):", rtCustomer.getCustomerID());

        if (YardiLeaseProcessor.isEligibleForProcessing(rtCustomer)) {
            new UnitOfWork(TransactionScopeOption.RequiresNew).execute(new Executable<Void, YardiServiceException>() {
                @Override
                public Void execute() throws YardiServiceException {

                    new YardiLeaseProcessor(executionMonitor).processLease(rtCustomer, yardiInterfaceId, propertyCode);
                    new YardiLeaseFinancialProcessor(executionMonitor).processLease(rtCustomer, yardiInterfaceId);

                    return null;
                }
            });
        } else {
            log.info("    Lease and transactions for: {} skipped, lease does not meet criteria.", rtCustomer.getCustomerID());
            // TODO skipping monitor message
        }
    }

    private void importLeaseCharges(Key yardiInterfaceId, ResidentTransactions leaseCharges, final ExecutionMonitor executionMonitor,
            final ExternalInterfaceLoggingStub interfaceLog) {
        log.info("LeaseCharges: import started...");

        // although we get properties here, all data inside is empty until we get down to the ChargeDetail level
        List<Property> properties = getProperties(leaseCharges);
        Map<String, List<Lease>> notProcessedLeases = new HashMap<String, List<Lease>>();
        for (final Property property : properties) {
            if (executionMonitor != null) {
                IterationProgressCounter expectedTotal = executionMonitor.getIterationProgressCounter("Lease");
                if (expectedTotal == null) {
                    executionMonitor.setIterationProgressCounter("Lease", new IterationProgressCounter(properties.size(), property.getRTCustomer().size()));
                } else {
                    expectedTotal.onIterationCompleted(property.getRTCustomer().size());
                }
            }

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
                        executionMonitor.addProcessedEvent("Lease");
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
        final Lease lease = new YardiLeaseProcessor(executionMonitor).findLease(yardiInterfaceId, propertyCode, customerId);
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
                    Persistence.ensureRetrieve(leaseId.unit().building(), AttachLevel.Attached);
                    String msg = SimpleMessageFormat.format("charges expired for lease {0} ({1})", leaseId.leaseId(), leaseId.unit().building().propertyCode()
                            .getValue());
                    log.info(msg);
                    if (executionMonitor != null) {
                        executionMonitor.addInfoEvent("chargesExpired", msg);
                    }
                }
                return null;
            }
        });
    }

    private List<Building> importPropertyMarketingInfo(final Key yardiInterfaceId, PhysicalProperty propertyInfo, List<Building> importedBuildings,
            final ExecutionMonitor executionMonitor) {
        log.info("PropertyMarketing: import started...");

        List<Building> newBuildings = new ArrayList<>();

        for (final com.yardi.entity.ils.Property property : propertyInfo.getProperty()) {
            if (executionMonitor != null) {
                IterationProgressCounter expectedTotal = executionMonitor.getIterationProgressCounter("Availability");
                if (expectedTotal == null) {
                    executionMonitor.setIterationProgressCounter("Availability", new IterationProgressCounter(propertyInfo.getProperty().size(), property
                            .getILSUnit().size()));
                } else {
                    expectedTotal.onIterationCompleted(property.getILSUnit().size());
                }
            }

            String propertyCode = BuildingsMapper.getPropertyCode(property.getPropertyID());

            try {
                log.info("  Processing building: {}", propertyCode);

                // import new buildings
                Building building = MappingUtils.getBuilding(yardiInterfaceId, propertyCode);
                if (building == null || !importedBuildings.contains(building)) {
                    building = importBuiling(yardiInterfaceId, new YardiILSMarketingProcessor().fixPropertyID(property.getPropertyID()), executionMonitor);
                    newBuildings.add(building);
                }
                executionMonitor.addProcessedEvent("Building");

                log.info("  Processing units for building: {}", propertyCode);

                // handle yardi Unit "Exclude" check box (no data sent for those units)
                Persistence.ensureRetrieve(building.units(), AttachLevel.IdOnly);
                Set<AptUnit> excludedUnits = building.units();

                // process new availability data
                for (ILSUnit ilsUnit : property.getILSUnit()) {
                    AptUnit aptUnit = importUnit(building, ilsUnit.getUnit(), executionMonitor);
                    if (updateAvailability(aptUnit, ilsUnit.getAvailability(), executionMonitor)) {
                        updateAvailabilityReport(aptUnit, ilsUnit, executionMonitor);
                    }
                    excludedUnits.remove(aptUnit);
                    executionMonitor.addProcessedEvent("Availability");
                }

                // clear unit availability for excluded units
                clearUnitAvailability(excludedUnits, executionMonitor);
            } catch (YardiServiceException e) {
                executionMonitor.addFailedEvent("Building", propertyCode, e);
            } catch (Throwable t) {
                executionMonitor.addErredEvent("Building", propertyCode, t);
            }

            if (executionMonitor.isTerminationRequested()) {
                break;
            }
        }

        executionMonitor.addInfoEvent(
                "ILSPropertyMarketing",
                SimpleMessageFormat.format("import new buildings: {0}{0,choice,0#|0< [{1}]}", newBuildings.size(),
                        ConverterUtils.convertCollection(newBuildings, new ToStringConverter<Building>() {
                            @Override
                            public String toString(Building value) {
                                return value.propertyCode().getStringView();
                            }
                        })));

        return newBuildings;
    }

    private void clearUnitAvailability(final Set<AptUnit> units, ExecutionMonitor executionMonitor) throws YardiServiceException {
        log.info("    clear unit availability: {}", units.size());
        new UnitOfWork(TransactionScopeOption.RequiresNew).execute(new Executable<Void, YardiServiceException>() {
            @Override
            public Void execute() throws YardiServiceException {
                for (AptUnit unit : units) {
                    new YardiILSMarketingProcessor().updateAvailability(unit, null);
                }
                return null;
            }
        });
    }

    private boolean updateAvailability(final AptUnit unit, final Availability avail, ExecutionMonitor executionMonitor) throws YardiServiceException {
        log.info("    availability: {}: {}", unit.getStringView(), (avail == null || avail.getVacateDate() == null ? "Not " : "") + "Available");
        return new UnitOfWork(TransactionScopeOption.RequiresNew).execute(new Executable<Boolean, YardiServiceException>() {
            @Override
            public Boolean execute() throws YardiServiceException {
                return new YardiILSMarketingProcessor().updateAvailability(unit, avail);
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

    private List<Lease> getActiveLeases(Key yardiInterfaceId, String propertyCode) {
        EntityQueryCriteria<Lease> criteria = EntityQueryCriteria.create(Lease.class);

        criteria.eq(criteria.proto().unit().building().propertyCode(), BuildingsMapper.getPropertyCode(propertyCode));
        criteria.eq(criteria.proto().unit().building().integrationSystemId(), yardiInterfaceId);
        criteria.in(criteria.proto().status(), Lease.Status.active());

        return Persistence.service().query(criteria);
    }

    private boolean removeLease(List<Lease> leases, final String leaseId) {
        if (leaseId != null) {
            Iterator<Lease> it = leases.iterator();
            while (it.hasNext()) {
                if (leaseId.equals(it.next().leaseId().getValue())) {
                    it.remove();
                    return true;
                }
            }
        }
        return false;
    }

    private void closeLeases(List<Lease> leases) {
        for (Lease lease : leases) {
            YardiLeaseProcessor.completeLease(lease);
        }
    }

    private boolean suspendBuilding(Key yardiInterfaceId, String propertyCode) {
        return suspendBuilding(MappingUtils.getBuilding(yardiInterfaceId, propertyCode));
    }

    private boolean suspendBuilding(Building building) {
        if (building != null && !building.suspended().getValue()) {
            ServerSideFactory.create(BuildingFacade.class).suspend(building);
            return true;
        }
        return false;
    }
}
