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
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

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
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.server.TransactionScopeOption;
import com.pyx4j.entity.server.UnitOfWork;

import com.propertyvista.biz.ExecutionMonitor;
import com.propertyvista.biz.ExecutionMonitor.IterationProgressCounter;
import com.propertyvista.biz.asset.BuildingFacade;
import com.propertyvista.biz.communication.NotificationFacade;
import com.propertyvista.biz.financial.payment.PaymentMethodFacade;
import com.propertyvista.biz.occupancy.OccupancyFacade;
import com.propertyvista.biz.system.yardi.YardiConfigurationFacade;
import com.propertyvista.biz.system.yardi.YardiNoTenantsExistException;
import com.propertyvista.biz.system.yardi.YardiPropertyNoAccessException;
import com.propertyvista.biz.system.yardi.YardiServiceException;
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
import com.propertyvista.yardi.processors.YardiLeaseProcessor;
import com.propertyvista.yardi.processors.YardiPaymentProcessor;
import com.propertyvista.yardi.processors.YardiProductCatalogProcessor;
import com.propertyvista.yardi.services.YardiResidentTransactionsData.LeaseTransactionData;
import com.propertyvista.yardi.services.YardiResidentTransactionsData.PropertyTransactionData;
import com.propertyvista.yardi.stubs.YardiGuestManagementStub;
import com.propertyvista.yardi.stubs.YardiILSGuestCardStub;
import com.propertyvista.yardi.stubs.YardiResidentTransactionsStub;
import com.propertyvista.yardi.stubs.YardiServiceMessageException;
import com.propertyvista.yardi.stubs.YardiStubFactory;

/**
 * Implementation functionality for updating properties/units/leases/tenants basing on getResidentTransactions from YARDI api
 * 
 * @author Mykola
 * 
 */
public class YardiResidentTransactionsService extends YardiAbstractService {

    private final static Logger log = LoggerFactory.getLogger(YardiResidentTransactionsService.class);

    private static class SingletonHolder {
        public static final YardiResidentTransactionsService INSTANCE = new YardiResidentTransactionsService();
    }

    private YardiResidentTransactionsService() {
    }

    // Public interface:

    public static YardiResidentTransactionsService getInstance() {
        return SingletonHolder.INSTANCE;
    }

    static public List<Property> getProperties(ResidentTransactions transaction) {
        List<Property> properties = new ArrayList<Property>();
        for (Property property : transaction.getProperty()) {
            properties.add(property);
        }
        return properties;
    }

    public List<YardiPropertyConfiguration> getPropertyConfigurations(PmcYardiCredential yc) throws YardiServiceException, RemoteException {
        return getPropertyConfigurations(YardiStubFactory.create(YardiResidentTransactionsStub.class).getPropertyConfigurations(yc));
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
        for (Building building : Persistence.service().query(criteria)) {
            String buildingCode = building.propertyCode().getValue();
            boolean suspended = building.suspended().getValue(false);
            if (propertyCodes.contains(buildingCode)) {
                if (suspended) {
                    // suspended buildings should be excluded from ILS property configuration
                    String error = "Suspended building '" + buildingCode + "' should be excluded from ILS property configuration.";
                    executionMonitor.addInfoEvent("YardiConfig", error);
                    ServerSideFactory.create(NotificationFacade.class).yardiConfigurationError(error);
                    propertyCodes.remove(buildingCode);
                }
            } else {
                if (!suspended) {
                    // suspend existing buildings not configured for ILS
                    suspendBuilding(building);
                    executionMonitor.addInfoEvent("BuildingSuspended", buildingCode);
                }
            }
        }

        updateProperties(yc, propertyCodes, executionMonitor, true);
    }

    public void updateBuilding(PmcYardiCredential yc, Building building, ExecutionMonitor executionMonitor) throws YardiServiceException, RemoteException {
        updateProperties(yc, Arrays.asList(building.propertyCode().getValue()), executionMonitor, false);
    }

