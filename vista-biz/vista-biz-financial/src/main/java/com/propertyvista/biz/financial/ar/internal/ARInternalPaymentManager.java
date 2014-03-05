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
package com.propertyvista.biz.financial.ar.internal;

import java.math.BigDecimal;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.biz.financial.ar.ARAbstractPaymentManager;
import com.propertyvista.biz.financial.payment.PaymentBatchContext;
import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.financial.billing.InvoicePayment;
import com.propertyvista.domain.financial.billing.InvoicePaymentBackOut;
import com.propertyvista.domain.property.asset.building.Building;

class ARInternalPaymentManager extends ARAbstractPaymentManager {

    private static final I18n i18n = I18n.get(ARInternalPaymentManager.class);

    private ARInternalPaymentManager() {
    }

    private static class SingletonHolder {
        public static final ARInternalPaymentManager INSTANCE = new ARInternalPaymentManager();
    }

    static ARInternalPaymentManager instance() {
        return SingletonHolder.INSTANCE;
    }

    @Override
    protected PaymentBatchContext createPaymentBatchContext(Building building) {
        return new ARInternalPaymentBatchContext();
    }

    @Override
    protected void postPayment(PaymentRecord paymentRecord, PaymentBatchContext paymentBatchContext) {

        InvoicePayment payment = EntityFactory.create(InvoicePayment.class);
        payment.paymentRecord().set(paymentRecord);
        payment.amount().setValue(paymentRecord.amount().getValue().negate());
        payment.billingAccount().set(paymentRecord.billingAccount());
        payment.description().setValue(i18n.tr("Payment Received - Thank You"));

        Persistence.service().persist(payment);

        ARInternalTransactionManager.instance().postInvoiceLineItem(payment);
    }

    @Override
    protected void rejectPayment(PaymentRecord paymentRecord, boolean applyNSF) {

        InvoicePaymentBackOut backOut = EntityFactory.create(InvoicePaymentBackOut.class);
        backOut.paymentRecord().set(paymentRecord);
        backOut.amount().setValue(paymentRecord.amount().getValue());
        backOut.billingAccount().set(paymentRecord.billingAccount());
        backOut.description().setValue(i18n.tr("Payment from ''{0,date,MM/dd/yyyy}'' was rejected", paymentRecord.createdDate().getValue()));
        backOut.taxTotal().setValue(BigDecimal.ZERO);
        backOut.applyNSF().setValue(applyNSF);

        Persistence.service().persist(backOut);

        ARInternalTransactionManager.instance().postInvoiceLineItem(backOut);

        if (applyNSF) {
            ARInternalNSFManager.instance().applyNSFCharge(paymentRecord);
        }
    }

}
