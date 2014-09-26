/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 15, 2014
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.yardi.mock.model.stub.impl;

import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import com.yardi.entity.mits.Address;
import com.yardi.entity.mits.Addressinfo;
import com.yardi.entity.mits.Customerinfo;
import com.yardi.entity.mits.Identification;
import com.yardi.entity.mits.Information;
import com.yardi.entity.mits.Name;
import com.yardi.entity.mits.Propertyidinfo;
import com.yardi.entity.mits.YardiCustomer;
import com.yardi.entity.mits.YardiCustomers;
import com.yardi.entity.resident.Charge;
import com.yardi.entity.resident.ChargeDetail;
import com.yardi.entity.resident.NumberOccupants;
import com.yardi.entity.resident.Property;
import com.yardi.entity.resident.PropertyID;
import com.yardi.entity.resident.RTCustomer;
import com.yardi.entity.resident.RTServiceTransactions;
import com.yardi.entity.resident.RTUnit;
import com.yardi.entity.resident.ResidentTransactions;
import com.yardi.entity.resident.Transactions;

import com.pyx4j.commons.LogicalDate;

import com.propertyvista.biz.system.yardi.YardiServiceException;
import com.propertyvista.domain.settings.PmcYardiCredential;
import com.propertyvista.yardi.beans.Messages;
import com.propertyvista.yardi.mock.model.domain.YardiAddress;
import com.propertyvista.yardi.mock.model.domain.YardiBuilding;
import com.propertyvista.yardi.mock.model.domain.YardiLease;
import com.propertyvista.yardi.mock.model.domain.YardiLeaseCharge;
import com.propertyvista.yardi.mock.model.domain.YardiTenant;
import com.propertyvista.yardi.mock.model.domain.YardiTransactionCharge;
import com.propertyvista.yardi.mock.model.domain.YardiUnit;
import com.propertyvista.yardi.mock.model.manager.impl.YardiMockModelUtils;
import com.propertyvista.yardi.services.YardiHandledErrorMessages;
import com.propertyvista.yardi.stubs.YardiResidentTransactionsStub;

public class YardiMockResidentTransactionsStubImpl extends YardiMockStubBase implements YardiResidentTransactionsStub {

    @Override
    public ResidentTransactions getAllResidentTransactions(PmcYardiCredential yc, String propertyId) throws YardiServiceException, RemoteException {
        ResidentTransactions rt = new ResidentTransactions();
        YardiBuilding building = getYardiBuilding(propertyId);
        if (building == null) {
            Messages.throwYardiResponseException(YardiHandledErrorMessages.errorMessage_NoAccess + ":" + propertyId);
        }
        Property property = getProperty(building);
        // tenants
        for (YardiLease lease : building.leases()) {
            RTCustomer rtCustomer = getRtCustomer(lease, building);
            property.getRTCustomer().add(rtCustomer);
            // transactions
            rtCustomer.setRTServiceTransactions(new RTServiceTransactions());
            rtCustomer.getRTServiceTransactions().getTransactions().addAll(getTransactions(lease));
        }
        rt.getProperty().add(property);
        return rt;
    }

    @Override
    public ResidentTransactions getResidentTransactionsForTenant(PmcYardiCredential yc, String propertyId, String tenantId) throws YardiServiceException,
            RemoteException {
        ResidentTransactions rt = new ResidentTransactions();
        YardiBuilding building = getYardiBuilding(propertyId);
        if (building == null) {
            Messages.throwYardiResponseException(YardiHandledErrorMessages.errorMessage_NoAccess + ":" + propertyId);
        }
        Property property = getProperty(building);
        YardiLease lease = YardiMockModelUtils.findLease(building, tenantId);
        if (lease == null) {
            Messages.throwYardiResponseException(YardiHandledErrorMessages.errorMessage_TenantNotFound);
        }
        RTCustomer rtCustomer = getRtCustomer(lease, building);
        property.getRTCustomer().add(rtCustomer);
        // transactions
        rtCustomer.setRTServiceTransactions(new RTServiceTransactions());
        rtCustomer.getRTServiceTransactions().getTransactions().addAll(getTransactions(lease));
        rt.getProperty().add(property);
        return rt;
    }

    @Override
    public void importResidentTransactions(PmcYardiCredential yc, ResidentTransactions reversalTransactions) throws YardiServiceException, RemoteException {
        // TODO Auto-generated method stub

    }

    @Override
    public ResidentTransactions getAllLeaseCharges(PmcYardiCredential yc, String propertyId, LogicalDate date) throws YardiServiceException, RemoteException {
        ResidentTransactions rt = new ResidentTransactions();
        YardiBuilding building = getYardiBuilding(propertyId);
        if (building == null) {
            Messages.throwYardiResponseException(YardiHandledErrorMessages.errorMessage_NoAccess + ":" + propertyId);
        }
        Property property = getProperty(building);
        // tenants
        for (YardiLease lease : building.leases()) {
            RTCustomer rtCustomer = getRtCustomer(lease, building);
            // lease charges
            rtCustomer.setRTServiceTransactions(new RTServiceTransactions());
            rtCustomer.getRTServiceTransactions().getTransactions().addAll(getCharges(lease, building.buildingId().getValue(), date));
            if (!rtCustomer.getRTServiceTransactions().getTransactions().isEmpty()) {
                property.getRTCustomer().add(rtCustomer);
            }
        }
        if (!property.getRTCustomer().isEmpty()) {
            rt.getProperty().add(property);
        }
        return rt;
    }

