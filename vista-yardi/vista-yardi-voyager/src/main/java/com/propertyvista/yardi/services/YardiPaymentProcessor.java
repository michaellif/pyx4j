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

import com.yardi.entity.resident.Payment;
import com.yardi.entity.resident.Property;
import com.yardi.entity.resident.RTCustomer;
import com.yardi.entity.resident.RTServiceTransactions;
import com.yardi.entity.resident.ResidentTransactions;
import com.yardi.entity.resident.Transactions;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.domain.financial.yardi.YardiBillingAccount;
import com.propertyvista.domain.financial.yardi.YardiPayment;
import com.propertyvista.domain.financial.yardi.YardiReceipt;
import com.propertyvista.domain.financial.yardi.YardiReceiptReversal;

public class YardiPaymentProcessor {
    private final static Logger log = LoggerFactory.getLogger(YardiPaymentProcessor.class);

    @Deprecated
    public void updatePayments(ResidentTransactions rt) {
        for (Property prop : rt.getProperty()) {
            for (RTCustomer cust : prop.getRTCustomer()) {
                // skip payment if lease expired
                if (new YardiLeaseProcessor().isSkipped(cust)) {
                    log.info("Transaction for: {} skipped, lease does not meet criteria.", cust.getCustomerID());
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
                    // add new payment transaction
                    Persistence.service().persist(YardiProcessorUtils.createPayment(account, payment));
                }
                Persistence.service().commit();
            }
        }

    }

    void removeOldPayments(YardiBillingAccount account) {
        EntityQueryCriteria<YardiPayment> oldPayments = EntityQueryCriteria.create(YardiPayment.class);
        oldPayments.add(PropertyCriterion.eq(oldPayments.proto().billingAccount(), account));
        oldPayments.add(PropertyCriterion.isNull(oldPayments.proto().paymentRecord()));
        Persistence.service().delete(oldPayments);
    }

    public Transactions createTransactionForPayment(YardiReceipt yp) {
        Persistence.ensureRetrieve(yp.billingAccount(), AttachLevel.Attached);
        Persistence.ensureRetrieve(yp.billingAccount().lease(), AttachLevel.Attached);
        Persistence.ensureRetrieve(yp.billingAccount().lease().unit().building(), AttachLevel.Attached);

        // Create Payment transaction
        Transactions transactions = new Transactions();
        transactions.setPayment(YardiProcessorUtils.getPayment(yp));
        return transactions;
    }

    public Transactions createTransactionForReversal(YardiReceiptReversal yr) {
        Persistence.ensureRetrieve(yr.billingAccount(), AttachLevel.Attached);
        Persistence.ensureRetrieve(yr.billingAccount().lease(), AttachLevel.Attached);
        Persistence.ensureRetrieve(yr.billingAccount().lease().unit().building(), AttachLevel.Attached);

        // Create Payment transaction
        Transactions transactions = new Transactions();
        transactions.setPayment(YardiProcessorUtils.getReceiptReversal(yr));
        return transactions;
    }

    public ResidentTransactions addTransactionToBatch(Transactions transaction, ResidentTransactions batch) {
        if (batch == null) {
            batch = new ResidentTransactions();
        }
        // Add Payment to Customer
        RTCustomer customer = new RTCustomer();
        customer.setRTServiceTransactions(new RTServiceTransactions());
        customer.getRTServiceTransactions().getTransactions().add(transaction);

        // Add Customer to Property
        Property property = new Property();
        property.getRTCustomer().add(customer);

        // Add Property to batch
        batch.getProperty().add(property);

        return batch;
    }

    public List<YardiReceiptReversal> getAllReceiptReversals() {
        EntityQueryCriteria<YardiReceiptReversal> nsfReversals = EntityQueryCriteria.create(YardiReceiptReversal.class);
        nsfReversals.add(PropertyCriterion.eq(nsfReversals.proto().claimed(), false));
        return Persistence.service().query(nsfReversals);
    }

    public void onPostReceiptSuccess(YardiReceipt receipt) {
        // mark reversal as posted
        receipt.claimed().setValue(true);
        Persistence.service().persist(receipt);
    }

    public void onPostReceiptReversalSuccess(YardiReceiptReversal reversal) {
        // mark reversal as posted
        reversal.claimed().setValue(true);
        Persistence.service().persist(reversal);
    }
}
