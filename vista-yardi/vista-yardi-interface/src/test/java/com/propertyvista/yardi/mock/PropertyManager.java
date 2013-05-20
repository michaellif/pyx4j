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

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.SerializationUtils;

import com.yardi.entity.mits.Address;
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

public class PropertyManager {

    private final String propertyId;

    private final com.yardi.entity.mits.Property property;

    private final ResidentTransactions transactions;

    private final Map<String, RTServiceTransactions> leaseCharges;

    public PropertyManager(String propertyId) {
        this.propertyId = propertyId;
        property = new com.yardi.entity.mits.Property();
        transactions = new ResidentTransactions();
        leaseCharges = new HashMap<String, RTServiceTransactions>();

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
        ResidentTransactions retVal = new ResidentTransactions();
        com.yardi.entity.resident.Property rtProperty = new com.yardi.entity.resident.Property();
        retVal.getProperty().add(rtProperty);

        for (String customerId : leaseCharges.keySet()) {
            RTCustomer rtCustomer = new RTCustomer();
            rtProperty.getRTCustomer().add(rtCustomer);
            rtCustomer.setRTServiceTransactions((RTServiceTransactions) SerializationUtils.clone(leaseCharges.get(customerId)));
        }

        return retVal;
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

        if (!leaseCharges.containsKey(updater.getCustomerID())) {
            RTServiceTransactions st = new RTServiceTransactions();
            leaseCharges.put(updater.getCustomerID(), st);
        }

        Map<com.propertyvista.yardi.mock.Name, Property<?>> map = updater.getPropertyMap();
        for (com.propertyvista.yardi.mock.Name name : map.keySet()) {
            Property<?> property = map.get(name);
            if (property.getName() instanceof RtCustomerUpdater.YCUSTOMER) {

                if (rtCustomer.getCustomers() == null) {
                    rtCustomer.setCustomers(new YardiCustomers());
                }

                if (rtCustomer.getCustomers().getCustomer().size() == 0) {
                    rtCustomer.getCustomers().getCustomer().add(new YardiCustomer());
                }

                updateProperty(rtCustomer.getCustomers().getCustomer().get(0), property);

            } else if (property.getName() instanceof RtCustomerUpdater.YCUSTOMERNAME) {

                if (rtCustomer.getCustomers().getCustomer().get(0).getName() == null) {
                    Name custName = new Name();
                    rtCustomer.getCustomers().getCustomer().get(0).setName(custName);
                }

                updateProperty(rtCustomer.getCustomers().getCustomer().get(0).getName(), property);

            } else if (property.getName() instanceof RtCustomerUpdater.YLEASE) {

                if (rtCustomer.getCustomers().getCustomer().get(0).getLease() == null) {
                    YardiLease lease = new YardiLease();
                    rtCustomer.getCustomers().getCustomer().get(0).setLease(lease);
                }

                updateProperty(rtCustomer.getCustomers().getCustomer().get(0).getLease(), property);

            } else if (property.getName() instanceof RtCustomerUpdater.UNITINFO) {

                if (rtCustomer.getRTUnit() == null) {
                    RTUnit rtunit = new RTUnit();
                    rtunit.setUnitID(rtCustomer.getCustomerID().substring(3));

                    Unit unit = new Unit();
                    Information info = new Information();
                    info.setUnitID(rtunit.getUnitID());
                    unit.getInformation().add(info);

                    rtunit.setUnit(unit);
                    rtCustomer.setRTUnit(rtunit);
                }

                updateProperty(rtCustomer.getRTUnit().getUnit().getInformation().get(0), property);

            }
        }

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

            for (com.propertyvista.yardi.mock.Name name : updater.getPropertyMap().keySet()) {
                Property<?> property = updater.getPropertyMap().get(name);
                updateProperty(detail, property);
            }

            detail.setUnitID(rtCustomer.getRTUnit().getUnitID());
            charge.setDetail(detail);

            rtCustomer.setRTServiceTransactions(rtServiceTransactions);
        }

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

        for (com.propertyvista.yardi.mock.Name name : updater.getPropertyMap().keySet()) {
            Property<?> property = updater.getPropertyMap().get(name);
            if (property.getName() instanceof CoTenantUpdater.YCUSTOMER) {

                updateProperty(coTenant, property);

            } else if (property.getName() instanceof CoTenantUpdater.YCUSTOMERNAME) {

                if (coTenant.getName() == null) {
                    Name custName = new Name();
                    coTenant.setName(custName);
                }

                updateProperty(coTenant.getName(), property);

            } else if (property.getName() instanceof CoTenantUpdater.YLEASE) {

                if (coTenant.getLease() == null) {
                    YardiLease lease = new YardiLease();
                    coTenant.setLease(lease);
                }

                updateProperty(coTenant.getLease(), property);

            }
        }

    }

    public void addOrUpdateLeaseCharge(LeaseChargeUpdater updater) {

        RTServiceTransactions transactions = leaseCharges.get(updater.getCustomerID());

        Transactions t = new Transactions();
        transactions.getTransactions().add(t);

        Charge c = new Charge();
        t.setCharge(c);

        ChargeDetail detail = new ChargeDetail();
        c.setDetail(detail);

        detail.setCustomerID(updater.getCustomerID());
        detail.setUnitID(updater.getCustomerID().substring(3));
        detail.setPropertyPrimaryID(updater.getPropertyID());

        for (com.propertyvista.yardi.mock.Name name : updater.getPropertyMap().keySet()) {
            Property<?> property = updater.getPropertyMap().get(name);
            updateProperty(detail, property);
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

    protected void updateProperty(Object model, Property<?> property) {
        try {
            Method setter = null;
            if (property.getValue() != null) {
                setter = model.getClass().getMethod("set" + property.getName(), property.getValue().getClass());
            } else {
                Method[] methods = model.getClass().getMethods();
                for (Method method : methods) {
                    if (method.getName().equals("set" + property.getName())) {
                        setter = method;
                    }
                }
            }
            setter.invoke(model, property.getValue());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
