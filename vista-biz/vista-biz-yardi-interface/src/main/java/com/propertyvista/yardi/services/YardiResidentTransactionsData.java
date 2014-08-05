/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 29, 2014
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.yardi.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.yardi.entity.resident.RTCustomer;

import com.pyx4j.commons.Key;

import com.propertyvista.biz.ExecutionMonitor;
import com.propertyvista.domain.tenant.lease.Lease;

public class YardiResidentTransactionsData {

    public class LeaseTransactionData {

        private RTCustomer resident = null;

        private RTCustomer charges = null;

        public LeaseTransactionData() {
        }

        public LeaseTransactionData(RTCustomer resident, RTCustomer charges) {
            this.resident = resident;
            this.charges = charges;
        }

        public RTCustomer getResident() {
            return resident;
        }

        public void setResident(RTCustomer resident) {
            this.resident = resident;
        }

        public RTCustomer getCharges() {
            return charges;
        }

        public void setCharges(RTCustomer charges) {
            this.charges = charges;
        }
    }

    public class PropertyTransactionData {

        private final Map<String, LeaseTransactionData> data = new HashMap<>();

        private final List<Lease> noChargesLeases = new ArrayList<>();

        public PropertyTransactionData() {
        }

        public Set<String> getKeySet() {
            return this.data.keySet();
        }

        public LeaseTransactionData getData(String key) {
            return this.data.get(key);
        }

        public void putData(String key, LeaseTransactionData data) {
            this.data.put(key, data);
        }

        public List<Lease> getNoChargesLeases() {
            return noChargesLeases;
        }
    }

    private final Map<String, PropertyTransactionData> data = new HashMap<>();

    private final ExecutionMonitor executionMonitor;

    private final Key yardiInterfaceId;

    public YardiResidentTransactionsData(ExecutionMonitor executionMonitor, Key yardiInterfaceId) {
        this.executionMonitor = executionMonitor;
        this.yardiInterfaceId = yardiInterfaceId;
    }

    public Set<String> getKeySet() {
        return this.data.keySet();
    }

    public PropertyTransactionData getData(String key) {
        return this.data.get(key);
    }

    public void putData(String key, PropertyTransactionData data) {
        this.data.put(key, data);
    }

    public ExecutionMonitor getExecutionMonitor() {
        return executionMonitor;
    }

    public Key getYardiInterfaceId() {
        return yardiInterfaceId;
    }
}