    public void updateLease(PmcYardiCredential yc, Lease lease, ExecutionMonitor executionMonitor) throws YardiServiceException, RemoteException {
        // Each DB update function called in this method should be UnitOfWork, Unable to wrap here because for dual exception thrown.
        //This transaction should not update Lease, only in child unit of work
        if (executionMonitor == null) {
            executionMonitor = new ExecutionMonitor();
        }
        executionMonitor.setExpectedTotal(4L);

        Key yardiInterfaceId = yc.getPrimaryKey();
        String propertyCode = lease.unit().building().propertyCode().getValue();
        YardiResidentTransactionsData rtd = new YardiResidentTransactionsData(yardiInterfaceId, executionMonitor, false);

        ResidentTransactions transactions = YardiStubFactory.create(YardiResidentTransactionsStub.class).getResidentTransactionsForTenant(yc, propertyCode,
                lease.leaseId().getValue());
        if (transactions != null) {
            preProcessLeaseResidentsData(rtd, transactions);
        }

        ResidentTransactions leaseCharges = null;
        try {
            BillingCycle nextCycle = ServerSideFactory.create(PaymentMethodFacade.class).getNextAutopayBillingCycle(lease);
            leaseCharges = YardiStubFactory.create(YardiResidentTransactionsStub.class).getLeaseChargesForTenant(yc, propertyCode, lease.leaseId().getValue(),
                    nextCycle.billingCycleStartDate().getValue());
        } catch (YardiNoTenantsExistException e) {
            log.warn("Can't get changes for {}; {}", lease.leaseId().getValue(), e.getMessage()); // log error and reset lease charges.
        }
        if (leaseCharges != null) {
            preProcessLeaseChargesData(rtd, leaseCharges);
        }

        if (!executionMonitor.isTerminationRequested()) {
            importLeases(rtd);
        }
    }

    public void updateProductCatalog(PmcYardiCredential yc, Building building, ExecutionMonitor executionMonitor) throws YardiServiceException, RemoteException {
        PhysicalProperty propertyMarketing = YardiStubFactory.create(YardiILSGuestCardStub.class).getPropertyMarketingInfo(yc,
                building.propertyCode().getValue());
        Map<String, BigDecimal> depositInfo = getBuildingDepositInfo(building,
                (propertyMarketing != null ? Arrays.asList(propertyMarketing) : Collections.<PhysicalProperty> emptyList()));

        updateProductCatalog(yc, building, depositInfo, executionMonitor);
    }

    public void updateProductCatalog(PmcYardiCredential yc, Building building, Map<String, BigDecimal> depositInfo, ExecutionMonitor executionMonitor)
            throws YardiServiceException, RemoteException {
        RentableItems rentableItems = YardiStubFactory.create(YardiGuestManagementStub.class).getRentableItems(yc, building.propertyCode().getValue());

        importProductCatalog(yc.getPrimaryKey(), building, rentableItems, depositInfo, executionMonitor);
    }

