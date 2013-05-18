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

import java.math.BigDecimal;

import org.apache.commons.lang.SerializationUtils;

import com.yardi.entity.mits.Address;
import com.yardi.entity.mits.Customerinfo;
import com.yardi.entity.mits.Identification;
import com.yardi.entity.mits.Information;
import com.yardi.entity.mits.Name;
import com.yardi.entity.mits.Propertyidinfo;
import com.yardi.entity.mits.Unit;
import com.yardi.entity.mits.YardiCustomer;
import com.yardi.entity.mits.YardiCustomers;
import com.yardi.entity.mits.YardiLease;
import com.yardi.entity.resident.Charge;
import com.yardi.entity.resident.ChargeDetail;
import com.yardi.entity.resident.PropertyID;
import com.yardi.entity.resident.RTCustomer;
import com.yardi.entity.resident.RTServiceTransactions;
import com.yardi.entity.resident.RTUnit;
import com.yardi.entity.resident.ResidentTransactions;
import com.yardi.entity.resident.Transactions;

import com.pyx4j.gwt.server.DateUtils;

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

//        addRtCustomer("t000111");

//        addLeaseCharge("t000111");

    }

    public void addRtCustomer(String customerID) {
        RTCustomer rtCustomer = new RTCustomer();
        rtCustomer.setCustomerID(customerID);

        YardiCustomers customers = new YardiCustomers();
        rtCustomer.setCustomers(customers);
        rtCustomer.setPaymentAccepted("0");

        {
            YardiCustomer customer = new YardiCustomer();
            customer.setType(Customerinfo.CURRENT_RESIDENT);
            customer.setCustomerID(customerID);
            Name name = new Name();
            name.setFirstName("John");
            name.setLastName("Smith");
            customer.setName(name);

            YardiLease lease = new YardiLease();
            lease.setCurrentRent(new BigDecimal("1234.56"));
            lease.setLeaseFromDate(DateUtils.detectDateformat("01-Jun-2012"));
            lease.setLeaseToDate(DateUtils.detectDateformat("31-Jul-2014"));
            lease.setResponsibleForLease(true);
            customer.setLease(lease);

            customers.getCustomer().add(customer);
        }

        {
            YardiCustomer customer = new YardiCustomer();
            customer.setType(Customerinfo.CUSTOMER);
            customer.setCustomerID("r" + customerID.substring(1));
            Name name = new Name();
            name.setFirstName("Jane");
            name.setLastName("Smith");
            customer.setName(name);

            YardiLease lease = new YardiLease();
            lease.setResponsibleForLease(true);
            customer.setLease(lease);

            customers.getCustomer().add(customer);
        }

        //=========== <RT_Unit> ===========
        {

            RTUnit rtunit = new RTUnit();
            rtunit.setUnitID(customerID.substring(3));

            Unit unit = new Unit();
            Information info = new Information();
            info.setUnitID(rtunit.getUnitID());
            info.setUnitType("2bdrm");
            info.setUnitBedrooms(new BigDecimal("2"));
            info.setUnitBathrooms(new BigDecimal("1"));
            info.setUnitRent(new BigDecimal("1300.00"));
            unit.getInformation().add(info);
            info.setFloorPlanID("2bdrm");
            info.setFloorplanName("2 Bedroom");
            rtunit.setUnit(unit);

            rtCustomer.setRTUnit(rtunit);
        }

        transactions.getProperty().get(0).getRTCustomer().add(rtCustomer);
    }

    private void addLeaseCharge(String customerID) {

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

    public void addOrUpdateTransactionCharge(TransactionChargeUpdater updater) {
        RTCustomer rtCustomer = null;
        for (RTCustomer customer : transactions.getProperty().get(0).getRTCustomer()) {
            if (customer.getCustomerID() == updater.getCustomerID()) {
                rtCustomer = customer;
            }
        }

        {
            RTServiceTransactions rtServiceTransactions = new RTServiceTransactions();
            Transactions transactions = new Transactions();
            rtServiceTransactions.getTransactions().add(transactions);

            Charge charge = new Charge();
            transactions.setCharge(charge);
            ChargeDetail detail = updater.update(new ChargeDetail());

            detail.setUnitID(rtCustomer.getRTUnit().getUnitID());
            charge.setDetail(detail);

            rtCustomer.setRTServiceTransactions(rtServiceTransactions);
        }

    }

    public void addOrUpdateRtCustomer(RtCustomerUpdater updater) {
        RTCustomer rtCustomer = null;
        for (RTCustomer customer : transactions.getProperty().get(0).getRTCustomer()) {
            if (customer.getCustomerID() == updater.getCustomerID()) {
                rtCustomer = customer;
                break;
            }
        }

        if (rtCustomer == null) {
            rtCustomer = new RTCustomer();
            rtCustomer.setCustomerID(updater.getCustomerID());
        }

        rtCustomer = updater.update(rtCustomer);

        transactions.getProperty().get(0).getRTCustomer().add(rtCustomer);
    }
}
