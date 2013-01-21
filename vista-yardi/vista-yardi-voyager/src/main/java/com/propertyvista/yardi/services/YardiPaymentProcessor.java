/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 19, 2013
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.yardi.services;

import com.yardi.entity.mits.Identification;
import com.yardi.entity.resident.Detail;
import com.yardi.entity.resident.Payment;
import com.yardi.entity.resident.Property;
import com.yardi.entity.resident.PropertyID;
import com.yardi.entity.resident.RTCustomer;
import com.yardi.entity.resident.RTServiceTransactions;
import com.yardi.entity.resident.ResidentTransactions;
import com.yardi.entity.resident.Transactions;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.domain.financial.yardi.YardiPayment;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.tenant.lease.Lease;

public class YardiPaymentProcessor {

    public enum YardiReversalType {
        NSF, Chargeback, Adjustment, Other
    }

    public enum YardiPaymentType {
        CreditCard, DebitCard, ACH, Check, Cash, MoneyOrder, Other
    }

    public enum YardiPaymentChannel {
        Online, Phone, Onsite, Mail, Lockbox, BankBillPayment, Other
    }

    // TODO add payment reversals
    public ResidentTransactions getAllPaymentTransactions() {
        EntityQueryCriteria<YardiPayment> allPayments = EntityQueryCriteria.create(YardiPayment.class);
        allPayments.add(PropertyCriterion.eq(allPayments.proto().claimed(), false));
        allPayments.asc(allPayments.proto().billingAccount().lease().unit().building());
        allPayments.asc(allPayments.proto().billingAccount().lease());

        ResidentTransactions paymentTransactions = new ResidentTransactions();
        Building building = null;
        Property property = null;
        RTCustomer customer = null;
        for (YardiPayment yp : Persistence.service().query(allPayments)) {
            Building _bld = yp.billingAccount().lease().unit().building();
            if (!_bld.getPrimaryKey().equals(building.getPrimaryKey())) {
                building = _bld;
                property = getProperty(building);
                paymentTransactions.getProperty().add(property);
                customer = null;
            }
            Lease lease = yp.billingAccount().lease();
            if (customer == null || !customer.getCustomerID().equals(lease.leaseId().getValue())) {
                customer = getRTCustomer(lease);
                customer.setRTServiceTransactions(new RTServiceTransactions());
                property.getRTCustomer().add(customer);
            }
            Transactions transactions = new Transactions();
            transactions.setPayment(getPayment(yp));
            customer.getRTServiceTransactions().getTransactions().add(transactions);
            // mark payment as read
            yp.claimed().setValue(true);
            Persistence.service().persist(yp);
        }
        return paymentTransactions;
    }

    private Property getProperty(Building building) {
        Property property = new Property();
        PropertyID propId = new PropertyID();
        Identification id = new Identification();
        id.setPrimaryID(building.propertyCode().getValue());
        propId.setIdentification(id);
        property.getPropertyID().add(propId);
        return property;
    }

    private RTCustomer getRTCustomer(Lease lease) {
        RTCustomer customer = new RTCustomer();
        customer.setCustomerID(lease.leaseId().getValue());
        return customer;
    }

    private Payment getPayment(YardiPayment yp) {
        Payment payment = new Payment();
        payment.setType(getPaymentType(yp));
        payment.setChannel(YardiPaymentChannel.Online.name());
        payment.setDetail(getPaymentDetail(yp));
        return payment;
    }

    private Detail getPaymentDetail(YardiPayment yp) {
        Detail detail = new Detail();
        detail.setTransactionDate(yp.postDate().getValue());
        detail.setCustomerID(yp.billingAccount().lease().leaseId().getValue());
        detail.setPaidBy(yp.paymentRecord().paymentMethod().customer().user().name().getValue());
        detail.setAmount(yp.amount().getValue().toString());
        detail.setDescription(yp.description().getValue());
        detail.setPropertyPrimaryID(yp.billingAccount().lease().unit().building().propertyCode().getValue());
        return detail;
    }

    private String getPaymentType(YardiPayment yp) {
        switch (yp.paymentRecord().paymentMethod().type().getValue()) {
        case Cash:
            return YardiPaymentType.Cash.name();
        case Check:
            return YardiPaymentType.Check.name();
        case Echeck:
            return YardiPaymentType.Other.name();
        case EFT:
            return YardiPaymentType.ACH.name();
        case CreditCard:
            return YardiPaymentType.CreditCard.name();
        case Interac:
            return YardiPaymentType.DebitCard.name();
        }
        return null;
    }
}
