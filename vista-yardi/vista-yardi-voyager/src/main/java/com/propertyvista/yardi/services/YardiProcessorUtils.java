/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 17, 2013
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.yardi.services;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yardi.entity.mits.Identification;
import com.yardi.entity.resident.ChargeDetail;
import com.yardi.entity.resident.Detail;
import com.yardi.entity.resident.Payment;
import com.yardi.entity.resident.Property;
import com.yardi.entity.resident.PropertyID;
import com.yardi.entity.resident.RTCustomer;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.domain.financial.BillingAccount;
import com.propertyvista.domain.financial.yardi.YardiBillingAccount;
import com.propertyvista.domain.financial.yardi.YardiCharge;
import com.propertyvista.domain.financial.yardi.YardiPayment;
import com.propertyvista.domain.financial.yardi.YardiService;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.tenant.lease.Lease;

public class YardiProcessorUtils {
    private final static Logger log = LoggerFactory.getLogger(YardiProcessorUtils.class);

    public enum YardiReversalType {
        NSF, Chargeback, Adjustment, Other
    }

    public enum YardiPaymentType {
        CreditCard, DebitCard, ACH, Check, Cash, MoneyOrder, Other
    }

    public enum YardiPaymentChannel {
        Online, Phone, Onsite, Mail, Lockbox, BankBillPayment, Other
    }

    public static String getPropertyId(PropertyID propertyID) {
        return propertyID.getIdentification() != null ? getPropertyId(propertyID.getIdentification()) : null;
    }

    public static String getPropertyId(Identification identification) {
        return identification != null ? (identification.getPrimaryID()) : null;
    }

    public static String getUnitId(RTCustomer customer) {
        return customer.getRTUnit() != null ? customer.getRTUnit().getUnitID() : null;
    }

    /*
     * ChargeProcessor utils
     */
    public static YardiBillingAccount getYardiBillingAccount(RTCustomer customer) {
        EntityQueryCriteria<Lease> leaseCrit = EntityQueryCriteria.create(Lease.class);
        leaseCrit.add(PropertyCriterion.eq(leaseCrit.proto().leaseId(), customer.getCustomerID()));
        Lease lease = Persistence.service().retrieve(leaseCrit);
        if (lease == null) {
            // no lease found - quit
            return null;
        }
        EntityQueryCriteria<YardiBillingAccount> accntCrit = EntityQueryCriteria.create(YardiBillingAccount.class);
        accntCrit.add(PropertyCriterion.eq(accntCrit.proto().lease(), lease));
        YardiBillingAccount account = Persistence.service().retrieve(accntCrit);
        if (account == null) {
            // create new account
            account = EntityFactory.create(YardiBillingAccount.class);
            account.lease().set(lease);
            Persistence.service().persist(account);
        }

        return account;
    }

    public static YardiCharge createCharge(YardiBillingAccount account, ChargeDetail detail) {
        YardiCharge charge = EntityFactory.create(YardiCharge.class);
        charge.billingAccount().set(account);
        if (detail.getService() != null) {
            try {
                charge.service().type().setValue(YardiService.Type.valueOf(detail.getService().getType()));
            } catch (Exception e) {
                log.info("ERROR - unknown service type: " + e);
            }
        }
        charge.chargeCode().setValue(detail.getChargeCode());
        charge.amount().setValue(new BigDecimal(detail.getAmount()));
        charge.description().setValue(detail.getDescription());
        charge.postDate().setValue(new LogicalDate(detail.getTransactionDate().getTime()));
        charge.transactionId().setValue(detail.getTransactionID());
        charge.amountPaid().setValue(new BigDecimal(detail.getAmountPaid()));
        charge.balanceDue().setValue(new BigDecimal(detail.getBalanceDue()));
        charge.comment().setValue(detail.getComment());
        charge.taxTotal().setValue(BigDecimal.ZERO);

        return charge;
    }

    /*
     * PaymentProcessor utils
     */
    public static Property getProperty(Building building) {
        Property property = new Property();
        PropertyID propId = new PropertyID();
        Identification id = new Identification();
        id.setPrimaryID(building.propertyCode().getValue());
        propId.setIdentification(id);
        property.getPropertyID().add(propId);
        return property;
    }

    public static RTCustomer getRTCustomer(Lease lease) {
        RTCustomer customer = new RTCustomer();
        customer.setCustomerID(lease.leaseId().getValue());
        return customer;
    }

    public static Payment getPayment(YardiPayment yp) {
        Payment payment = new Payment();
        payment.setType(getPaymentType(yp));
        payment.setChannel(YardiPaymentChannel.Online.name());
        payment.setDetail(getPaymentDetail(yp));
        return payment;
    }

    public static Detail getPaymentDetail(YardiPayment yp) {
        Detail detail = new Detail();
        detail.setTransactionID(yp.getPrimaryKey().toString());
        detail.setTransactionDate(yp.postDate().getValue());
        detail.setCustomerID(yp.billingAccount().lease().leaseId().getValue());
        detail.setPaidBy(yp.paymentRecord().paymentMethod().customer().user().name().getValue());
        detail.setAmount(yp.amount().getValue().toString());
        detail.setDescription(yp.description().getValue());
        detail.setPropertyPrimaryID(yp.billingAccount().lease().unit().building().propertyCode().getValue());
        return detail;
    }

    public static String getPaymentType(YardiPayment yp) {
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

    public static YardiPayment createPayment(BillingAccount account, Payment payment) {
        YardiPayment yp = EntityFactory.create(YardiPayment.class);

        yp.billingAccount().set(account);
        Detail detail = payment.getDetail();
        yp.amount().setValue(new BigDecimal(detail.getAmountPaid()).negate());
        yp.description().setValue(detail.getDescription());
        yp.postDate().setValue(new LogicalDate(detail.getTransactionDate().getTime()));

        return yp;
    }
}
