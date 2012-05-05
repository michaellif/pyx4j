/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 1, 2012
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.biz.financial.payment;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.shared.UserRuntimeException;

import com.propertyvista.biz.financial.ar.ARFacade;
import com.propertyvista.domain.financial.PaymentRecord;
import com.propertyvista.domain.payment.CreditCardInfo;
import com.propertyvista.domain.payment.PaymentMethod;

public class PaymentFacadeImpl implements PaymentFacade {

    private static final I18n i18n = I18n.get(PaymentFacadeImpl.class);

    @Override
    public PaymentMethod persistPaymentMethod(PaymentMethod paymentMethod) {

        //TODO store credit cards
        switch (paymentMethod.type().getValue()) {
        case CreditCard:
            CreditCardInfo cc = paymentMethod.details().cast();
            if (!cc.number().isNull()) {
                cc.numberRefference().setValue(last4Numbers(cc.number().getValue()));
            }
        }

        Persistence.service().merge(paymentMethod);
        return paymentMethod;
    }

    private String last4Numbers(String value) {
        if (value.length() < 4) {
            return null;
        } else {
            return value.substring(value.length() - 4, value.length());
        }
    }

    @Override
    public PaymentRecord persistPayment(PaymentRecord payment) {
        if (payment.paymentStatus().isNull()) {
            payment.paymentStatus().setValue(PaymentRecord.PaymentStatus.Submitted);
        }
        if (!payment.paymentStatus().getValue().equals(PaymentRecord.PaymentStatus.Submitted)) {
            throw new Error();
        }
        persistPaymentMethod(payment.paymentMethod());
        Persistence.service().merge(payment);
        return payment;
    }

    @Override
    public PaymentRecord processPayment(PaymentRecord payment) {
        if (!payment.paymentStatus().getValue().equals(PaymentRecord.PaymentStatus.Submitted)) {
            throw new Error();
        }
        payment.paymentStatus().setValue(PaymentRecord.PaymentStatus.Processing);

        switch (payment.paymentMethod().type().getValue()) {
        case Cash:
            payment.paymentStatus().setValue(PaymentRecord.PaymentStatus.Received);
            Persistence.service().merge(payment);
            ServerSideFactory.create(ARFacade.class).postPayment(payment);
            break;
        case Check:
            Persistence.service().merge(payment);
            ServerSideFactory.create(ARFacade.class).postPayment(payment);
            break;
        case CreditCard:
            Persistence.service().merge(payment);
            CreditCardProcessor.realTimeSale(payment);
            break;
        case Interac:
            throw new IllegalArgumentException("Not implemented");
        case Echeck:
            Persistence.service().merge(payment);
            ServerSideFactory.create(ARFacade.class).postPayment(payment);
            PADProcessor.queuePayment(payment);
        case EFT:
            payment.paymentStatus().setValue(PaymentRecord.PaymentStatus.Received);
            Persistence.service().merge(payment);
            ServerSideFactory.create(ARFacade.class).postPayment(payment);
        default:
            throw new IllegalArgumentException();
        }

        return payment;
    }

    @Override
    public PaymentRecord cancel(PaymentRecord paymentStub) {
        PaymentRecord payment = Persistence.service().retrieve(PaymentRecord.class, paymentStub.getPrimaryKey());
        if (!payment.paymentStatus().getValue().equals(PaymentRecord.PaymentStatus.Submitted)) {
            throw new UserRuntimeException(i18n.tr("Processed payment can't be canceled"));
        }
        payment.paymentStatus().setValue(PaymentRecord.PaymentStatus.Canceled);
        Persistence.service().merge(payment);
        return payment;
    }

    @Override
    public PaymentRecord clear(PaymentRecord paymentStub) {
        PaymentRecord payment = Persistence.service().retrieve(PaymentRecord.class, paymentStub.getPrimaryKey());
        if (!payment.paymentStatus().getValue().equals(PaymentRecord.PaymentStatus.Processing)) {
            throw new UserRuntimeException(i18n.tr("Processed payment can't be cleared"));
        }
        payment.paymentStatus().setValue(PaymentRecord.PaymentStatus.Received);
        Persistence.service().merge(payment);
        return payment;
    }

    @Override
    public PaymentRecord reject(PaymentRecord paymentStub) {
        PaymentRecord payment = Persistence.service().retrieve(PaymentRecord.class, paymentStub.getPrimaryKey());
        if (!payment.paymentStatus().getValue().equals(PaymentRecord.PaymentStatus.Processing)) {
            throw new UserRuntimeException(i18n.tr("Processed payment can't be rejected"));
        }
        payment.paymentStatus().setValue(PaymentRecord.PaymentStatus.Rejected);
        Persistence.service().merge(payment);

        switch (payment.paymentMethod().type().getValue()) {
        case Check:
        case Echeck:
            ServerSideFactory.create(ARFacade.class).rejectPayment(payment);
            break;
        }
        return payment;
    }

}
