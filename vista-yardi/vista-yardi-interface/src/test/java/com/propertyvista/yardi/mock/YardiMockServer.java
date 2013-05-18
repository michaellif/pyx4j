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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yardi.entity.resident.ResidentTransactions;

import com.propertyvista.test.mock.MockEventBus;

public class YardiMockServer implements TransactionChargeUpdateEvent.Handler, PropertyUpdateEvent.Handler {

    private static final Logger log = LoggerFactory.getLogger(YardiMockServer.class);

    static final ThreadLocal<YardiMockServer> threadLocalContext = new ThreadLocal<YardiMockServer>() {
        @Override
        protected YardiMockServer initialValue() {
            return new YardiMockServer();
        }
    };

    private final Map<String, PropertyManager> propertyManagers;

    public static YardiMockServer instance() {
        return threadLocalContext.get();
    }

    private YardiMockServer() {
        propertyManagers = new HashMap<String, PropertyManager>();
        MockEventBus.addHandler(PropertyUpdateEvent.class, this);
        MockEventBus.addHandler(TransactionChargeUpdateEvent.class, this);
    }

    public ResidentTransactions getAllResidentTransactions(String propertyId) {
        if (!propertyManagers.containsKey(propertyId)) {
            throw new Error(propertyId + " not found");
        }
        return propertyManagers.get(propertyId).getAllResidentTransactions();
    }

    public ResidentTransactions getAllLeaseCharges(String propertyId) {
        if (!propertyManagers.containsKey(propertyId)) {
            throw new Error(propertyId + " not found");
        }
        return propertyManagers.get(propertyId).getAllLeaseCharges();
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
        String propertyId = updater.getPropertyID();
        if (!propertyManagers.containsKey(updater.getPropertyID())) {
            throw new Error(propertyId + " not found");
        }
        propertyManagers.get(propertyId).addOrUpdateTransactionCharge(updater);
    }

}
