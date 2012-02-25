/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 25, 2012
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.server.billing;

import java.io.Serializable;
import java.util.List;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.domain.financial.billing.Bill;
import com.propertyvista.domain.financial.billing.BillPayment;
import com.propertyvista.domain.financial.billing.Payment;

public class PaymentProcessor {

    private final Bill bill;

    PaymentProcessor(Bill bill) {
        this.bill = bill;
    }

    void createPayments() {
        EntityQueryCriteria<Payment> criteria = EntityQueryCriteria.create(Payment.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().billPayment(), (Serializable) null));
        List<Payment> payments = Persistence.service().query(criteria);
        for (Payment item : payments) {
            createPayment(item);
        }
    }

    private void createPayment(Payment payment) {
        BillPayment billPayment = EntityFactory.create(BillPayment.class);
        billPayment.payment().set(payment);
        billPayment.bill().set(bill);
        bill.paymentReceivedAmount().setValue(bill.paymentReceivedAmount().getValue().add(billPayment.payment().amount().getValue()));
    }

}
