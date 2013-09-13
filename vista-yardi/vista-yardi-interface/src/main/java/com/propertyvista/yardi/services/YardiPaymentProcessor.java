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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yardi.entity.resident.Property;
import com.yardi.entity.resident.RTCustomer;
import com.yardi.entity.resident.RTServiceTransactions;
import com.yardi.entity.resident.ResidentTransactions;
import com.yardi.entity.resident.Transactions;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;

import com.propertyvista.biz.financial.ar.yardi.YardiARIntegrationAgent;
import com.propertyvista.domain.financial.BillingAccount;
import com.propertyvista.domain.financial.yardi.YardiPayment;
import com.propertyvista.domain.financial.yardi.YardiReceipt;
import com.propertyvista.domain.financial.yardi.YardiReceiptReversal;

public class YardiPaymentProcessor {

    private final static Logger log = LoggerFactory.getLogger(YardiPaymentProcessor.class);

    void removeOldPayments(BillingAccount account) {
        EntityQueryCriteria<YardiPayment> criteria = EntityQueryCriteria.create(YardiPayment.class);
        criteria.eq(criteria.proto().billingAccount(), account);
        criteria.isNull(criteria.proto().paymentRecord());
        Persistence.service().delete(criteria);
    }

    public Transactions createTransactionForPayment(YardiReceipt yp) {
        Persistence.ensureRetrieve(yp.billingAccount(), AttachLevel.Attached);
        Persistence.ensureRetrieve(yp.billingAccount().lease(), AttachLevel.Attached);
        Persistence.ensureRetrieve(yp.billingAccount().lease().unit().building(), AttachLevel.Attached);

        // Create Payment transaction
        Transactions transactions = new Transactions();
        transactions.setPayment(YardiARIntegrationAgent.getPaymentReceipt(yp));
        return transactions;
    }

    public Transactions createTransactionForReversal(YardiReceiptReversal yr) {
        Persistence.ensureRetrieve(yr.billingAccount(), AttachLevel.Attached);
        Persistence.ensureRetrieve(yr.billingAccount().lease(), AttachLevel.Attached);
        Persistence.ensureRetrieve(yr.billingAccount().lease().unit().building(), AttachLevel.Attached);

        // Create Payment transaction
        Transactions transactions = new Transactions();
        transactions.setPayment(YardiARIntegrationAgent.getReceiptReversal(yr));
        return transactions;
    }

    public ResidentTransactions createTransactions(Transactions transaction) {
        ResidentTransactions transactions = new ResidentTransactions();
        // Add Payment to Customer
        RTCustomer customer = new RTCustomer();
        customer.setRTServiceTransactions(new RTServiceTransactions());
        customer.getRTServiceTransactions().getTransactions().add(transaction);

        // Add Customer to Property
        Property property = new Property();
        property.getRTCustomer().add(customer);

        // Add Property to batch
        transactions.getProperty().add(property);

        return transactions;
    }
}
