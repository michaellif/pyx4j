/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 19, 2012
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.oapi.marshaling;

import com.pyx4j.entity.core.EntityFactory;

import com.propertyvista.domain.financial.billingext.dto.PaymentRecordDTO;
import com.propertyvista.oapi.model.PaymentRecordIO;
import com.propertyvista.oapi.xml.BigDecimalIO;
import com.propertyvista.oapi.xml.LogicalDateIO;
import com.propertyvista.oapi.xml.StringIO;

public class PaymentRecordMarshaller implements Marshaller<PaymentRecordDTO, PaymentRecordIO> {

    private static class SingletonHolder {
        public static final PaymentRecordMarshaller INSTANCE = new PaymentRecordMarshaller();
    }

    private PaymentRecordMarshaller() {
    }

    public static PaymentRecordMarshaller getInstance() {
        return SingletonHolder.INSTANCE;
    }

    @Override
    public PaymentRecordIO marshal(PaymentRecordDTO payment) {
        if (payment == null || payment.isNull()) {
            return null;
        }
        PaymentRecordIO paymentIO = new PaymentRecordIO();
        paymentIO.transactionId = MarshallerUtils.getValue(payment.transactionId());

        paymentIO.externalTransactionId = MarshallerUtils.createIo(StringIO.class, payment.externalTransactionId());
        paymentIO.leaseId = MarshallerUtils.createIo(StringIO.class, payment.leaseId());
        paymentIO.amount = MarshallerUtils.createIo(BigDecimalIO.class, payment.amount());
        paymentIO.transactionDate = MarshallerUtils.createIo(LogicalDateIO.class, payment.transactionDate());
        paymentIO.paymentType = MarshallerUtils.createIo(StringIO.class, payment.paymentType());
        return paymentIO;
    }

    @Override
    public PaymentRecordDTO unmarshal(PaymentRecordIO paymentIO) {
        PaymentRecordDTO payment = EntityFactory.create(PaymentRecordDTO.class);
        payment.transactionId().setValue(paymentIO.transactionId);

        MarshallerUtils.setValue(payment.externalTransactionId(), paymentIO.externalTransactionId);
        MarshallerUtils.setValue(payment.leaseId(), paymentIO.leaseId);
        MarshallerUtils.setValue(payment.amount(), paymentIO.amount);
        MarshallerUtils.setValue(payment.transactionDate(), paymentIO.transactionDate);
        MarshallerUtils.setValue(payment.paymentType(), paymentIO.paymentType);
        return payment;
    }

}
