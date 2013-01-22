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

import com.pyx4j.commons.Key;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.domain.financial.yardi.YardiBillingAccount;
import com.propertyvista.domain.financial.yardi.YardiPayment;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.tenant.lease.Lease;

public class YardiPaymentProcessor {
    private final static Logger log = LoggerFactory.getLogger(YardiPaymentProcessor.class);

    public void updatePayments(List<ResidentTransactions> allTransactions) {
        for (ResidentTransactions rt : allTransactions) {
            for (Property prop : rt.getProperty()) {
                for (RTCustomer cust : prop.getRTCustomer()) {
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
                        // if it's our payment returning back (the transactionID will match the primary key), remove the original
                        Key ypKey = new Key(payment.getDetail().getTransactionID());
                        EntityQueryCriteria<YardiPayment> origPayment = EntityQueryCriteria.create(YardiPayment.class);
                        origPayment.add(PropertyCriterion.eq(origPayment.proto().billingAccount(), account));
                        origPayment.add(PropertyCriterion.eq(origPayment.proto().claimed(), true));
                        origPayment.add(PropertyCriterion.eq(origPayment.proto().id(), ypKey));
                        Persistence.service().delete(origPayment);
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
        allPayments.asc(allPayments.proto().billingAccount().lease().unit().building());
        allPayments.asc(allPayments.proto().billingAccount().lease());

        ResidentTransactions paymentTransactions = new ResidentTransactions();
        Building building = null;
        Property property = null;
        RTCustomer customer = null;
        for (YardiPayment yp : Persistence.service().query(allPayments)) {
            Persistence.ensureRetrieve(yp.billingAccount(), AttachLevel.Attached);
            Persistence.ensureRetrieve(yp.billingAccount().lease(), AttachLevel.Attached);
            Persistence.ensureRetrieve(yp.billingAccount().lease().unit().building(), AttachLevel.Attached);
            Building _bld = yp.billingAccount().lease().unit().building();
            if (building == null || !building.getPrimaryKey().equals(_bld.getPrimaryKey())) {
                building = _bld;
                property = YardiProcessorUtils.getProperty(building);
                paymentTransactions.getProperty().add(property);
                customer = null;
            }
            Lease lease = yp.billingAccount().lease();
            if (customer == null || !customer.getCustomerID().equals(lease.leaseId().getValue())) {
                customer = YardiProcessorUtils.getRTCustomer(lease);
                customer.setRTServiceTransactions(new RTServiceTransactions());
                property.getRTCustomer().add(customer);
            }
            Transactions transactions = new Transactions();
            transactions.setPayment(YardiProcessorUtils.getPayment(yp));
            customer.getRTServiceTransactions().getTransactions().add(transactions);
            // mark payment as read
            yp.claimed().setValue(true);
            Persistence.service().persist(yp);
        }
        return paymentTransactions;
    }
}
