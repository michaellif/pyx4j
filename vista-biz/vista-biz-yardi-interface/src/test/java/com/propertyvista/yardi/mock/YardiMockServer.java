/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 16, 2013
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.yardi.mock;

import java.util.HashMap;
import java.util.Map;

import com.yardi.entity.guestcard40.MarketingSources;
import com.yardi.entity.guestcard40.PropertyMarketingSources;
import com.yardi.entity.guestcard40.RentableItems;
import com.yardi.entity.resident.RTCustomer;
import com.yardi.entity.resident.ResidentTransactions;
import com.yardi.entity.resident.Transactions;

import com.propertyvista.biz.system.yardi.YardiServiceException;
import com.propertyvista.test.mock.MockEventBus;
import com.propertyvista.yardi.beans.Messages;
import com.propertyvista.yardi.beans.Properties;
import com.propertyvista.yardi.beans.Property;
import com.propertyvista.yardi.mock.updater.CoTenantUpdateEvent;
import com.propertyvista.yardi.mock.updater.CoTenantUpdater;
import com.propertyvista.yardi.mock.updater.LeaseChargeUpdateEvent;
import com.propertyvista.yardi.mock.updater.LeaseChargeUpdater;
import com.propertyvista.yardi.mock.updater.PropertyUpdateEvent;
import com.propertyvista.yardi.mock.updater.PropertyUpdater;
import com.propertyvista.yardi.mock.updater.RentableItemTypeUpdateEvent;
import com.propertyvista.yardi.mock.updater.RentableItemTypeUpdater;
import com.propertyvista.yardi.mock.updater.RtCustomerUpdateEvent;
import com.propertyvista.yardi.mock.updater.RtCustomerUpdater;
import com.propertyvista.yardi.mock.updater.TransactionChargeUpdateEvent;
import com.propertyvista.yardi.mock.updater.TransactionChargeUpdater;
import com.propertyvista.yardi.mock.updater.UnitTransferSimulator;
import com.propertyvista.yardi.mock.updater.UnitTransferSimulatorEvent;
import com.propertyvista.yardi.services.YardiHandledErrorMessages;

