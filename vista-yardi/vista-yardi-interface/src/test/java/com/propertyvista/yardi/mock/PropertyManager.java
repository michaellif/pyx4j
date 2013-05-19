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

import org.apache.commons.lang.SerializationUtils;

import com.yardi.entity.mits.Address;
import com.yardi.entity.mits.Identification;
import com.yardi.entity.mits.Propertyidinfo;
import com.yardi.entity.mits.YardiCustomer;
import com.yardi.entity.resident.Charge;
import com.yardi.entity.resident.ChargeDetail;
import com.yardi.entity.resident.PropertyID;
import com.yardi.entity.resident.RTCustomer;
import com.yardi.entity.resident.RTServiceTransactions;
import com.yardi.entity.resident.ResidentTransactions;
import com.yardi.entity.resident.Transactions;

import com.propertyvista.yardi.bean.Property;

public class PropertyManager {

    private final String propertyId;

    private final Property property;

    private final ResidentTransactions transactions;

    private final ResidentTransactions leaseCharges;

    public PropertyManager(String propertyId) {
        this.propertyId = propertyId;
        property = new Property();
        transactions = new ResidentTransactions();
        leaseCharges = new ResidentTransactions();

        com.yardi.entity.resident.Property rtProperty = new com.yardi.entity.resident.Property();

        //=========== <PropertyID> ===========
        {
            PropertyID propertyID = new PropertyID();
            Identification identification = new Identification();
            identification.setType(Propertyidinfo.OTHER);
            identification.setPrimaryID(propertyId);
            identification.setMarketingName("Marketing" + propertyId);

            Address address = new Address();
            address.setAddress1("11 " + propertyId + " str");
            address.setCountry("Canada");

            propertyID.getAddress().add(address);

            propertyID.setIdentification(identification);
            rtProperty.getPropertyID().add(propertyID);
        }

        transactions.getProperty().add(rtProperty);

    }

    public ResidentTransactions getAllResidentTransactions() {
        try {
            return (ResidentTransactions) SerializationUtils.clone(transactions);
        } catch (Exception e) {
            throw new Error(e);
        }
    }

    public ResidentTransactions getResidentTransactionsForTenant(String tenantId) {
        return null;
    }

    public ResidentTransactions getAllLeaseCharges() {
        try {
            return (ResidentTransactions) SerializationUtils.clone(leaseCharges);
        } catch (Exception e) {
            throw new Error(e);
        }
    }

    public ResidentTransactions getLeaseChargesForTenant(String tenantId) {
        return null;
    }

    public void updateProperty(PropertyUpdater updater) {
        //TODO
    }

    public void addOrUpdateRtCustomer(RtCustomerUpdater updater) {
        RTCustomer rtCustomer = getRTCustomer(updater.getCustomerID());

        if (rtCustomer == null) {
            rtCustomer = new RTCustomer();
            rtCustomer.setCustomerID(updater.getCustomerID());
            rtCustomer.setPaymentAccepted("0");
            transactions.getProperty().get(0).getRTCustomer().add(rtCustomer);
        }

        updater.update(rtCustomer);

    }

    public void addOrUpdateCoTenant(CoTenantUpdater updater) {
        RTCustomer rtCustomer = getRTCustomer(updater.getCustomerID());
        if (rtCustomer == null) {
            throw new RuntimeException("rtCustomer with customerID " + updater.getCustomerID() + " not found");
        }

        YardiCustomer coTenant = null;
        for (int i = 1; i < rtCustomer.getCustomers().getCustomer().size(); i++) {
            if (rtCustomer.getCustomers().getCustomer().get(i).getCustomerID().equals(updater.getCoTenantCustomerID())) {
                coTenant = rtCustomer.getCustomers().getCustomer().get(i);
                break;
            }
        }

        if (coTenant == null) {
            coTenant = new YardiCustomer();
            rtCustomer.getCustomers().getCustomer().add(coTenant);
        }

        updater.update(coTenant);
    }

    public void addOrUpdateTransactionCharge(TransactionChargeUpdater updater) {
        RTCustomer rtCustomer = getRTCustomer(updater.getCustomerID());

        {
            RTServiceTransactions rtServiceTransactions = new RTServiceTransactions();
            Transactions transactions = new Transactions();
            rtServiceTransactions.getTransactions().add(transactions);

            Charge charge = new Charge();
            transactions.setCharge(charge);
            ChargeDetail detail = new ChargeDetail();
            updater.update(detail);

            detail.setUnitID(rtCustomer.getRTUnit().getUnitID());
            charge.setDetail(detail);

            rtCustomer.setRTServiceTransactions(rtServiceTransactions);
        }

    }

    private RTCustomer getRTCustomer(String customerID) {
        RTCustomer rtCustomer = null;
        for (RTCustomer customer : transactions.getProperty().get(0).getRTCustomer()) {
            if (customer.getCustomerID() == customerID) {
                rtCustomer = customer;
            }
        }
        return rtCustomer;
    }
}
