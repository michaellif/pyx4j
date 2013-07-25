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
import java.util.Iterator;
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
import com.yardi.entity.resident.Payment;
import com.yardi.entity.resident.PropertyID;
import com.yardi.entity.resident.RTCustomer;
import com.yardi.entity.resident.RTServiceTransactions;
import com.yardi.entity.resident.RTUnit;
import com.yardi.entity.resident.ResidentTransactions;
import com.yardi.entity.resident.Transactions;

import com.pyx4j.config.server.SystemDateManager;

public class PropertyManager {

    private final String propertyId;

    private final com.yardi.entity.mits.Property property;

    private final ResidentTransactions transactions;

    private final Map<String, Map<String, Charge>> leaseCharges;

    public PropertyManager(String propertyId) {
        this.propertyId = propertyId;
        property = new com.yardi.entity.mits.Property();
        transactions = new ResidentTransactions();
        leaseCharges = new HashMap<String, Map<String, Charge>>();

        transactions.getProperty().add(new com.yardi.entity.resident.Property());
    }

    public String getPropertyId() {
        return propertyId;
    }

    public ResidentTransactions getAllResidentTransactions() {
        try {
            return (ResidentTransactions) SerializationUtils.clone(transactions);
        } catch (Exception e) {
            throw new Error(e);
        }
    }

    public ResidentTransactions getResidentTransactionsForTenant(String tenantId) {
        getExistingRTCustomer(tenantId);

        ResidentTransactions transaction = null;
        try {
            transaction = (ResidentTransactions) SerializationUtils.clone(transactions);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        //remove redundant rtCustomers
        for (Iterator<RTCustomer> iterator = transaction.getProperty().get(0).getRTCustomer().iterator(); iterator.hasNext();) {
            RTCustomer rtCustomer = iterator.next();
            if (!tenantId.equals(rtCustomer.getCustomerID())) {
                iterator.remove();
            }
        }

        return transaction;
    }

    public ResidentTransactions getAllLeaseCharges() {
        ResidentTransactions retVal = new ResidentTransactions();
        com.yardi.entity.resident.Property rtProperty = new com.yardi.entity.resident.Property();
        retVal.getProperty().add(rtProperty);

        for (String customerId : leaseCharges.keySet()) {

            RTCustomer rtCustomer = createRtCustomerWithCharges(leaseCharges.get(customerId));
            rtProperty.getRTCustomer().add(rtCustomer);

        }

        return retVal;
    }

    public ResidentTransactions getLeaseChargesForTenant(String tenantId) {
        if (!leaseCharges.containsKey(tenantId)) {
            throw new RuntimeException("lease charges for tenantId " + tenantId + " not found");
        }

        ResidentTransactions retVal = new ResidentTransactions();
        com.yardi.entity.resident.Property rtProperty = new com.yardi.entity.resident.Property();
        retVal.getProperty().add(rtProperty);

        RTCustomer rtCustomer = createRtCustomerWithCharges(leaseCharges.get(tenantId));
        rtProperty.getRTCustomer().add(rtCustomer);

        return retVal;
    }

    private RTCustomer createRtCustomerWithCharges(Map<String, Charge> charges) {
        RTCustomer rtCustomer = new RTCustomer();

        RTServiceTransactions st = new RTServiceTransactions();
        rtCustomer.setRTServiceTransactions(st);

        for (String chargeId : charges.keySet()) {
            Transactions t = new Transactions();
            Charge charge = charges.get(chargeId);
            // Don't add expired products
            if (charge.getDetail().getServiceToDate() == null || charge.getDetail().getServiceToDate().after(SystemDateManager.getDate())) {
                t.setCharge((Charge) SerializationUtils.clone(charge));
                st.getTransactions().add(t);
            }
        }

        return rtCustomer;
    }

    public void updateProperty(PropertyUpdater updater) {
        com.yardi.entity.resident.Property rtProperty = transactions.getProperty().get(0);

        if (rtProperty.getPropertyID().isEmpty()) {
            PropertyID propertyID = new PropertyID();

            Identification identification = new Identification();
            identification.setType(Propertyidinfo.OTHER);
            identification.setPrimaryID(propertyId);
            identification.setMarketingName("Marketing" + propertyId);

            propertyID.setIdentification(identification);
            propertyID.getAddress().add(new Address());

            rtProperty.getPropertyID().add(propertyID);
        }

        for (com.propertyvista.yardi.mock.Name name : updater.getPropertyMap().keySet()) {
            Property<?> property = updater.getPropertyMap().get(name);
            if (property.getName() instanceof PropertyUpdater.ADDRESS) {
                Address address = rtProperty.getPropertyID().get(0).getAddress().get(0);
                updateProperty(address, property);
            }
        }
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
            leaseCharges.put(updater.getCustomerID(), new HashMap<String, Charge>());
        }

        Map<com.propertyvista.yardi.mock.Name, Property<?>> map = updater.getPropertyMap();
        for (com.propertyvista.yardi.mock.Name name : map.keySet()) {
            Property<?> property = map.get(name);

            if (property.getName() instanceof RtCustomerUpdater.RTCUSTOMER) {

                updateProperty(rtCustomer, property);

            } else if (property.getName() instanceof RtCustomerUpdater.YCUSTOMER) {

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

            } else if (property.getName() instanceof RtCustomerUpdater.YCUSTOMERADDRESS) {

                if (rtCustomer.getCustomers().getCustomer().get(0).getAddress().isEmpty()) {
                    Address custAddress = new Address();
                    rtCustomer.getCustomers().getCustomer().get(0).getAddress().add(custAddress);
                }

                updateProperty(rtCustomer.getCustomers().getCustomer().get(0).getAddress().get(0), property);

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

                if (property.getName() == RtCustomerUpdater.UNITINFO.UnitID) {
                    rtCustomer.getRTUnit().setUnitID(property.getValue().toString());
                }
            }
        }

    }