public class YardiMockServer implements TransactionChargeUpdateEvent.Handler, PropertyUpdateEvent.Handler, RtCustomerUpdateEvent.Handler,
        CoTenantUpdateEvent.Handler, LeaseChargeUpdateEvent.Handler, UnitTransferSimulatorEvent.Handler, RentableItemTypeUpdateEvent.Handler {

    static final ThreadLocal<YardiMockServer> threadLocalContext = new ThreadLocal<YardiMockServer>() {
        @Override
        protected YardiMockServer initialValue() {
            return new YardiMockServer();
        }
    };

    private final Map<String, PropertyManager> propertyManagers;

    private final Map<Long, PaymentBatchManager> openBatches;

    public static YardiMockServer instance() {
        return threadLocalContext.get();
    }

    private YardiMockServer() {
        propertyManagers = new HashMap<String, PropertyManager>();
        openBatches = new HashMap<Long, PaymentBatchManager>();
        MockEventBus.addHandler(PropertyUpdateEvent.class, this);
        MockEventBus.addHandler(TransactionChargeUpdateEvent.class, this);
        MockEventBus.addHandler(RtCustomerUpdateEvent.class, this);
        MockEventBus.addHandler(CoTenantUpdateEvent.class, this);
        MockEventBus.addHandler(LeaseChargeUpdateEvent.class, this);
        MockEventBus.addHandler(UnitTransferSimulatorEvent.class, this);
        MockEventBus.addHandler(RentableItemTypeUpdateEvent.class, this);
    }

    public void cleanup() {
        propertyManagers.clear();
    }

    PropertyManager getExistingPropertyManagerSetup(String propertyId) throws YardiServiceException {
        PropertyManager propertyManager = propertyManagers.get(propertyId);
        if (propertyManager == null) {
            throw new RuntimeException("Property '" + propertyId + "' not found");
        }
        handleMockFeatures(propertyManager);
        return propertyManager;
    }

    PropertyManager getExistingPropertyManagerRuntime(String propertyId) throws YardiServiceException {
        PropertyManager propertyManager = propertyManagers.get(propertyId);
        if (propertyManager == null) {
            throw new RuntimeException("Property '" + propertyId + "' not found");
        }
        handleMockFeatures(propertyManager);
        return propertyManager;
    }

    void handleMockFeatures(PropertyManager propertyManager) throws YardiServiceException {
        if (propertyManager.mockFeatures.isBlockAccess()) {
            Messages.throwYardiResponseException(YardiHandledErrorMessages.errorMessage_NoAccess + " " + propertyManager.getPropertyId());
        }
        if (propertyManager.mockFeatures.isBlockBatchOpening()) {
            Messages.throwYardiResponseException("Cannot open Batch for Yardi Property " + propertyManager.getPropertyId());
        }
    }

    public Properties getPropertyConfigurations() {
        Properties properties = new Properties();

        for (PropertyManager propertyManager : propertyManagers.values()) {
            Property property = new Property();
            property.setCode(propertyManager.getPropertyId());
            properties.getProperties().add(property);
        }

        return properties;
    }

    public MarketingSources getMarketingSources(String propertyId) throws YardiServiceException {
        MarketingSources marketingSources = new MarketingSources();

        PropertyMarketingSources propertyMarketingSources = new PropertyMarketingSources();

        PropertyManager propertyManager = getExistingPropertyManagerRuntime(propertyId);

        propertyMarketingSources.setPropertyCode(propertyManager.getPropertyId());

        marketingSources.getProperty().add(propertyMarketingSources);
        return marketingSources;
    }

    public ResidentTransactions getAllResidentTransactions(String propertyId) throws YardiServiceException {
        return getExistingPropertyManagerRuntime(propertyId).getAllResidentTransactions();
    }

    public ResidentTransactions getResidentTransactionsForTenant(String propertyId, String tenantId) throws YardiServiceException {
        return getExistingPropertyManagerRuntime(propertyId).getResidentTransactionsForTenant(tenantId);
    }

    public ResidentTransactions getAllLeaseCharges(String propertyId) throws YardiServiceException {
        return getExistingPropertyManagerRuntime(propertyId).getAllLeaseCharges();
    }

    public ResidentTransactions getLeaseChargesForTenant(String propertyId, String tenantId) throws YardiServiceException {
        return getExistingPropertyManagerRuntime(propertyId).getLeaseChargesForTenant(tenantId);
    }

    public RentableItems getRentableItems(String propertyId) throws YardiServiceException {
        return getExistingPropertyManagerRuntime(propertyId).getRentableItems();
    }

    public long openReceiptBatch(String propertyId) {
        // per yardi spec, this method returns 0 or negative batch id in case of an error
        try {
            PropertyManager propertyManager = getExistingPropertyManagerRuntime(propertyId);
            PaymentBatchManager b = new PaymentBatchManager(propertyManager);
            openBatches.put(b.getId(), b);
            return b.getId();
        } catch (YardiServiceException e) {
            if (e.getMessage().contains(YardiHandledErrorMessages.errorMessage_NoAccess)) {
                return -2;
            } else {
                return 0;
            }
        }
    }

    PaymentBatchManager getBatch(long batchId) throws YardiServiceException {
        if (batchId > 0) {
            if (openBatches.containsKey(batchId)) {
                return openBatches.get(batchId);
            }
            Messages.throwYardiResponseException(batchId + " batch not found");
        } else if (batchId == -2) {
            Messages.throwYardiResponseException(YardiHandledErrorMessages.errorMessage_NoAccess);
        } else {
            Messages.throwYardiResponseException("Invalid batch: " + batchId);
        }
        return null;
    }

    public void addReceiptsToBatch(long batchId, ResidentTransactions residentTransactions) throws YardiServiceException {
        getBatch(batchId).addReceiptsToBatch(residentTransactions);
    }

    public void postReceiptBatch(long batchId) throws YardiServiceException {
        getBatch(batchId).postReceiptBatch();
    }

    public void cancelReceiptBatch(long batchId) throws YardiServiceException {
        getBatch(batchId).cancelReceiptBatch();
        openBatches.remove(batchId);
    }

    public void importResidentTransactions(ResidentTransactions reversalTransactions) throws YardiServiceException {
        for (com.yardi.entity.resident.Property rtProperty : reversalTransactions.getProperty()) {
            for (RTCustomer rtCustomer : rtProperty.getRTCustomer()) {
                for (Transactions transaction : rtCustomer.getRTServiceTransactions().getTransactions()) {
                    getExistingPropertyManagerRuntime(transaction.getPayment().getDetail().getPropertyPrimaryID()).importResidentTransactions(
                            transaction.getPayment());
                }
            }
        }
    }

    @Override
    public void addOrUpdateProperty(PropertyUpdateEvent event) {
        PropertyUpdater updater = event.getUpdater();
        String propertyId = updater.getPropertyID();
        if (!propertyManagers.containsKey(propertyId)) {
            PropertyManager propertyManager = new PropertyManager(propertyId);
            propertyManagers.put(propertyId, propertyManager);
        }
        propertyManagers.get(propertyId).updateProperty(updater);
    }

    @Override
    public void addOrUpdateTransactionCharge(TransactionChargeUpdateEvent event) {
        TransactionChargeUpdater updater = event.getUpdater();
        propertyManagers.get(updater.getPropertyID()).addOrUpdateTransactionCharge(updater);
    }

    @Override
    public void addOrUpdateRtCustomer(RtCustomerUpdateEvent event) {
        RtCustomerUpdater updater = event.getUpdater();
        propertyManagers.get(updater.getPropertyID()).addOrUpdateRtCustomer(updater);
    }

    @Override
    public void addOrUpdateCoTenant(CoTenantUpdateEvent event) {
        CoTenantUpdater updater = event.getUpdater();
        propertyManagers.get(updater.getPropertyID()).addOrUpdateCoTenant(updater);
    }

    @Override
    public void addOrUpdateLeaseCharge(LeaseChargeUpdateEvent event) {
        LeaseChargeUpdater updater = event.getUpdater();
        propertyManagers.get(updater.getPropertyID()).addOrUpdateLeaseCharge(updater);
    }

    @Override
    public void removeLeaseCharge(LeaseChargeUpdateEvent event) {
        LeaseChargeUpdater updater = event.getUpdater();
        propertyManagers.get(updater.getPropertyID()).removeLeaseCharge(updater);
    }

    @Override
    public void unitTransferSimulation(UnitTransferSimulatorEvent event) {
        UnitTransferSimulator updater = event.getUpdater();
        propertyManagers.get(updater.getPropertyID()).unitTransferSimulation(updater);
    }

    @Override
    public void addOrUpdateRentableItemType(RentableItemTypeUpdateEvent event) {
        RentableItemTypeUpdater updater = event.getUpdater();
        propertyManagers.get(updater.getPropertyID()).addOrUpdateRentableItemType(updater);
    }

    @Override
    public void removeRentableItemType(RentableItemTypeUpdateEvent event) {
        RentableItemTypeUpdater updater = event.getUpdater();
        propertyManagers.get(updater.getPropertyID()).removeRentableItemType(updater);
    }

}
