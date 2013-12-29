/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 8, 2013
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.yardi.mock;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yardi.entity.resident.RTCustomer;
import com.yardi.entity.resident.ResidentTransactions;
import com.yardi.entity.resident.Transactions;

import com.propertyvista.biz.system.YardiServiceException;
import com.propertyvista.domain.financial.BillingAccount;
import com.propertyvista.domain.financial.BillingAccount.PaymentAccepted;

class PaymentBatchManager {

    private final static Logger log = LoggerFactory.getLogger(PaymentBatchManager.class);

    private static long nextId = 0;

    private final long id;

    private final PropertyManager propertyManager;

    private final List<Transactions> transactions = new ArrayList<Transactions>();

    private enum State {

        New,

        Posted,

        Canceled,
    }

    private State state;

    PaymentBatchManager(PropertyManager propertyManager) {
        id = nextId++;
        this.propertyManager = propertyManager;
        state = State.New;
        log.debug("ReceiptBatch #{} created", id);
    }

    public long getId() {
        return id;
    }

    public void addReceiptsToBatch(ResidentTransactions residentTransactions) throws YardiServiceException {
        Validate.isTrue(state == State.New);
        for (com.yardi.entity.resident.Property rtProperty : residentTransactions.getProperty()) {
            for (RTCustomer rtCustomer : rtProperty.getRTCustomer()) {
                for (Transactions transaction : rtCustomer.getRTServiceTransactions().getTransactions()) {
                    validateTransaction(transaction);
                    transactions.add(transaction);
                }
            }
        }
    }

    private void validateTransaction(Transactions transaction) throws YardiServiceException {
        Validate.isTrue(propertyManager.getPropertyId().equals(transaction.getPayment().getDetail().getPropertyPrimaryID()));
        RTCustomer rtCustomer = propertyManager.getExistingRTCustomer(transaction.getPayment().getDetail().getCustomerID());

        if (propertyManager.mockFeatures.isBlockTransactionPostLease(transaction.getPayment().getDetail().getCustomerID())) {
            throw new YardiServiceException("Payments have being blocked for " + transaction.getPayment().getDetail().getCustomerID());
        }

        if (rtCustomer.getPaymentAccepted() != null) {
            PaymentAccepted accepted = BillingAccount.PaymentAccepted.getPaymentType(rtCustomer.getPaymentAccepted());
            switch (accepted) {
            case CashEquivalent:
            case DoNotAccept:
                throw new YardiServiceException(
                        "Message type= Error, value= Message Type=Error. Item Number=1. Payments are not being accepted for this tenant.");
            default:
                break;
            }
        }
    }

    public void cancelReceiptBatch() {
        log.debug("cancelReceiptBatch #{} of size {}", id, transactions.size());
        Validate.isTrue(state == State.New);
        state = State.Canceled;
    }

    public void postReceiptBatch() {

        log.debug("postReceiptBatch #{} of size {}", id, transactions.size());
        Validate.isTrue(state == State.New);
        for (Transactions transaction : transactions) {
            propertyManager.addTransaction(transaction);
        }
        transactions.clear();
        state = State.Posted;
    }

}