    void addTransaction(Transactions transaction) {
        RTCustomer rtCustomer = getExistingRTCustomer(transaction.getPayment().getDetail().getCustomerID());
        {
            RTServiceTransactions rtServiceTransactions = rtCustomer.getRTServiceTransactions();
            if (rtServiceTransactions == null) {
                rtServiceTransactions = new RTServiceTransactions();
                rtCustomer.setRTServiceTransactions(rtServiceTransactions);
            }
            rtServiceTransactions.getTransactions().add(transaction);
        }
    }

    public void importResidentTransactions(Payment payment) {
        RTCustomer rtCustomer = getExistingRTCustomer(payment.getDetail().getCustomerID());
        Transactions origTransaction = getPaymentTransaction(rtCustomer, payment.getDetail().getDocumentNumber());
        if ("Reverse".equals(payment.getDetail().getReversal().getType())) {
            rtCustomer.getRTServiceTransactions().getTransactions().remove(origTransaction);
        } else {
            // TODO, Generate NSF when yardi will do this
            rtCustomer.getRTServiceTransactions().getTransactions().remove(origTransaction);
        }
    }

    private Transactions getPaymentTransaction(RTCustomer rtCustomer, String documentNumber) {
        for (Transactions transaction : rtCustomer.getRTServiceTransactions().getTransactions()) {
            if (transaction.getPayment() == null) {
                continue;
            }
            if (documentNumber.equals(transaction.getPayment().getDetail().getDocumentNumber())) {
                return transaction;
            }
        }
        throw new RuntimeException("Transaction " + documentNumber + " for Lease Id " + rtCustomer.getLeaseID() + " not found");
    }

    public void addOrUpdateTransactionCharge(TransactionChargeUpdater updater) {
        RTCustomer rtCustomer = getExistingRTCustomer(updater.getCustomerID());
        {
            RTServiceTransactions rtServiceTransactions = rtCustomer.getRTServiceTransactions();
            if (rtServiceTransactions == null) {
                rtServiceTransactions = new RTServiceTransactions();
                rtCustomer.setRTServiceTransactions(rtServiceTransactions);
            }
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

        }
    }

    public void addOrUpdateCoTenant(CoTenantUpdater updater) {
        RTCustomer rtCustomer = getExistingRTCustomer(updater.getCustomerID());

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

        Map<String, Charge> charges = leaseCharges.get(updater.getCustomerID());
        Charge charge = charges.get(updater.getLeaseChargeID());
        if (charge == null) {
            charge = new Charge();
            ChargeDetail detail = new ChargeDetail();
            charge.setDetail(detail);
            charges.put(updater.getLeaseChargeID(), charge);
        }
        ChargeDetail detail = charge.getDetail();
        detail.setCustomerID(updater.getCustomerID());
        detail.setUnitID(updater.getCustomerID().substring(3));
        detail.setPropertyPrimaryID(updater.getPropertyID());

        for (com.propertyvista.yardi.mock.Name name : updater.getPropertyMap().keySet()) {
            Property<?> property = updater.getPropertyMap().get(name);
            updateProperty(charge.getDetail(), property);
        }
        charge.getDetail();
    }

    public void removeLeaseCharge(LeaseChargeUpdater updater) {
        Map<String, Charge> charges = leaseCharges.get(updater.getCustomerID());
        charges.remove(updater.getLeaseChargeID());
    }

    RTCustomer getExistingRTCustomer(String customerID) {
        RTCustomer rtCustomer = getRTCustomer(customerID);
        if (rtCustomer == null) {
            throw new RuntimeException("rtCustomer with customerID " + customerID + " not found");
        }
        return rtCustomer;
    }

    private RTCustomer getRTCustomer(String customerID) {
        RTCustomer rtCustomer = null;
        for (RTCustomer customer : transactions.getProperty().get(0).getRTCustomer()) {
            if (customer.getCustomerID().equals(customerID)) {
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

    public void unitTransferSimulation(UnitTransferSimulator simulator) {
        RTCustomer rtCustomer = getExistingRTCustomer(simulator.getCustomerID());

        // Create waisted Lease
        RTCustomer rtCustomerTransfered = (RTCustomer) SerializationUtils.clone(rtCustomer);

        rtCustomerTransfered.setCustomerID(simulator.getNewCustomerID());
        rtCustomerTransfered.getCustomers().getCustomer().get(0).setCustomerID(simulator.getNewCustomerID());

        //
        rtCustomer.getCustomers().getCustomer().get(0).setDescription(simulator.getNewYardiPersonId());

        if (simulator.getCoTenantCustomerIDs() == null) {
            // DO not transfer CoTenants.
            for (int i = rtCustomer.getCustomers().getCustomer().size() - 1; i > 1; i--) {
                rtCustomer.getCustomers().getCustomer().remove(i);
            }
        } else {
            for (int i = 0; i < simulator.getCoTenantCustomerIDs().length; i++) {
                rtCustomer.getCustomers().getCustomer().get(i + 1).setCustomerID(simulator.getCoTenantCustomerIDs()[i]);
            }
        }
        transactions.getProperty().get(0).getRTCustomer().add(rtCustomerTransfered);

        // TODO transactions and Lease charges
    }
}
