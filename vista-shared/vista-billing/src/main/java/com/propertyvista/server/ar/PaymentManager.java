/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 24, 2012
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.server.ar;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.domain.financial.billing.InvoicePayment;
import com.propertyvista.domain.tenant.lease.PaymentRecord;

public class PaymentManager {

    private static final I18n i18n = I18n.get(PaymentManager.class);

    static void postPayment(PaymentRecord paymentRecord) {
        InvoicePayment invoicePayment = EntityFactory.create(InvoicePayment.class);
        invoicePayment.paymentRecord().set(paymentRecord);
        invoicePayment.amount().setValue(paymentRecord.amount().getValue());

        invoicePayment.description().setValue(i18n.tr("Payment Received - Thank You"));
        invoicePayment.fromDate().setValue(paymentRecord.receivedDate().getValue());

        Persistence.service().persist(invoicePayment);

        Persistence.service().retrieve(paymentRecord.billingAccount());
        Persistence.service().retrieve(paymentRecord.billingAccount().interimLineItems());

        paymentRecord.billingAccount().interimLineItems().add(invoicePayment);

        Persistence.service().persist(paymentRecord.billingAccount());
        Persistence.service().commit();

    }

    static void rejectPayment(PaymentRecord payment) {
        payment.paymentStatus().setValue(PaymentRecord.PaymentStatus.Rejected);
        Persistence.service().persist(payment);
        Persistence.service().commit();
    }

}