    @Override
    public ResidentTransactions getLeaseChargesForTenant(PmcYardiCredential yc, String propertyId, String tenantId, LogicalDate date)
            throws YardiServiceException, RemoteException {
        ResidentTransactions rt = new ResidentTransactions();
        // building
        YardiBuilding building = getYardiBuilding(propertyId);
        if (building == null) {
            Messages.throwYardiResponseException(YardiHandledErrorMessages.errorMessage_NoAccess + ":" + propertyId);
        }
        Property property = getProperty(building);
        // tenant
        YardiLease lease = YardiMockModelUtils.findLease(building, tenantId);
        if (lease == null) {
            Messages.throwYardiResponseException(YardiHandledErrorMessages.errorMessage_TenantNotFound);
        }
        RTCustomer rtCustomer = getRtCustomer(lease, building);
        // transactions
        rtCustomer.setRTServiceTransactions(new RTServiceTransactions());
        rtCustomer.getRTServiceTransactions().getTransactions().addAll(getCharges(lease, building.buildingId().getValue(), date));
        if (!rtCustomer.getRTServiceTransactions().getTransactions().isEmpty()) {
            property.getRTCustomer().add(rtCustomer);
            rt.getProperty().add(property);
        }
        return rt;
    }

    // private methods
    // ---------------
    private Property getProperty(YardiBuilding building) {
        Property property = new Property();
        // set identifier
        PropertyID propertyID = new PropertyID();
        Identification identification = new Identification();
        identification.setType(Propertyidinfo.OTHER);
        identification.setPrimaryID(building.buildingId().getValue());
        identification.setMarketingName("Residential Property " + building.buildingId().getValue());
        propertyID.setIdentification(identification);
        propertyID.getAddress().add(getAddress(building.address()));
        property.getPropertyID().add(propertyID);
        return property;
    }

    private RTCustomer getRtCustomer(YardiLease lease, YardiBuilding building) {
        RTCustomer rtCustomer = new RTCustomer();
        rtCustomer.setCustomerID(lease.leaseId().getValue());
        rtCustomer.setBuildingID(building.buildingId().getValue());
        rtCustomer.setLeaseID(lease.leaseId().getValue());
        // customers
        rtCustomer.setCustomers(new YardiCustomers());
        for (YardiTenant tenant : lease.tenants()) {
            rtCustomer.getCustomers().getCustomer().add(getCustomer(tenant, building, lease));
        }
        // unit
        rtCustomer.setRTUnit(new RTUnit());
        rtCustomer.getRTUnit().setNumberOccupants(toNumberOccupants(lease.tenants().size()));
        rtCustomer.getRTUnit().setUnitID(lease.unit().unitId().getValue());
        rtCustomer.getRTUnit().setUnit(toUnitRef(lease.unit(), building));
        // accepted payment type
        rtCustomer.setPaymentAccepted("0"); // 0 = Any type
        return rtCustomer;
    }

    private List<Transactions> getTransactions(YardiLease lease) {
        List<Transactions> transactions = new ArrayList<>();
        for (YardiTransactionCharge yt : lease.transactions()) {
            transactions.add(toTransaction(yt, lease));
        }
        return transactions;
    }

    private List<Transactions> getCharges(YardiLease lease, String propertyId, LogicalDate date) {
        List<Transactions> charges = new ArrayList<>();
        for (YardiLeaseCharge ylc : lease.charges()) {
            // filter future and expired charges
            if (!ylc.serviceFromDate().isNull() && (date.lt(ylc.serviceFromDate().getValue()) || date.gt(ylc.serviceToDate().getValue()))) {
                continue;
            }
            charges.add(toTransaction(ylc, lease, propertyId));
        }
        return charges;
    }

    private Transactions toTransaction(YardiLeaseCharge ylc, YardiLease lease, String propertyId) {
        Transactions trans = new Transactions();
        ChargeDetail detail = new ChargeDetail();
        detail.setPropertyPrimaryID(propertyId);
        detail.setAmount(ylc.amount().getValue().toPlainString());
        detail.setServiceFromDate(ylc.serviceFromDate().getValue());
        detail.setServiceToDate(ylc.serviceToDate().getValue());
        detail.setChargeCode(ylc.chargeCode().getValue());
        detail.setGLAccountNumber(ylc.glAccountNumber().getValue());
        detail.setDescription(ylc.description().getValue());
        detail.setCustomerID(lease.leaseId().getValue());
        detail.setUnitID(lease.unit().unitId().getValue());
        trans.setCharge(new Charge());
        trans.getCharge().setDetail(detail);
        return trans;
    }