    public void postReceiptReversal(PmcYardiCredential yc, YardiReceiptReversal reversal) throws YardiServiceException, RemoteException {
        YardiPaymentProcessor paymentProcessor = new YardiPaymentProcessor();
        ResidentTransactions reversalTransactions = paymentProcessor.createTransactions(paymentProcessor.createTransactionForReversal(reversal));

        YardiStubFactory.create(YardiResidentTransactionsStub.class).importResidentTransactions(yc, reversalTransactions);
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

    private List<ResidentTransactions> getResidentTransactions(PmcYardiCredential yc, ExecutionMonitor executionMonitor, List<String> propertyCodes)
            throws YardiServiceException, RemoteException {
        List<ResidentTransactions> transactions = new ArrayList<ResidentTransactions>();

        final Key yardiInterfaceId = yc.getPrimaryKey();
        for (String propertyCode : propertyCodes) {
            if (executionMonitor.isTerminationRequested()) {
                break;
            }
            Building building = MappingUtils.retrieveBuilding(yardiInterfaceId, propertyCode);
            if (building != null) {
                // this should not happen since Suspended buildings have been filtered earlier - see updateAll()
                if (building.suspended().getValue(false)) {
                    executionMonitor.addInfoEvent("YardiConfig", CompletionType.failed, "Suspended property excluded for transaction import: " + propertyCode,
                            null);
                    continue;
                }
            } else {
                // process as propertyCode or new building
            }
            try {
                ResidentTransactions residentTransactions = YardiStubFactory.create(YardiResidentTransactionsStub.class).getAllResidentTransactions(yc,
                        propertyCode);
                if (residentTransactions != null) {
                    transactions.add(residentTransactions);
                }
                executionMonitor.addInfoEvent("Property", propertyCode);
            } catch (YardiServiceMessageException e) {
                if (e.getMessages().hasErrorMessage(YardiHandledErrorMessages.errorMessage_TenantNotFound)) {
                    // All Ok there are no transactions
                }
            } catch (YardiPropertyNoAccessException e) {
                if (suspendBuilding(yardiInterfaceId, propertyCode)) {
                    executionMonitor.addErredEvent("BuildingSuspended", e);
                } else {
                    executionMonitor.addFailedEvent("Property", propertyCode, e);
                }
            }
        }

        return transactions;
    }

    // TODO - we may need to request Yardi charges for one more cycle forward
    private List<ResidentTransactions> getLeaseCharges(PmcYardiCredential yc, ExecutionMonitor executionMonitor, List<Building> buildings)
            throws YardiServiceException, RemoteException {
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
            // this should not happen since Suspended buildings have been filtered earlier - see updateAll()
            if (building.suspended().getValue(false)) {
                executionMonitor.addInfoEvent("YardiConfig", CompletionType.failed, "Suspended property excluded for transaction import: "
                        + building.propertyCode().getValue(), null);
            } else {
                BillingCycle nextCycle = ServerSideFactory.create(PaymentMethodFacade.class).getNextAutopayBillingCycle(building, BillingPeriod.Monthly, 1);
                try {
                    ResidentTransactions residentTransactions = YardiStubFactory.create(YardiResidentTransactionsStub.class).getAllLeaseCharges(yc,
                            building.propertyCode().getValue(), nextCycle.billingCycleStartDate().getValue());
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

    private List<PhysicalProperty> getILSPropertyMarketing(PmcYardiCredential yc, ExecutionMonitor executionMonitor, List<String> propertyCodes)
            throws RemoteException {
        List<PhysicalProperty> marketingInfo = new ArrayList<PhysicalProperty>();

        for (String propertyCode : propertyCodes) {
            if (executionMonitor.isTerminationRequested()) {
                break;
            }

            Building building = MappingUtils.retrieveBuilding(yc.getPrimaryKey(), propertyCode);
            if (building != null && building.suspended().getValue(false)) {
                executionMonitor.addInfoEvent("ILSPropertyMarketing", "skipped suspended building: " + propertyCode);
                continue;
            }

            try {
                PhysicalProperty propertyMarketing = YardiStubFactory.create(YardiILSGuestCardStub.class).getPropertyMarketingInfo(yc, propertyCode);
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

    private void updateProperties(PmcYardiCredential yc, List<String> propertyCodes, ExecutionMonitor executionMonitor, boolean closeNonProcessedLeases)
            throws YardiServiceException, RemoteException {
        try {
            ServerSideFactory.create(YardiConfigurationFacade.class).startYardiTimer();
            ServerSideFactory.create(NotificationFacade.class).aggregateNotificationsStart();

            final Key yardiInterfaceId = yc.getPrimaryKey();

            List<Building> importedBuildings = new ArrayList<>();

            // properties:
            List<ResidentTransactions> transactions = getResidentTransactions(yc, executionMonitor, propertyCodes);
            if (!executionMonitor.isTerminationRequested()) {
                for (ResidentTransactions transaction : transactions) {
                    if (executionMonitor.isTerminationRequested()) {
                        break;
                    }
                    importedBuildings.addAll(importProperties(yardiInterfaceId, transaction, executionMonitor));
                }
            }

            // product catalog:
            if (!executionMonitor.isTerminationRequested() && (ApplicationMode.isDevelopment() || !VistaTODO.pendingYardiConfigPatchILS)) {
                for (Building building : importedBuildings) {
                    if (executionMonitor.isTerminationRequested()) {
                        break;
                    }
                    updateProductCatalog(yc, building, executionMonitor);
                }
            }

            // lease resident data + charges:
            YardiResidentTransactionsData rtd = new YardiResidentTransactionsData(yardiInterfaceId, executionMonitor, closeNonProcessedLeases);
            preProcessResidentTransactionsData(rtd, transactions, getLeaseCharges(yc, executionMonitor, importedBuildings));
            if (!executionMonitor.isTerminationRequested()) {
                importLeases(rtd);
            }

            // availability:
            List<Building> newBuildings = new ArrayList<>();
            List<PhysicalProperty> properties = Collections.emptyList();
            if (!executionMonitor.isTerminationRequested() && (ApplicationMode.isDevelopment() || !VistaTODO.pendingYardiConfigPatchILS)) {
                properties = getILSPropertyMarketing(yc, executionMonitor, propertyCodes);
                for (PhysicalProperty property : properties) {
                    if (executionMonitor.isTerminationRequested()) {
                        break;
                    }
                    newBuildings.addAll(importPropertyMarketingInfo(yardiInterfaceId, property, importedBuildings, executionMonitor));
                }
            }

            // product catalog for new buildings:
            if (!executionMonitor.isTerminationRequested() && (ApplicationMode.isDevelopment() || !VistaTODO.pendingYardiConfigPatchILS)) {
                for (Building building : newBuildings) {
                    if (executionMonitor.isTerminationRequested()) {
                        break;
                    }
                    updateProductCatalog(yc, building, getBuildingDepositInfo(building, properties), executionMonitor);
                }
            }

        } finally {
            AtomicReference<Long> maxRequestTime = new AtomicReference<>();
            long yardiTime = ServerSideFactory.create(YardiConfigurationFacade.class).stopYardiTimer(maxRequestTime);

            executionMonitor.addInfoEvent("yardiTime", new BigDecimal(yardiTime), TimeUtils.durationFormat(yardiTime));
            executionMonitor.addInfoEvent("yardiMaxRequestTime", new BigDecimal(maxRequestTime.get()), TimeUtils.durationFormat(maxRequestTime.get()));

            ServerSideFactory.create(NotificationFacade.class).aggregatedNotificationsSend();
        }

        log.debug("updateProperties - completed.");
    }

    private List<Building> importProperties(Key yardiInterfaceId, ResidentTransactions transaction, final ExecutionMonitor executionMonitor) {
        log.debug("ResidentTransactions: Properties import started...");

        List<Building> importedBuildings = new ArrayList<Building>();

        List<Property> properties = getProperties(transaction);
        for (final Property property : properties) {
            IterationProgressCounter expectedTotal = executionMonitor.getIterationProgressCounter("Unit");
            if (expectedTotal == null) {
                executionMonitor.setIterationProgressCounter("Unit", new IterationProgressCounter(properties.size(), property.getRTCustomer().size()));
            } else {
                expectedTotal.onIterationCompleted(property.getRTCustomer().size());
            }

            String propertyCode = BuildingsMapper.getPropertyCode(property.getPropertyID().get(0));

            try {
                Building building = importBuiling(yardiInterfaceId, property.getPropertyID().get(0), executionMonitor);
                importedBuildings.add(building);

                log.debug("Processing building: {}", propertyCode);
                executionMonitor.addProcessedEvent("Building");

                for (final RTCustomer rtCustomer : property.getRTCustomer()) {
                    try {
                        importUnit(building, rtCustomer, executionMonitor);
                    } catch (YardiServiceException e) {
                        executionMonitor.addFailedEvent("Unit", e);
                    } catch (Throwable t) {
                        executionMonitor.addErredEvent("Unit", t);
                    }
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

        return importedBuildings;
    }

    private Building importBuiling(final Key yardiInterfaceId, final PropertyIDType propertyId, final ExecutionMonitor executionMonitor)
            throws YardiServiceException {
        log.debug("Updating building {}", propertyId.getIdentification().getPrimaryID());

        return new UnitOfWork(TransactionScopeOption.RequiresNew).execute(new Executable<Building, YardiServiceException>() {
            @Override
            public Building execute() throws YardiServiceException {
                Building building = new YardiBuildingProcessor(executionMonitor).updateBuilding(yardiInterfaceId, propertyId);
                ServerSideFactory.create(BuildingFacade.class).persist(building);
                return building;
            }
        });
    }

    private AptUnit importUnit(final Building building, final RTCustomer rtCustomer, final ExecutionMonitor executionMonitor) throws YardiServiceException {
        final Unit yardiUnit = rtCustomer.getRTUnit().getUnit();
        // VISTA-3307 Yardi API ResidentTransactions is broken! the filed unitRent is actually Market Rent, Make vista internal API consistent wit ISL
        {
            Information yardiUnitInfo = yardiUnit.getInformation().get(0);
            if ((yardiUnitInfo.getMarketRent() == null) || (yardiUnitInfo.getMarketRent().compareTo(BigDecimal.ZERO) == 0)) {
                yardiUnitInfo.setMarketRent(yardiUnitInfo.getUnitRent());
            }
        }

        log.debug("    Updating unit #" + yardiUnit.getInformation().get(0).getUnitID());

        return new UnitOfWork(TransactionScopeOption.RequiresNew).execute(new Executable<AptUnit, YardiServiceException>() {
            @Override
            public AptUnit execute() throws YardiServiceException {
                AptUnit aptUnit = new YardiBuildingProcessor().updateUnit(building, yardiUnit);

                // try to assign legal address for the unit
                if (aptUnit.info().legalAddress().isEmpty() && rtCustomer.getCustomers().getCustomer().get(0).getAddress().size() > 0) {
                    assignLegalAddress(aptUnit, rtCustomer.getCustomers().getCustomer().get(0).getAddress().get(0), executionMonitor);
                }

                ServerSideFactory.create(BuildingFacade.class).persist(aptUnit);
                return aptUnit;
            }
        });
    }

    private AptUnit importUnit(final Building building, final Unit yardiUnit, ExecutionMonitor executionMonitor) throws YardiServiceException {
        log.debug("    Updating unit #" + yardiUnit.getInformation().get(0).getUnitID());

        return new UnitOfWork(TransactionScopeOption.RequiresNew).execute(new Executable<AptUnit, YardiServiceException>() {
            @Override
            public AptUnit execute() throws YardiServiceException {
                AptUnit aptUnit = new YardiBuildingProcessor().updateUnit(building, yardiUnit);
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

    private void importLeases(final YardiResidentTransactionsData rtd) throws YardiServiceException {
        new YardiLeaseProcessor(rtd).process();
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

        StringBuilder addrErr = new StringBuilder();
        unit.info().legalAddress().set(MappingUtils.getAddress(address, addrErr));
        if (addrErr.length() > 0) {
            String msg = SimpleMessageFormat.format("Unit pk={0}: got invalid address {1}", unit.getPrimaryKey(), addrErr);
            executionMonitor.addInfoEvent("ParseAddress", msg);
            log.warn(msg);
        }
    }

    private void preProcessResidentTransactionsData(YardiResidentTransactionsData rtd, List<ResidentTransactions> transactions,
            List<ResidentTransactions> allLeaseCharges) {
        for (ResidentTransactions transaction : transactions) {
            if (rtd.getExecutionMonitor().isTerminationRequested()) {
                break;
            }
            preProcessLeaseResidentsData(rtd, transaction);
        }

        for (ResidentTransactions leaseCharges : allLeaseCharges) {
            if (rtd.getExecutionMonitor().isTerminationRequested()) {
                break;
            }
            preProcessLeaseChargesData(rtd, leaseCharges);
        }
    }

    private void preProcessLeaseResidentsData(YardiResidentTransactionsData rtd, ResidentTransactions transaction) {
        List<Property> properties = getProperties(transaction);

        for (Property property : properties) {
            String propertyCode = BuildingsMapper.getPropertyCode(property.getPropertyID().get(0));
            PropertyTransactionData prop = rtd.getData(propertyCode);
            if (prop == null) {
                rtd.putData(propertyCode, prop = rtd.new PropertyTransactionData());
            }

            // note - sorting input data list by lease status: former -> current -> future:
            for (RTCustomer rtCustomer : YardiLeaseProcessor.sortRtCustomers(property.getRTCustomer())) {
                if (!YardiLeaseProcessor.isEligibleForProcessing(rtCustomer)) {
                    String msg = SimpleMessageFormat.format("Transactions for: {0} skipped, lease does not meet criteria.", rtCustomer.getCustomerID());
                    rtd.getExecutionMonitor().addInfoEvent("Lease", msg);
                    log.warn(msg);
                    continue;
                }

                String leaseId = YardiLeaseProcessor.getLeaseID(rtCustomer.getCustomerID());

                LeaseTransactionData lease = prop.getData(propertyCode);
                if (lease == null) {
                    prop.putData(leaseId, lease = rtd.new LeaseTransactionData(rtCustomer, null));
                } else {
                    lease.setResident(rtCustomer);
                }
            }
        }
    }

    private void preProcessLeaseChargesData(YardiResidentTransactionsData rtd, ResidentTransactions leaseCharges) {
        List<Property> properties = getProperties(leaseCharges);
        // although we get properties here, all data inside is empty until we get down to the ChargeDetail level

        for (Property property : properties) {
            String propertyCode = null;
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
                log.warn("preProcessLeaseChargesData: can't deduct property code!?");
                continue;
            }

            propertyCode = BuildingsMapper.getPropertyCode(propertyCode);
            PropertyTransactionData prop = rtd.getData(propertyCode);
            if (prop == null) {
                rtd.putData(propertyCode, prop = rtd.new PropertyTransactionData());
            }

            for (RTCustomer rtCustomer : property.getRTCustomer()) {
                if (rtCustomer.getRTServiceTransactions().getTransactions().size() == 0) {
                    continue;
                }

                String leaseId = YardiLeaseProcessor.getLeaseID(rtCustomer.getRTServiceTransactions().getTransactions().get(0).getCharge().getDetail()
                        .getCustomerID());
                if (leaseId != null) {
                    LeaseTransactionData lease = prop.getData(leaseId);
                    if (lease == null) {
                        prop.putData(leaseId, lease = rtd.new LeaseTransactionData(null, rtCustomer));
                    } else {
                        lease.setCharges(rtCustomer);
                    }
                }
            }
        }
    }

    private List<Building> importPropertyMarketingInfo(final Key yardiInterfaceId, PhysicalProperty propertyInfo, List<Building> importedBuildings,
            final ExecutionMonitor executionMonitor) {
        log.debug("PropertyMarketing: import started...");

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
                log.debug("  Processing building: {}", propertyCode);

                // import new buildings
                Building building = MappingUtils.retrieveBuilding(yardiInterfaceId, propertyCode);
                if (building == null || !importedBuildings.contains(building)) {
                    building = importBuiling(yardiInterfaceId, new YardiILSMarketingProcessor().fixPropertyID(property.getPropertyID()), executionMonitor);
                    newBuildings.add(building);
                }
                executionMonitor.addProcessedEvent("Building");

                log.debug("  Processing units for building: {}", propertyCode);

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
        log.debug("    clear unit availability: {}", units.size());
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
        log.debug("    availability: {}: {}", unit.getStringView(), (avail == null || avail.getVacateDate() == null ? "Not " : "") + "Available");
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
            executionMonitor.addProcessedEvent("AvailabilityStatus", new BigDecimal(1));
        } catch (Throwable e) {
            String msg = SimpleMessageFormat.format("failed to import availability status for unit pk={0}", unit.getPrimaryKey());
            executionMonitor.addFailedEvent("AvailabilityStatus", msg);
            log.error(msg, e);
        }
    }

    private UnitAvailabilityStatus retrieveCurrentAvailabilityStatus(AptUnit unit) {
        EntityQueryCriteria<UnitAvailabilityStatus> criteria = EntityQueryCriteria.create(UnitAvailabilityStatus.class);

        criteria.eq(criteria.proto().unit(), unit);
        criteria.eq(criteria.proto().statusUntil(), OccupancyFacade.MAX_DATE);

        return Persistence.service().retrieve(criteria);
    }

    private boolean suspendBuilding(Key yardiInterfaceId, String propertyCode) {
        return suspendBuilding(MappingUtils.retrieveBuilding(yardiInterfaceId, propertyCode));
    }

    private boolean suspendBuilding(Building building) {
        if (building != null && !building.suspended().getValue()) {
            ServerSideFactory.create(BuildingFacade.class).suspend(building);
            return true;
        }
        return false;
    }
}
