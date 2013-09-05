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

import com.yardi.entity.resident.RTCustomer;
import com.yardi.entity.resident.ResidentTransactions;
import com.yardi.entity.resident.Transactions;

import com.propertyvista.biz.system.YardiServiceException;
import com.propertyvista.test.mock.MockEventBus;

public class YardiMockServer implements TransactionChargeUpdateEvent.Handler, PropertyUpdateEvent.Handler, RtCustomerUpdateEvent.Handler,
        CoTenantUpdateEvent.Handler, LeaseChargeUpdateEvent.Handler, UnitTransferSimulatorEvent.Handler {

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
    }

    public void cleanup() {
        propertyManagers.clear();
    }

    PropertyManager getExistingPropertyManager(String propertyId) {
        PropertyManager propertyManager = propertyManagers.get(propertyId);
        if (propertyManager == null) {
            throw new RuntimeException("Property '" + propertyId + "' not found");
        }
        if (propertyManager.mockFeatures.isBlockAccess()) {
            throw new RuntimeException("Access disabled for " + propertyId);
        }
        return propertyManager;
    }

    public ResidentTransactions getAllResidentTransactions(String propertyId) {
        return getExistingPropertyManager(propertyId).getAllResidentTransactions();
    }

    public ResidentTransactions getResidentTransactionsForTenant(String propertyId, String tenantId) {
        return getExistingPropertyManager(propertyId).getResidentTransactionsForTenant(tenantId);
    }

    public ResidentTransactions getAllLeaseCharges(String propertyId) {
        return getExistingPropertyManager(propertyId).getAllLeaseCharges();
    }

    public ResidentTransactions getLeaseChargesForTenant(String propertyId, String tenantId) {
        return getExistingPropertyManager(propertyId).getLeaseChargesForTenant(tenantId);
    }

    public long openReceiptBatch(String propertyId) {
        PropertyManager propertyManager = getExistingPropertyManager(propertyId);
        if (propertyManager.mockFeatures.isBlockBatchOpening()) {
            throw new RuntimeException("BatchOpening disabled for " + propertyId);
        }
        PaymentBatchManager b = new PaymentBatchManager(propertyManager);
        openBatches.put(b.getId(), b);
        return b.getId();
    }

    PaymentBatchManager getBatch(long batchId) {
        if (!openBatches.containsKey(batchId)) {
            throw new Error(batchId + " batch not found");
        }
        return openBatches.get(batchId);
    }

    public void addReceiptsToBatch(long batchId, ResidentTransactions residentTransactions) throws YardiServiceException {
        getBatch(batchId).addReceiptsToBatch(residentTransactions);
    }

    public void postReceiptBatch(long batchId) {
        getBatch(batchId).postReceiptBatch();
    }

    public void cancelReceiptBatch(long batchId) {
        getBatch(batchId).cancelReceiptBatch();
        openBatches.remove(batchId);
    }

    public void importResidentTransactions(ResidentTransactions reversalTransactions) {
        for (com.yardi.entity.resident.Property rtProperty : reversalTransactions.getProperty()) {
            for (RTCustomer rtCustomer : rtProperty.getRTCustomer()) {
                for (Transactions transaction : rtCustomer.getRTServiceTransactions().getTransactions()) {
                    getExistingPropertyManager(transaction.getPayment().getDetail().getPropertyPrimaryID())
                            .importResidentTransactions(transaction.getPayment());
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
        getExistingPropertyManager(updater.getPropertyID()).addOrUpdateTransactionCharge(updater);
    }

    @Override
    public void addOrUpdateRtCustomer(RtCustomerUpdateEvent event) {
        RtCustomerUpdater updater = event.getUpdater();
        getExistingPropertyManager(updater.getPropertyID()).addOrUpdateRtCustomer(updater);
    }

    @Override
    public void addOrUpdateCoTenant(CoTenantUpdateEvent event) {
        CoTenantUpdater updater = event.getUpdater();
        getExistingPropertyManager(updater.getPropertyID()).addOrUpdateCoTenant(updater);
    }

    @Override
    public void addOrUpdateLeaseCharge(LeaseChargeUpdateEvent event) {
        LeaseChargeUpdater updater = event.getUpdater();
        getExistingPropertyManager(updater.getPropertyID()).addOrUpdateLeaseCharge(updater);
    }

    @Override
    public void removeLeaseCharge(LeaseChargeUpdateEvent event) {
        LeaseChargeUpdater updater = event.getUpdater();
        getExistingPropertyManager(updater.getPropertyID()).removeLeaseCharge(updater);
    }

    @Override
    public void unitTransferSimulation(UnitTransferSimulatorEvent event) {
        UnitTransferSimulator updater = event.getUpdater();
        getExistingPropertyManager(updater.getPropertyID()).unitTransferSimulation(updater);
    }

}