    private Transactions toTransaction(YardiTransactionCharge ytc, YardiLease lease) {
        Transactions trans = new Transactions();
        ChargeDetail detail = new ChargeDetail();
        detail.setAmount(ytc.amount().getValue().toPlainString());
        detail.setAmountPaid(ytc.amountPaid().getValue().toPlainString());
        detail.setBalanceDue(ytc.balanceDue().getValue().toPlainString());
        detail.setTransactionID(ytc.transactionId().getValue());
        detail.setTransactionDate(ytc.transactionDate().getValue());
        detail.setChargeCode(ytc.chargeCode().getValue());
        detail.setGLAccountNumber(ytc.glAccountNumber().getValue());
        detail.setDescription(ytc.description().getValue());
        detail.setCustomerID(lease.leaseId().getValue());
        detail.setUnitID(lease.unit().unitId().getValue());
        trans.setCharge(new Charge());
        trans.getCharge().setDetail(detail);
        return trans;
    }

    private YardiCustomer getCustomer(YardiTenant tenant, YardiBuilding building, YardiLease lease) {
        YardiCustomer customer = new YardiCustomer();
        customer.setCustomerID(tenant.tenantId().getValue());
        customer.setType(toCustomerinfo(tenant.type().getValue()));
        customer.setName(toName(tenant.firstName().getValue(), tenant.lastName().getValue()));
        customer.setLease(toLeaseRef(tenant, lease));
        // address
        Address addr = getAddress(building.address(), "Apt " + lease.unit().unitId().getValue());
        customer.getAddress().add(addr);
        // email
        addr.setEmail(tenant.email().getValue());
        // TODO - below only needed for main tenant
        customer.setProperty(toPropertyRef(building.buildingId().getValue(), lease.unit().unitId().getValue()));
        return customer;
    }

    private com.yardi.entity.mits.Unit toUnitRef(YardiUnit unit, YardiBuilding building) {
        com.yardi.entity.mits.Unit unitRef = new com.yardi.entity.mits.Unit();
        unitRef.setPropertyPrimaryID(building.buildingId().getValue());
        Information unitInfo = new Information();
        unitInfo.setUnitID(unit.unitId().getValue());
        unitInfo.setUnitType(unit.floorplan().floorplanId().getValue());
        unitInfo.setUnitBedrooms(new BigDecimal(unit.floorplan().bedrooms().getValue()));
        unitInfo.setUnitBathrooms(new BigDecimal(unit.floorplan().bathrooms().getValue()));
        unitInfo.setUnitRent(unit.rent().getValue());
        unitInfo.setFloorPlanID(unit.floorplan().floorplanId().getValue());
        unitInfo.setFloorplanName(unit.floorplan().name().getValue());
        unitRef.getInformation().add(unitInfo);
        return unitRef;
    }

    private com.yardi.entity.mits.YardiLease toLeaseRef(YardiTenant tenant, YardiLease lease) {
        com.yardi.entity.mits.YardiLease leaseRef = new com.yardi.entity.mits.YardiLease();
        leaseRef.setResponsibleForLease(tenant.responsibleForLease().getValue(false));
        // TODO - below only needed for main tenant
        leaseRef.setCurrentRent(lease.currentRent().getValue());
        leaseRef.setLeaseFromDate(lease.leaseFrom().getValue());
        leaseRef.setLeaseToDate(lease.leaseTo().getValue());
        leaseRef.setExpectedMoveInDate(lease.expectedMoveIn().getValue());
        leaseRef.setExpectedMoveOutDate(lease.expectedMoveOut().getValue());
        leaseRef.setActualMoveIn(lease.actualMoveIn().getValue());
        leaseRef.setActualMoveOut(lease.actualMoveOut().getValue());
        return leaseRef;
    }

    private com.yardi.entity.mits.Property toPropertyRef(String buildingId, String unitId) {
        com.yardi.entity.mits.Property propertyRef = new com.yardi.entity.mits.Property();
        propertyRef.setPrimaryID(buildingId);
        propertyRef.setUnitID(unitId);
        return propertyRef;
    }

    private Name toName(String firstName, String lastName) {
        Name name = new Name();
        name.setFirstName(firstName);
        name.setLastName(lastName);
        return name;
    }

    NumberOccupants toNumberOccupants(int total) {
        NumberOccupants num = new NumberOccupants();
        num.setTotal(String.valueOf(total));
        return num;
    }

    private Customerinfo toCustomerinfo(YardiTenant.Type type) {
        return Customerinfo.valueOf(type.name());
    }

    private Address getAddress(YardiAddress ya) {
        return getAddress(ya, null);
    }

    private Address getAddress(YardiAddress ya, String unit) {
        Address address = new Address();
        address.setType(Addressinfo.CURRENT);
        address.setAddress1(ya.street().getValue());
        if (unit != null) {
            address.getAddress2().add(unit);
        }
        address.setCity(ya.city().getValue());
        address.setProvince(ya.province().getValue());
        address.setState(ya.province().getValue());
        address.setCountry(ya.country().getValue());
        address.setPostalCode(ya.postalCode().getValue());
        return address;
    }
}
