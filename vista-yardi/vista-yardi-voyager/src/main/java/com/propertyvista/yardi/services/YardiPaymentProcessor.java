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

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yardi.entity.mits.YardiLease;
import com.yardi.entity.resident.Payment;
import com.yardi.entity.resident.Property;
import com.yardi.entity.resident.RTCustomer;
import com.yardi.entity.resident.RTServiceTransactions;
import com.yardi.entity.resident.ResidentTransactions;
import com.yardi.entity.resident.Transactions;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.domain.financial.yardi.YardiBillingAccount;
import com.propertyvista.domain.financial.yardi.YardiPayment;

public class YardiPaymentProcessor {
    private final static Logger log = LoggerFactory.getLogger(YardiPaymentProcessor.class);

    public void updatePayments(List<ResidentTransactions> allTransactions) {
        for (ResidentTransactions rt : allTransactions) {
            for (Property prop : rt.getProperty()) {
                for (RTCustomer cust : prop.getRTCustomer()) {
                    // skip payment if lease expired
                    YardiLease yardiLease = cust.getCustomers().getCustomer().get(0).getLease();
                    if (new LogicalDate(yardiLease.getLeaseToDate()).before(new LogicalDate())) {
                        log.info("Transaction for: {} skipped, lease expired.", cust.getCustomerID());
                        continue;
                    }

                    log.info("Transaction for: " + cust.getCustomerID() + "/" + cust.getRTUnit().getUnitID());
                    // 1. get customer's YardiBillingAccount
                    YardiBillingAccount account = YardiProcessorUtils.getYardiBillingAccount(cust);
                    if (account == null) {
                        try {
                            Persistence.service().rollback();
                        } catch (Throwable ignore) {
                        }
                        continue;
                    }
                    // 2. remove previously received yardi payments
                    EntityQueryCriteria<YardiPayment> oldPayments = EntityQueryCriteria.create(YardiPayment.class);
                    oldPayments.add(PropertyCriterion.eq(oldPayments.proto().billingAccount(), account));
                    oldPayments.add(PropertyCriterion.isNull(oldPayments.proto().paymentRecord()));
                    Persistence.service().delete(oldPayments);
                    for (Transactions tr : cust.getRTServiceTransactions().getTransactions()) {
                        if (tr == null || tr.getPayment() == null) {
                            continue;
                        }
                        Payment payment = tr.getPayment();
                        // if it's our payment returning back (the DocumentNumber will match the primary key), remove the original
                        String keyStr = payment.getDetail().getDocumentNumber();
                        if (keyStr != null && keyStr.length() > 0) {
                            Key ypKey = new Key(keyStr);
                            EntityQueryCriteria<YardiPayment> origPayment = EntityQueryCriteria.create(YardiPayment.class);
                            origPayment.add(PropertyCriterion.eq(origPayment.proto().billingAccount(), account));
                            origPayment.add(PropertyCriterion.eq(origPayment.proto().claimed(), true));
                            origPayment.add(PropertyCriterion.eq(origPayment.proto().id(), ypKey));
                            Persistence.service().delete(origPayment);
                        }
                        // add new payment transaction
                        Persistence.service().persist(YardiProcessorUtils.createPayment(account, payment));
                    }
                    Persistence.service().commit();
                }
            }
        }
    }

    // TODO add payment reversals
    public ResidentTransactions getAllPaymentTransactions() {
        EntityQueryCriteria<YardiPayment> allPayments = EntityQueryCriteria.create(YardiPayment.class);
        allPayments.add(PropertyCriterion.eq(allPayments.proto().claimed(), false));

        ResidentTransactions paymentTransactions = new ResidentTransactions();
        for (YardiPayment yp : Persistence.service().query(allPayments)) {
            Persistence.ensureRetrieve(yp.billingAccount(), AttachLevel.Attached);
            Persistence.ensureRetrieve(yp.billingAccount().lease(), AttachLevel.Attached);
            Persistence.ensureRetrieve(yp.billingAccount().lease().unit().building(), AttachLevel.Attached);

            // Create Payment transaction
            Transactions transactions = new Transactions();
            transactions.setPayment(YardiProcessorUtils.getPayment(yp));

            // Add Payment to Customer
            RTCustomer customer = new RTCustomer();
            customer.setRTServiceTransactions(new RTServiceTransactions());
            customer.getRTServiceTransactions().getTransactions().add(transactions);

            // Add Customer to Property
            Property property = new Property();
            property.getRTCustomer().add(customer);

            // Add Property to batch
            paymentTransactions.getProperty().add(property);

            // mark payment as read
            yp.claimed().setValue(true);
            Persistence.service().persist(yp);
        }
        return paymentTransactions;
    }
}
